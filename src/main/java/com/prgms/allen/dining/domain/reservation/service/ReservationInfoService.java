package com.prgms.allen.dining.domain.reservation.service;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;
import com.prgms.allen.dining.domain.restaurant.provider.RestaurantProvider;

@Service
@Transactional(readOnly = true)
public class ReservationInfoService implements ReservationProvider {

	private static final int BOOKING_COUNT = 2;

	private final ReservationRepository reservationRepository;
	private final RestaurantProvider restaurantProvider;

	public ReservationInfoService(ReservationRepository reservationRepository, RestaurantProvider restaurantProvider) {
		this.reservationRepository = reservationRepository;
		this.restaurantProvider = restaurantProvider;
	}

	@Override
	public List<LocalTime> getAvailableTimes(RestaurantOperationInfo restaurant) {
		Map<LocalTime, Long> bookingCounts = reservationRepository.findAllByDateAndStatus(
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
	public List<LocalDate> getAvailableDates(Long restaurantId) {
		RestaurantOperationInfo restaurant = restaurantProvider.findById(restaurantId);

		Map<LocalDate, Long> bookingCounts = reservationRepository.findAllByRestaurantIdAndStatus(restaurant.getId(),
				BEFORE_VISIT_STATUSES)
			.stream()
			.collect(
				groupingBy(
					Reservation::getVisitDate,
					summingLong(Reservation::getVisitorCount))
			);

		return restaurant.getAvailableDates(BOOKING_COUNT, bookingCounts);
	}
}
