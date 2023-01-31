package com.prgms.allen.dining.domain.reservation.dto;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public record ReservationDetailResForCustomer(
	ReservationInfoResForCustomer reservationInfoResForCustomer,
	RestaurantInfoRes restaurantInfoRes
) {

	public ReservationDetailResForCustomer(Reservation reservation) {
		this(
			new ReservationInfoResForCustomer(reservation.getCustomer(), reservation.getCustomerInput()),
			new RestaurantInfoRes(reservation.getRestaurant())
		);
	}
}
