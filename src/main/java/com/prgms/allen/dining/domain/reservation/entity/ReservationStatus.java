package com.prgms.allen.dining.domain.reservation.entity;

public enum ReservationStatus {

	PENDING("예약 대기"),
	CONFIRMED("예약 확정"),
	VISIT("방문 완료"),
	CANCEL("취소"),
	NO_SHOW("노쇼");

	private final String value;

	ReservationStatus(String value) {
		this.value = value;
	}
}
