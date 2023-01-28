package com.prgms.allen.dining.domain.reservation;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationAvailableTimesRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.dto.VisitStatus;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleRes;
import com.prgms.allen.dining.domain.reservation.dto.VisitorCountPerVisitTimeProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;
import com.prgms.allen.dining.global.error.exception.ReserveFailException;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private static final List<ReservationStatus> BEFORE_VISIT_STATUSES =
		List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

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

		ReservationCustomerInput customerInput = createRequest
			.reservationCustomerInput()
			.toEntity();
		checkAvailableReservation(restaurant, customerInput.getVisitDateTime(), customerInput.getVisitorCount());

		Reservation newReservation = new Reservation(customer, restaurant, customerInput);
		reservationRepository.save(newReservation);
		return newReservation.getId();
	}

	private void checkAvailableReservation(Restaurant restaurant, LocalDateTime visitDateTime, int visitorCount) {
		checkAvailableVisitDateTime(restaurant, visitDateTime);
		checkAvailableVisitorCount(restaurant, visitDateTime, visitorCount);
	}

	private void checkAvailableVisitDateTime(Restaurant restaurant, LocalDateTime visitDateTime) {
		boolean isAvailableVisitDateTime = restaurant.isAvailableVisitDateTime(visitDateTime);
		if (!isAvailableVisitDateTime) {
			throw new ReserveFailException(
				String.format(
					"Reservation for restaurant ID %d failed. "
						+ "Requested visit date time %s is not between %s and %s",
					restaurant.getId(),
					visitDateTime,
					restaurant.getOpenTime(),
					restaurant.getLastOrderTime()
				)
			);
		}
	}

	private void checkAvailableVisitorCount(Restaurant restaurant, LocalDateTime visitDateTime, int visitorCount) {
		int totalVisitorCount = reservationRepository.countTotalVisitorCount(restaurant,
			visitDateTime.toLocalDate(),
			visitDateTime.toLocalTime(),
			BEFORE_VISIT_STATUSES
		).orElse(0);

		boolean isAvailableVisitorCount = restaurant.isAvailableVisitorCount(totalVisitorCount, visitorCount);
		if (!isAvailableVisitorCount) {
			throw new ReserveFailException(
				String.format(
					"Reservation for restaurant ID %d on %s failed. "
						+ "Requested visitor count is %d, but maximum available visitor count is %d",
					restaurant.getId(),
					visitDateTime,
					visitorCount,
					restaurant.getCapacity() - totalVisitorCount
				)
			);
		}
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

	public ReservationAvailableTimesRes getAvailableTimes(ReservationAvailableTimesReq availableTimesReq) {
		Restaurant restaurant = restaurantService.findById(
			availableTimesReq.restaurantId()
		);

		Map<LocalTime, Long> visitorCountPerTimeMap = getVisitorCountPerTimeMap(availableTimesReq.date(), restaurant);

		List<LocalTime> availableTimes = getAvailableTimes(
			availableTimesReq.visitorCount(),
			restaurant,
			visitorCountPerTimeMap
		);
		return new ReservationAvailableTimesRes(availableTimes);
	}

	private Map<LocalTime, Long> getVisitorCountPerTimeMap(LocalDate visitDate, Restaurant restaurant) {
		return reservationRepository.findVisitorCountPerVisitTime(
				restaurant,
				visitDate,
				BEFORE_VISIT_STATUSES
			)
			.stream()
			.collect(Collectors.toMap(
					VisitorCountPerVisitTimeProj::visitTime,
					VisitorCountPerVisitTimeProj::totalVisitorCount
				)
			);
	}

	private List<LocalTime> getAvailableTimes(
		int visitorCount,
		Restaurant restaurant,
		Map<LocalTime, Long> visitorCountPerTimeMap
	) {
		return restaurant.generateTimeTable()
			.stream()
			.filter(availableVisitorCountPredicate(visitorCount, restaurant, visitorCountPerTimeMap))
			.toList();
	}

	private Predicate<LocalTime> availableVisitorCountPredicate(
		int visitorCount,
		Restaurant restaurant,
		Map<LocalTime, Long> visitorCountPerTimeMap
	) {
		return time -> {
			Long totalVisitorCount = visitorCountPerTimeMap.getOrDefault(time, 0L);
			return restaurant.isAvailableVisitorCount(
				totalVisitorCount.intValue(),
				visitorCount
			);
		};
	}
}
