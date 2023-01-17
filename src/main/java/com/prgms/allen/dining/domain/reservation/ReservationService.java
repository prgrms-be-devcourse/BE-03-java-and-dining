package com.prgms.allen.dining.domain.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.customer.CustomerService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateRequest;
import com.prgms.allen.dining.domain.reservation.entity.ReservationDetail;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResponse;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;


@Service
@Transactional(readOnly = true)
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final RestaurantService restaurantService;
	private final CustomerService customerService;

	public ReservationService(
		ReservationRepository reservationRepository,
		RestaurantService restaurantService,
		CustomerService customerService
	) {
		this.reservationRepository = reservationRepository;
		this.restaurantService = restaurantService;
		this.customerService = customerService;
	}

	/**
	 * pull받으면 메서드 명, 파라미터 등 다른 서비스 참조하는 것들 수정
	 */
	public void reserve(ReservationCreateRequest createRequest) {
		// Customer consumer = customerService.findConsumerById(createRequest.consumerId())
		// 	.orElseThrow(() -> new NotFoundResourceException(
		// 		String.format("Not found customerId: %d", createRequest.consumerId())));

		Restaurant restaurant = restaurantService.findById(createRequest.restaurantId())
			.orElseThrow(() -> new NotFoundResourceException(
				String.format("Not found restaurantId: %d", createRequest.restaurantId())
				)
			);

		ReservationDetail reservationDetail = createRequest
			.reservationDetail()
			.toEntity();
		// restaurantService.checkPossible(
		// 	reservationDetail.getVisitDate(),
		// 	reservationDetail.getVisitTime(),
		// 	reservationDetail.getVisitorCount());
		// Reservation newReservation = new Reservation(
		// 	consumer,
		// 	restaurant,
		// 	reservationDetail
		// );

		// reservationRepository.save(newReservation);
		// restaurantService.minusVisitorCount(reservationDetail);
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
