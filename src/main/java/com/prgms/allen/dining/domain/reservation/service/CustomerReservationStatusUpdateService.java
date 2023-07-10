package com.prgms.allen.dining.domain.reservation.service;

import static com.prgms.allen.dining.domain.reservation.entity.ReservationStatus.*;

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
@Qualifier("customerReservationStatusUpdateService")
public class CustomerReservationStatusUpdateService implements ReservationStatusUpdateService {

	private static final Logger log = LoggerFactory.getLogger(CustomerReservationStatusUpdateService.class);

	private final ReservationReserveService reservationService;
	private final SlackNotifyService slackNotifyService;
	private final MemberService memberService;
	private final RestaurantProvider restaurantProvider;

	public CustomerReservationStatusUpdateService(
		ReservationReserveService reservationService,
		SlackNotifyService slackNotifyService,
		MemberService memberService,
		RestaurantProvider restaurantProvider
	) {
		this.reservationService = reservationService;
		this.slackNotifyService = slackNotifyService;
		this.memberService = memberService;
		this.restaurantProvider = restaurantProvider;
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
		Member customer = memberService.findCustomerById(customerId);

		RestaurantInfo restaurant = restaurantProvider.getInfoById(findReservation.getRestaurantId());

		Assert.state(
			customer.matchesId(customerId),
			MessageFormat.format(
				"Customer does not match. Parameter customerId={0} but actual customerId={1}",
				customerId,
				customer.getId()
			)
		);

		findReservation.cancel();
		slackNotifyService.notifyCancel(findReservation, restaurant);
		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}
}
