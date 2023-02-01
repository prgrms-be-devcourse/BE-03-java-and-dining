package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDate;

public record DateAndTotalVisitCountPerDayProj(
	LocalDate date,
	long count
) {
}
