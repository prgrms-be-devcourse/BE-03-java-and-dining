package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.notification.FakeSlackNotifyService;
import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCustomerInputCreateReq;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.reservation.service.ReservationInfoService;
import com.prgms.allen.dining.domain.reservation.service.ReservationProvider;
import com.prgms.allen.dining.domain.reservation.service.ReservationReserveService;
import com.prgms.allen.dining.domain.restaurant.FakeRestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantFindService;
import com.prgms.allen.dining.domain.restaurant.RestaurantProvider;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

class ReservationServiceTest {

	private final ReservationRepository reservationRepository = new FakeReservationRepository();
	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	private final MemberRepository memberRepository = new FakeMemberRepository();
	private SlackNotifyService slackNotifyService = new FakeSlackNotifyService();
	private final MemberService memberService = new MemberService(memberRepository);
	private final RestaurantFindService restaurantFindService = new RestaurantFindService(
		restaurantRepository);
	private final ReservationReserveService reservationReserveService = new ReservationReserveService(
		reservationRepository,
		restaurantFindService,
		memberService,
		slackNotifyService, restaurantRepository);
	private final RestaurantProvider restaurantProvider = new RestaurantFindService(restaurantRepository);
	private final ReservationProvider reservationService = new ReservationInfoService(reservationRepository,
		restaurantProvider);

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
		reservationReserveService.reserve(customer.getId(), reservationCreateReq);

		// then
		long actualCount = reservationRepository.count();
		assertThat(actualCount).isEqualTo(1);
	}

	@Test
	@DisplayName("휴무일과 예약이 모두 찬 날을 제외한 이용가능한 날짜 목록을 받을 수 있다.")
	public void testGetAvailableDates() {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurantWith2Capacity(owner));
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		List<Reservation> reservations = DummyGenerator.createReservationEveryHour(customer, restaurant);
		reservationRepository.saveAll(reservations);

		// when
		List<LocalDate> expect =
			reservationService.getAvailableDates(restaurant.getId());

		// then
		assertThat(expect)
			.doesNotContain(reservations.get(0).getVisitDateTime().toLocalDate())
			.allMatch(localDate -> !restaurant.isClosingDay(localDate));
	}
}