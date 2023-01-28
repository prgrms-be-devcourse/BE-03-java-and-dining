package com.prgms.allen.dining.domain.reservation.dto;

import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;

public record VisitedAndNoShowReservationCountProj(
	ReservationStatus status,
	int count
) {
}
