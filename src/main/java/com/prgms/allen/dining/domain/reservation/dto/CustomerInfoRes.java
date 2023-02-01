package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

public record CustomerInfoRes(
	String name,
	String phone,
	long visitedCount,
	long noShowCount,
	LocalDateTime lastVisitedDateTime
) {
	public CustomerInfoRes(CustomerReservationInfoProj customerReservationInfo) {
		this(
			customerReservationInfo.getName(),
			customerReservationInfo.getPhone(),
			customerReservationInfo.getVisitedCount(),
			customerReservationInfo.getNoShowCount(),
			getLastVisitedDateTime(customerReservationInfo)
		);
	}

	private static LocalDateTime getLastVisitedDateTime(CustomerReservationInfoProj customerReservationInfo) {
		return customerReservationInfo.getLastVisitedDateTime()
			.map(LocalDateTime::parse)
			.orElse(null);
	}
}
