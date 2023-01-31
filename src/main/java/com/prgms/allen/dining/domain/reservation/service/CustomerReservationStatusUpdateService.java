package com.prgms.allen.dining.domain.reservation.service;

import static com.prgms.allen.dining.domain.reservation.entity.ReservationStatus.*;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.dto.ReservationStatusUpdateReq;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;

@Service
@Transactional
@Qualifier("customerReservationStatusUpdateService")
public class CustomerReservationStatusUpdateService implements ReservationStatusUpdateService {

	private static final Logger log = LoggerFactory.getLogger(CustomerReservationStatusUpdateService.class);

	private final ReservationService reservationService;

	public CustomerReservationStatusUpdateService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@Override
	public void update(Long reservationId, Long customerId, ReservationStatusUpdateReq statusUpdateReq) {
		if (statusUpdateReq.status() != CANCELLED) {
			throw new UnsupportedOperationException(MessageFormat.format(
				"Cannot {0} reservation. Not supported yet.", statusUpdateReq.status()
			));
		}

		cancel(reservationId, customerId);
	}

	private void cancel(Long reservationId, Long customerId) {
		Reservation findReservation = reservationService.findById(reservationId);
		findReservation.cancel(MemberType.CUSTOMER, customerId);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}
}
