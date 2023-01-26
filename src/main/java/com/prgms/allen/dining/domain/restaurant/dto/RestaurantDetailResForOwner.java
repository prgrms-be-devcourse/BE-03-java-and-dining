package com.prgms.allen.dining.domain.restaurant.dto;

import java.time.LocalTime;
import java.util.List;

import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public record RestaurantDetailResForOwner(
	String name,
	String description,
	FoodType foodType,
	String location,
	LocalTime openTime,
	LocalTime lastOrderTime,
	String phone,
	int capacity,
	List<MenuSimpleRes> menuList,
	List<ClosingDayRes> closingDays
) {
	public RestaurantDetailResForOwner(
		Restaurant restaurant,
		List<MenuSimpleRes> menuList,
		List<ClosingDayRes> closingDays
	) {
		this(
			restaurant.getName(),
			restaurant.getDescription(),
			restaurant.getFoodType(),
			restaurant.getLocation(),
			restaurant.getOpenTime(),
			restaurant.getLastOrderTime(),
			restaurant.getPhone(),
			restaurant.getCapacity(),
			menuList,
			closingDays
		);
	}

}
