package com.prgms.allen.dining.domain.reservation;

import org.springframework.stereotype.Service;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

@Service
public class ReservationStatusUpdateService {

	private final ReservationService reservationService;

	public ReservationStatusUpdateService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	public void confirmReservation(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.confirm(ownerId);
	}
}
