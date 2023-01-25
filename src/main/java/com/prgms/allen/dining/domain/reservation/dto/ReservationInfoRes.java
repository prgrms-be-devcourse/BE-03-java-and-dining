package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;

public record ReservationInfoRes(
	String customerName,
	String phone,
	LocalDateTime visitDateTime,
	int visitorCount,
	String memo
) {
	public ReservationInfoRes(Member customer, ReservationCustomerInput customerInput) {
		this(
			customer.getName(),
			customer.getPhone(),
			customerInput.getVisitDateTime(),
			customerInput.getVisitorCount(),
			customerInput.getCustomerMemo()
		);
	}
}
