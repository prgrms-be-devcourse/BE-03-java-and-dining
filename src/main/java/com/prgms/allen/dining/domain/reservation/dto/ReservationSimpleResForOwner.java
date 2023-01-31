package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public record ReservationSimpleResForOwner(
	String visitorName,
	String phone,
	LocalDateTime visitDateTime,
	int visitorCount
) {
	public ReservationSimpleResForOwner(Reservation reservation) {
		this(
			reservation.getCustomerName(),
			reservation.getCustomerPhone(),
			reservation.getVisitDateTime(),
			reservation.getVisitorCount()
		);
	}
}
