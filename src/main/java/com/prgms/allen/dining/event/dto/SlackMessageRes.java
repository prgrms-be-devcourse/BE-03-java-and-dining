package com.prgms.allen.dining.event.dto;

import java.time.LocalDateTime;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public record SlackMessageRes(
	String customerName,
	String customerPhone,
	int visitorCount,
	LocalDateTime visitDateTime,
	String restaurantName,
	HeaderMessage headerMessage
) {
	public SlackMessageRes(Reservation reservation, HeaderMessage headerMessage) {
		this(
			reservation.getCustomerName(),
			reservation.getCustomerPhone(),
			reservation.getVisitorCount(),
			reservation.getVisitDateTime(),
			reservation.getRestaurantName(),
			headerMessage
		);
	}
}
