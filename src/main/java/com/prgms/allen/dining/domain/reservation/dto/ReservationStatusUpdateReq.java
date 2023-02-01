package com.prgms.allen.dining.domain.reservation.dto;

import javax.validation.constraints.NotNull;

import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;

public record ReservationStatusUpdateReq(

	@NotNull
	ReservationStatus status
) {
}
