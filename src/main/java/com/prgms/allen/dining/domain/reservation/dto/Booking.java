package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalTime;

public record Booking(
	LocalTime visitTime,
	Long totalVisitorCount
) {
}
