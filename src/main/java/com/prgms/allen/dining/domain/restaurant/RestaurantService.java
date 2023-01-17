package com.prgms.allen.dining.domain.restaurant;

import org.springframework.stereotype.Service;

import com.prgms.allen.dining.domain.customer.CustomerService;
import com.prgms.allen.dining.domain.customer.entity.Customer;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateRequest;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.ErrorCode;
import com.prgms.allen.dining.global.error.exception.RestaurantDuplicateCreationException;

@Service
public class RestaurantService {

	private final RestaurantRepository restaurantRepository;
	private final CustomerService customerService;

	public RestaurantService(RestaurantRepository restaurantRepository, CustomerService customerService) {
		this.restaurantRepository = restaurantRepository;
		this.customerService = customerService;
	}

	public long save(RestaurantCreateRequest restaurantCreateRequest, Long ownerId) {
		validAlreadyHasRestaurant(ownerId);
		final Customer owner = customerService.findOwnerById(ownerId);
		final Restaurant restaurant = restaurantRepository.save(restaurantCreateRequest.toEntity(owner));
		return restaurant.getId();
	}

	private void validAlreadyHasRestaurant(Long ownerId) {
		if (restaurantRepository.existsRestaurantByOwner_Id(ownerId)) {
			throw new RestaurantDuplicateCreationException(ErrorCode.DUPLICATE_ERROR);
		}
	}
}
