package com.prgms.allen.dining.domain.reservation.service;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationStatusUpdateReq;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;

@Service
@Transactional
@Qualifier("ownerReservationStatusUpdateService")
public class OwnerReservationStatusUpdateService implements ReservationStatusUpdateService {

	private static final Logger log = LoggerFactory.getLogger(OwnerReservationStatusUpdateService.class);

	private final ReservationReserveService reservationService;
	private final SlackNotifyService slackNotifyService;

	public OwnerReservationStatusUpdateService(
		ReservationReserveService reservationService,
		SlackNotifyService slackNotifyService
	) {
		this.reservationService = reservationService;
		this.slackNotifyService = slackNotifyService;
	}

	@Override
	public void update(Long reservationId, Long ownerId, ReservationStatusUpdateReq updateReq) {
		switch (updateReq.status()) {
			case CONFIRMED -> confirm(reservationId, ownerId);
			case CANCELLED -> cancel(reservationId, ownerId);
			case VISITED -> visit(reservationId, ownerId);
			case NO_SHOW -> noShow(reservationId, ownerId);
			default -> throw new IllegalArgumentException(MessageFormat.format(
				"Cannot update reservation status for status={0}. Check your Payload.",
				updateReq.status()
			));
		}
	}

	private void confirm(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.confirm(ownerId);
		slackNotifyService.notifyConfirm(findReservation);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}

	private void cancel(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.cancel(MemberType.OWNER, ownerId);
		slackNotifyService.notifyCancel(findReservation);
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
