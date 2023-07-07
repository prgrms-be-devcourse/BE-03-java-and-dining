package com.prgms.allen.dining.domain.restaurant.dto;

import java.time.LocalTime;
import java.util.List;

import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public record RestaurantSimpleRes(
	FoodType foodType,
	String restaurantName,
	String location,
	List<LocalTime> reservationAvailableTimes
) {
	public static RestaurantSimpleRes toDto(Restaurant restaurant,
		List<LocalTime> reservationAvailableTimes) {

		return new RestaurantSimpleRes(restaurant.getFoodType(), restaurant.getName(), restaurant.getLocation(),
			reservationAvailableTimes);
	}

}
