package com.prgms.allen.dining.domain.reservation.dto;

public record ReservationCreateRes(
	boolean isReserved,
	Long reservationId
) {
}
