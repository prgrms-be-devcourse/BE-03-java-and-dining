package com.prgms.allen.dining.domain.reservation;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.web.domain.owner.reservation.ReservationStatusUpdateReq;

@Service
@Transactional
public class ReservationStatusUpdateService {

	private static final Logger log = LoggerFactory.getLogger(ReservationStatusUpdateService.class);

	private final ReservationService reservationService;

	public ReservationStatusUpdateService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	public void update(Long reservationId, Long ownerId, ReservationStatusUpdateReq updateReq) {
		switch (updateReq.status()) {
			case "confirm" -> confirm(reservationId, ownerId);
			case "cancel" -> cancel(reservationId, ownerId);
			case "visit" -> visit(reservationId, ownerId);
			case "no-show" -> noShow(reservationId, ownerId);
			default -> throw new IllegalArgumentException(MessageFormat.format(
				"Cannot update reservation status for status={0}. Check your Payload.",
				updateReq.status()
			));
		}
	}

	private void confirm(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.confirm(ownerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}

	private void cancel(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.cancel(ownerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}

	private void visit(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.visit(ownerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}

	private void noShow(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.noShow(ownerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}
}
