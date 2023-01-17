package com.prgms.allen.dining.domain.reservation.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgms.allen.dining.domain.customer.entity.Customer;
import com.prgms.allen.dining.domain.customer.entity.CustomerType;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

class ReservationTest {

	private Customer validConsumer;
	private Restaurant validRestaurant;

	@BeforeEach
	void init() {
		validConsumer = new Customer(
			"consumer123",
			"소비",
			"01012342345",
			"asdfg123!",
			CustomerType.CUSTOMER
		);
		Customer validOwner = new Customer(
			"onwer123",
			"점주님",
			"01012342345",
			"asdfg123!",
			CustomerType.OWNER
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
			"01012342345"
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
			validConsumer,
			validRestaurant,
			new ReservationDetail(
				notVisitToday,
				2,
				"맛있게 해주세요~"
			)
		);

		// then
		ReservationStatus status = reservation.getStatus();
		Assertions.assertThat(status).isEqualTo(ReservationStatus.PENDING);
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
			validConsumer,
			validRestaurant,
			new ReservationDetail(
				visitToday,
				2,
				"맛있게 해주세요~"
			)
		);

		// then
		ReservationStatus status = reservation.getStatus();
		Assertions.assertThat(status).isEqualTo(ReservationStatus.CONFIRMED);
	}
}