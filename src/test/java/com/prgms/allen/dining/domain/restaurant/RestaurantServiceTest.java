package com.prgms.allen.dining.domain.restaurant;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.restaurant.dto.ClosingDayCreateRequest;
import com.prgms.allen.dining.domain.restaurant.dto.MenuCreateRequest;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateRequest;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.global.error.exception.RestaurantDuplicateCreationException;

@Transactional
class RestaurantServiceTest {

	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	private final FakeMemberRepository customerRepository = new FakeMemberRepository();
	private final MemberService memberService = new MemberService(customerRepository);
	private final RestaurantService restaurantService = new RestaurantService(restaurantRepository, memberService);

	private Member savedOwner;
	private List<ClosingDayCreateRequest> closingDayList;
	private List<MenuCreateRequest> menuList;
	private RestaurantCreateRequest restaurantCreateRequest;

	@BeforeEach
	void setUp() {
		final Member owner = new Member(
			"nickname",
			"익명",
			"01011112222",
			"qwer1234!",
			MemberType.OWNER);
		savedOwner = customerRepository.save(owner);

		closingDayList = List.of(new ClosingDayCreateRequest(DayOfWeek.MONDAY));
		menuList = List.of(new MenuCreateRequest("맛있는 밥", BigDecimal.valueOf(10000), "맛있어용"));

		restaurantCreateRequest = new RestaurantCreateRequest(
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

	@AfterEach
	void clean() {
		customerRepository.deleteAll();
		restaurantRepository.deleteAll();
	}

	@Test
	@DisplayName("점주는 식당을 등록할 수 있다.")
	public void testSave() {

		// when
		restaurantService.save(restaurantCreateRequest, savedOwner.getId());

		// then
		assertThat(restaurantRepository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("점주가 식당을 2개이상 생성하려할 경우 RestaurantDuplicateCreationException 을 던진다.")
	public void failSave() {

		final RestaurantCreateRequest restaurantCreateRequest2 = new RestaurantCreateRequest(
			FoodType.KOREAN,
			"유명 레스토랑",
			30,
			LocalTime.of(11, 0),
			LocalTime.of(21, 0),
			"서울특별시 강남구 어딘가로 123 무슨빌딩 1층",
			"우리는 유명한 한식당입니다.",
			"0211112222",
			menuList,
			closingDayList
		);

		restaurantService.save(restaurantCreateRequest, savedOwner.getId());

		assertThatThrownBy(() -> restaurantService.save(restaurantCreateRequest2, savedOwner.getId()))
			.isInstanceOf(RestaurantDuplicateCreationException.class);
	}
}