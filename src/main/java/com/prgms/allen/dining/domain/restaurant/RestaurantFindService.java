package com.prgms.allen.dining.domain.restaurant;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.common.NotFoundResourceException;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;

@Service
@Transactional(readOnly = true)
public class RestaurantFindService implements RestaurantProvider {

	private final RestaurantRepository restaurantRepository;

	public RestaurantFindService(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}

	public RestaurantOperationInfo findById(Long restaurantId) {
		return restaurantRepository.findById(restaurantId)
			.map(RestaurantOperationInfo::toOperationInfo)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Restaurant entity for restaurant id = {0}", restaurantId)
			));
	}
}
