package com.prgms.allen.dining.api.customer.reservation;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationStatusUpdateReq;
import com.prgms.allen.dining.domain.reservation.entity.VisitStatus;
import com.prgms.allen.dining.domain.reservation.service.ReservationFindService;
import com.prgms.allen.dining.domain.reservation.service.ReservationProvider;
import com.prgms.allen.dining.domain.reservation.service.ReservationReserveService;
import com.prgms.allen.dining.domain.reservation.service.ReservationStatusUpdateService;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;
import com.prgms.allen.dining.domain.restaurant.provider.RestaurantProvider;
import com.prgms.allen.dining.security.jwt.JwtAuthenticationPrincipal;

@RestController
@RequestMapping("/customer/api/reservations")
public class CustomerReservationApi {

	private final ReservationFindService reservationFindService;
	private final ReservationStatusUpdateService statusUpdateService;
	private final ReservationReserveService reservationReserveService;
	private final ReservationProvider reservationService;
	private final RestaurantProvider restaurantProvider;

	public CustomerReservationApi(
		ReservationReserveService reservationReserveService,
		ReservationFindService reservationFindService,
		@Qualifier("customerReservationStatusUpdateService") ReservationStatusUpdateService statusUpdateService,
		ReservationProvider reservationService,
		RestaurantProvider restaurantProvider) {
		this.reservationReserveService = reservationReserveService;
		this.reservationFindService = reservationFindService;
		this.statusUpdateService = statusUpdateService;
		this.reservationService = reservationService;
		this.restaurantProvider = restaurantProvider;
	}

	@GetMapping
	public ResponseEntity<Page<ReservationSimpleResForCustomer>> getReservations(
		@RequestParam VisitStatus status,
		@AuthenticationPrincipal JwtAuthenticationPrincipal principal,
		Pageable pageable
	) {
		return ResponseEntity.ok(reservationFindService.getReservations(
			principal.memberId(),
			status,
			pageable
		));
	}

	@GetMapping("/{reservationId}")
	public ResponseEntity<ReservationDetailResForCustomer> getReservationDetail(
		@PathVariable Long reservationId,
		@AuthenticationPrincipal JwtAuthenticationPrincipal principal
	) {
		return ResponseEntity.ok(reservationFindService.getReservationDetail(
			reservationId,
			principal.memberId()
		));
	}

	@PostMapping
	public ResponseEntity<Void> reserve(
		@AuthenticationPrincipal JwtAuthenticationPrincipal principal,
		@RequestBody @Valid ReservationCreateReq createRequest
	) {
		final Long reservationId = reservationReserveService.reserve(principal.memberId(), createRequest);

		final URI location = UriComponentsBuilder.fromPath("/customer/api/me/reservations/{reservationId}")
			.buildAndExpand(reservationId)
			.toUri();

		return ResponseEntity.created(location)
			.build();
	}

	@GetMapping("/available-times")
	public ResponseEntity<List<LocalTime>> getAvailableTimes(
		@ModelAttribute @Valid ReservationAvailableTimesReq availableTimesReq
	) {
		RestaurantOperationInfo restaurant = restaurantProvider.findById(availableTimesReq.restaurantId());
		List<LocalTime> availableTimes = reservationService.getAvailableTimes(restaurant);

		return ResponseEntity.ok()
			.body(availableTimes);
	}

	@PatchMapping("/{reservationId}")
	public ResponseEntity<Void> cancel(
		@PathVariable Long reservationId,
		@AuthenticationPrincipal JwtAuthenticationPrincipal principal,
		@Valid @RequestBody ReservationStatusUpdateReq statusUpdateReq
	) {

		statusUpdateService.update(reservationId, principal.memberId(), statusUpdateReq);
		return ResponseEntity.ok()
			.build();
	}

	@GetMapping("/available-dates")
	public ResponseEntity<List<LocalDate>> getAvailableDates(@RequestParam Long restaurantId) {
		List<LocalDate> availableDates = reservationService.getAvailableDates(restaurantId);

		return ResponseEntity.ok(availableDates);
	}
}
