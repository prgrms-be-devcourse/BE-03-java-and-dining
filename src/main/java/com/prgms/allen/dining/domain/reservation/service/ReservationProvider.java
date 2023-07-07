package com.prgms.allen.dining.domain.reservation.service;

import java.time.LocalTime;
import java.util.List;

import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.dto.ReservationAvailableDatesRes;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;

public interface ReservationProvider {

	List<ReservationStatus> BEFORE_VISIT_STATUSES =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

	List<LocalTime> getAvailableTimes(RestaurantOperationInfo restaurant);

	ReservationAvailableDatesRes getAvailableDates(Long restaurantId);
}
