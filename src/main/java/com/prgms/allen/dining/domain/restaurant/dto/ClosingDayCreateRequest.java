package com.prgms.allen.dining.domain.restaurant.dto;

import java.time.DayOfWeek;

import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;

public record ClosingDayCreateRequest(DayOfWeek dayOfWeek) {
	public ClosingDay toClosingDay() {
		return new ClosingDay(
			this.dayOfWeek
		);
	}
}
