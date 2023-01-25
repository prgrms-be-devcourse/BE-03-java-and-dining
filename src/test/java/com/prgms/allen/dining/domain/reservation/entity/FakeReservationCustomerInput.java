package com.prgms.allen.dining.domain.reservation.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeReservationCustomerInput extends ReservationCustomerInput {

	private static final Logger log = LoggerFactory.getLogger(FakeReservationCustomerInput.class);

	public FakeReservationCustomerInput(LocalDate visitDate, LocalTime visitTime, int visitorCount) {
		super(visitDate, visitTime, visitorCount);
	}

	@Override
	protected void validateVisitBoundary(LocalDateTime visitDateTime) {
		log.info("FakeReservationCustomerInput.validateVisitBoundary: No validation for visitDateTime.");
	}
}
