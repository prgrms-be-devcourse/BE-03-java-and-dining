package com.prgms.allen.dining.domain.reservation.dto;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantInfo;

public record ReservationDetailResForCustomer(
	ReservationInfoResForCustomer reservationInfoResForCustomer,
	String name,
	String location,
	String phone
) {

	public ReservationDetailResForCustomer(Reservation reservation, RestaurantInfo restaurantInfo) {
		this(
			new ReservationInfoResForCustomer(reservation.getCustomer(), reservation.getCustomerInput()),
			restaurantInfo.getName(),
			restaurantInfo.getLocation(),
			restaurantInfo.getPhone()
		);
	}
}
