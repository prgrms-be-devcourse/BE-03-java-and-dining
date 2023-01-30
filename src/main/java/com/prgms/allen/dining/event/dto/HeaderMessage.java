package com.prgms.allen.dining.event.dto;

public enum HeaderMessage {
	RESERVATION_ACCEPTED("예약이 접수되었습니다"),
	RESERVATION_CONFIRMED("예약이 확정되었습니다"),
	RESERVATION_CANCELED("예약이 취소되었습니다"),
	RESERVATION_REMINDER("예약 1시간 전입니다");

	private final String message;

	HeaderMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
