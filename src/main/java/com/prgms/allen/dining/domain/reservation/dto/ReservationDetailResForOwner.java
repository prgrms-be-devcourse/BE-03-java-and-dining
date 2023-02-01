package com.prgms.allen.dining.domain.reservation.dto;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public record ReservationDetailResForOwner(
	CustomerInfoRes customerInfoRes,
	ReservationInfoResForOwner reservationInfoResForOwner
) {
	public ReservationDetailResForOwner(CustomerReservationInfoProj customerReservationInfo, Reservation reservation) {
		this(
			new CustomerInfoRes(customerReservationInfo),
			new ReservationInfoResForOwner(reservation));
	}
}
