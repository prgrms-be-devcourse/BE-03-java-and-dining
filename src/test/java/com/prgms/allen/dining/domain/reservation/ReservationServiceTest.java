package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoProj;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCustomerInputCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationDetailResForOwner;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForCustomer;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.dto.VisitStatus;
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

		PageImpl<ReservationSimpleResForOwner> expect = new PageImpl<>(
			savedReservations.stream()
				.map(ReservationSimpleResForOwner::new)
				.toList()
		);

		// when
		Page<ReservationSimpleResForOwner> actual = reservationService.getRestaurantReservations(
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
			reservationService.getRestaurantReservations(
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

		ReservationDetailResForCustomer expect = new ReservationDetailResForCustomer(reservation);

		// when
		ReservationDetailResForCustomer actual = reservationService.getReservationDetail(reservation.getId(),
			customer.getId());

		// then
		assertThat(actual)
			.isEqualTo(expect);

	}

	@Test
	@DisplayName("고객은 식당의 예약을 요청할 수 있다.")
	void create_reservation() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);

		ReservationCreateReq reservationCreateReq = new ReservationCreateReq(
			restaurant.getId(),
			new ReservationCustomerInputCreateReq(
				LocalDateTime.of(
					LocalDate.now().plusDays(1),
					restaurant.getOpenTime()
				),
				2,
				"가지 빼주세요"
			)
		);

		// when
		reservationService.reserve(customer.getId(), reservationCreateReq);

		// then
		long actualCount = reservationRepository.count();
		assertThat(actualCount).isEqualTo(1);
	}

	@Test
	@DisplayName("점주는 예약을 상세조회 할 수 있다.")
	void getReservationDetail() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;

		Reservation reservation1 = reservationRepository.save(DummyGenerator.createReservation(
			customer,
			restaurant,
			ReservationStatus.VISITED,
			customerInput
		));
		Reservation reservation2 = reservationRepository.save(DummyGenerator.createReservation(
			customer,
			restaurant,
			ReservationStatus.VISITED,
			customerInput
		));
		Reservation reservation3 = reservationRepository.save(DummyGenerator.createReservation(
			customer,
			restaurant,
			ReservationStatus.VISITED,
			customerInput
		));
		Reservation reservation4 = reservationRepository.save(DummyGenerator.createReservation(
			customer,
			restaurant,
			ReservationStatus.NO_SHOW,
			customerInput
		));

		ReservationDetailResForOwner expect = new ReservationDetailResForOwner(
			new CustomerReservationInfoProj(
				customer.getName(), customer.getPhone(), 3L,
				1L,
				reservation3.getVisitDateTime().toString()
			),
			reservation4
		);

		// when
		ReservationDetailResForOwner actual = reservationService.getReservationDetail(reservation3.getId());

		// then
		assertThat(actual)
			.isEqualTo(expect);

	}
}