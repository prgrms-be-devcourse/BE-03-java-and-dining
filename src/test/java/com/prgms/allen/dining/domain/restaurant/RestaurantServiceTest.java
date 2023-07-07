package com.prgms.allen.dining.domain.restaurant;

import static com.prgms.allen.dining.generator.DummyGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.fake.FakeMember;
import com.prgms.allen.dining.domain.fake.FakeRestaurant;
import com.prgms.allen.dining.domain.member.FakeMemberRepository;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.FakeReservationRepository;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.reservation.service.ReservationProvider;
import com.prgms.allen.dining.domain.restaurant.dto.ClosingDayCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.MenuCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.MenuDetailRes;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantSimpleRes;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

@ExtendWith(MockitoExtension.class)
@Transactional
class RestaurantServiceTest {

	@Mock
	private RestaurantRepository restaurantRepository;

	@Mock
	private MemberService memberService;

	@Mock
	private ReservationProvider reservationProvider;

	@InjectMocks
	private RestaurantService restaurantService;

	private final MemberRepository memberRepository = new FakeMemberRepository();
	private final ReservationRepository reservationRepository = new FakeReservationRepository();

	private Member savedOwner;
	private List<ClosingDayCreateReq> closingDayList;
	private List<MenuCreateReq> menuList;
	private RestaurantCreateReq restaurantCreateReq;

	@BeforeEach
	void setUp() {
		savedOwner = new FakeMember(DummyGenerator.createOwner(), 1L);
		restaurantCreateReq = DummyGenerator.createRestaurantCreateReq();
	}

	@Test
	@DisplayName("점주는 식당을 등록할 수 있다.")
	public void testSave() {
		Long restaurantId = 1L;
		Restaurant restaurant = new FakeRestaurant(restaurantId, createRestaurant(savedOwner));

		when(memberService.findOwnerById(anyLong())).thenReturn(savedOwner);
		when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

		// when
		Long savedId = restaurantService.save(restaurantCreateReq, savedOwner.getId());

		// then
		verify(memberService).findOwnerById(anyLong());
		verify(restaurantRepository).save(any(Restaurant.class));

		assertThat(savedId).isEqualTo(restaurantId);
	}

	@Test
	@DisplayName("점주가 식당을 2개이상 생성하려할 경우 RestaurantDuplicateCreationException 을 던진다.")
	public void failSave() {
		final RestaurantCreateReq restaurantCreateReq2 = createRestaurantCreateReq();

		when(restaurantRepository.existsRestaurantByOwnerId(savedOwner.getId())).thenReturn(true);

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

		final Pageable pageable = PageRequest.of(0, 2);
		List<Restaurant> restaurants = restaurantSaveAll(restaurantCreateReq, members);
		PageImpl page = new PageImpl(restaurants, pageable, 2);

		when(restaurantRepository.findAll(pageable)).thenReturn(page);
		when(reservationProvider.getAvailableTimes(any(RestaurantOperationInfo.class)))
			.thenReturn(new ArrayList<>());

		//When
		final Page<RestaurantSimpleRes> actualRestaurantList = restaurantService.getRestaurantList(pageable);

	}

	// @Test
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

	// @Test
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

	// @Test
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

	// private Restaurant createRestaurant(Member owner) {
	// 	Restaurant restaurant = new Restaurant(
	// 		owner,
	// 		FoodType.KOREAN,
	// 		"편의점",
	// 		20,
	// 		LocalTime.of(11, 0),
	// 		LocalTime.of(20, 0),
	// 		"경기도 용인시",
	// 		"앨런팀은 지금 배가 고프다",
	// 		"01011111111",
	// 		createMenuList(),
	// 		Collections.emptyList()
	// 	);
	// 	return restaurantRepository.save(restaurant);
	// }

	private List<Restaurant> restaurantSaveAll(RestaurantCreateReq createReq, List<Member> members) {
		List<Restaurant> restaurants = new ArrayList<>();
		for (long id = 1L; id <= members.size(); id++) {
			restaurants.add(new FakeRestaurant(id, createRestaurant(members.get((int)id))));
		}

		return restaurants;
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