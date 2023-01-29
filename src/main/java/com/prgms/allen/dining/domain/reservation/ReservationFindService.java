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
import com.prgms.allen.dining.domain.member.entity.VisitStatus;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;

@Service
@Transactional(readOnly = true)
public class ReservationFindService {

	private final ReservationRepository reservationRepository;
	private final RestaurantService restaurantService;
	private final MemberService memberService;

	public ReservationFindService(ReservationRepository reservationRepository, RestaurantService restaurantService,
		MemberService memberService) {
		this.reservationRepository = reservationRepository;
		this.restaurantService = restaurantService;
		this.memberService = memberService;
	}

	// TODO: Owner 정보 추가하기
	public Page<ReservationSimpleResForOwner> getReservations(
		//ownerId
		long restaurantId,
		ReservationStatus status,
		Pageable pageable
	) {
		final Restaurant restaurant = restaurantService.findById(restaurantId);

		return new PageImpl<>(
			reservationRepository.findAllByRestaurantAndStatus(restaurant, status, pageable)
				.stream()
				.map(ReservationSimpleResForOwner::new)
				.toList()
		);
	}

	public Page<ReservationSimpleResForCustomer> getReservations(
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
				MessageFormat.format(
					"Cannot find Reservation entity satisfies both reservation id={0} and customer id={1}",
					reservationId,
					customerId
				))));
	}
}
