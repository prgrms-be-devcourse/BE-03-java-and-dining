package com.prgms.allen.dining.domain.reservation.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.common.NotFoundResourceException;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.BookingScheduleService;
import com.prgms.allen.dining.domain.reservation.ReserveFailException;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.entity.BookingSchedule;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantProvider;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantInfo;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;

@Service
@Transactional(readOnly = true)
public class ReservationReserveService {

	private static final List<ReservationStatus> BEFORE_VISIT_STATUSES =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

	private final Logger logger = LoggerFactory.getLogger(ReservationReserveService.class);

	private final ReservationRepository reservationRepository;
	private final RestaurantProvider restaurantProvider;
	private final MemberService memberService;
	private final BookingScheduleService bookingScheduleService;
	private final SlackNotifyService slackNotifyService;

	public ReservationReserveService(
		ReservationRepository reservationRepository,
		RestaurantProvider restaurantProvider,
		MemberService memberService, BookingScheduleService bookingScheduleService,
		SlackNotifyService slackNotifyService) {
		this.reservationRepository = reservationRepository;
		this.restaurantProvider = restaurantProvider;
		this.memberService = memberService;
		this.bookingScheduleService = bookingScheduleService;
		this.slackNotifyService = slackNotifyService;
	}

	@Transactional
	public Long reserve(Long customerId, ReservationCreateReq createRequest) {
		Member customer = memberService.findCustomerForReserve(customerId);
		RestaurantOperationInfo restaurantOperationInfo = restaurantProvider.findById(createRequest.restaurantId());

		ReservationCustomerInput customerInput = createRequest
			.reservationCustomerInput()
			.toEntity();

		LocalDateTime bookingDateTime = customerInput.getVisitDateTime();
		Long restaurantId = restaurantOperationInfo.getId();
		int capacity = restaurantOperationInfo.getCapacity();

		BookingSchedule schedule = bookingScheduleService.findSchedule(restaurantId, bookingDateTime, capacity);

		checkAvailableReservation(restaurantOperationInfo, schedule, customerInput);

		int visitorCount = customerInput.getVisitorCount();
		bookingScheduleService.booking(schedule, visitorCount);

		Reservation newReservation = new Reservation(customer, restaurantId, customerInput);
		reservationRepository.save(newReservation);

		logger.warn("예약자명 : {}", customer.getNickname());

		sendSlackNotify(restaurantOperationInfo);

		return newReservation.getId();
	}

	private void sendSlackNotify(RestaurantOperationInfo restaurantOperationInfo) {
		RestaurantInfo restaurantInfo = restaurantProvider.getInfoById(restaurantOperationInfo.getId());

		// slackNotifyService.notifyReserve(newReservation, restaurantInfo);
	}

	private void checkAvailableReservation(RestaurantOperationInfo restaurant, BookingSchedule schedule,
		ReservationCustomerInput customerInput) {
		LocalDateTime visitDateTime = customerInput.getVisitDateTime();
		int visitorCount = customerInput.getVisitorCount();

		// 레스토랑이 운영할 때인지
		boolean isOperationTime = restaurant.isAvailable(visitDateTime);

		// 해당 날짜와 시간에 신청한 인원이 예약 가능한지
		boolean isAvailableBooking = schedule.isLowerThanRemainCounts(visitorCount);

		boolean isAvailableReserve = isOperationTime && isAvailableBooking;

		if (isAvailableReserve) {
			return;
		}

		logger.warn("isOperationTime: {}, isAvailableBooking : {}", isOperationTime, isAvailableBooking);

		String errorMessage = String.format(
			"Reservation for restaurant ID %d failed. isOperationTime: %b, isAvailableBooking : %b",
			restaurant.getId(), isOperationTime, isAvailableBooking);

		throw new ReserveFailException(errorMessage);
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
