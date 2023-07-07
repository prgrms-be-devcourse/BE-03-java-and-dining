package com.prgms.allen.dining.domain.reservation.service;

import java.util.List;

import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesRes;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.dto.ReservationAvailableDatesRes;

public interface ReservationService {

	List<ReservationStatus> BEFORE_VISIT_STATUSES =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

	ReservationAvailableTimesRes getAvailableTimes(ReservationAvailableTimesReq availableTimesReq);

	ReservationAvailableDatesRes getAvailableDates(Long restaurantId);
}
