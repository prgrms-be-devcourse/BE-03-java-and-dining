package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoParam;
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoProj;
import com.prgms.allen.dining.domain.reservation.dto.DateAndTotalVisitCountPerDayProj;
import com.prgms.allen.dining.domain.reservation.dto.VisitorCountPerVisitTimeProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@DataJpaTest
class ReservationRepositoryTest {

	Logger log = LoggerFactory.getLogger(ReservationRepositoryTest.class);

	private final Member customer = new Member(
		"customer",
		"김환이",
		"01012342345",
		"asdfg123!",
		MemberType.CUSTOMER
	);

	private final Member owner = new Member(
		"owner",
		"김환이",
		"01012342345",
		"asdfg123!",
		MemberType.OWNER
	);

	private final Restaurant restaurant = new Restaurant(
		owner,
		FoodType.KOREAN,
		"유명 레스토랑",
		6,
		LocalTime.of(11, 0),
		LocalTime.of(21, 0),
		"서울특별시 강남구 어딘가로 123 무슨빌딩 1층",
		"우리는 유명한 한식당입니다.",
		"0211112222",
		List.of(new Menu("맛있는 밥", BigInteger.valueOf(10000), "맛있어용")),
		List.of(new ClosingDay(DayOfWeek.MONDAY))
	);
	private final Restaurant restaurant2 = new Restaurant(
		owner,
		FoodType.KOREAN,
		"유명 레스토랑",
		6,
		LocalTime.of(11, 0),
		LocalTime.of(21, 0),
		"서울특별시 강남구 어딘가로 123 무슨빌딩 1층",
		"우리는 유명한 한식당입니다.",
		"0211112222",
		List.of(new Menu("맛있는 밥", BigInteger.valueOf(10000), "맛있어용")),
		List.of(new ClosingDay(DayOfWeek.MONDAY))
	);

	private LocalDate reserveDate = LocalDate.of(
		LocalDate.now().getYear(),
		LocalDate.now().getMonth(),
		LocalDate.now().plusDays(1L).getDayOfMonth()
	);

	private LocalTime reserveTime = LocalTime.of(13, 0);

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@BeforeEach
	void initMembersAndRestaurant() {
		memberRepository.saveAll(List.of(customer, owner));
		restaurantRepository.saveAll(List.of(restaurant, restaurant2));
	}

	@Test
	@DisplayName("예약 날짜와 예약 상태들을 통해 예약 시간 별 총 예약 인원수를 조회할 수 있습니다.")
	void find_visitor_counts_per_visit_time() {
		// given
		LocalDateTime visitTomorrow = LocalDateTime.now()
			.plusDays(1L)
			.truncatedTo(ChronoUnit.HOURS);
		ReservationCustomerInput tomorrowCustomerInput1 = new ReservationCustomerInput(
			visitTomorrow,
			6,
			"메모메모"
		);
		ReservationCustomerInput tomorrowCustomerInput2 = new ReservationCustomerInput(
			visitTomorrow,
			4,
			"메모메모"
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.CONFIRMED,
			tomorrowCustomerInput1
		);
		saveReservation(
			customer,
			restaurant2,
			ReservationStatus.VISITED,
			tomorrowCustomerInput2
		);

		LocalDateTime visitToday = LocalDateTime.now()
			.plusHours(1L)
			.truncatedTo(ChronoUnit.HOURS);
		ReservationCustomerInput todayCustomerInput = new ReservationCustomerInput(
			visitToday,
			5,
			"메모메모"
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.CONFIRMED,
			todayCustomerInput
		);

		// when
		List<VisitorCountPerVisitTimeProj> visitorCountPerVisitTime = reservationRepository
			.findVisitorCountPerVisitTime(
				restaurant,
				visitToday.toLocalDate(),
				List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
			);
		visitorCountPerVisitTime.forEach(
			v -> log.info("visitTime at {} - totalVisitCount is {}", v.visitTime(), v.totalVisitorCount()));

		// then
		Long actualTotalVisitorCount = visitorCountPerVisitTime.get(0)
			.totalVisitorCount();
		assertThat(actualTotalVisitorCount).isEqualTo(5);
	}

