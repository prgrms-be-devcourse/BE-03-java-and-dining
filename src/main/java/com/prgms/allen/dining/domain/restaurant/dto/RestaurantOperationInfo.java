package com.prgms.allen.dining.domain.restaurant.dto;

import static com.prgms.allen.dining.domain.common.Time.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public class RestaurantOperationInfo {

	private static final long DEFAULT_TIME = 0L;

	private Long id;

	private int capacity;

	private LocalTime openTime;

	private LocalTime lastOrderTime;

	public RestaurantOperationInfo(Long id, int capacity, LocalTime openTime, LocalTime lastOrderTime) {
		this.id = id;
		this.capacity = capacity;
		this.openTime = openTime;
		this.lastOrderTime = lastOrderTime;
	}

	public static RestaurantOperationInfo toOperationInfo(Restaurant restaurant) {
		return new RestaurantOperationInfo(
			restaurant.getId(),
			restaurant.getCapacity(),
			restaurant.getOpenTime(),
			restaurant.getLastOrderTime());
	}

	public Long getId() {
		return id;
	}

	private boolean isAvailable(int totalBookingCount, int requestBookingCount) {
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
				int totalBookingCount = bookingCounts.getOrDefault(time, DEFAULT_TIME).intValue();

				return isAvailable(totalBookingCount, requestBookingCount);
			})
			.toList();
	}

}
