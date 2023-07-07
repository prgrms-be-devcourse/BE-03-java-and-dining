package com.prgms.allen.dining.domain.restaurant.dto;

import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesRes;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public record RestaurantSimpleRes(
	FoodType foodType,
	String restaurantName,
	String location,
	ReservationAvailableTimesRes reservationAvailableTimesRes
) {
	public static RestaurantSimpleRes toDto(Restaurant restaurant,
		ReservationAvailableTimesRes reservationAvailableTimesRes) {

		return new RestaurantSimpleRes(restaurant.getFoodType(), restaurant.getName(), restaurant.getLocation(),
			reservationAvailableTimesRes);
	}

}
