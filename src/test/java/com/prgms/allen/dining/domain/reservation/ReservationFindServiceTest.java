package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.VisitStatus;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailRes;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.FakeRestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

class ReservationFindServiceTest {

	private final ReservationRepository reservationRepository = new FakeReservationRepository();
	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	private final MemberRepository memberRepository = new FakeMemberRepository();
	private final MemberService memberService = new MemberService(memberRepository);
	private final RestaurantService restaurantService = new RestaurantService(restaurantRepository, memberService);
	private final ReservationFindService reservationFindService = new ReservationFindService(
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

		PageImpl<ReservationSimpleResForOwner> expect = new PageImpl<>(
			savedReservations.stream()
				.map(ReservationSimpleResForOwner::new)
				.toList()
		);

		// when
		Page<ReservationSimpleResForOwner> actual = reservationFindService.getReservations(
			restaurant.getId(),
			ReservationStatus.valueOf(status),
			PageRequest.of(0, 5)
		);

		// then
		assertThat(actual)
			.isEqualTo(expect);
	}

	@ParameterizedTest
	@CsvSource({"PLANNED", "DONE", "CANCEL"})
	@DisplayName("구매자는 자신이 예약한 정보들을 상태별로 볼 수 있다.")
	public void getRestaurantReservationsTest(String status) {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);

		VisitStatus visitStatus = VisitStatus.valueOf(status);
		List<ReservationStatus> reservationStatuses = visitStatus.getStatuses();
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;

		List<Reservation> reservations = reservationStatuses.stream()
			.map(reservationStatus -> reservationRepository.save(DummyGenerator.createReservation(
				customer,
				restaurant,
				reservationStatus,
				customerInput
			)))
			.toList();

		PageImpl<ReservationSimpleResForCustomer> expect = new PageImpl<>(
			reservations
				.stream()
				.map(ReservationSimpleResForCustomer::new)
				.toList());

		// when
		Page<ReservationSimpleResForCustomer> actual =
			reservationFindService.getReservations(
				customer.getId(),
				visitStatus,
				PageRequest.of(0, 5)
			);

		// then
		assertThat(actual)
			.isEqualTo(expect);
	}

	@ParameterizedTest
	@CsvSource({"PENDING", "CONFIRMED", "VISITED", "CANCELLED", "NO_SHOW"})
	@DisplayName("식당의 예약 상세 조회")
	public void getReservationDetail(String status) {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;

		Reservation reservation = reservationRepository.save(DummyGenerator.createReservation(
			customer,
			restaurant,
			ReservationStatus.valueOf(status),
			customerInput
		));

		ReservationDetailRes expect = new ReservationDetailRes(reservation);

		// when
		ReservationDetailRes actual = reservationFindService.getReservationDetail(reservation.getId(),
			customer.getId());

		// then
		assertThat(actual)
			.isEqualTo(expect);

	}
}