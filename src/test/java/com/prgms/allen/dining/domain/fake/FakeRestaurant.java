package com.prgms.allen.dining.domain.fake;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public class FakeRestaurant extends Restaurant {

	private Long id;

	public FakeRestaurant(Long id, Restaurant restaurant) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
