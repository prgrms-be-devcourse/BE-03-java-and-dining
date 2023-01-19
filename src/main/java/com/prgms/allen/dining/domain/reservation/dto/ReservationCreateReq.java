package com.prgms.allen.dining.domain.reservation.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record ReservationCreateReq(

	@NotNull
	Long restaurantId,

	@Valid
	@NotNull
	ReservationCustomerInputCreateReq reservationCustomerInput
) {
}
