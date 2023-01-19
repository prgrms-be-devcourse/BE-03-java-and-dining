package com.prgms.allen.dining.domain.restaurant.entity;

import java.time.DayOfWeek;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class ClosingDay {

	@Enumerated(EnumType.STRING)
	@Column(name = "day_of_week", nullable = false)
	private DayOfWeek dayOfWeek;

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	protected ClosingDay() {

	}

	public ClosingDay(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ClosingDay that = (ClosingDay)o;
		return dayOfWeek == that.dayOfWeek;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dayOfWeek);
	}
}
