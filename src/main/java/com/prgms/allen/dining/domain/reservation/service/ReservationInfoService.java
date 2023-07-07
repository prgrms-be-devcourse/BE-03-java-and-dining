package com.prgms.allen.dining.domain.reservation.service;

import static com.prgms.allen.dining.domain.reservation.policy.ReservationPolicy.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.dto.DateAndTotalVisitCountPerDayProj;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesRes;
import com.prgms.allen.dining.domain.reservation.dto.VisitorCountPerVisitTimeProj;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantFindService;
import com.prgms.allen.dining.domain.restaurant.dto.ReservationAvailableDatesRes;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Service
@Transactional(readOnly = true)
public class ReservationInfoService implements ReservationService {

	private final ReservationRepository reservationRepository;
	private final RestaurantFindService restaurantFindService;

	public ReservationInfoService(ReservationRepository reservationRepository,
		RestaurantFindService restaurantFindService) {
		this.reservationRepository = reservationRepository;
		this.restaurantFindService = restaurantFindService;
	}

	@Override
	public ReservationAvailableTimesRes getAvailableTimes(ReservationAvailableTimesReq availableTimesReq) {
		Restaurant restaurant = restaurantFindService.findById(availableTimesReq.restaurantId());

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

	@Override
	public ReservationAvailableDatesRes getAvailableDates(Long restaurantId) {
		Restaurant restaurant = restaurantFindService.findById(restaurantId);

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
