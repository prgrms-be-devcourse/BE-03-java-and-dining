package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCustomerInputCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleRes;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.FakeRestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

class ReservationServiceTest {

	private final ReservationRepository reservationRepository = new FakeReservationRepository();
	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	private final MemberRepository memberRepository = new FakeMemberRepository();
	private final MemberService memberService = new MemberService(memberRepository);
	private final RestaurantService restaurantService = new RestaurantService(restaurantRepository, memberService);
	private final ReservationService reservationService = new ReservationService(
		reservationRepository,
		restaurantService,
		memberService
	);

	@ParameterizedTest
	@CsvSource({"PENDING", "CONFIRMED", "VISITED", "CANCELLED", "NO_SHOW"})
	@DisplayName("식당의 특정 상태의 예약들을 조회할 수 있다.")
	public void getReservationsTest(String status) {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;

		Reservation reservation1 = DummyGenerator.createReservation(
			customer,
			restaurant,
			ReservationStatus.valueOf(status),
			customerInput
		);
		Reservation reservation2 = DummyGenerator.createReservation(
			customer,
			restaurant,
			ReservationStatus.valueOf(status),
			customerInput
		);

		List<Reservation> savedReservations = reservationRepository.saveAll(List.of(reservation1, reservation2));

		PageImpl<ReservationSimpleRes> expect = new PageImpl<>(
			savedReservations.stream()
				.map(ReservationSimpleRes::new)
				.toList()
		);

		// when
		Page<ReservationSimpleRes> actual = reservationService.getRestaurantReservations(
			restaurant.getId(),
			ReservationStatus.valueOf(status),
			PageRequest.of(0, 5)
		);

		// then
		assertThat(actual).isEqualTo(expect);
	}

	@Test
	@DisplayName("고객은 식당의 예약을 요청할 수 있다.")
	void create_reservation() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);

		ReservationCustomerInputCreateReq customerInputCreateReq = new ReservationCustomerInputCreateReq(
			LocalDateTime.now()
				.plus(2, ChronoUnit.HOURS)
				.truncatedTo(ChronoUnit.HOURS),
			2,
			"맛있게 해주세요"
		);

		ReservationCreateReq reservationCreateReq = new ReservationCreateReq(
			restaurant.getId(),
			customerInputCreateReq
		);

		// when
		reservationService.reserve(customer.getId(), reservationCreateReq);

		// then
		long actualCount = reservationRepository.count();
		assertThat(actualCount).isEqualTo(1);
	}
}