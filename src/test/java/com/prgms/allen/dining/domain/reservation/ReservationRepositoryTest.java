package com.prgms.allen.dining.domain.reservation;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.dto.VisitorCountsPerVisitTimeProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@DataJpaTest
@Transactional
class ReservationRepositoryTest {

	Logger log = LoggerFactory.getLogger(ReservationRepositoryTest.class);

	private static final int VISITOR_COUNT = 2;

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

	private LocalDate reserveDate = LocalDate.of(
		LocalDate.now().getYear(),
		LocalDate.now().getMonth(),
		LocalDate.now().getDayOfMonth() + 1
	);

	private LocalTime reserveTime = LocalTime.of(13, 0);

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@BeforeEach
	void init() {
		memberRepository.saveAll(List.of(customer, owner));
		restaurantRepository.save(restaurant);
		reservationRepository.saveAll(createDummyReservations());
	}

	@Test
	@DisplayName("예약 날짜와 예약 상태들을 통해 예약 시간 별 총 예약 인원수를 조회할 수 있습니다.")
	void find_visitor_counts_per_visit_time() {
		// given
		LocalDate visitDate = LocalDate.now().plusDays(1L);

		// when
		List<VisitorCountsPerVisitTimeProj> visitorCountsPerVisitTime = reservationRepository.findVisitorCountsPerVisitTime(
			visitDate, List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED));

		// then
		visitorCountsPerVisitTime.forEach(v -> log.info("{}: {}", v.visitTime(), v.totalVisitorCount()));
	}

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
		Assertions.assertThat(currentReservedCount.get())
			.isEqualTo(VISITOR_COUNT);
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

	private List<Reservation> createDummyReservations() {

		List<Reservation> reservations = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			for (int j = 2 + i; j < 5 + i; j++) {
				reservations.add(
					new Reservation(
						customer,
						restaurant,
						new ReservationCustomerInput(
							LocalDate.now().plusDays(1L),
							LocalTime.now().plusHours(i).truncatedTo(ChronoUnit.HOURS),
							j, "예약 메모" // 1시간 뒤 : 3 4 5 / 2시간 뒤 : 4 5 6
						)
					)
				);
			}

		}
		return reservations;
	}
}