package com.prgms.allen.dining.domain.reservation.dto;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public record ReservationDetailRes(
	ReservationInfoRes reservationInfoRes,
	RestaurantInfoRes restaurantInfoRes
) {

	public ReservationDetailRes(Reservation reservation) {
		this(
			new ReservationInfoRes(reservation.getCustomer(), reservation.getCustomerInput()),
			new RestaurantInfoRes(reservation.getRestaurant())
		);
	}
}
