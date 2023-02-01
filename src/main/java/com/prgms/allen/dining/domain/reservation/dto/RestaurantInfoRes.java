package com.prgms.allen.dining.domain.reservation.dto;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public record RestaurantInfoRes(
	String name,
	String location,
	String phone
) {
	public RestaurantInfoRes(Restaurant restaurant) {
		this(restaurant.getName(), restaurant.getLocation(), restaurant.getPhone());
	}
}
