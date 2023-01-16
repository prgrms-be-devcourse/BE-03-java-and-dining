package com.prgms.allen.dining.web.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.reservation.ReservationService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResponse;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantApi {

	private final ReservationService reservationService;

	public RestaurantApi(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@GetMapping("/{restaurantId}/reservations")
	public ResponseEntity<Page<ReservationSimpleResponse>> getOwnerReservations(
		@PathVariable Long restaurantId,
		@RequestParam ReservationStatus reservationStatus,
		Pageable pageable
	) {
		return ResponseEntity.ok(reservationService.getOwnerReservations(
			restaurantId,
			reservationStatus,
			pageable
		));
	}
}
