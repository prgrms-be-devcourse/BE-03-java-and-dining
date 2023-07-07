package com.prgms.allen.dining.domain.restaurant.dto;

import java.time.LocalTime;
import java.util.List;

import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public record RestaurantDetailResForCustomer(
	String name,
	String description,
	FoodType foodType,
	String location,
	LocalTime openTime,
	LocalTime lastOrderTime,
	String phone,
	List<MenuSimpleRes> menuList,
	List<ClosingDayRes> closingDays
) {
	public RestaurantDetailResForCustomer(Restaurant restaurant) {
		this(
			restaurant.getName(),
			restaurant.getDescription(),
			restaurant.getFoodType(),
			restaurant.getLocation(),
			restaurant.getOpenTime(),
			restaurant.getLastOrderTime(),
			restaurant.getPhone(),
			restaurant.getMenu()
				.stream()
				.map(MenuSimpleRes::new)
				.toList(),
			restaurant.getClosingDays()
				.stream()
				.map(ClosingDayRes::new)
				.toList()
		);
	}
}
