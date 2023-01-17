package com.prgms.allen.dining.web.owner;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateRequest;

@RestController
@RequestMapping("owner/api/restaurants")
public class OwnerRestaurantApi {

	private final RestaurantService restaurantService;

	public OwnerRestaurantApi(RestaurantService restaurantService) {
		this.restaurantService = restaurantService;
	}

	@PostMapping()
	public ResponseEntity<Void> create(
		@RequestBody RestaurantCreateRequest restaurantCreateRequest,
		@RequestParam Long ownerId
	) {
		final long restaurantId = restaurantService.save(restaurantCreateRequest, ownerId);

		URI location = UriComponentsBuilder.fromPath("/api/restaurants/{restaurantId}")
			.buildAndExpand(restaurantId)
			.toUri();

		return ResponseEntity.created(location)
			.build();
	}
}
