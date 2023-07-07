package com.prgms.allen.dining.domain.reservation.service;

import static com.prgms.allen.dining.domain.reservation.policy.ReservationPolicy.*;
import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.dto.DateAndTotalVisitCountPerDayProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.dto.ReservationAvailableDatesRes;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Service
@Transactional(readOnly = true)
public class ReservationInfoService implements ReservationProvider {

	private static final int BOOKING_COUNT = 2;

	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;

	public ReservationInfoService(ReservationRepository reservationRepository,
		RestaurantRepository restaurantRepository) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository;
	}

	@Override
	public List<LocalTime> getAvailableTimes(RestaurantOperationInfo restaurant) {
		Map<LocalTime, Long> bookingCounts = reservationRepository.findBookingCounts(
				restaurant.getId(),
				LocalDate.now(),
				BEFORE_VISIT_STATUSES
			)
			.stream()
			.collect(
				groupingBy(
					Reservation::getVisitTime,
					summingLong(Reservation::getVisitorCount))
			);

		return restaurant.getAvailableTimes(BOOKING_COUNT, bookingCounts);
	}

	@Override
	public ReservationAvailableDatesRes getAvailableDates(Long restaurantId) {
		Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();

		List<LocalDate> notAvailableDates = getReserveNotAvailableDates(restaurant);

		List<LocalDate> canReserveDates = getOpenDays(restaurant);

		return new ReservationAvailableDatesRes(
			filterReserveNotAvailableDates(notAvailableDates, canReserveDates));
	}

	private List<LocalDate> filterReserveNotAvailableDates(List<LocalDate> notAvailableDates,
		List<LocalDate> canReserveDates) {
		return canReserveDates.stream()
			.filter(localDate -> !notAvailableDates.contains(localDate))
			.toList();
	}

	private List<LocalDate> getOpenDays(Restaurant restaurant) {
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(MAX_RESERVE_PERIOD);

		return start.datesUntil(end)
			.filter(localDate -> !restaurant.isClosingDay(localDate))
			.toList();
	}

	private List<LocalDate> getReserveNotAvailableDates(Restaurant restaurant) {
		return reservationRepository.findTotalVisitorCountPerDay(restaurant, BEFORE_VISIT_STATUSES)
			.stream()
			.filter(proj -> restaurant.isNotReserveAvailableForDay(proj.count()))
			.map(DateAndTotalVisitCountPerDayProj::date)
			.toList();
	}
}
