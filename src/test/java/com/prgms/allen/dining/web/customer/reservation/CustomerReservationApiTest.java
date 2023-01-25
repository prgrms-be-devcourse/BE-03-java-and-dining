package com.prgms.allen.dining.web.customer.reservation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.ReservationRepository;
import com.prgms.allen.dining.domain.reservation.dto.VisitStatus;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class CustomerReservationApiTest {

	private static Member owner;
	private static Member customer;
	private static Restaurant restaurant;
	private static ReservationCustomerInput customerInput;
	private static String page;
	private static String size;
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private ReservationRepository reservationRepository;

	@ParameterizedTest
	@CsvSource({"PLANNED", "DONE", "CANCEL"})
	@DisplayName("고객은 자신의 예약을 상태별로 조회할 수 있다.")
	void getReservations(String status) throws Exception {
		// given
		VisitStatus visitStatus = VisitStatus.valueOf(status);
		List<ReservationStatus> statuses = visitStatus.getStatuses();
		owner = memberRepository.save(DummyGenerator.OWNER);
		customer = memberRepository.save(DummyGenerator.CUSTOMER);
		restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		customerInput = DummyGenerator.CUSTOMER_INPUT;
		statuses.forEach(reservationStatus -> reservationRepository.save(DummyGenerator.createReservation(
			customer,
			restaurant,
			reservationStatus,
			customerInput
		)));

		page = "0";
		size = "5";

		// when && then
		// mockMvc.perform(get("/customer/api/reservations?status=" + status + "&customerId=" + customer.getId())

		mockMvc.perform(get("/customer/api/reservations")
				.param("status", status)
				.param("customerId", customer.getId().toString())
				.param("page", page)
				.param("size", size)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(
				document("reservation-getAllByCustomer",
					requestParameters(
						parameterWithName("status").description("조회할 상태"),
						parameterWithName("customerId").description("조회하는 구매자 아이디"),
						parameterWithName("page").description("페이지 번호"),
						parameterWithName("size").description("한 페이지 당 조회 개수")
					),
					responseFields(
						fieldWithPath("content[]").type(JsonFieldType.ARRAY).description("조회된 예약들"),
						fieldWithPath("content[].restaurantName").type(JsonFieldType.STRING).description("식당 이름"),
						fieldWithPath("content[].address").type(JsonFieldType.STRING).description("식당 주소"),
						fieldWithPath("content[].visitDateTime").type(JsonFieldType.STRING).description("예약한 시간 및 날짜"),
						fieldWithPath("content[].visitorCount").type(JsonFieldType.NUMBER).description("방문 인원수"),

						fieldWithPath("pageable").type(JsonFieldType.STRING).description(""),
						fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 개수"),
						fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지인지 여부"),
						fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
						fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
						fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("요청 페이지에서 조회된 데이터 개수"),
						fieldWithPath("size").type(JsonFieldType.NUMBER).description("한 페이지당 조회할 데이터 개수"),
						fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
						fieldWithPath("sort").type(JsonFieldType.OBJECT).description("정렬 기준"),
						fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
						fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
						fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),
						fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부")
					)
				)
			);
	}

	@Test
	@DisplayName("고객의 예약 상세 조회")
	public void getReservationDetail() throws Exception {
		// given
		owner = memberRepository.save(DummyGenerator.OWNER);
		customer = memberRepository.save(DummyGenerator.CUSTOMER);
		restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		customerInput = DummyGenerator.CUSTOMER_INPUT;
		Reservation reservation = reservationRepository.save(DummyGenerator.createReservation(
			customer,
			restaurant,
			ReservationStatus.PENDING,
			customerInput
		));

		// when & then
		mockMvc.perform(get("/customer/api/reservations/{reservationId}", reservation.getId())
				.param("customerId", customer.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(
				document("reservation-getAllByCustomer",
					pathParameters(
						parameterWithName("reservationId").description("조회할 상태")
					),
					requestParameters(
						parameterWithName("customerId").description("조회하는 구매자 아이디")
					),
					responseFields(
						fieldWithPath("reservationInfoRes").type(JsonFieldType.OBJECT).description("예약 정보"),
						fieldWithPath("reservationInfoRes.customerName").type(JsonFieldType.STRING)
							.description("예약자 이름"),
						fieldWithPath("reservationInfoRes.phone").type(JsonFieldType.STRING).description("예약자 핸드폰 번호"),
						fieldWithPath("reservationInfoRes.visitDateTime").type(JsonFieldType.STRING)
							.description("예약한 날짜"),
						fieldWithPath("reservationInfoRes.visitorCount").type(JsonFieldType.NUMBER)
							.description("예약한 시간"),
						fieldWithPath("reservationInfoRes.memo").type(JsonFieldType.STRING)
							.optional()
							.description("예약 메모"),

						fieldWithPath("restaurantInfoRes").type(JsonFieldType.OBJECT).description("정보"),
						fieldWithPath("restaurantInfoRes.name").type(JsonFieldType.STRING).description("식당 이름"),
						fieldWithPath("restaurantInfoRes.location").type(JsonFieldType.STRING).description("식당 위치"),
						fieldWithPath("restaurantInfoRes.phone").type(JsonFieldType.STRING).description("식당 전화번호")
					)
				)
			);
	}

}