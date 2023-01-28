package com.prgms.allen.dining.web.domain.customer.restaurant;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantService;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateReq;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class CustomerRestaurantApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RestaurantService restaurantService;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@BeforeEach
	void setUp() {
		final List<Member> members = List.of(
			createOwner("nickName1"),
			createOwner("nickName2"),
			createOwner("nickName3"),
			createOwner("nickName4"),
			createOwner("nickName5")
		);

		restaurantSaveAll(restaurantCreateReq(), memberRepository.saveAll(members));
	}

	@Test
	@DisplayName("고객은 하나의 식당의 상세 정보를 조회할 수 있다.")
	void testGetMyRestaurant() throws Exception {

		Member owner = createOwner("nickname123");
		List<ClosingDay> closingDays = List.of(new ClosingDay(DayOfWeek.MONDAY));
		List<Menu> menu = List.of(new Menu("맛있는 밥", BigInteger.valueOf(10000), "맛있어용"));

		Restaurant restaurant = new Restaurant(
			owner,
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
		restaurant = restaurantRepository.save(restaurant);

		mockMvc.perform(
				get("/customer/api/restaurants/{restaurantId}", restaurant.getId()))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-get-one-restaurant",
				responseFields(
					fieldWithPath("foodType").type(JsonFieldType.STRING).description("음식 종류"),
					fieldWithPath("name").type(JsonFieldType.STRING).description("식당 이름"),
					fieldWithPath("openTime").type(JsonFieldType.STRING).description("식당 오픈 시간"),
					fieldWithPath("lastOrderTime").type(JsonFieldType.STRING).description("식당 라스트 오더 시간"),
					fieldWithPath("location").type(JsonFieldType.STRING).description("식당 주소"),
					fieldWithPath("description").type(JsonFieldType.STRING).description("식당 설명"),
					fieldWithPath("phone").type(JsonFieldType.STRING).description("식당 전화번호"),
					fieldWithPath("menuList").type(JsonFieldType.ARRAY).optional().description("메뉴 리스트"),
					fieldWithPath("menuList[].name").type(JsonFieldType.STRING).optional().description("메뉴 이름"),
					fieldWithPath("menuList[].price").type(JsonFieldType.NUMBER).optional().description("메뉴 가격"),
					fieldWithPath("closingDays").type(JsonFieldType.ARRAY).optional().description("휴무일 리스트"),
					fieldWithPath("closingDays[].dayOfWeek").optional().type(JsonFieldType.STRING)
						.description("휴무 요일")
				))
			);
	}

	@Test
	@DisplayName("구매자는 레스토랑의 목록을 페이징 조회할 수 있다")
	void getRestaurants() throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("page", "0");
		params.add("size", "3");

		mockMvc.perform(get("/customer/api/restaurants")
				.queryParams(params))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-get-restaurant-list",
				requestParameters(
					parameterWithName("page").description("페이지"),
					parameterWithName("size").description("페이지 사이즈")
				),
				responseFields(
					fieldWithPath("content[].foodType").description("음식 카테고리"),
					fieldWithPath("content[].restaurantName").description("레스토랑 이름"),
					fieldWithPath("content[].location").description("레스토랑 주소"),
					fieldWithPath("pageable").description(""),
					fieldWithPath("totalElements").description("전체 데이터 개수"),
					fieldWithPath("first").description("첫번째 페이지인지 여부"),
					fieldWithPath("last").description("마지막 페이지인지 여부"),
					fieldWithPath("totalPages").description("전체 페이지 개수"),
					fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 개수"),
					fieldWithPath("size").description("한 페이지당 조회할 데이터 개수"),
					fieldWithPath("number").description("현재 페이지 번호"),
					fieldWithPath("sort").description("정렬 기준"),
					fieldWithPath("sort.sorted").description("정렬 됐는지 여부"),
					fieldWithPath("sort.unsorted").description("정렬 안됐는지 여부"),
					fieldWithPath("sort.empty").description("데이터가 비었는지 여부"),
					fieldWithPath("empty").description("데이터가 비었는지 여부")
				)));
	}

	@Test
	@DisplayName("구매자는 검색한 단어가 포함된 이름을 가진 레스토랑들을 페이징 조회할 수 있다")
	void getRestaurantsContains() throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("restaurantName", "유명");
		params.add("page", "0");
		params.add("size", "2");

		mockMvc.perform(get("/customer/api/restaurants/search")
				.queryParams(params))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-get-restaurant-list-containing-name",
				requestParameters(
					parameterWithName("page").description("페이지"),
					parameterWithName("size").description("페이지 사이즈"),
					parameterWithName("restaurantName").description("검색할 레스토랑 이름")
				),
				responseFields(
					fieldWithPath("content[].foodType").description("음식 카테고리"),
					fieldWithPath("content[].restaurantName").description("레스토랑 이름"),
					fieldWithPath("content[].location").description("레스토랑 주소"),
					fieldWithPath("pageable").description(""),
					fieldWithPath("totalElements").description("전체 데이터 개수"),
					fieldWithPath("first").description("첫번째 페이지인지 여부"),
					fieldWithPath("last").description("마지막 페이지인지 여부"),
					fieldWithPath("totalPages").description("전체 페이지 개수"),
					fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 개수"),
					fieldWithPath("size").description("한 페이지당 조회할 데이터 개수"),
					fieldWithPath("number").description("현재 페이지 번호"),
					fieldWithPath("sort").description("정렬 기준"),
					fieldWithPath("sort.sorted").description("정렬 됐는지 여부"),
					fieldWithPath("sort.unsorted").description("정렬 안됐는지 여부"),
					fieldWithPath("sort.empty").description("데이터가 비었는지 여부"),
					fieldWithPath("empty").description("데이터가 비었는지 여부")
				)));
	}

	@Test
	@DisplayName("구매자는 특정 레스토랑의 메뉴 리스트를 조회할 수 있다")
	void getMenus() throws Exception {
		Member owner = memberRepository.save(createOwner("주인장"));
		Restaurant savedRestaurant = restaurantRepository.save(createRestaurant(owner));
		long restaurantId = savedRestaurant.getId();

		mockMvc.perform(get(MessageFormat.format(
				"/customer/api/restaurants/{0}/menu", restaurantId)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-get-menus",
				responseFields(
					fieldWithPath("content[].name").description("메뉴 이름"),
					fieldWithPath("content[].price").description("메뉴 가격"),
					fieldWithPath("content[].description").description("메뉴 설명"),
					fieldWithPath("pageable").description(""),
					fieldWithPath("totalElements").description("전체 데이터 개수"),
					fieldWithPath("first").description("첫번째 페이지인지 여부"),
					fieldWithPath("last").description("마지막 페이지인지 여부"),
					fieldWithPath("totalPages").description("전체 페이지 개수"),
					fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 개수"),
					fieldWithPath("size").description("한 페이지당 조회할 데이터 개수"),
					fieldWithPath("number").description("현재 페이지 번호"),
					fieldWithPath("sort").description("정렬 기준"),
					fieldWithPath("sort.sorted").description("정렬 됐는지 여부"),
					fieldWithPath("sort.unsorted").description("정렬 안됐는지 여부"),
					fieldWithPath("sort.empty").description("데이터가 비었는지 여부"),
					fieldWithPath("empty").description("데이터가 비었는지 여부")
				)));
	}

	private Member createOwner(String nickName) {

		return new Member(
			nickName,
			"익명",
			"01011112222",
			"qwer1234!",
			MemberType.OWNER);
	}

	private Restaurant createRestaurant(Member member) {
		return new Restaurant(
			member,
			FoodType.KOREAN,
			"돼지국밥 맛난집",
			40,
			LocalTime.of(8, 0),
			LocalTime.of(20, 0),
			"경기도 어딘가",
			"",
			"01011112222",
			createMenuList(),
			Collections.emptyList()
		);
	}

	private List<Menu> createMenuList() {
		Menu menu1 = new Menu("라면", BigInteger.valueOf(1500), "너구리 한마리 몰고가세용~");
		Menu menu2 = new Menu("감자칩", BigInteger.valueOf(1500), "감자칩의 근본 포카칩");
		Menu menu3 = new Menu("계란찜", BigInteger.valueOf(1500), "닭발과 최강 조합");
		Menu menu4 = new Menu("짜장면", BigInteger.valueOf(1500), "오늘은 내가 요리사~");
		Menu menu5 = new Menu("파스타", BigInteger.valueOf(1500), "봉골레 파스타 하나!");

		return List.of(menu1, menu2, menu3, menu4, menu5);
	}

	private void restaurantSaveAll(RestaurantCreateReq createReq, List<Member> members) {

		for (Member member : members) {
			restaurantService.save(createReq, member.getId());
		}
	}

	private RestaurantCreateReq restaurantCreateReq() {

		return new RestaurantCreateReq(
			FoodType.KOREAN,
			"유명 레스토랑",
			30,
			LocalTime.of(11, 0),
			LocalTime.of(21, 0),
			"서울특별시 강남구 어딘가로 123 무슨빌딩 1층",
			"우리는 유명한 한식당입니다.",
			"0211112222",
			null,
			null);
	}

}