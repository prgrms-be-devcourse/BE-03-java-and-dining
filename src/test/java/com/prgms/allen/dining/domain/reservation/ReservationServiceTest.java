package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResponse;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.FakeRestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

class ReservationServiceTest {

	private final ReservationRepository reservationRepository = new FakeReservationRepository();
	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	private final MemberRepository memberRepository = new FakeMemberRepository();
	private final RestaurantService restaurantService = new RestaurantService(restaurantRepository);
	private final ReservationService reservationService = new ReservationService(
		reservationRepository,
		restaurantService
	);

	@AfterEach
	void tearDown() {
		reservationRepository.deleteAll();
		memberRepository.deleteAll();
		restaurantRepository.deleteAll();
	}

	@ParameterizedTest
	@CsvSource({"PENDING", "CONFIRMED", "VISITED", "CANCELLED", "NO_SHOW"})
	@DisplayName("식당의 특정 상태의 예약들을 조회할 수 있다.")
	public void getReservationsTest(String status) {
		// given
		Member owner = createOwner();
		Member consumer = createConsumer();
		memberRepository.save(owner);
		memberRepository.save(consumer);

		Restaurant restaurant = createRestaurant(owner);
		Restaurant savedRestaurant = restaurantRepository.save(restaurant);

		List<Reservation> reservations = createReservations(status, savedRestaurant, consumer);
		List<Reservation> savedReservations = reservationRepository.saveAll(reservations);

		PageImpl<ReservationSimpleResponse> expect = new PageImpl<>(
			savedReservations
				.stream()
				.map(ReservationSimpleResponse::new)
				.toList());

		long restaurantId = savedRestaurant.getId();

		// when
		Page<ReservationSimpleResponse> actual = reservationService.getRestaurantReservations(
			restaurantId,
			ReservationStatus.valueOf(status),
			PageRequest.of(0, 5)
		);

		// then
		Assertions.assertThat(actual)
			.isEqualTo(expect);
	}

	private List<Reservation> createReservations(String status, Restaurant restaurant, Member consumer) {
		Reservation reservation1 = createReservation(
			status,
			consumer,
			restaurant
		);

		Reservation reservation2 = createReservation(
			status,
			consumer,
			restaurant
		);

		return List.of(reservation1, reservation2);
	}

	private Reservation createReservation(String status, Member consumer, Restaurant savedRestaurant) {
		ReservationCustomerInput customerInput = new ReservationCustomerInput(
			LocalDate.of(2023, 1, 16),
			LocalTime.of(16, 59), 2,
			"단무지는 빼주세요"
		);

		return new Reservation(
			consumer,
			savedRestaurant,
			ReservationStatus.valueOf(status),
			customerInput
		);
	}

	private Restaurant createRestaurant(Member owner) {
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

	private Member createConsumer() {
		return new Member(
			"dlxortmd321",
			"이택승이",
			"01012341234",
			"qwer1234!",
			MemberType.CUSTOMER
		);
	}

	private Member createOwner() {
		return new Member(
			"dlxortmd123",
			"이택승",
			"01012341234",
			"qwer1234!",
			MemberType.OWNER
		);
	}

}