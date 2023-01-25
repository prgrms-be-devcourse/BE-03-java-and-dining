package com.prgms.allen.dining.web.domain.owner.reservation;

import javax.validation.constraints.NotBlank;

public record ReservationStatusUpdateReq(

	@NotBlank
	String status
) {
}
