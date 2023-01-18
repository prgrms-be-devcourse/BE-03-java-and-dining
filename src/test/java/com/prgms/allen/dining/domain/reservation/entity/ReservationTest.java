package com.prgms.allen.dining.domain.reservation.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.FakeReservationRepository;
import com.prgms.allen.dining.domain.reservation.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.FakeRestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;
import com.prgms.allen.dining.global.error.exception.IllegalReservationStateException;

class ReservationTest {

	private final MemberRepository memberRepository = new FakeMemberRepository();
	private final ReservationRepository reservationRepository = new FakeReservationRepository();
	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();

	@Test
	@DisplayName("점주는 확정 대기중인 예약을 확정할 수 있다.")
	void confirm_reservation_in_pending_status() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.PENDING)
		);

		// when
		reservation.confirm(owner.getId());

		// then
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
	}

	@Test
	@DisplayName("확정 대기 상태가 아닌 예약을 확정 상태로 변경 시 예외가 발생한다.")
	void should_throw_exception_when_update_reservation_has_invalid_status() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);

		ReservationStatus invalidStatus = ReservationStatus.CANCELLED;
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, invalidStatus)
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

		ReservationCustomerInput customerInput = new ReservationCustomerInput(
			LocalDate.now(),
			LocalTime.now().plusMinutes(1),
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