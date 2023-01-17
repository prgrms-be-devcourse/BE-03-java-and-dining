package com.prgms.allen.dining.web.owner;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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
import com.prgms.allen.dining.domain.member.dto.MemberSignupRequest;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.restaurant.dto.ClosingDayCreateRequest;
import com.prgms.allen.dining.domain.restaurant.dto.MenuCreateRequest;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateRequest;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class OwnerRestaurantApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private static Member owner;

	@Test
	@DisplayName("점주는 식당을 하나 등록할 수 있다.")
	void testSave() throws Exception {

		owner = createOwner();
		mockMvc.perform(post("/owner/api/restaurants?ownerId=" + owner.getId())
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
					fieldWithPath("openTime").type(JsonFieldType.ARRAY).description("restaurant open time"),
					fieldWithPath("lastOrderTime").type(JsonFieldType.ARRAY).description("restaurant last order time"),
					fieldWithPath("location").type(JsonFieldType.STRING).description("restaurant location"),
					fieldWithPath("description").type(JsonFieldType.STRING).description("restaurant description"),
					fieldWithPath("phone").type(JsonFieldType.STRING).description("restaurant phone"),
					fieldWithPath("menuList").type(JsonFieldType.ARRAY).description("menu list"),
					fieldWithPath("menuList[].name").type(JsonFieldType.STRING).description("menu name"),
					fieldWithPath("menuList[].price").type(JsonFieldType.NUMBER).description("menu price"),
					fieldWithPath("menuList[].description").type(JsonFieldType.STRING).description("menu description"),
					fieldWithPath("closingDays").type(JsonFieldType.ARRAY).description("closing days"),
					fieldWithPath("closingDays[].dayOfWeek").type(JsonFieldType.STRING)
						.description("closing day of week")
				))
			);
	}

	private Member createOwner() {
		String nickName = "이세상에제일가는짱구";
		MemberSignupRequest memberSignupRequest =
			new MemberSignupRequest(
				nickName,
				"짱구",
				"01011112222",
				"1q2w3e4r!",
				MemberType.OWNER);

		memberRepository.save(memberSignupRequest.toEntity());
		return memberRepository.findAll()
			.stream()
			.filter(member -> nickName.equals(member.getNickname()))
			.findAny()
			.get();
	}

	private RestaurantCreateRequest restaurantCreateResource() {

		List<ClosingDayCreateRequest> closingDayList = List.of(new ClosingDayCreateRequest(DayOfWeek.MONDAY));
		List<MenuCreateRequest> menuList = List.of(
			new MenuCreateRequest("맛있는 밥", BigDecimal.valueOf(10000), "맛있어용"),
			new MenuCreateRequest("맛있는 국", BigDecimal.valueOf(20000), "맛있어용")
		);

		return new RestaurantCreateRequest(
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