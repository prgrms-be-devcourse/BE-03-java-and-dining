package com.prgms.allen.dining.domain.notification.slack.dto;

import java.time.LocalDateTime;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantInfo;

public record SlackNotificationMessageRes(
	String customerName,
	String customerPhone,
	int visitorCount,
	LocalDateTime visitDateTime,
	String restaurantName,
	HeaderMessage headerMessage
) {

	public SlackNotificationMessageRes(Reservation reservation, HeaderMessage headerMessage,
		RestaurantInfo restaurant) {
		this(
			reservation.getCustomerName(),
			reservation.getCustomerPhone(),
			reservation.getVisitorCount(),
			reservation.getVisitDateTime(),
			restaurant.getName(),
			headerMessage
		);
	}
}
