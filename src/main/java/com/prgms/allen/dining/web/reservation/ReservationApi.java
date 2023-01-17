package com.prgms.allen.dining.web.reservation;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.reservation.ReservationService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateRequest;

@RestController
@RequestMapping("/api/reservations")
public class ReservationApi {

	private final ReservationService reservationService;

	public ReservationApi(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	/**
	 * TODO: ResponseEntity<Void>로 최종 수정
	 */
	@PostMapping
	public ResponseEntity<ReservationCreateRequest> reserve(
		@RequestBody @Valid ReservationCreateRequest createRequest) {
		reservationService.reserve(createRequest);
		return ResponseEntity.ok()
			.body(createRequest);
	}
}