	@Test
	@DisplayName("예약의 방문 날짜와 시간을 통해 그 시간대의 총 방문 예정 인원수를 조회할 수 있다.")
	void countTotalVisitorCount() {
		// given
		LocalDateTime visitDateTime = LocalDateTime.now()
			.plusHours(1L)
			.truncatedTo(ChronoUnit.HOURS);
		ReservationCustomerInput customerInput = new ReservationCustomerInput(
			visitDateTime, 2, "메모메모"
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.CONFIRMED,
			customerInput
		);

		// when
		Optional<Integer> currentReservedCount = reservationRepository.countTotalVisitorCount(restaurant,
			visitDateTime.toLocalDate(),
			visitDateTime.toLocalTime(),
			List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING));

		// then
		assertThat(currentReservedCount).contains(2);
	}

	@Test
	@DisplayName("예약한 고객의 해당 식당에 대한 방문 횟수, 노쇼한 횟수, 마지막 방문 일자를 조회할 수 있다.")
	void findCountsPerStatus() {
		// given
		ReservationCustomerInput customerInput = new ReservationCustomerInput(
			LocalDateTime.of(
				LocalDate.now().plusDays(1),
				restaurant.getOpenTime()
			),
			2,
			"가지 빼주세요"
		);
		ReservationCustomerInput customerInput1 = new ReservationCustomerInput(
			LocalDateTime.of(
				LocalDate.now().plusDays(1),
				restaurant.getOpenTime()
					.plusHours(1)
			),
			2,
			"가지 빼주세요"
		);
		ReservationCustomerInput customerInput2 = new ReservationCustomerInput(
			LocalDateTime.of(
				LocalDate.now().plusDays(1),
				restaurant.getOpenTime()
					.plusHours(2)
			),
			2,
			"가지 빼주세요"
		);
		ReservationCustomerInput customerInput3 = new ReservationCustomerInput(
			LocalDateTime.of(
				LocalDate.now().plusDays(1),
				restaurant.getOpenTime()
					.plusHours(3)
			),
			2,
			"가지 빼주세요"
		);
		ReservationCustomerInput customerInput4 = new ReservationCustomerInput(
			LocalDateTime.of(
				LocalDate.now().plusDays(1),
				restaurant.getOpenTime()
					.plusHours(4)
			),
			2,
			"가지 빼주세요"
		);

		saveReservation(
			customer,
			restaurant,
			ReservationStatus.VISITED,
			customerInput
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.VISITED,
			customerInput1
		);
		Reservation lastVisitedReservation = saveReservation(
			customer,
			restaurant,
			ReservationStatus.VISITED,
			customerInput2
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.NO_SHOW,
			customerInput3
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.CONFIRMED,
			customerInput4
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.CONFIRMED,
			customerInput4
		);

		CustomerReservationInfoParam customerReservationInfoParam = new CustomerReservationInfoParam(
			lastVisitedReservation.getId()
		);

		CustomerReservationInfoProj expect = new CustomerReservationInfoProj(
			customer.getName(),
			customer.getPhone(),
			3L,
			1L,
			lastVisitedReservation.getVisitDateTime().toString()
		);

		// when
		CustomerReservationInfoProj actual = reservationRepository.findCustomerReservationInfo(
			customerReservationInfoParam
		);

		System.out.println("actual = " + actual);

		// then
		assertThat(actual)
			.isEqualTo(expect);
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

	private Reservation saveReservation(
		Member consumer,
		Restaurant savedRestaurant,
		ReservationStatus status,
		ReservationCustomerInput customerInput
	) {

		return reservationRepository.save(Reservation.newTestInstance(
			null,
			consumer,
			savedRestaurant,
			status,
			customerInput
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

	private Member createCustomer() {
		return memberRepository.save(new Member(
			"dlxortmd321",
			"이택승이",
			"01012341234",
			"qwer1234!",
			MemberType.CUSTOMER
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

	private void createReservations(Member consumer, Restaurant savedRestaurant, ReservationStatus status) {

		reservationRepository.save(Reservation.newTestInstance(
			null,
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

		reservationRepository.save(Reservation.newTestInstance(
			null,
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

		reservationRepository.save(Reservation.newTestInstance(
			null,
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

		reservationRepository.save(Reservation.newTestInstance(
			null,
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
