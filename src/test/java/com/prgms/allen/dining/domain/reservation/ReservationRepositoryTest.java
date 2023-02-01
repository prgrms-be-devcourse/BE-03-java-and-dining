package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.dto.DateAndTotalVisitCountPerDayProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@DataJpaTest
public class ReservationRepositoryTest {

	private static final int VISITOR_COUNT = 2;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MemberRepository memberRepository;

	private LocalDate reserveDate = LocalDate.of(
		LocalDate.now().getYear(),
		LocalDate.now().getMonth(),
		LocalDate.now().plusDays(1L).getDayOfMonth()
	);

	private LocalTime reserveTime = LocalTime.of(13, 0);

	@Test
	void countTotalVisitorCount() {
		// given
		Member owner = createOwner();
		Member customer = createCustomer();
		Restaurant restaurant = createRestaurant(owner);
		Reservation reservation = createReservation(ReservationStatus.CONFIRMED, customer, restaurant);

		// when
		Optional<Integer> currentReservedCount = reservationRepository.countTotalVisitorCount(restaurant,
			reserveDate,
			reserveTime,
			List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING));

		// then
		assertThat(currentReservedCount.get())
			.isEqualTo(VISITOR_COUNT);
	}

	@Test
	void countTotalVisitorCountPerDay() {
		// given
		int expect = 8;
		Member owner = createOwner();
		Member customer = createCustomer();
		Restaurant restaurant = createRestaurant(owner);
		createReservations(customer, restaurant, ReservationStatus.CONFIRMED);

		// when
		List<DateAndTotalVisitCountPerDayProj> actual = reservationRepository.findTotalVisitorCountPerDay(restaurant,
			List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING));

		// then
		assertThat(actual.get(0).count())
			.isEqualTo(expect);

	}

	private Reservation createReservation(ReservationStatus status, Member consumer, Restaurant savedRestaurant) {

		ReservationCustomerInput detail = new ReservationCustomerInput(
			reserveDate,
			reserveTime,
			2,
			"단무지는 빼주세요"
		);

		return reservationRepository.save(new Reservation(
			consumer,
			savedRestaurant,
			status,
			detail
		));
	}

	private Restaurant createRestaurant(Member owner) {
		return restaurantRepository.save(new Restaurant(
			owner,
			FoodType.KOREAN,
			"장충동국밥",
			100,
			LocalTime.of(9, 0),
			LocalTime.of(23, 0),
			"서울특별시 서초구 어디길11 2층",
			"실망시키지 않는 맛집",
			"021234123",
			List.of(new Menu("메뉴이름", BigInteger.valueOf(10000), "메모")),
			List.of(new ClosingDay(DayOfWeek.MONDAY))
		));
	}

	private Member createCustomer() {
		return memberRepository.save(new Member(
			"dlxortmd321",
			"이택승이",
			"01012341234",
			"qwer1234!",
			MemberType.CUSTOMER
		));
	}

	private Member createOwner() {
		return memberRepository.save(new Member(
			"dlxortmd123",
			"이택승",
			"01012341234",
			"qwer1234!",
			MemberType.OWNER
		));
	}

	private void createReservations(Member consumer, Restaurant savedRestaurant, ReservationStatus status) {

		reservationRepository.save(new Reservation(
			consumer,
			savedRestaurant,
			status,
			new ReservationCustomerInput(
				reserveDate,
				LocalTime.of(16, 0),
				2,
				"단무지는 빼주세요"
			)
		));

		reservationRepository.save(new Reservation(
			consumer,
			savedRestaurant,
			status,
			new ReservationCustomerInput(
				reserveDate,
				LocalTime.of(16, 0),
				2,
				"단무지는 빼주세요"
			)
		));

		reservationRepository.save(new Reservation(
			consumer,
			savedRestaurant,
			status,
			new ReservationCustomerInput(
				reserveDate,
				LocalTime.of(16, 0),
				2,
				"단무지는 빼주세요"
			)
		));

		reservationRepository.save(new Reservation(
			consumer,
			savedRestaurant,
			status,
			new ReservationCustomerInput(
				reserveDate,
				LocalTime.of(16, 0),
				2,
				"단무지는 빼주세요"
			)
		));
	}
}
