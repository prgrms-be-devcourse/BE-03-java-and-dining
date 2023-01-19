package com.prgms.allen.dining.domain.reservation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.dto.VisitStatus;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

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

	// TODO: Owner 정보 추가하기
	public Page<ReservationSimpleResForOwner> getRestaurantReservations(
		//ownerId
		long restaurantId,
		ReservationStatus status,
		Pageable pageable
	) {
		restaurantService.validateRestaurantExists(restaurantId);
		final Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();

		return new PageImpl<>(
			reservationRepository.findAllByRestaurantAndStatus(restaurant, status, pageable)
				.stream()
				.map(ReservationSimpleResForOwner::new)
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
