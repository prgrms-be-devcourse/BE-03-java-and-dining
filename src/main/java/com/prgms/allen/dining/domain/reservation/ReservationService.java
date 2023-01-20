package com.prgms.allen.dining.domain.reservation;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.dto.VisitStatus;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final MemberService memberService;
	private final RestaurantService restaurantService;

	public ReservationService(
		ReservationRepository reservationRepository,
		MemberService memberService,
		RestaurantService restaurantService
	) {
		this.reservationRepository = reservationRepository;
		this.memberService = memberService;
		this.restaurantService = restaurantService;
	}

	// TODO: Owner 정보 추가하기
	public Page<ReservationSimpleResForOwner> getRestaurantReservations(
		//ownerId
		long restaurantId,
		ReservationStatus status,
		Pageable pageable
	) {
		final Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

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

		final Member customer = memberService.findCustomerById(customerId);

		return new PageImpl<>(reservationRepository.findAllByCustomerAndStatusIn(customer, statuses, pageable)
			.stream()
			.map(ReservationSimpleResForCustomer::new)
			.toList());
	}

	public ReservationDetailRes getReservationDetail(Long reservationId, Long customerId) {

		final Member customer = memberService.findCustomerById(customerId);

		return new ReservationDetailRes(reservationRepository.findByIdAndCustomer(reservationId, customer)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Reservation entity for reservationId = {0}", reservationId)
			)));
	}
}
