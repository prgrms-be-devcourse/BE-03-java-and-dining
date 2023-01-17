package com.prgms.allen.dining.domain.reservation;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResponseForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResponseForOwner;
import com.prgms.allen.dining.domain.reservation.dto.VisitStatus;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;
	private final MemberRepository memberRepository;
	private final RestaurantService restaurantService;

	public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository,
		MemberRepository memberRepository,
		RestaurantService restaurantService) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository;
		this.memberRepository = memberRepository;
		this.restaurantService = restaurantService;
	}

	public Page<ReservationSimpleResponseForOwner> getRestaurantReservations(
		long restaurantId,
		ReservationStatus status,
		Pageable pageable
	) {
		restaurantService.validateRestaurantExists(restaurantId);

		return new PageImpl<>(
			reservationRepository.findAllByRestaurantIdAndStatus(restaurantId, status, pageable)
				.stream()
				.map(ReservationSimpleResponseForOwner::new)
				.toList()
		);
	}

	public Page<ReservationSimpleResForCustomer> getRestaurantReservations(
		long customerId,
		VisitStatus status,
		Pageable pageable
	) {
		final List<ReservationStatus> statuses = status.getStatuses();

		final Member customer = memberRepository.findById(customerId).orElseThrow();

		return new PageImpl<>(reservationRepository.findAllByCustomerAndStatusIn(customer, statuses, pageable)
			.stream()
			.map(ReservationSimpleResForCustomer::new)
			.toList());
	}
}
