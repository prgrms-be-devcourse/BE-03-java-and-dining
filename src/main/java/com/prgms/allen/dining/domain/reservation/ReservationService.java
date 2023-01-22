package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleRes;
import com.prgms.allen.dining.domain.reservation.dto.VisitorCountPerVisitTimeProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private static final List<ReservationStatus> BEFORE_VISIT_STATUSES =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

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

	public ReservationAvailableTimesRes getAvailableTimes(Long restaurantId, LocalDate requestDate, int visitorCount) {
		Restaurant restaurant = restaurantService.findById(restaurantId);

		List<VisitorCountPerVisitTimeProj> visitorCountPerVisitTime = reservationRepository.findVisitorCountPerVisitTime(
			requestDate, BEFORE_VISIT_STATUSES);

		List<LocalTime> availableTimes = visitorCountPerVisitTime.stream()
			.filter(countPerTime ->
				restaurant.isAvailable(
					countPerTime.totalVisitorCount().intValue(),
					visitorCount
				)
			)
			.map(VisitorCountPerVisitTimeProj::visitTime)
			.toList();

		return new ReservationAvailableTimesRes(availableTimes);
	}

	public boolean isAvailableReserve(
		Restaurant restaurant,
		LocalDateTime requestTime,
		int numberOfPeople
	) {

		Optional<Integer> totalCount = reservationRepository.countTotalVisitorCount(restaurant,
			requestTime.toLocalDate(),
			requestTime.toLocalTime(),
			BEFORE_VISIT_STATUSES);

		return restaurant.isAvailable(totalCount.get(), numberOfPeople);
	}
}
