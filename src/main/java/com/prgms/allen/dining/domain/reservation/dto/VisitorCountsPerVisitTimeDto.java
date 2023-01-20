package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalTime;

public record VisitorCountsPerVisitTimeDto(
	LocalTime visitTime,
	long totalVisitorCount
) {
}
