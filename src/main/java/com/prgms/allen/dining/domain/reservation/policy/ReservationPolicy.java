package com.prgms.allen.dining.domain.reservation.policy;

public final class ReservationPolicy {

	public static final long UNIT_SECONDS = 3600L;
	public static final long MAX_RESERVE_PERIOD = 30L;
	public static final int MIN_VISITOR_COUNT = 2;
	public static final int MAX_VISITOR_COUNT = 8;

	private ReservationPolicy() {
	}
}
