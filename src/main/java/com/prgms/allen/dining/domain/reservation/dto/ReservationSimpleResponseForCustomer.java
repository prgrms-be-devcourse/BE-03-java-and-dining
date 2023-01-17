package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public record ReservationSimpleResponseForCustomer(
	String restaurantName,
	String address,
	LocalDateTime visitDateTime,
	int visitorCount
) {

	public ReservationSimpleResponseForCustomer(Reservation reservation) {
		this(
			reservation.getRestaurantName(),
			reservation.getRestaurantAddress(),
			reservation.getVisitDateTime(),
			reservation.getVisitorCount()
		);
	}
}
