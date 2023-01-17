package com.prgms.allen.dining.domain.restaurant;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.global.error.ErrorCode;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;

@Service
@Transactional(readOnly = true)
public class RestaurantService {

	private final RestaurantRepository restaurantRepository;

	public RestaurantService(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}

	public void validateRestaurantExists(long restaurantId) {
		if (!restaurantRepository.existsById(restaurantId)) {
			throw new NotFoundResourceException(
				ErrorCode.NOT_FOUND_RESOURCE,
				MessageFormat.format("Cannot find Restaurant entity for restaurant id = {0}", restaurantId)
			);
		}
	}
}