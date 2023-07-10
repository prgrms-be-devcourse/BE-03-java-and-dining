package com.prgms.allen.dining.domain.fake;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public class FakeRestaurant extends Restaurant {

	private Long id;

	public FakeRestaurant(Long id, Restaurant restaurant) {
		super(restaurant.getOwner(),
			restaurant.getFoodType(),
			restaurant.getName(),
			restaurant.getCapacity(),
			restaurant.getOpenTime(),
			restaurant.getLastOrderTime(),
			restaurant.getLocation(),
			restaurant.getDescription(),
			restaurant.getPhone());

		this.id = id;
	}

	@Override
	public Long getId() {
		return this.id;
	}
}
