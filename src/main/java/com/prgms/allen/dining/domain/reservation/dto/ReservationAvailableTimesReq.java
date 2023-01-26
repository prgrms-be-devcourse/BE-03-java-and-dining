package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

public record ReservationAvailableTimesReq(

	@NotNull
	Long restaurantId,

	@NotNull
	@Future
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate date,

	@Range(min = 2, max = 8)
	Integer visitorCount
) {
	@Override
	public String toString() {
		return "ReservationAvailableTimesReq{" +
			"restaurantId=" + restaurantId +
			", date=" + date +
			", visitorCount=" + visitorCount +
			'}';
	}
}
