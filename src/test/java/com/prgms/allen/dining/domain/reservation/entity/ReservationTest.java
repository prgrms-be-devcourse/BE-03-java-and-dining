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
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));

		ReservationCustomerInput fakeCustomerInput = new FakeReservationCustomerInput(
			LocalDate.now(),
			LocalTime.now()
				.plusHours(1)
				.truncatedTo(ChronoUnit.HOURS),
			2
		);

		// when
		Reservation reservation = new Reservation(customer, restaurant, fakeCustomerInput);

		// then
		ReservationStatus actualStatus = reservation.getStatus();
		assertThat(actualStatus).isEqualTo(ReservationStatus.CONFIRMED);
	}

	@Test
	@DisplayName("고객은 자신의 예약을 취소할 수 있다.")
	void cancel_reservation_by_customer() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);

		ReservationCustomerInput customerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.plusDays(1),
			LocalTime.now()
				.truncatedTo(ChronoUnit.HOURS),
			2
		);

		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.PENDING, customerInput)
		);

		// when
		reservation.cancel(MemberType.CUSTOMER, customer.getId());

		// then
		assertThat(reservation.getStatus()).isSameAs(ReservationStatus.CANCELLED);
	}

	@Test
	@DisplayName("점주는 확정 대기중인 예약을 확정할 수 있다.")
	void confirm_reservation() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);

		ReservationCustomerInput customerInput = new ReservationCustomerInput(
			LocalDate.now()
				.plusDays(1),
			LocalTime.now()
				.truncatedTo(ChronoUnit.HOURS),
			2
		);

		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.PENDING, customerInput)
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
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;

		ReservationStatus invalidStatus = ReservationStatus.CANCELLED;
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, invalidStatus, customerInput)
		);

		// when & then
		assertThrows(IllegalStateException.class, () ->
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

		ReservationCustomerInput fakeCustomerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.minusDays(1),
			LocalTime.now()
				.minusHours(1)
				.truncatedTo(ChronoUnit.HOURS),
			2
		);

		Reservation lateReservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.PENDING, fakeCustomerInput)
		);

		// when & then
		assertThrows(IllegalStateException.class, () ->
			lateReservation.confirm(owner.getId())
		);
	}

	@Test
	@DisplayName("점주는 확정 대기 또는 확정 상태의 예약을 취소할 수 있다.")
	void cancel_reservation() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.PENDING, customerInput)
		);

		// when
		reservation.cancel(MemberType.OWNER, owner.getId());

		// then
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
	}

	@Test
	@DisplayName("점주가 확정 대기 또는 확정 상태가 아닌 예약을 취소 상태로 변경 시 예외가 발생한다.")
	void should_throw_exception_when_cancel_reservation_has_invalid_status() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;

		ReservationStatus invalidStatus = ReservationStatus.NO_SHOW;
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, invalidStatus, customerInput)
		);

		// when & then
		assertThrows(IllegalStateException.class, () ->
			reservation.cancel(MemberType.OWNER, owner.getId())
		);
	}

	@Test
	@DisplayName("점주가 방문 시간 이후에 예약을 취소 시 예외가 발생한다.")
	void should_throw_exception_when_cancel_reservation_if_currentDateTime_is_after_visitDateTime() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);

		ReservationCustomerInput fakeCustomerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.minusDays(1),
			LocalTime.now()
				.minusHours(1)
				.truncatedTo(ChronoUnit.HOURS),
			2
		);

		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.PENDING, fakeCustomerInput)
		);

		// when & then
		assertThrows(IllegalStateException.class, () ->
			reservation.confirm(owner.getId())
		);
	}

	@Test
	@DisplayName("점주는 확정된 예약을 방문 시간 이후 방문완료로 변경할 수 있다.")
	void visit_reservation() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput fakeCustomerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.minusDays(1),
			LocalTime.now()
				.truncatedTo(ChronoUnit.HOURS),
			2
		);
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.CONFIRMED, fakeCustomerInput)
		);

		// when
		reservation.visit(owner.getId());

		// then
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.VISITED);
	}

	@Test
	@DisplayName("점주는 확정된 예약을 방문 시간 이후 노쇼로 변경할 수 있다.")
	void noShow_reservation() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput fakeCustomerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.minusDays(1),
			LocalTime.now()
				.truncatedTo(ChronoUnit.HOURS),
			2
		);
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.CONFIRMED, fakeCustomerInput)
		);

		// when
		reservation.noShow(owner.getId());

		// then
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.NO_SHOW);
	}

	@Test
	@DisplayName("점주가 확정된 예약을 방문시간 전에 방문완료 또는 노쇼로 변경 시 예외가 발생한다.")
	void throws_exception_when_owner_change_reservation_status_before_visitDateTime() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput fakeCustomerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.plusDays(1),
			LocalTime.now()
				.plusHours(1)
				.truncatedTo(ChronoUnit.HOURS),
			2
		);
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.CONFIRMED, fakeCustomerInput)
		);

		// when & then
		assertAll(
			() -> assertThrows(IllegalStateException.class, () -> reservation.visit(owner.getId())),
			() -> assertThrows(IllegalStateException.class, () -> reservation.noShow(owner.getId()))
		);
	}

	@Test
	@DisplayName("점주가 확정된 예약을 방문일로부터 30일 이후에 방문완료 또는 노쇼로 변경 시 예외가 발생한다.")
	void throws_exception_when_owner_change_reservation_status_after_30_days_from_visitDate() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput fakeCustomerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.minusDays(30),
			LocalTime.now()
				.truncatedTo(ChronoUnit.HOURS),
			2
		);
		Reservation reservation = reservationRepository.save(
			DummyGenerator.createReservation(customer, restaurant, ReservationStatus.CONFIRMED, fakeCustomerInput)
		);

		// when & then
		assertAll(
			() -> assertThrows(IllegalStateException.class, () -> reservation.visit(owner.getId())),
			() -> assertThrows(IllegalStateException.class, () -> reservation.noShow(owner.getId()))
		);
	}
}