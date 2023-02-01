package com.prgms.allen.dining.web.domain.customer.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.dto.MenuDetailRes;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantDetailResForCustomer;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantSimpleRes;

@RestController
@RequestMapping("/customer/api/restaurants")
public class CustomerRestaurantApi {

	private final RestaurantService restaurantService;

	public CustomerRestaurantApi(RestaurantService restaurantService) {
		this.restaurantService = restaurantService;
	}

	@GetMapping
	public ResponseEntity<Page<RestaurantSimpleRes>> getRestaurants(Pageable pageable) {

		Page<RestaurantSimpleRes> restaurants = restaurantService.getRestaurantList(pageable);

		return ResponseEntity.ok(restaurants);
	}

	@GetMapping(path = "/search", params = "restaurantName")
	public ResponseEntity<Page<RestaurantSimpleRes>> getRestaurantsContainsName(Pageable pageable,
		@RequestParam String restaurantName) {

		Page<RestaurantSimpleRes> restaurants = restaurantService.getRestaurantsContains(pageable, restaurantName);

		return ResponseEntity.ok(restaurants);
	}

	@GetMapping("/{restaurantId}")
	public ResponseEntity<RestaurantDetailResForCustomer> getOne(@PathVariable Long restaurantId) {
		RestaurantDetailResForCustomer restaurantDetailResForCustomer = restaurantService.getRestaurant(restaurantId);
		return ResponseEntity.ok(restaurantDetailResForCustomer);
	}

	@GetMapping("/{restaurantId}/menu")
	public ResponseEntity<Page<MenuDetailRes>> getMenu(Pageable pageable,
		@PathVariable Long restaurantId) {

		Page<MenuDetailRes> menus = restaurantService.getMenus(pageable, restaurantId);

		return ResponseEntity.ok(menus);
	}
}