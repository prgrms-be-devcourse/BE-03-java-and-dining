package com.prgms.allen.dining.domain.reservation.service;

import static com.prgms.allen.dining.domain.reservation.policy.ReservationPolicy.*;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.common.NotFoundResourceException;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.ReserveFailException;
import com.prgms.allen.dining.domain.reservation.dto.DateAndTotalVisitCountPerDayProj;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.VisitorCountPerVisitTimeProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.dto.ReservationAvailableDatesRes;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.domain.schedule.service.ScheduleServiceFacade;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private static final List<ReservationStatus> BEFORE_VISIT_STATUSES =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

	private final ReservationRepository reservationRepository;
	private final RestaurantService restaurantService;
	private final MemberService memberService;
	private final SlackNotifyService slackNotifyService;
	private final ScheduleServiceFacade scheduleServiceFacade;

	public ReservationService(
		ReservationRepository reservationRepository,
		RestaurantService restaurantService,
		MemberService memberService,
		SlackNotifyService slackNotifyService,
		ScheduleServiceFacade scheduleServiceFacade
	) {
		this.reservationRepository = reservationRepository;
		this.restaurantService = restaurantService;
		this.memberService = memberService;
		this.slackNotifyService = slackNotifyService;
		this.scheduleServiceFacade = scheduleServiceFacade;
	}

	@Transactional
	public Reservation reserve(Member customer, Restaurant restaurant, ReservationCreateReq createRequest) {

		ReservationCustomerInput customerInput = createRequest
			.reservationCustomerInput()
			.toEntity();

		return reservationRepository.save(new Reservation(customer, restaurant, customerInput));
	}

	//Todo: 지워
	@Transactional
	public Long reserve(Long customerId, ReservationCreateReq createRequest) {
		Member customer = memberService.findCustomerById(customerId);
		Restaurant restaurant = restaurantService.findById(createRequest.restaurantId());

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

	public ReservationAvailableTimesRes getAvailableTimes(ReservationAvailableTimesReq availableTimesReq) {
		Restaurant restaurant = restaurantService.findById(
			availableTimesReq.restaurantId()
		);

		Map<LocalTime, Long> visitorCountPerTimeMap = getVisitorCountPerTimeMap(availableTimesReq.date(), restaurant);

		List<LocalTime> availableTimes = getAvailableTimes(
			availableTimesReq.visitorCount(),
			restaurant,
			visitorCountPerTimeMap
		);
		return new ReservationAvailableTimesRes(availableTimes);
	}

	private Map<LocalTime, Long> getVisitorCountPerTimeMap(LocalDate visitDate, Restaurant restaurant) {
		return reservationRepository.findVisitorCountPerVisitTime(
				restaurant,
				visitDate,
				BEFORE_VISIT_STATUSES
			)
			.stream()
			.collect(Collectors.toMap(
					VisitorCountPerVisitTimeProj::visitTime,
					VisitorCountPerVisitTimeProj::totalVisitorCount
				)
			);
	}

	private List<LocalTime> getAvailableTimes(
		int visitorCount,
		Restaurant restaurant,
		Map<LocalTime, Long> visitorCountPerTimeMap
	) {
		return restaurant.generateTimeTable()
			.stream()
			.filter(availableVisitorCountPredicate(visitorCount, restaurant, visitorCountPerTimeMap))
			.toList();
	}

	private Predicate<LocalTime> availableVisitorCountPredicate(
		int visitorCount,
		Restaurant restaurant,
		Map<LocalTime, Long> visitorCountPerTimeMap
	) {
		return time -> {
			Long totalVisitorCount = visitorCountPerTimeMap.getOrDefault(time, 0L);
			return restaurant.isAvailableVisitorCount(
				totalVisitorCount.intValue(),
				visitorCount
			);
		};
	}

	public ReservationAvailableDatesRes getAvailableDates(Long restaurantId) {
		Restaurant restaurant = restaurantService.findById(restaurantId);

		List<LocalDate> notAvailableDates = getReserveNotAvailableDates(restaurant);

		List<LocalDate> canReserveDates = getOpenDays(restaurant);

		return new ReservationAvailableDatesRes(
			filterReserveNotAvailableDates(notAvailableDates, canReserveDates));
	}

	private List<LocalDate> filterReserveNotAvailableDates(List<LocalDate> notAvailableDates,
		List<LocalDate> canReserveDates) {
		return canReserveDates.stream()
			.filter(localDate -> !notAvailableDates.contains(localDate))
			.toList();
	}

	private List<LocalDate> getOpenDays(Restaurant restaurant) {
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(MAX_RESERVE_PERIOD);

		return start.datesUntil(end)
			.filter(localDate -> !restaurant.isClosingDay(localDate))
			.toList();
	}

	private List<LocalDate> getReserveNotAvailableDates(Restaurant restaurant) {
		return reservationRepository.findTotalVisitorCountPerDay(restaurant, BEFORE_VISIT_STATUSES)
			.stream()
			.filter(proj -> restaurant.isNotReserveAvailableForDay(proj.count()))
			.map(DateAndTotalVisitCountPerDayProj::date)
			.toList();
	}
}
