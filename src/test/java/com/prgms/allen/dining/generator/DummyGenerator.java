package com.prgms.allen.dining.generator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public class DummyGenerator {

	public static final Member CUSTOMER = new Member(
		"customer",
		"구매자",
		"01012341234",
		"password1!",
		MemberType.CUSTOMER
	);

	public static final Member OWNER = new Member(
		"owner",
		"점주",
		"01012341234",
		"qwer1234!",
		MemberType.OWNER
	);

	public static final ReservationCustomerInput CUSTOMER_INPUT = new ReservationCustomerInput(
		LocalDate.now()
			.plusDays(1),
		LocalTime.now()
			.plusHours(1)
			.truncatedTo(ChronoUnit.HOURS),
		2
	);

	public static final ReservationCustomerInput CUSTOMER_INPUT_PLUS_1_HOUR = new ReservationCustomerInput(
		LocalDate.now()
			.plusDays(1),
		LocalTime.now()
			.plusHours(2)
			.truncatedTo(ChronoUnit.HOURS),
		2
	);

	public static final ReservationCustomerInput CUSTOMER_INPUT_PLUS_2_HOUR = new ReservationCustomerInput(
		LocalDate.now()
			.plusDays(1),
		LocalTime.now()
			.plusHours(3)
			.truncatedTo(ChronoUnit.HOURS),
		2
	);

	public static final ReservationCustomerInput CUSTOMER_INPUT_PLUS_3_HOUR = new ReservationCustomerInput(
		LocalDate.now()
			.plusDays(1),
		LocalTime.now()
			.plusHours(4)
			.truncatedTo(ChronoUnit.HOURS),
		2
	);

	public static final ReservationCustomerInput CUSTOMER_INPUT_PLUS_4_HOUR = new ReservationCustomerInput(
		LocalDate.now()
			.plusDays(1),
		LocalTime.now()
			.plusHours(5)
			.truncatedTo(ChronoUnit.HOURS),
		2
	);

	public static Restaurant createRestaurant(Member owner) {
		return new Restaurant(
			owner,
			FoodType.KOREAN,
			"장충동국밥",
			100,
			LocalTime.of(9, 0),
			LocalTime.of(23, 0),
			"서울특별시 서초구 어디길11 2층",
			"실망시키지 않는 맛집",
			"021234123"
		);
	}

	public static Reservation createReservation(
		Member customer,
		Restaurant restaurant,
		ReservationStatus status,
		ReservationCustomerInput customerInput
	) {
		return Reservation.newTestInstance(
			null,
			customer,
			restaurant,
			status,
			customerInput
		);
	}

	public static Restaurant createRestaurantWith2Capacity(Member owner) {
		return new Restaurant(
			owner,
			FoodType.KOREAN,
			"오마카세",
			2,
			LocalTime.of(9, 0),
			LocalTime.of(10, 0),
			"서울특별시 강남구 어디길20 2층",
			"한 시간에 단 2명만 받는다",
			"022345667"
		);
	}

	public static List<Reservation> createReservationEveryHour(
		Member customer,
		Restaurant restaurant
	) {
		List<Reservation> reservations = new ArrayList<>();
		for (int i = restaurant.getOpenTime().getHour(); i <= restaurant.getLastOrderTime().getHour(); i++) {
			reservations.add(Reservation.newTestInstance(
				null,
				customer,
				restaurant,
				ReservationStatus.CONFIRMED,
				new ReservationCustomerInput(
					LocalDate.now()
						.plusDays(1),
					LocalTime.of(i, 0)
						.truncatedTo(ChronoUnit.HOURS),
					2
				)
			));
		}
		return reservations;
	}
}
