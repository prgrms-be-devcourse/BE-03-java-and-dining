package com.prgms.allen.dining.domain.fake;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public class FakeReservation extends Reservation {

	private Long id;

	public FakeReservation(Long id, Reservation reservation) {
		super(reservation.getCustomer(), reservation.getRestaurantId(), reservation.getCustomerInput());
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
