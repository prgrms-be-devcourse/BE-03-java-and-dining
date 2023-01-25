package com.prgms.allen.dining.domain.reservation;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.dto.VisitStatus;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private static final List<ReservationStatus> TAKEN_STATUS_LIST =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

	private final ReservationRepository reservationRepository;
	private final MemberService memberService;
	private final RestaurantService restaurantService;

	public ReservationService(
		ReservationRepository reservationRepository,
		RestaurantService restaurantService,
		MemberService memberService
	) {
		this.reservationRepository = reservationRepository;
		this.restaurantService = restaurantService;
		this.memberService = memberService;
	}

	// TODO: Owner 정보 추가하기
	public Page<ReservationSimpleResForOwner> getRestaurantReservations(
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

	@Transactional
	public Long reserve(Long customerId, ReservationCreateReq createRequest) {
		Member customer = memberService.findCustomerById(customerId);
		Restaurant restaurant = restaurantService.findById(createRequest.restaurantId());

		ReservationCustomerInput reservationCustomerInput = createRequest
			.reservationCustomerInput()
			.toEntity();

		Reservation newReservation = new Reservation(
			customer,
			restaurant,
			reservationCustomerInput
		);

		reservationRepository.save(newReservation);
		return newReservation.getId();
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

	public Reservation findById(Long id) {
		return reservationRepository.findById(id)
			.orElseThrow(() ->
				new NotFoundResourceException(MessageFormat.format(
					"Cannot find Reservation for reservationId={0}", id
				))
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
