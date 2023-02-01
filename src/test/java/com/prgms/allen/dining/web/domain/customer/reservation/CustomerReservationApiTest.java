package com.prgms.allen.dining.web.domain.customer.reservation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class CustomerReservationApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Test
	@DisplayName("고객은 하나의 식당의 상세 정보를 조회할 수 있다.")
	void testGetMyRestaurant() throws Exception {

		Member owner = memberRepository.save(DummyGenerator.OWNER);

		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurantWith2Capacity(owner));

		mockMvc.perform(
				get("/customer/api/reservations/available-dates?restaurantId=" + restaurant.getId()))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-get-available-dates-restaurant",
				responseFields(
					fieldWithPath("availableDates[]").type(JsonFieldType.ARRAY).description("예약 가능 날짜 리스트")
				))
			);
	}
}