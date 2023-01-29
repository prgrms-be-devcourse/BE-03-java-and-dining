package com.prgms.allen.dining.domain.reservation.entity;

import java.util.List;

public enum VisitStatus {

	PLANNED(List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)),
	DONE(List.of(ReservationStatus.VISITED)),
	CANCEL(List.of(ReservationStatus.CANCELLED, ReservationStatus.NO_SHOW));

	private final List<ReservationStatus> statuses;

	VisitStatus(List<ReservationStatus> statuses) {
		this.statuses = statuses;
	}

	public List<ReservationStatus> getStatuses() {
		return statuses;
	}
}
