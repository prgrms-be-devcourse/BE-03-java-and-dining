package com.prgms.allen.dining.web.domain.owner.reservation;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailResForOwner;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.dto.ReservationStatusUpdateReq;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.service.ReservationFindService;
import com.prgms.allen.dining.domain.reservation.service.ReservationStatusUpdateService;

@RestController
@RequestMapping("/owner/api/reservations")
public class OwnerReservationApi {

	private final ReservationFindService reservationFindService;
	private final ReservationStatusUpdateService statusUpdateService;

	public OwnerReservationApi(
		ReservationFindService reservationFindService,
		@Qualifier("ownerReservationStatusUpdateService") ReservationStatusUpdateService statusUpdateService
	) {
		this.reservationFindService = reservationFindService;
		this.statusUpdateService = statusUpdateService;
	}

	@GetMapping
	public ResponseEntity<Page<ReservationSimpleResForOwner>> getOwnerReservations(
		@RequestParam ReservationStatus reservationStatus,
		@RequestParam Long restaurantId,
		Pageable pageable
	) {
		return ResponseEntity.ok(reservationFindService.getReservations(
			restaurantId,
			reservationStatus,
			pageable
		));
	}

	@GetMapping("/{reservationId}")
	public ResponseEntity<ReservationDetailResForOwner> getReservationDetail(
		@PathVariable Long reservationId
	) {
		return ResponseEntity.ok(reservationFindService.getReservationDetail(
			reservationId
		));
	}

	@PatchMapping("/{reservationId}")
	public ResponseEntity<Void> updateStatus(
		@PathVariable Long reservationId,
		@RequestParam Long ownerId,
		@Valid @RequestBody ReservationStatusUpdateReq statusUpdateReq
	) {
		statusUpdateService.update(reservationId, ownerId, statusUpdateReq);
		return ResponseEntity.ok()
			.build();
	}
}
