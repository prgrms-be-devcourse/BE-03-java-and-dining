package com.prgms.allen.dining.domain.restaurant.dto;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public class RestaurantInfo {

	private Long id;

	private String name;

	private String location;

	private String phone;

	public RestaurantInfo(Long id, String name, String location, String phone) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.phone = phone;
	}

	public static RestaurantInfo toRestaurantInfo(Restaurant restaurant) {
		return new RestaurantInfo(
			restaurant.getId(),
			restaurant.getName(),
			restaurant.getLocation(),
			restaurant.getPhone()
		);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public String getPhone() {
		return phone;
	}
}
