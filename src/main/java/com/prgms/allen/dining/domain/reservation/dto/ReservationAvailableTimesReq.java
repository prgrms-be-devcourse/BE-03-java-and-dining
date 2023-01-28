package com.prgms.allen.dining.domain.reservation.dto;

import static com.prgms.allen.dining.domain.reservation.policy.ReservationPolicy.*;

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

	@Range(min = MIN_VISITOR_COUNT, max = MAX_VISITOR_COUNT)
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
