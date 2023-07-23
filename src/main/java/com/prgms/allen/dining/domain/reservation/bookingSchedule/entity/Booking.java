package com.prgms.allen.dining.domain.reservation.bookingSchedule.entity;

import java.time.LocalTime;

public record Booking(
	LocalTime visitTime,
	Long totalVisitorCount
) {
}
