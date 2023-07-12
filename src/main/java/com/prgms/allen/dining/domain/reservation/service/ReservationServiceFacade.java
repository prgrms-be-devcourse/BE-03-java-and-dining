package com.prgms.allen.dining.domain.reservation.service;

import org.springframework.stereotype.Component;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.domain.schedule.service.ScheduleServiceFacade;

@Component
public class ReservationServiceFacade {

	private final ReservationService reservationService;
	private final RestaurantService restaurantService;
	private final MemberService memberService;
	private final SlackNotifyService slackNotifyService;
	private final ScheduleServiceFacade scheduleServiceFacade;

	public ReservationServiceFacade(ReservationService reservationService, RestaurantService restaurantService,
		MemberService memberService, SlackNotifyService slackNotifyService,
		ScheduleServiceFacade scheduleServiceFacade) {
		this.reservationService = reservationService;
		this.restaurantService = restaurantService;
		this.memberService = memberService;
		this.slackNotifyService = slackNotifyService;
		this.scheduleServiceFacade = scheduleServiceFacade;
	}

	public Long reserve(Long customerId, ReservationCreateReq createRequest){
		final Member customer = memberService.findCustomerById(customerId);
		final Restaurant restaurant = restaurantService.findById(createRequest.restaurantId());

		scheduleServiceFacade.fix(
			createRequest.reservationCustomerInput().visitDateTime(),
			restaurant,
			createRequest.reservationCustomerInput().visitorCount()
		);

		final Reservation newReservation = reservationService.reserve(customer, restaurant, createRequest);

		slackNotifyService.notifyReserve(newReservation);

		return newReservation.getId();
	}
}
