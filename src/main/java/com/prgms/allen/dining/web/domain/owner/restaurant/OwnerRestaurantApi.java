package com.prgms.allen.dining.web.domain.owner.restaurant;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantDetailResForOwner;
import com.prgms.allen.dining.security.jwt.JwtAuthenticationPrincipal;

@RestController
@RequestMapping("/owner/api/restaurants")
public class OwnerRestaurantApi {

	private final RestaurantService restaurantService;

	public OwnerRestaurantApi(RestaurantService restaurantService) {
		this.restaurantService = restaurantService;
	}

	@PostMapping
	public ResponseEntity<Void> create(
		@Valid @RequestBody RestaurantCreateReq restaurantCreateReq,
		@AuthenticationPrincipal JwtAuthenticationPrincipal principal
	) {
		final long restaurantId = restaurantService.save(restaurantCreateReq, principal.memberId());

		final URI location = UriComponentsBuilder.fromPath("/owner/api/restaurants/{restaurantId}")
			.buildAndExpand(restaurantId)
			.toUri();

		return ResponseEntity.created(location)
			.build();
	}

	@GetMapping("/{restaurantId}")
	public ResponseEntity<RestaurantDetailResForOwner> getOne(
		@PathVariable Long restaurantId,
		@AuthenticationPrincipal JwtAuthenticationPrincipal principal
	) {
		final RestaurantDetailResForOwner restaurantDetailResForCustomer = restaurantService.getRestaurant(restaurantId,
			principal.memberId());

		return ResponseEntity.ok(restaurantDetailResForCustomer);
	}
}
