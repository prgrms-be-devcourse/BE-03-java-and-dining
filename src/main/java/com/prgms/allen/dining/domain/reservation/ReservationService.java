package com.prgms.allen.dining.domain.reservation;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateRes;
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
	public ReservationCreateRes reserve(Long customerId, ReservationCreateReq createRequest) {
		Member customer = memberService.findCustomerById(customerId);
		Restaurant restaurant = restaurantService.findById(createRequest.restaurantId());

		ReservationCustomerInput reservationCustomerInput = createRequest
			.reservationCustomerInput()
			.toEntity();

		boolean isReserved = isAvailableReserve(
			restaurant,
			reservationCustomerInput.getVisitDateTime(),
			reservationCustomerInput.getVisitorCount()
		);

		Reservation newReservation = new Reservation(
			customer,
			restaurant,
			reservationCustomerInput
		);

		reservationRepository.save(newReservation);

		return new ReservationCreateRes(
			isReserved,
			newReservation.getId()
		);
	}

	private boolean isAvailableReserve(
		Restaurant restaurant,
		LocalDateTime requestTime,
		int visitorCount
	) {

		Integer totalVisitorCount = reservationRepository.countTotalVisitorCount(restaurant,
			requestTime.toLocalDate(),
			requestTime.toLocalTime(),
			BEFORE_VISIT_STATUSES
		).orElse(0);

		return restaurant.isAvailable(totalVisitorCount, visitorCount);
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

		Map<LocalTime, Long> visitorCountPerTimeMap = reservationRepository.findVisitorCountPerVisitTime(
				requestDate,
				BEFORE_VISIT_STATUSES
			)
			.stream()
			.collect(toMap(
					VisitorCountPerVisitTimeProj::visitTime,
					VisitorCountPerVisitTimeProj::totalVisitorCount
				)
			);

		List<LocalTime> availableTimes = generateTimeTable(restaurant)
			.filter(time -> {
					Long totalVisitorCount = visitorCountPerTimeMap.getOrDefault(time, 0L);
					return restaurant.isAvailable(
						totalVisitorCount.intValue(),
						visitorCount
					);
				}
			)
			.toList();

		return new ReservationAvailableTimesRes(availableTimes);
	}

	private Stream<LocalTime> generateTimeTable(Restaurant restaurant) {
		return Stream.iterate(
			restaurant.getOpenTime(),
			time -> time.plusHours(1L)
				.truncatedTo(ChronoUnit.MINUTES)
		).limit(restaurant.getRunningTime());
	}
}
