package com.prgms.allen.dining.domain.restaurant;

import com.prgms.allen.dining.domain.restaurant.dto.RestaurantInfo;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;

public interface RestaurantProvider {

	RestaurantOperationInfo findById(Long restaurantId);

	RestaurantInfo getInfoById(Long restaurantId);
}
