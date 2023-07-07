package com.prgms.allen.dining.domain.restaurant.dto;

import static com.prgms.allen.dining.domain.common.Time.*;
import static com.prgms.allen.dining.domain.reservation.policy.ReservationPolicy.*;
import static java.util.function.Predicate.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public class RestaurantOperationInfo {

	private static final long DEFAULT_COUNT = 0L;

	private Long id;

	private int capacity;

	private LocalTime openTime;

	private LocalTime lastOrderTime;

	private List<ClosingDay> closingDays;

	public RestaurantOperationInfo(Long id, int capacity, LocalTime openTime, LocalTime lastOrderTime,
		List<ClosingDay> closingDays) {
		this.id = id;
		this.capacity = capacity;
		this.openTime = openTime;
		this.lastOrderTime = lastOrderTime;
		this.closingDays = closingDays;
	}

	public static RestaurantOperationInfo toOperationInfo(Restaurant restaurant) {
		return new RestaurantOperationInfo(
			restaurant.getId(),
			restaurant.getCapacity(),
			restaurant.getOpenTime(),
			restaurant.getLastOrderTime(),
			restaurant.getClosingDays());
	}

	public Long getId() {
		return id;
	}

	public LocalTime getOpenTime() {
		return openTime;
	}

	public LocalTime getLastOrderTime() {
		return lastOrderTime;
	}

	public boolean isAvailable(int totalBookingCount, int requestBookingCount) {
		int availableCount = this.capacity - totalBookingCount;
		return availableCount >= requestBookingCount;
	}

	public List<LocalTime> getAvailableTimes(
		int requestBookingCount,
		Map<LocalTime, Long> bookingCounts
	) {
		return TIME_TABLE.stream()
			.filter(time -> time.isAfter(openTime) && time.isBefore(lastOrderTime))
			.filter(time -> {
				int totalBookingCount = bookingCounts.getOrDefault(time, DEFAULT_COUNT).intValue();

				return isAvailable(totalBookingCount, requestBookingCount);
			})
			.toList();
	}

	public List<LocalDate> getAvailableDates(int bookingCount, Map<LocalDate, Long> bookingCounts) {
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(MAX_RESERVE_PERIOD);

		return start.datesUntil(end)
			.filter(
				not(date -> this.closingDays.contains(date.getDayOfWeek()))
			)
			.filter(date -> {
				int totalBookingCount = bookingCounts.getOrDefault(date, DEFAULT_COUNT).intValue();

				return isAvailable(totalBookingCount, bookingCount);
			}).toList();
	}

	public boolean isAvailable(LocalDateTime visitDateTime) {
		LocalDate visitDate = visitDateTime.toLocalDate();
		LocalTime visitTime = visitDateTime.toLocalTime();

		boolean isOnAfterOpenTime = visitTime.isAfter(openTime) && visitTime.equals(openTime);
		boolean isOnBeforeLastOrderTime = visitTime.isBefore(lastOrderTime) && visitTime.equals(lastOrderTime);
		boolean isNotClosingDay = !this.closingDays.contains(visitDate.getDayOfWeek());

		return isNotClosingDay && isOnAfterOpenTime && isOnBeforeLastOrderTime;
	}
}
