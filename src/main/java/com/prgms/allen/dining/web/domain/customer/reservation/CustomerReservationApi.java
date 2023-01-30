package com.prgms.allen.dining.web.domain.customer.reservation;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.prgms.allen.dining.domain.member.entity.VisitStatus;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.service.ReservationFindService;
import com.prgms.allen.dining.domain.reservation.service.ReservationService;

@RestController
@RequestMapping("/customer/api/reservations")
public class CustomerReservationApi {

	private final ReservationService reservationService;
	private final ReservationFindService reservationFindService;

	public CustomerReservationApi(ReservationService reservationService,
		ReservationFindService reservationFindService) {
		this.reservationService = reservationService;
		this.reservationFindService = reservationFindService;
	}

	@GetMapping
	public ResponseEntity<Page<ReservationSimpleResForCustomer>> getReservations(
		@RequestParam VisitStatus status,
		@RequestParam Long customerId,
		Pageable pageable
	) {
		return ResponseEntity.ok(reservationFindService.getReservations(
			customerId,
			status,
			pageable
		));
	}

	@GetMapping("/{reservationId}")
	public ResponseEntity<ReservationDetailRes> getReservationDetail(
		@PathVariable Long reservationId,
		@RequestParam Long customerId
	) {
		return ResponseEntity.ok(reservationFindService.getReservationDetail(
			reservationId,
			customerId
		));
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
		@ModelAttribute @Valid ReservationAvailableTimesReq availableTimesReq
	) {
		ReservationAvailableTimesRes availableTimes = reservationService.getAvailableTimes(availableTimesReq);

		return ResponseEntity.ok()
			.body(availableTimes);
	}
}
