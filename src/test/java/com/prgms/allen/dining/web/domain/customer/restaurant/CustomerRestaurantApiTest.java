package com.prgms.allen.dining.web.domain.customer.restaurant;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigInteger;
import java.text.MessageFormat;
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
					parameterWithName("page").description("pageable page"),
					parameterWithName("size").description("pageable size")
				),
				responseFields(
					fieldWithPath("content[].foodType").description("food type"),
					fieldWithPath("content[].restaurantName").description("restaurant Name"),
					fieldWithPath("content[].location").description("restaurant location"),
					fieldWithPath("pageable").description("pageable"),
					fieldWithPath("totalElements").description("totalElements"),
					fieldWithPath("first").description("first"),
					fieldWithPath("last").description("last"),
					fieldWithPath("totalPages").description("totalPages"),
					fieldWithPath("numberOfElements").description("numberOfElements"),
					fieldWithPath("size").description("size"),
					fieldWithPath("number").description("number"),
					fieldWithPath("sort").description("sort"),
					fieldWithPath("sort.sorted").description("sort sorted"),
					fieldWithPath("sort.unsorted").description("sort unsorted"),
					fieldWithPath("sort.empty").description("sort empty"),
					fieldWithPath("empty").description("empty")
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
					parameterWithName("page").description("pageable page"),
					parameterWithName("size").description("pageable size"),
					parameterWithName("restaurantName").description("search keyword(restaurant name)")
				),
				responseFields(
					fieldWithPath("content[].foodType").description("food type"),
					fieldWithPath("content[].restaurantName").description("restaurant Name"),
					fieldWithPath("content[].location").description("restaurant location"),
					fieldWithPath("pageable").description("pageable"),
					fieldWithPath("totalElements").description("totalElements"),
					fieldWithPath("first").description("first"),
					fieldWithPath("last").description("last"),
					fieldWithPath("totalPages").description("totalPages"),
					fieldWithPath("numberOfElements").description("numberOfElements"),
					fieldWithPath("size").description("size"),
					fieldWithPath("number").description("number"),
					fieldWithPath("sort").description("sort"),
					fieldWithPath("sort.sorted").description("sort sorted"),
					fieldWithPath("sort.unsorted").description("sort unsorted"),
					fieldWithPath("sort.empty").description("sort empty"),
					fieldWithPath("empty").description("empty")
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
			.andDo(document("customer-get-menus"));
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
