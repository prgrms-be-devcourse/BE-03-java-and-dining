package com.prgms.allen.dining.global.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtils {

	private TimeUtils() {
	}

	public static LocalDateTime getCurrentSeoulDateTime() {
		return ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
			.toLocalDateTime();
	}
}
