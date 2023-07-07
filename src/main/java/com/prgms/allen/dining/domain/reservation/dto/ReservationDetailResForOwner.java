package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public record ReservationDetailResForOwner(
	CustomerInfoRes customerInfoRes,
	LocalDateTime visitDateTime,
	int visitorCount,
	String memo
) {
	public ReservationDetailResForOwner(CustomerReservationInfoProj customerReservationInfo, Reservation reservation) {
		this(
			new CustomerInfoRes(customerReservationInfo),
			reservation.getVisitDateTime(),
			reservation.getVisitorCount(),
			reservation.getMemo()
		);
	}
}
