package com.prgms.allen.dining.web.domain.owner.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.reservation.ReservationService;
import com.prgms.allen.dining.domain.reservation.ReservationStatusUpdateService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResponse;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;

@RestController
@RequestMapping("/owner/api/reservations")
public class OwnerReservationApi {

	private final ReservationService reservationService;
	private final ReservationStatusUpdateService reservationStatusUpdateService;

	public OwnerReservationApi(
		ReservationService reservationService,
		ReservationStatusUpdateService reservationStatusUpdateService
	) {
		this.reservationService = reservationService;
		this.reservationStatusUpdateService = reservationStatusUpdateService;
	}

	@GetMapping
	public ResponseEntity<Page<ReservationSimpleResponse>> getOwnerReservations(
		@RequestParam ReservationStatus reservationStatus,
		@RequestParam Long restaurantId,
		Pageable pageable
	) {
		return ResponseEntity.ok(reservationService.getRestaurantReservations(
			restaurantId,
			reservationStatus,
			pageable
		));
	}

	@PatchMapping("/{reservationId}/confirm")
	public ResponseEntity<Void> confirmReservation(
		@PathVariable Long reservationId,
		@RequestParam Long owner
	) {
		reservationStatusUpdateService.confirmReservation(reservationId, owner);
		return ResponseEntity.ok()
			.build();
	}
}
