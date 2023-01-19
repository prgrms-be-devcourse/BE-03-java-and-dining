package com.prgms.allen.dining.domain.restaurant;

import static org.assertj.core.api.Assertions.*;

import java.math.BigInteger;
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
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.exception.RestaurantDuplicateCreationException;

class RestaurantServiceTest {

	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	private final MemberRepository memberRepository = new FakeMemberRepository();
	private final MemberService memberService = new MemberService(memberRepository);
	private final RestaurantService restaurantService = new RestaurantService(
		restaurantRepository, memberService
	);

	private Member savedOwner;

	@BeforeEach
	void setUp() {
		final Member owner = new Member(
			"nickname",
			"익명",
			"01011112222",
			"qwer1234!",
			MemberType.OWNER);
		savedOwner = memberRepository.save(owner);
	}

	@Test
	@DisplayName("점주는 식당을 등록할 수 있다.")
	public void testSave() {
		// given
		RestaurantCreateReq restaurantCreateReq = createRestaurantCreateReq();

		// when
		restaurantService.save(restaurantCreateReq, savedOwner.getId());

		// then
		assertThat(restaurantRepository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("점주가 식당을 2개이상 생성하려할 경우 RestaurantDuplicateCreationException 을 던진다.")
	public void failSave() {
		// given
		RestaurantCreateReq restaurantCreateReq = createRestaurantCreateReq();
		RestaurantCreateReq restaurantCreateReq2 = createRestaurantCreateReq();

		restaurantService.save(restaurantCreateReq, savedOwner.getId());

		assertThatThrownBy(() -> restaurantService.save(restaurantCreateReq2, savedOwner.getId()))
			.isInstanceOf(RestaurantDuplicateCreationException.class);
	}

	@Test
	@DisplayName("식당의 상세정보를 조회할 수 있다.")
	public void testGetOneRestaurant() {
		// given
		Restaurant restaurant = createRestaurant();

		// when
		Restaurant findRestaurant = restaurantService.findRestaurantById(restaurant.getId());

		// then
		assertThat(restaurant)
			.usingRecursiveComparison()
			.isEqualTo(findRestaurant);
	}

	private Restaurant createRestaurant() {
		List<ClosingDay> closingDays = List.of(new ClosingDay(DayOfWeek.MONDAY));
		List<Menu> menu = List.of(new Menu("맛있는 밥", BigInteger.valueOf(10000), "맛있어용"));

		Restaurant restaurant = new Restaurant(
			savedOwner,
			FoodType.WESTERN,
			"유명한 레스토랑",
			30,
			LocalTime.of(12, 0),
			LocalTime.of(20, 0),
			"서울특별시 어딘구 어딘가로 222",
			"BTS가 다녀간 유명한 레스토랑",
			"023334444",
			menu,
			closingDays
		);
		return restaurantRepository.save(restaurant);
	}

	private RestaurantCreateReq createRestaurantCreateReq() {
		List<ClosingDayCreateReq> closingDayList = List.of(new ClosingDayCreateReq(DayOfWeek.MONDAY));
		List<MenuCreateReq> menuList = List.of(new MenuCreateReq("맛있는 밥", BigInteger.valueOf(10000), "맛있어용"));

		return new RestaurantCreateReq(
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
}