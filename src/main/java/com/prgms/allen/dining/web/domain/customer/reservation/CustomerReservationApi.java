package com.prgms.allen.dining.web.domain.customer.reservation;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.reservation.ReservationService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;

@RestController
@RequestMapping("/customer/api/reservations")
public class CustomerReservationApi {

	private final ReservationService reservationService;

	public CustomerReservationApi(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@PostMapping
	public ResponseEntity<Void> reserve(
		@RequestParam Long customerId,
		@RequestBody @Valid ReservationCreateReq createRequest) {
		reservationService.reserve(customerId, createRequest);
		return ResponseEntity.ok()
			.build();
	}
}
