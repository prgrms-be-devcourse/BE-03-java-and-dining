package com.prgms.allen.dining.domain.reservation;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.DateAndTotalVisitCountPerDayProj;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleRes;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.dto.ReservationAvailableDatesRes;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private static final List<ReservationStatus> TAKEN_STATUS_LIST =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

	private static final long MAX_RESERVE_PERIOD = 30L;

	private final ReservationRepository reservationRepository;
	private final RestaurantService restaurantService;
	private final MemberService memberService;

	public ReservationService(
		ReservationRepository reservationRepository,
		RestaurantService restaurantService,
		MemberService memberService
	) {
		this.reservationRepository = reservationRepository;
		this.restaurantService = restaurantService;
		this.memberService = memberService;
	}

	@Transactional
	public Long reserve(Long customerId, ReservationCreateReq createRequest) {
		Member customer = memberService.findCustomerById(customerId);
		Restaurant restaurant = restaurantService.findById(createRequest.restaurantId());

		ReservationCustomerInput reservationCustomerInput = createRequest
			.reservationCustomerInput()
			.toEntity();

		Reservation newReservation = new Reservation(
			customer,
			restaurant,
			reservationCustomerInput
		);

		reservationRepository.save(newReservation);
		return newReservation.getId();
	}

	public Page<ReservationSimpleRes> getRestaurantReservations(
		long restaurantId,
		ReservationStatus status,
		Pageable pageable
	) {
		restaurantService.validateRestaurantExists(restaurantId);

		return new PageImpl<>(
			reservationRepository.findAllByRestaurantIdAndStatus(restaurantId, status, pageable)
				.stream()
				.map(ReservationSimpleRes::new)
				.toList()
		);
	}

	public Reservation findById(Long id) {
		return reservationRepository.findById(id)
			.orElseThrow(() ->
				new NotFoundResourceException(MessageFormat.format(
					"Cannot find Reservation for reservationId={0}", id
				))
			);
	}

	public boolean isAvailableReserve(
		Restaurant restaurant,
		LocalDateTime requestTime,
		int numberOfPeople
	) {

		Optional<Integer> totalCount = reservationRepository.countTotalVisitorCount(restaurant,
			requestTime.toLocalDate(),
			requestTime.toLocalTime(),
			TAKEN_STATUS_LIST);

		return restaurant.isAvailable(totalCount.get(), numberOfPeople);
	}

	public ReservationAvailableDatesRes getAvailableDates(Long restaurantId) {
		Restaurant restaurant = restaurantService.findById(restaurantId);

		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(MAX_RESERVE_PERIOD);

		List<LocalDate> availableDatesByRestaurant = getDatesExceptFullReserveDates(restaurant);

		List<LocalDate> canReverseDates = start.datesUntil(end)
			.filter(localDate -> !restaurant.isClosingDay(localDate))
			.filter(availableDatesByRestaurant::contains)
			.toList();

		return new ReservationAvailableDatesRes(canReverseDates);
	}

	private List<LocalDate> getDatesExceptFullReserveDates(Restaurant restaurant) {
		return reservationRepository.findTotalVisitorCountPerDay(restaurant, TAKEN_STATUS_LIST)
			.stream()
			.filter(proj -> restaurant.isAvailableForDay(proj.count()))
			.map(DateAndTotalVisitCountPerDayProj::date)
			.toList();
	}
}
