package com.prgms.allen.dining.domain.restaurant;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.common.NotFoundResourceException;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Service
@Transactional(readOnly = true)
public class RestaurantFindService {

	private final RestaurantRepository restaurantRepository;

	public RestaurantFindService(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}

	public Restaurant findById(Long restaurantId) {
		return restaurantRepository.findById(restaurantId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Restaurant entity for restaurant id = {0}", restaurantId)
			));
	}
}
