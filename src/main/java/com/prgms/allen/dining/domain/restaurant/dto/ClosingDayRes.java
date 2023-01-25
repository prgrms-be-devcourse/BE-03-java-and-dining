package com.prgms.allen.dining.domain.restaurant.dto;

import java.time.DayOfWeek;

import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;

public record ClosingDayRes(
	DayOfWeek dayOfWeek
) {
	public ClosingDayRes(ClosingDay closingDay) {
		this(closingDay.getDayOfWeek());
	}
}
