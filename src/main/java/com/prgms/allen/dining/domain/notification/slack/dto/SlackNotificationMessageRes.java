package com.prgms.allen.dining.domain.notification.slack.dto;

import java.time.LocalDateTime;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public record SlackNotificationMessageRes(
	String customerName,
	String customerPhone,
	int visitorCount,
	LocalDateTime visitDateTime,
	String restaurantName,
	HeaderMessage headerMessage
) {

	public SlackNotificationMessageRes(Reservation reservation, HeaderMessage headerMessage) {
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
