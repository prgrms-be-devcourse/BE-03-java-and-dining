package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgms.allen.dining.domain.fake.FakeMember;
import com.prgms.allen.dining.domain.fake.FakeReservation;
import com.prgms.allen.dining.domain.fake.FakeRestaurant;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCustomerInputCreateReq;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.reservation.service.ReservationReserveService;
import com.prgms.allen.dining.domain.restaurant.RestaurantProvider;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

	// private final ReservationRepository reservationRepository = new FakeReservationRepository();
	// private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	// private final MemberRepository memberRepository = new FakeMemberRepository();
	// private SlackNotifyService slackNotifyService = new FakeSlackNotifyService();
	// private final MemberService memberService = new MemberService(memberRepository);
	// private final RestaurantFindService restaurantFindService = new RestaurantFindService(
	// 	restaurantRepository);
	// private final ReservationReserveService reservationReserveService = new ReservationReserveService(
	// 	reservationRepository,
	// 	restaurantFindService,
	// 	memberService,
	// 	slackNotifyService);
	// private final RestaurantProvider restaurantProvider = new RestaurantFindService(restaurantRepository);
	// private final ReservationProvider reservationService = new ReservationInfoService(reservationRepository,
	// 	restaurantProvider);
	//
	// @Test
	// @DisplayName("고객은 식당의 예약을 요청할 수 있다.")
	// void create_reservation() {
	// 	// given
	// 	Member owner = memberRepository.save(DummyGenerator.OWNER);
	// 	Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
	// 	Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
	//
	// 	ReservationCreateReq reservationCreateReq = new ReservationCreateReq(
	// 		restaurant.getId(),
	// 		new ReservationCustomerInputCreateReq(
	// 			LocalDateTime.of(
	// 				LocalDate.now().plusDays(1),
	// 				restaurant.getOpenTime()
	// 			),
	// 			2,
	// 			"가지 빼주세요"
	// 		)
	// 	);
	//
	// 	// when
	// 	reservationReserveService.reserve(customer.getId(), reservationCreateReq);
	//
	// 	// then
	// 	long actualCount = reservationRepository.count();
	// 	assertThat(actualCount).isEqualTo(1);
	// }
	//
	// @Test
	// @DisplayName("휴무일과 예약이 모두 찬 날을 제외한 이용가능한 날짜 목록을 받을 수 있다.")
	// public void testGetAvailableDates() {
	// 	// given
	// 	Member owner = memberRepository.save(DummyGenerator.OWNER);
	// 	Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurantWith2Capacity(owner));
	// 	Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
	// 	List<Reservation> reservations = DummyGenerator.createReservationEveryHour(customer, restaurant);
	// 	reservationRepository.saveAll(reservations);
	//
	// 	// when
	// 	List<LocalDate> expect =
	// 		reservationService.getAvailableDates(restaurant.getId());
	//
	// 	// then
	// 	assertThat(expect)
	// 		.doesNotContain(reservations.get(0).getVisitDateTime().toLocalDate())
	// 		.allMatch(localDate -> !restaurant.isClosingDay(localDate));
	// }

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private RestaurantProvider restaurantProvider;

	@Mock
	private MemberService memberService;

	@InjectMocks
	private ReservationReserveService reservationReserveService;

	private Member customer;

	private Member owner;

	private Long customerId = 1L;

	private Long ownerId = 2L;

	private Restaurant restaurant;

	private Long restaurantId = 1L;

	private int capacity = 2;

	private Reservation reservation;

	private Long reservationId = 1L;

	@BeforeEach
	void setup() {
		customer = new FakeMember(DummyGenerator.createCustomer("customer1"), customerId);
		owner = new FakeMember(DummyGenerator.createOwner("owner1"), ownerId);
		restaurant = new FakeRestaurant(restaurantId, DummyGenerator.createRestaurant(owner, capacity));
	}

	@Test
	void test_save_reservation_fail() {
		LocalTime visitTime = LocalTime.of(13, 0);
		LocalDate visitDate = LocalDate.now().plusDays(1L);
		int visitorCount = 2;

		when(memberService.findCustomerForReserve(customerId)).thenReturn(customer);

		ReservationCustomerInput customerInput = new ReservationCustomerInput(visitDate, visitTime, visitorCount);
		reservation = new FakeReservation(
			reservationId,
			DummyGenerator.createReservation(customer, restaurant.getId(), customerInput)
		);

		RestaurantOperationInfo restaurantInfo = new RestaurantOperationInfo(
			restaurantId,
			capacity,
			restaurant.getOpenTime(),
			restaurant.getLastOrderTime(),
			restaurant.getClosingDays()
		);

		when(restaurantProvider.findById(restaurantId)).thenReturn(restaurantInfo);

		List<ReservationStatus> reserved_statuses = List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

		when(reservationRepository.findReservationsByDateTime(restaurantId, visitDate, visitTime,
			reserved_statuses)).thenReturn(List.of(reservation));

		ReservationCreateReq createReq = new ReservationCreateReq(
			restaurantId,
			new ReservationCustomerInputCreateReq(
				LocalDateTime.of(visitDate, visitTime),
				visitorCount,
				"")
		);

		assertThatThrownBy(() -> reservationReserveService.reserve(customerId, createReq))
			.isInstanceOf(ReserveFailException.class);
	}

}