package com.prgms.allen.dining.domain.restaurant;

import static org.assertj.core.api.Assertions.*;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.restaurant.dto.ClosingDayCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.MenuCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.MenuDetailRes;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantAvailableDatesRes;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantSimpleRes;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
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
		menuList = List.of(new MenuCreateReq("맛있는 밥", BigInteger.valueOf(10000), "맛있어용"));

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

		// when
		restaurantService.save(restaurantCreateReq, savedOwner.getId());

		// then
		assertThat(restaurantRepository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("점주가 식당을 2개이상 생성하려할 경우 RestaurantDuplicateCreationException 을 던진다.")
	public void failSave() {

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

	@Test
	@DisplayName("구매자는 레스토랑의 목록을 페이징 조회할 수 있다")
	public void getRestaurantList() {

		//Given
		final List<Member> members = List.of(
			createOwner("nickName1"),
			createOwner("nickName2"),
			createOwner("nickName3"),
			createOwner("nickName4"),
			createOwner("nickName5")
		);

		restaurantSaveAll(restaurantCreateReq, memberRepository.saveAll(members));
		final Pageable pageable = PageRequest.of(0, 2);
		final Page<Restaurant> expectRestaurantSimpleRes = restaurantRepository.findAll(pageable);

		//When
		final Page<RestaurantSimpleRes> actualRestaurantList = restaurantService.getRestaurantList(pageable);

		//Then
		assertThat(actualRestaurantList).hasSize(expectRestaurantSimpleRes.getSize());
	}

	@Test
	@DisplayName("식당의 상세정보를 조회할 수 있다.")
	public void testGetOneRestaurant() {
		// given
		Restaurant restaurant = createRestaurant(savedOwner);

		// when
		Restaurant findRestaurant = restaurantService.findById(restaurant.getId());

		// then
		assertThat(restaurant)
			.usingRecursiveComparison()
			.isEqualTo(findRestaurant);
	}

	@Test
	@DisplayName("구매자는 검색한 단어가 포함된 이름을 가진 레스토랑들을 페이징 조회할 수 있다")
	void getRestaurantsContaining() {

		//Given
		final String filterName = "유명";
		final Pageable pageable = PageRequest.of(0, 2);

		final List<Restaurant> expectRestaurants = restaurantRepository.findAll()
			.stream()
			.filter(restaurant -> restaurant.getName().contains(filterName))
			.toList();

		//When
		final Page<RestaurantSimpleRes> actualRestaurants = restaurantService.getRestaurantsContains(pageable,
			filterName);

		//Then
		assertThat(actualRestaurants).hasSize(expectRestaurants.size());
	}

	@Test
	@DisplayName("구매자는 특정 레스토랑의 메뉴리스트를 조회할 수 있다")
	void getMenus() {

		//given
		final int pageSize = 2;
		final Member owner = memberRepository.save(createOwner("슈크림"));
		final Restaurant savedRestaurant = restaurantRepository.save(createRestaurant(owner));
		final Pageable page = PageRequest.of(0, pageSize);
		final List<MenuDetailRes> expectMenus = createMenuList()
			.stream()
			.map(MenuDetailRes::new)
			.toList();

		//when
		final Page<MenuDetailRes> actualMenus = restaurantService.getMenus(page, savedRestaurant.getId());

		//then
		assertThat(actualMenus.getSize()).isEqualTo(pageSize);
		assertThat(expectMenus).containsAll(actualMenus);
	}

	@Test
	@DisplayName("휴무일을 제외한 이용가능한 날짜 목록을 받을 수 있다.")
	public void testGetAvailableDates() {
		// given
		Member owner = createOwner("nickname123");
		Restaurant restaurant = createRestaurant(owner);

		// when
		RestaurantAvailableDatesRes restaurantAvailableDatesRes =
			restaurantService.getAvailableReserveDates(restaurant.getId());

		// then
		assertThat(restaurantAvailableDatesRes.canReserveDates()
			.stream()
			.noneMatch(localDate ->
				restaurant.getAllClosingDayOfWeek()
					.contains(localDate.getDayOfWeek())))
			.isTrue();
	}

	private Member createOwner(String nickName) {
		return new Member(
			nickName,
			"익명",
			"01011112222",
			"qwer1234!",
			MemberType.OWNER);
	}

	private Restaurant createRestaurant(Member owner) {
		Restaurant restaurant = new Restaurant(
			owner,
			FoodType.KOREAN,
			"편의점",
			20,
			LocalTime.of(11, 0),
			LocalTime.of(20, 0),
			"경기도 용인시",
			"앨런팀은 지금 배가 고프다",
			"01011111111",
			createMenuList(),
			Collections.emptyList()
		);
		return restaurantRepository.save(restaurant);
	}

	private void restaurantSaveAll(RestaurantCreateReq createReq, List<Member> members) {
		for (Member member : members) {
			restaurantService.save(createReq, member.getId());
		}
	}

	private List<Menu> createMenuList() {
		final Menu menu1 = new Menu("라면", BigInteger.valueOf(1500), "너구리 한마리 몰고가세용~");
		final Menu menu2 = new Menu("감자칩", BigInteger.valueOf(1500), "감자칩의 근본 포카칩");
		final Menu menu3 = new Menu("계란찜", BigInteger.valueOf(1500), "닭발과 최강 조합");
		final Menu menu4 = new Menu("짜장면", BigInteger.valueOf(1500), "오늘은 내가 요리사~");
		final Menu menu5 = new Menu("파스타", BigInteger.valueOf(1500), "봉골레 파스타 하나!");

		return List.of(menu1, menu2, menu3, menu4, menu5);
	}

}