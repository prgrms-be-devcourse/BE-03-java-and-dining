package com.prgms.allen.dining.domain.reservation.entity;

public enum ReservationStatus {

	PENDING("확정 대기"),
	CONFIRMED("확정"),
	VISITED("방문 완료"),
	CANCELLED("취소"),
	NO_SHOW("노쇼");

	private final String value;

	ReservationStatus(String value) {
		this.value = value;
	}
}
