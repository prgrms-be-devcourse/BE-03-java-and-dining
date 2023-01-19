package com.prgms.allen.dining.web.domain.customer.restaurant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantDetailResForCustomer;

@RestController
@RequestMapping("/customer/api/restaurants")
public class CustomerRestaurantApi {

	private final RestaurantService restaurantService;

	public CustomerRestaurantApi(RestaurantService restaurantService) {
		this.restaurantService = restaurantService;
	}

	@GetMapping("/{restaurantId}")
	public ResponseEntity<RestaurantDetailResForCustomer> getOne(@PathVariable Long restaurantId) {
		RestaurantDetailResForCustomer restaurantDetailResForCustomer = restaurantService.getRestaurant(restaurantId);
		return ResponseEntity.ok(restaurantDetailResForCustomer);
	}
}
