package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public record ReservationInfoResForOwner(
	LocalDateTime visitDateTime,
	int visitorCount,
	String memo
) {
	public ReservationInfoResForOwner(Reservation reservation) {
		this(
			reservation.getVisitDateTime(),
			reservation.getVisitorCount(),
			reservation.getMemo()
		);
	}
}
