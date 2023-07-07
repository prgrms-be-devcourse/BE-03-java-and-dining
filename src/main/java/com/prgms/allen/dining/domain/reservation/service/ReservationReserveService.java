package com.prgms.allen.dining.domain.reservation.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.common.NotFoundResourceException;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.ReserveFailException;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantProvider;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Service
@Transactional(readOnly = true)
public class ReservationReserveService {

	private static final List<ReservationStatus> BEFORE_VISIT_STATUSES =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

	private final ReservationRepository reservationRepository;
	private final RestaurantProvider restaurantService;
	private final MemberService memberService;
	private final SlackNotifyService slackNotifyService;
	private final RestaurantRepository restaurantRepository;

	public ReservationReserveService(
		ReservationRepository reservationRepository,
		RestaurantProvider restaurantService,
		MemberService memberService,
		SlackNotifyService slackNotifyService,
		RestaurantRepository restaurantRepository) {
		this.reservationRepository = reservationRepository;
		this.restaurantService = restaurantService;
		this.memberService = memberService;
		this.slackNotifyService = slackNotifyService;
		this.restaurantRepository = restaurantRepository;
	}

	@Transactional
	public Long reserve(Long customerId, ReservationCreateReq createRequest) {
		Member customer = memberService.findCustomerById(customerId);
		Restaurant restaurant = restaurantRepository.findById(createRequest.restaurantId()).orElseThrow();

		ReservationCustomerInput customerInput = createRequest
			.reservationCustomerInput()
			.toEntity();
		checkAvailableReservation(restaurant, customerInput.getVisitDateTime(), customerInput.getVisitorCount());

		Reservation newReservation = new Reservation(customer, restaurant, customerInput);
		reservationRepository.save(newReservation);

		slackNotifyService.notifyReserve(newReservation);

		return newReservation.getId();
	}

	private void checkAvailableReservation(Restaurant restaurant, LocalDateTime visitDateTime, int visitorCount) {
		checkAvailableVisitDateTime(restaurant, visitDateTime);
		checkAvailableVisitorCount(restaurant, visitDateTime, visitorCount);
	}

	private void checkAvailableVisitDateTime(Restaurant restaurant, LocalDateTime visitDateTime) {
		boolean isAvailableVisitDateTime = restaurant.isAvailableVisitDateTime(visitDateTime);
		if (!isAvailableVisitDateTime) {
			throw new ReserveFailException(
				String.format(
					"Reservation for restaurant ID %d failed. "
						+ "Requested visit date time %s is not between %s and %s",
					restaurant.getId(),
					visitDateTime,
					restaurant.getOpenTime(),
					restaurant.getLastOrderTime()
				)
			);
		}
	}

	private void checkAvailableVisitorCount(Restaurant restaurant, LocalDateTime visitDateTime, int visitorCount) {
		int totalVisitorCount = reservationRepository.countTotalVisitorCount(restaurant,
			visitDateTime.toLocalDate(),
			visitDateTime.toLocalTime(),
			BEFORE_VISIT_STATUSES
		).orElse(0);

		boolean isAvailableVisitorCount = restaurant.isAvailableVisitorCount(totalVisitorCount, visitorCount);
		if (!isAvailableVisitorCount) {
			throw new ReserveFailException(
				String.format(
					"Reservation for restaurant ID %d on %s failed. "
						+ "Requested visitor count is %d, but maximum available visitor count is %d",
					restaurant.getId(),
					visitDateTime,
					visitorCount,
					restaurant.getCapacity() - totalVisitorCount
				)
			);
		}
	}

	public Reservation findById(Long id) {
		return reservationRepository.findById(id)
			.orElseThrow(() ->
				new NotFoundResourceException(MessageFormat.format(
					"Cannot find Reservation for reservationId={0}", id
				))
			);
	}

}
