package com.prgms.allen.dining.domain.reservation.service;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationStatusUpdateReq;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.restaurant.RestaurantProvider;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantInfo;

@Service
@Transactional
@Qualifier("ownerReservationStatusUpdateService")
public class OwnerReservationStatusUpdateService implements ReservationStatusUpdateService {

	private static final Logger log = LoggerFactory.getLogger(OwnerReservationStatusUpdateService.class);

	private final ReservationReserveService reservationService;
	private final SlackNotifyService slackNotifyService;
	private final MemberService memberService;
	private final RestaurantProvider restaurantProvider;

	public OwnerReservationStatusUpdateService(
		ReservationReserveService reservationService,
		SlackNotifyService slackNotifyService,
		MemberService memberService, RestaurantProvider restaurantProvider) {
		this.reservationService = reservationService;
		this.slackNotifyService = slackNotifyService;
		this.memberService = memberService;
		this.restaurantProvider = restaurantProvider;
	}

	@Override
	public void update(Long reservationId, Long ownerId, ReservationStatusUpdateReq updateReq) {
		Reservation findReservation = reservationService.findById(reservationId);
		RestaurantInfo restaurant = restaurantProvider.getInfoById(findReservation.getRestaurantId());

		switch (updateReq.status()) {
			case CONFIRMED -> confirm(findReservation, ownerId, restaurant);
			case CANCELLED -> cancel(findReservation, ownerId, restaurant);
			case VISITED -> visit(findReservation, ownerId);
			case NO_SHOW -> noShow(findReservation, ownerId);
			default -> throw new IllegalArgumentException(MessageFormat.format(
				"Cannot update reservation status for status={0}. Check your Payload.",
				updateReq.status()
			));
		}
	}

	private void confirm(Reservation findReservation, Long ownerId, RestaurantInfo restaurant) {
		checkOwner(ownerId);

		findReservation.confirm();

		slackNotifyService.notifyConfirm(findReservation, restaurant);
		log.info("Reservation {}'s status updated to {}", findReservation.getId(), findReservation.getStatus());
	}

	private void cancel(Reservation findReservation, Long ownerId, RestaurantInfo restaurant) {
		checkOwner(ownerId);

		findReservation.cancel();

		slackNotifyService.notifyCancel(findReservation, restaurant);
		log.info("Reservation {}'s status updated to {}", findReservation.getId(), findReservation.getStatus());
	}

	private void visit(Reservation findReservation, Long ownerId) {
		checkOwner(ownerId);

		findReservation.visit();
		log.info("Reservation {}'s status updated to {}", findReservation.getId(), findReservation.getStatus());
	}

	private void noShow(Reservation findReservation, Long ownerId) {
		checkOwner(ownerId);

		findReservation.noShow();
		log.info("Reservation {}'s status updated to {}", findReservation.getId(), findReservation.getStatus());
	}

	private void checkOwner(Long ownerId) {
		Member owner = memberService.findOwnerById(ownerId);

		Assert.state(
			owner.matchesId(ownerId),
			MessageFormat.format(
				"Owner does not match. Parameter ownerId={0} but actual ownerId={1}",
				ownerId,
				owner.getId()
			)
		);
	}
}
