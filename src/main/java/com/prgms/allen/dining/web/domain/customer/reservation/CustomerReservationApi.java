package com.prgms.allen.dining.web.domain.customer.reservation;

import java.net.URI;
import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.prgms.allen.dining.domain.reservation.ReservationService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesRes;
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
		@RequestBody @Valid ReservationCreateReq createRequest
	) {
		final Long reservationId = reservationService.reserve(customerId, createRequest);

		final URI location = UriComponentsBuilder.fromPath("/customer/api/me/reservations/{reservationId}")
			.buildAndExpand(reservationId)
			.toUri();
		return ResponseEntity.created(location)
			.build();
	}

	@GetMapping("/available-times")
	public ResponseEntity<ReservationAvailableTimesRes> getAvailableTimes(
		@RequestParam Long restaurantId,
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestParam int visitorCount
	) {
		ReservationAvailableTimesRes availableTimes = reservationService.getAvailableTimes(restaurantId, date,
			visitorCount);

		return ResponseEntity.ok()
			.body(availableTimes);
	}
}
