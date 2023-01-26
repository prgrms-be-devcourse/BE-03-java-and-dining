package com.prgms.allen.dining.domain.reservation.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReservationStatus {

	PENDING("확정 대기", "pending"),
	CONFIRMED("확정", "confirm"),
	VISITED("방문 완료", "visit"),
	CANCELLED("취소", "cancel"),
	NO_SHOW("노쇼", "no-show");

	private final String korean;
	private final String updateCommand;

	ReservationStatus(String korean, String updateCommand) {
		this.korean = korean;
		this.updateCommand = updateCommand;
	}

	@JsonValue
	public String getUpdateCommand() {
		return this.updateCommand;
	}
}
