package com.prgms.allen.dining.domain.restaurant.dto;

import java.time.DayOfWeek;

import javax.validation.constraints.NotNull;

import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;

public record ClosingDayCreateReq(

	@NotNull
	DayOfWeek dayOfWeek

) {
	public ClosingDay toClosingDay() {
		return new ClosingDay(
			this.dayOfWeek
		);
	}
}
