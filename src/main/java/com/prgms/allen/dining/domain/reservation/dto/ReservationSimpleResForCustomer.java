package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantInfo;

public record ReservationSimpleResForCustomer(
	String restaurantName,
	String address,
	LocalDateTime visitDateTime,
	int visitorCount
) {

	public static ReservationSimpleResForCustomer from(Reservation reservation, RestaurantInfo restaurant) {
		return new ReservationSimpleResForCustomer(
			restaurant.getName(),
			restaurant.getLocation(),
			reservation.getVisitDateTime(),
			reservation.getVisitorCount()
		);
	}
}
