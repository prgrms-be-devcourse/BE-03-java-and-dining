package com.prgms.allen.dining.domain.restaurant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RestaurantService {

	private final RestaurantRepository restaurantRepository;

	public RestaurantService(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}

	public boolean existRestaurant(long restaurantId) {
		return restaurantRepository.existsById(restaurantId);
	}
}
