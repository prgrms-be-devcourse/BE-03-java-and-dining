package com.prgms.allen.dining.web.domain.customer.reservation;

import static com.prgms.allen.dining.domain.reservation.entity.ReservationStatus.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCustomerInputCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationStatusUpdateReq;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.entity.VisitStatus;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
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

	@ParameterizedTest
	@CsvSource({"PLANNED", "DONE", "CANCEL"})
	@DisplayName("고객은 자신의 예약을 상태별로 조회할 수 있다.")
	void getReservations(String status) throws Exception {
		// given
		VisitStatus visitStatus = VisitStatus.valueOf(status);
		List<ReservationStatus> statuses = visitStatus.getStatuses();
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;
		statuses.forEach(reservationStatus -> reservationRepository.save(DummyGenerator.createReservation(
			customer,
			restaurant,
			reservationStatus,
			customerInput
		)));

		String page = "0";
		String size = "5";

		// when && then
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
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;
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
						fieldWithPath("reservationInfoResForCustomer").type(JsonFieldType.OBJECT).description("예약 정보"),
						fieldWithPath("reservationInfoResForCustomer.customerName").type(JsonFieldType.STRING)
							.description("예약자 이름"),
						fieldWithPath("reservationInfoResForCustomer.phone").type(JsonFieldType.STRING)
							.description("예약자 핸드폰 번호"),
						fieldWithPath("reservationInfoResForCustomer.visitDateTime").type(JsonFieldType.STRING)
							.description("예약한 날짜"),
						fieldWithPath("reservationInfoResForCustomer.visitorCount").type(JsonFieldType.NUMBER)
							.description("예약한 시간"),
						fieldWithPath("reservationInfoResForCustomer.memo").type(JsonFieldType.STRING)
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

	@Test
	@DisplayName("고객은 자신의 예약을 취소할 수 있다.")
	void cancel_reservation() throws Exception {
		// given
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		ReservationCustomerInput customerInput = new ReservationCustomerInput(
			LocalDate.now()
				.plusDays(1),
			LocalTime.now()
				.truncatedTo(ChronoUnit.HOURS),
			2
		);
		Reservation reservation = reservationRepository.save(new Reservation(customer, restaurant, customerInput));

		ReservationStatusUpdateReq statusUpdateReq = new ReservationStatusUpdateReq(CANCELLED);

		// when & then
		mockMvc.perform(patch("/customer/api/reservations/{reservationId}?", reservation.getId())
				.param("customerId", customer.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-reservation-update-status-cancel",
				pathParameters(
					parameterWithName("reservationId").description("예약 식별자")
				),
				requestParameters(
					parameterWithName("customerId").description("고객 식별자")
				),
				requestFields(
					fieldWithPath("status").description("변경할 상태")
				))
			);
	}

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