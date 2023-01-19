package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResponse;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private static final List<ReservationStatus> TAKEN_STATUS_LIST =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

	private final ReservationRepository reservationRepository;
	private final RestaurantService restaurantService;

	public ReservationService(ReservationRepository reservationRepository, RestaurantService restaurantService) {
		this.reservationRepository = reservationRepository;
		this.restaurantService = restaurantService;
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

	public boolean isAvailableReserve(
		Restaurant restaurant,
		LocalDateTime requestTime,
		int numberOfPeople
	) {

		Optional<Integer> totalCount = reservationRepository.countTotalVisitorCount(restaurant,
			requestTime.toLocalDate(),
			requestTime.toLocalTime(),
			TAKEN_STATUS_LIST);

		return restaurant.isAvailable(totalCount.get(), numberOfPeople);
	}
}
