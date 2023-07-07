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
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;

@Service
@Transactional(readOnly = true)
public class ReservationReserveService {

	private static final List<ReservationStatus> BEFORE_VISIT_STATUSES =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

	private final ReservationRepository reservationRepository;
	private final RestaurantProvider restaurantProvider;
	private final MemberService memberService;
	private final SlackNotifyService slackNotifyService;

	public ReservationReserveService(
		ReservationRepository reservationRepository,
		RestaurantProvider restaurantProvider,
		MemberService memberService,
		SlackNotifyService slackNotifyService) {
		this.reservationRepository = reservationRepository;
		this.restaurantProvider = restaurantProvider;
		this.memberService = memberService;
		this.slackNotifyService = slackNotifyService;
	}

	@Transactional
	public Long reserve(Long customerId, ReservationCreateReq createRequest) {
		Member customer = memberService.findCustomerById(customerId);
		RestaurantOperationInfo restaurant = restaurantProvider.findById(createRequest.restaurantId());

		ReservationCustomerInput customerInput = createRequest
			.reservationCustomerInput()
			.toEntity();

		checkAvailableReservation(restaurant, customerInput.getVisitDateTime(), customerInput.getVisitorCount());

		Reservation newReservation = new Reservation(customer, restaurant, customerInput);
		reservationRepository.save(newReservation);

		slackNotifyService.notifyReserve(newReservation);

		return newReservation.getId();
	}

	private void checkAvailableReservation(RestaurantOperationInfo restaurant, LocalDateTime visitDateTime,
		int visitorCount) {
		boolean isAvailableBook = restaurant.isAvailable(visitDateTime);

		int totalBookCount = reservationRepository.findReservationsByDateTime(
				restaurant.getId(),
				visitDateTime.toLocalDate(),
				visitDateTime.toLocalTime(),
				BEFORE_VISIT_STATUSES
			).stream()
			.mapToInt(Reservation::getVisitorCount)
			.sum();

		boolean isAvailableVisitCount = restaurant.isAvailable(totalBookCount, visitorCount);

		if (!isAvailableBook && !isAvailableVisitCount) {
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

	public Reservation findById(Long id) {
		return reservationRepository.findById(id)
			.orElseThrow(() ->
				new NotFoundResourceException(MessageFormat.format(
					"Cannot find Reservation for reservationId={0}", id
				))
			);
	}

}
