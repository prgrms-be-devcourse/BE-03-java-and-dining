package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalTime;

public record VisitorCountPerVisitTimeProj(
	LocalTime visitTime,
	Long totalVisitorCount
) {
}
