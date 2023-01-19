package com.prgms.allen.dining.domain.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

@Service
public class ReservationStatusUpdateService {

	private static final Logger log = LoggerFactory.getLogger(ReservationStatusUpdateService.class);

	private final ReservationService reservationService;

	public ReservationStatusUpdateService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@Transactional
	public void confirmReservation(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.confirm(ownerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}
}
