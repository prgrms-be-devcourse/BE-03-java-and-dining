package com.prgms.allen.dining.domain.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

@Service
@Transactional
public class ReservationStatusUpdateService {

	private static final Logger log = LoggerFactory.getLogger(ReservationStatusUpdateService.class);

	private final ReservationService reservationService;

	public ReservationStatusUpdateService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	public void confirm(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.confirm(ownerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}

	public void cancel(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.cancel(ownerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}

	public void visit(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.visit(ownerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}

	public void noShow(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.noShow(ownerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}
}
