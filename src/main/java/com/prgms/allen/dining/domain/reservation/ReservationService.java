package com.prgms.allen.dining.domain.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResponse;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.global.error.ErrorCode;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final RestaurantService restaurantService;

	public ReservationService(ReservationRepository reservationRepository, RestaurantService restaurantService) {
		this.reservationRepository = reservationRepository;
		this.restaurantService = restaurantService;
	}

	public Page<ReservationSimpleResponse> getOwnerReservations(
		long restaurantId,
		ReservationStatus status,
		Pageable pageable
	) {
		validExistRestaurant(restaurantId);

		return new PageImpl<>(
			reservationRepository.findAllByRestaurantIdAndStatus(restaurantId, status, pageable)
				.stream()
				.map(ReservationSimpleResponse::new)
				.toList()
		);
	}

	private void validExistRestaurant(long restaurantId) {
		if (!restaurantService.existRestaurant(restaurantId)) {
			throw new NotFoundResourceException(ErrorCode.NOT_FOUND_RESOURCE);
		}
	}
}
