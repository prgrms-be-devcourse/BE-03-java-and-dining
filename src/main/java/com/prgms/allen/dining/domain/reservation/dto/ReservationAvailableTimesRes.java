package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalTime;
import java.util.List;

public record ReservationAvailableTimesRes(
	List<LocalTime> availableTimes
) {
}
