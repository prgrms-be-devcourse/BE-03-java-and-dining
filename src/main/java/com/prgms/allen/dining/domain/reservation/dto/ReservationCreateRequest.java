package com.prgms.allen.dining.domain.reservation.dto;

import javax.validation.constraints.NotNull;

public record ReservationCreateRequest(

	@NotNull
	Long restaurantId,

	@NotNull
	Long customerId,

	@NotNull
	ReservationDetailRequest reservationDetail
) {
}
