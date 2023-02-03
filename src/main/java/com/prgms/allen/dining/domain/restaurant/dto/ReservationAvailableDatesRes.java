package com.prgms.allen.dining.domain.restaurant.dto;

import java.time.LocalDate;
import java.util.List;

public record ReservationAvailableDatesRes(List<LocalDate> availableDates) {
}
