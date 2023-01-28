package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

public record CustomerInfoRes(
	String name,
	String phone,
	long visitedCount,
	long noShowCount,
	LocalDateTime lastVisitedDateTime
) {
	public static final String DEFAULT_DATE_TIME = LocalDateTime.MIN.toString();

	public CustomerInfoRes(CustomerReservationInfoProj customerReservationInfo) {
		this(
			customerReservationInfo.getName(),
			customerReservationInfo.getPhone(),
			customerReservationInfo.getVisitedCount(),
			customerReservationInfo.getNoShowCount(),
			LocalDateTime.parse(
				customerReservationInfo.getLastVisitedDateTime()
					.orElse(DEFAULT_DATE_TIME)
			)
		);
	}
}
