package com.prgms.allen.dining.web.domain.owner.restaurant;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.dto.MemberSignupReq;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.dto.ClosingDayCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.MenuCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateReq;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.JwtGenerator;
import com.prgms.allen.dining.security.config.HeaderValue;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class OwnerRestaurantApiTest {

	private static Member owner;
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private JwtGenerator jwtGenerator;

	@Test
	@DisplayName("점주는 식당을 하나 등록할 수 있다.")
	void testSave() throws Exception {

		owner = createOwner();
		mockMvc.perform(post("/owner/api/restaurants")
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(owner))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.registerModule(new JavaTimeModule())
					.writeValueAsString(restaurantCreateResource())))
			.andExpect(status().isCreated())
			.andDo(print())
			.andDo(document("owner-create-restaurant",
				requestFields(
					fieldWithPath("foodType").type(JsonFieldType.STRING).description("food type"),
					fieldWithPath("name").type(JsonFieldType.STRING).description("restaurant name"),
					fieldWithPath("capacity").type(JsonFieldType.NUMBER).description("restaurant capacity"),
					fieldWithPath("openTime").type(JsonFieldType.STRING).description("restaurant open time"),
					fieldWithPath("lastOrderTime").type(JsonFieldType.STRING).description("restaurant last order time"),
					fieldWithPath("location").type(JsonFieldType.STRING).description("restaurant location"),
					fieldWithPath("description").type(JsonFieldType.STRING).description("restaurant description"),
					fieldWithPath("phone").type(JsonFieldType.STRING).description("restaurant phone"),
					fieldWithPath("menuList").type(JsonFieldType.ARRAY).optional().description("menu list"),
					fieldWithPath("menuList[].name").type(JsonFieldType.STRING).optional().description("menu name"),
					fieldWithPath("menuList[].price").type(JsonFieldType.NUMBER).optional().description("menu price"),
					fieldWithPath("menuList[].description").type(JsonFieldType.STRING).optional()
						.description("menu description"),
					fieldWithPath("closingDays").type(JsonFieldType.ARRAY).optional().description("closing days"),
					fieldWithPath("closingDays[].dayOfWeek").optional().type(JsonFieldType.STRING)
						.description("closing day of week")
				))
			);
	}

	@Test
	@DisplayName("점주는 자신의 식당을 조회할 수 있다.")
	void testGetMyRestaurant() throws Exception {

		Member owner = createOwner();
		Restaurant restaurant = createRestaurant(owner);

		mockMvc.perform(get("/owner/api/restaurants/{restaurantId}", restaurant.getId())
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(owner)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("owner-get-owned-restaurant",
				responseFields(
					fieldWithPath("foodType").type(JsonFieldType.STRING).description("food type"),
					fieldWithPath("name").type(JsonFieldType.STRING).description("restaurant name"),
					fieldWithPath("capacity").type(JsonFieldType.NUMBER).description("restaurant capacity"),
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

	private Member createOwner() {
		String nickName = "이세상에제일가는짱구";
		MemberSignupReq memberSignupReq =
			new MemberSignupReq(
				nickName,
				"짱구",
				"01011112222",
				"1q2w3e4r!",
				MemberType.OWNER);

		memberRepository.save(memberSignupReq.toEntity());
		return memberRepository.findAll()
			.stream()
			.filter(member -> member.getNickname().equals(nickName))
			.findAny()
			.get();
	}

	private RestaurantCreateReq restaurantCreateResource() {

		List<ClosingDayCreateReq> closingDayList = List.of(new ClosingDayCreateReq(DayOfWeek.MONDAY));
		List<MenuCreateReq> menuList = List.of(
			new MenuCreateReq("맛있는 밥", BigInteger.valueOf(10000), "맛있어용"),
			new MenuCreateReq("맛있는 국", BigInteger.valueOf(20000), "맛있어용")
		);

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

	private Restaurant createRestaurant(Member owner) {
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
		return restaurantRepository.save(restaurant);
	}
}