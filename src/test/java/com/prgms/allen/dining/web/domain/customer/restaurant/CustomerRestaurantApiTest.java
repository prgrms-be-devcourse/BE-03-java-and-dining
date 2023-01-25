package com.prgms.allen.dining.web.domain.customer.restaurant;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;
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
	private RestaurantRepository restaurantRepository;

	@Autowired
	private RestaurantService restaurantService;
	
	

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
					fieldWithPath("foodType").type(JsonFieldType.STRING).description("food type"),
					fieldWithPath("name").type(JsonFieldType.STRING).description("restaurant name"),
					fieldWithPath("openTime").type(JsonFieldType.STRING).description("restaurant open time"),
					fieldWithPath("lastOrderTime").type(JsonFieldType.STRING).description("restaurant last order time"),
					fieldWithPath("location").type(JsonFieldType.STRING).description("restaurant location"),
					fieldWithPath("description").type(JsonFieldType.STRING).description("restaurant description"),
					fieldWithPath("phone").type(JsonFieldType.STRING).description("restaurant phone"),
					fieldWithPath("menuList").type(JsonFieldType.ARRAY).optional().description("menu list"),
					fieldWithPath("menuList[].name").type(JsonFieldType.STRING).optional().description("menu name"),
					fieldWithPath("menuList[].price").type(JsonFieldType.NUMBER).optional().description("menu price"),
					fieldWithPath("closingDays").type(JsonFieldType.ARRAY).optional().description("closing days"),
					fieldWithPath("closingDays[].dayOfWeek").optional().type(JsonFieldType.STRING)
						.description("closing day of week")
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

	private Member createOwner(String nickName) {

		return new Member(
			nickName,
			"익명",
			"01011112222",
			"qwer1234!",
			MemberType.OWNER);
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