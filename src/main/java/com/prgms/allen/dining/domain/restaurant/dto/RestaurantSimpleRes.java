package com.prgms.allen.dining.domain.restaurant.dto;

import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public record RestaurantSimpleRes(
	FoodType foodType,
	String restaurantName,
	String location
) {
	public RestaurantSimpleRes(Restaurant restaurant) {
		this(restaurant.getFoodType(), restaurant.getName(), restaurant.getLocation());
	}

}
