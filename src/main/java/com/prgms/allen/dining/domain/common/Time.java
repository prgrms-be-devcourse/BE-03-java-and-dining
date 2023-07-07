package com.prgms.allen.dining.domain.common;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

public class Time {

	public static final List<LocalTime> TIME_TABLE = IntStream.range(0, 24)
		.boxed()
		.map(hour -> LocalTime.of(hour, 0))
		.toList();

	private Time() {
	}
}
