package com.prgms.allen.dining.domain.restaurant;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.restaurant.dto.ClosingDayCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.MenuCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateReq;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.global.error.exception.RestaurantDuplicateCreationException;

class RestaurantServiceTest {

	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	private final MemberRepository memberRepository = new FakeMemberRepository();
	private final MemberService memberService = new MemberService(memberRepository);
	private final RestaurantService restaurantService = new RestaurantService(
		restaurantRepository,
		memberService
	);

	private Member savedOwner;
	private List<ClosingDayCreateReq> closingDayList;
	private List<MenuCreateReq> menuList;
	private RestaurantCreateReq restaurantCreateReq;

	@BeforeEach
	void setUp() {
		final Member owner = new Member(
			"nickname",
			"익명",
			"01011112222",
			"qwer1234!",
			MemberType.OWNER);
		savedOwner = memberRepository.save(owner);

		closingDayList = List.of(new ClosingDayCreateReq(DayOfWeek.MONDAY));
		menuList = List.of(new MenuCreateReq("맛있는 밥", BigDecimal.valueOf(10000), "맛있어용"));

		restaurantCreateReq = new RestaurantCreateReq(
			FoodType.KOREAN,
			"유명 레스토랑",
			30,
			LocalTime.of(11, 0),
			LocalTime.of(21, 0),
			"서울특별시 강남구 어딘가로 123 무슨빌딩 1층",
			"우리는 유명한 한식당입니다.",
			"0211112222",
			menuList,
			closingDayList);
	}

	@Test
	@DisplayName("점주는 식당을 등록할 수 있다.")
	public void testSave() {
		System.out.println(savedOwner.getId());

		// when
		restaurantService.save(restaurantCreateReq, savedOwner.getId());

		// then
		assertThat(restaurantRepository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("점주가 식당을 2개이상 생성하려할 경우 RestaurantDuplicateCreationException 을 던진다.")
	public void failSave() {
		System.out.println(savedOwner.getId());

		final RestaurantCreateReq restaurantCreateReq2 = new RestaurantCreateReq(
			FoodType.KOREAN,
			"유명 레스토랑인척하는레스토랑",
			30,
			LocalTime.of(11, 0),
			LocalTime.of(21, 0),
			"서울특별시 강남구 어딘가로 123 무슨빌딩 1층",
			"우리는 유명한 한식당입니다.",
			"0211112222",
			menuList,
			closingDayList
		);

		restaurantService.save(restaurantCreateReq, savedOwner.getId());

		assertThatThrownBy(() -> restaurantService.save(restaurantCreateReq2, savedOwner.getId()))
			.isInstanceOf(RestaurantDuplicateCreationException.class);
	}
}