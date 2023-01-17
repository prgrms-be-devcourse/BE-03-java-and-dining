package com.prgms.allen.dining.domain.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateRequest;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResponse;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationDetail;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.ErrorCode;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final RestaurantService restaurantService;
	private final MemberService memberService;

	public ReservationService(
		ReservationRepository reservationRepository,
		RestaurantService restaurantService,
		MemberService memberService
	) {
		this.reservationRepository = reservationRepository;
		this.restaurantService = restaurantService;
		this.memberService = memberService;
	}

	@Transactional
	public void reserve(ReservationCreateRequest createRequest) {
		Member customer = memberService.findCustomerById(createRequest.customerId())
			.orElseThrow(() -> new NotFoundResourceException(
					ErrorCode.NOT_FOUND_RESOURCE,
					String.format("Not found customerId: %d", createRequest.customerId())
				)
			);

		Restaurant restaurant = restaurantService.findById(createRequest.restaurantId())
			.orElseThrow(() -> new NotFoundResourceException(
					ErrorCode.NOT_FOUND_RESOURCE,
					String.format("Not found restaurantId: %d", createRequest.restaurantId())
				)
			);

		ReservationDetail reservationDetail = createRequest
			.reservationDetail()
			.toEntity();

		Reservation newReservation = new Reservation(
			customer,
			restaurant,
			reservationDetail
		);

		reservationRepository.save(newReservation);
	}

	public Page<ReservationSimpleResponse> getRestaurantReservations(
		long restaurantId,
		ReservationStatus status,
		Pageable pageable
	) {
		restaurantService.validateRestaurantExists(restaurantId);

		return new PageImpl<>(
			reservationRepository.findAllByRestaurantIdAndStatus(restaurantId, status, pageable)
				.stream()
				.map(ReservationSimpleResponse::new)
				.toList()
		);
	}
}
