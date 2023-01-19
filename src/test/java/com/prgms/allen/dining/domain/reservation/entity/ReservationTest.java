package com.prgms.allen.dining.domain.reservation.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.FakeReservationRepository;
import com.prgms.allen.dining.domain.reservation.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.FakeRestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;
import com.prgms.allen.dining.global.error.exception.IllegalReservationStateException;

class ReservationTest {

	private final MemberRepository memberRepository = new FakeMemberRepository();
	private final ReservationRepository reservationRepository = new FakeReservationRepository();
	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();

	private Member validCustomer;
	private Restaurant validRestaurant;

	@BeforeEach
	void init() {
		validCustomer = new Member(
			"consumer123",
			"소비",
			"01012342345",
			"asdfg123!",
			MemberType.CUSTOMER
		);
		Member validOwner = new Member(
			"onwer123",
			"점주님",
			"01012342345",
			"asdfg123!",
			MemberType.OWNER
		);

		validRestaurant = new Restaurant(
			validOwner,
			FoodType.CHINESE,
			"맛있는 식당",
			30,
			LocalTime.now(),
			LocalTime.now(),
			"서울시 뭐뭐구 뭐뭐동 202",
			"맛있는 식당입니다.",
			"01012342345",
			new ArrayList<>(),
			new ArrayList<>()
		);
	}

	@Test
	@DisplayName("당일 예약이 아니면 예약 상태가 PENDING(확정 대기) 상태입니다.")
	void reservation_status_pending() {
		// given
		LocalDateTime notVisitToday = LocalDateTime.now()
			.plusDays(1L)
			.truncatedTo(ChronoUnit.HOURS);

		// when
		Reservation reservation = new Reservation(
			validCustomer,
			validRestaurant,
			new ReservationCustomerInput(
				notVisitToday,
				2,
				"맛있게 해주세요~"
			)
		);

		// then
		ReservationStatus status = reservation.getStatus();
		assertThat(status).isEqualTo(ReservationStatus.PENDING);
	}

	@Test
	@DisplayName("당일 예약이면 예약 상태가 CONFIRMED(예약 확정) 상태입니다.")
	void reservation_status_confirmed() {
		// given
		LocalDateTime visitToday = LocalDateTime.now()
			.plusHours(1L)
			.truncatedTo(ChronoUnit.HOURS);

		// when
		Reservation reservation = new Reservation(
			validCustomer,
			validRestaurant,
			new ReservationCustomerInput(
				visitToday,
				2,
				"맛있게 해주세요~"
			)
		);

		// then
		ReservationStatus status = reservation.getStatus();
		assertThat(status).isEqualTo(ReservationStatus.CONFIRMED);
	}

	@Test
	@DisplayName("확정 대기 상태가 아닌 예약을 확정 상태로 변경 시 예외가 발생한다.")
	void should_throw_exception_when_update_reservation_has_invalid_status() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;

		ReservationStatus invalidStatus = ReservationStatus.CANCELLED;
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, invalidStatus, customerInput)
		);

		// when & then
		assertThrows(IllegalReservationStateException.class, () ->
			reservation.confirm(owner.getId())
		);
	}

	@Test
	@DisplayName("확정 대기 중인 예약을 방문시간 이후에 확정 시 예외가 발생한다.")
	void should_throw_exception_when_visitDateTime_does_not_precede_currentDateTIme() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);

		LocalTime invalidVisitTime = LocalTime.now()
			.plusHours(1)
			.truncatedTo(ChronoUnit.HOURS);
		ReservationCustomerInput customerInput = new ReservationCustomerInput(
			LocalDate.now(),
			invalidVisitTime,
			2
		);

		Reservation lateReservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.PENDING, customerInput)
		);

		// when & then
		assertThrows(IllegalReservationStateException.class, () ->
			lateReservation.confirm(owner.getId())
		);
	}
}