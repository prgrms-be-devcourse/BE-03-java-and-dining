package com.prgms.allen.dining.domain.reservation.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.common.NotFoundResourceException;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoParam;
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoProj;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailResForOwner;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.entity.VisitStatus;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantProvider;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantInfo;

@Service
@Transactional(readOnly = true)
public class ReservationFindService {

	private final ReservationRepository reservationRepository;
	private final RestaurantProvider restaurantProvider;
	private final MemberService memberService;
	private final ReservationReserveService reservationService;

	public ReservationFindService(
		ReservationRepository reservationRepository,
		RestaurantProvider restaurantProvider, MemberService memberService,
		ReservationReserveService reservationService
	) {
		this.reservationRepository = reservationRepository;
		this.restaurantProvider = restaurantProvider;
		this.memberService = memberService;
		this.reservationService = reservationService;
	}

	// TODO: Owner 정보 추가하기
	public Page<ReservationSimpleResForOwner> getReservations(
		//ownerId
		long restaurantId,
		ReservationStatus status,
		Pageable pageable
	) {
		final RestaurantInfo restaurant = restaurantProvider.getInfoById(restaurantId);

		return new PageImpl<>(
			reservationRepository.findAllByRestaurantIdAndStatus(restaurant.getId(), status, pageable)
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
			.map(reservation -> {
				RestaurantInfo restaurantInfo = restaurantProvider.getInfoById(reservation.getRestaurantId());
				return ReservationSimpleResForCustomer.from(reservation, restaurantInfo);
			})
			.toList());
	}

	public ReservationDetailResForCustomer getReservationDetail(Long reservationId, Long customerId) {

		final Member customer = memberService.findCustomerById(customerId);

		String errorMessage = MessageFormat.format(
			"Cannot find Reservation entity satisfies both reservation id={0} and customer id={1}",
			reservationId, customerId
		);

		Reservation reservation = reservationRepository.findByIdAndCustomer(reservationId, customer)
			.orElseThrow(() -> new NotFoundResourceException(errorMessage));

		RestaurantInfo restaurant = restaurantProvider.getInfoById(reservation.getRestaurantId());

		return new ReservationDetailResForCustomer(reservation, restaurant);
	}

	public ReservationDetailResForOwner getReservationDetail(
		Long reservationId
	) {
		final Reservation reservation = reservationService.findById(reservationId);
		final CustomerReservationInfoParam customerReservationInfoParam = new CustomerReservationInfoParam(
			reservationId
		);
		final CustomerReservationInfoProj customerReservationInfo = reservationRepository.findCustomerReservationInfo(
			customerReservationInfoParam
		);

		return new ReservationDetailResForOwner(customerReservationInfo, reservation);
	}
}
