package com.prgms.allen.dining.web.domain.customer.reservation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.ReservationRepository;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCustomerInputCreateReq;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class CustomerReservationApiTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Test
	@DisplayName("고객은 식당 예약을 요청할 수 있다.")
	void request_reserve() throws Exception {
		// given
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));

		ReservationCreateReq reservationCreateReq = new ReservationCreateReq(
			restaurant.getId(),
			new ReservationCustomerInputCreateReq(
				LocalDateTime.of(
					LocalDate.now().plusDays(1),
					restaurant.getOpenTime()
				),
				2,
				"가지 빼주세요"
			)
		);

		// when & then
		mockMvc.perform(post("/customer/api/reservations?customerId=" + customer.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(reservationCreateReq)))
			.andExpect(status().isCreated())
			.andDo(print())
			.andDo(document("customer-reserve",
					requestParameters(
						parameterWithName("customerId").description("고객 식별자")
					),
					requestFields(
						fieldWithPath("restaurantId").description("식당 상태"),
						fieldWithPath("reservationCustomerInput.visitDateTime").description("방문할 날짜와 시간"),
						fieldWithPath("reservationCustomerInput.visitorCount").description("방문 인원수"),
						fieldWithPath("reservationCustomerInput.memo").description("예약자 메모")
					)
				)
			);
	}

	@Test
	@DisplayName("고객은 예약하려는 식당의 방문 날짜와 인원수를 고르면 식당의 예약 가능한 시간들을 확인할 수 있다.")
	void get_reservation_available_times() throws Exception {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));

		// when & then
		mockMvc.perform(get("/customer/api/reservations/available-times?restaurantId=" + restaurant.getId()
				+ "&date=" + LocalDate.now().plusDays(1L) + "&visitorCount=" + 2)
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-reservation-get-available-times",
				requestParameters(
					parameterWithName("restaurantId").description("식당 식별자"),
					parameterWithName("date").description("방문 날짜"),
					parameterWithName("visitorCount").description("방문객 수")
				),
				responseFields(
					fieldWithPath("availableTimes[]").description("예약 가능한 시간")
				))
			);
	}
}