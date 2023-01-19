package com.prgms.allen.dining.web.domain.customer.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.reservation.ReservationService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.VisitStatus;

@RestController
@RequestMapping("/customer/api/reservations")
public class CustomerReservationApi {

	private final ReservationService reservationService;

	public CustomerReservationApi(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@GetMapping
	public ResponseEntity<Page<ReservationSimpleResForCustomer>> getReservations(
		@RequestParam VisitStatus status,
		@RequestParam Long customerId,
		Pageable pageable
	) {
		return ResponseEntity.ok(reservationService.getRestaurantReservations(
			customerId,
			status,
			pageable
		));
	}
}
