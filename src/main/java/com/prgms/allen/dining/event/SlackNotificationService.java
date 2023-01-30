package com.prgms.allen.dining.event;

import org.springframework.stereotype.Service;

import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.ReservationService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.event.dto.HeaderMessage;
import com.prgms.allen.dining.event.dto.SlackMessageRes;
import com.prgms.allen.dining.event.util.SlackUtils;

@Service
public class SlackNotificationService implements NotificationService {

	private final ReservationService reservationService;

	public SlackNotificationService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@Override
	public Long reserve(Long customerId, ReservationCreateReq createReq) {

		Long reservationId = reservationService.reserve(customerId, createReq);

		Reservation reservation = reservationService.findById(reservationId);

		SlackUtils.notify(
			new SlackMessageRes(reservation, HeaderMessage.RESERVATION_ACCEPTED),
			MemberType.OWNER
		);

		return reservationId;
	}
}
