package com.prgms.allen.dining.domain.reservation.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record ReservationCreateRequest(

	@NotNull
	Long restaurantId,

	@Valid
	@NotNull
	ReservationCustomerInputCreateRequest reservationCustomerInput
) {
}
