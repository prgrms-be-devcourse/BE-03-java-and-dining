package com.prgms.allen.dining.api.customer.reservation;

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
import com.prgms.allen.dining.generator.JwtGenerator;
import com.prgms.allen.dining.security.config.HeaderValue;

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

	@Autowired
	private JwtGenerator jwtGenerator;

	@Test
	@DisplayName("????????? ?????? ????????? ????????? ??? ??????.")
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
				"?????? ????????????"
			)
		);

		// when & then
		mockMvc.perform(post("/customer/api/reservations")
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(customer))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(reservationCreateReq)))
			.andExpect(status().isCreated())
			.andDo(print())
			.andDo(document("customer-reserve",
				requestFields(
					fieldWithPath("restaurantId").description("?????? ??????"),
					fieldWithPath("reservationCustomerInput.visitDateTime").description("????????? ????????? ??????"),
					fieldWithPath("reservationCustomerInput.visitorCount").description("?????? ?????????"),
					fieldWithPath("reservationCustomerInput.memo").description("????????? ??????")
					)
				)
			);
	}

	@Test
	@DisplayName("????????? ??????????????? ????????? ?????? ????????? ???????????? ????????? ????????? ?????? ????????? ???????????? ????????? ??? ??????.")
	void get_reservation_available_times() throws Exception {
		// given
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));

		// when & then
		String restaurantId = String.valueOf(restaurant.getId());
		String date = LocalDate.now().plusDays(1L).toString();
		String visitorCount = "2";

		mockMvc.perform(get("/customer/api/reservations/available-times")
				.param("restaurantId", restaurantId)
				.param("date", date)
				.param("visitorCount", visitorCount)
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-reservation-get-available-times",
				requestParameters(
					parameterWithName("restaurantId").description("?????? ?????????"),
					parameterWithName("date").description("?????? ??????"),
					parameterWithName("visitorCount").description("????????? ???")
				),
				responseFields(
					fieldWithPath("availableTimes[]").description("?????? ????????? ??????")
				))
			);
	}

	@ParameterizedTest
	@CsvSource({"PLANNED", "DONE", "CANCEL"})
	@DisplayName("????????? ????????? ????????? ???????????? ????????? ??? ??????.")
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
				.param("page", page)
				.param("size", size)
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(customer))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(
				document("customer-reservation-get-by-visitStatus",
					requestParameters(
						parameterWithName("status").description("????????? ??????"),
						parameterWithName("page").description("????????? ??????"),
						parameterWithName("size").description("??? ????????? ??? ?????? ??????")
					),
					responseFields(
						fieldWithPath("content[]").type(JsonFieldType.ARRAY).description("????????? ?????????"),
						fieldWithPath("content[].restaurantName").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("content[].address").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("content[].visitDateTime").type(JsonFieldType.STRING).description("????????? ?????? ??? ??????"),
						fieldWithPath("content[].visitorCount").type(JsonFieldType.NUMBER).description("?????? ?????????"),

						fieldWithPath("pageable").type(JsonFieldType.STRING).description(""),
						fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
						fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("????????? ??????????????? ??????"),
						fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("????????? ??????????????? ??????"),
						fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
						fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("?????? ??????????????? ????????? ????????? ??????"),
						fieldWithPath("size").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ????????? ??????"),
						fieldWithPath("number").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
						fieldWithPath("sort").type(JsonFieldType.OBJECT).description("?????? ??????"),
						fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ????????? ??????"),
						fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
						fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????"),
						fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????")
					)
				)
			);
	}

	@Test
	@DisplayName("????????? ?????? ?????? ??????")
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
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(customer))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(
				document("customer-reservation-get-detail",
					pathParameters(
						parameterWithName("reservationId").description("????????? ??????")
					),
					responseFields(
						fieldWithPath("reservationInfoResForCustomer").type(JsonFieldType.OBJECT).description("?????? ??????"),
						fieldWithPath("reservationInfoResForCustomer.customerName").type(JsonFieldType.STRING)
							.description("????????? ??????"),
						fieldWithPath("reservationInfoResForCustomer.phone").type(JsonFieldType.STRING)
							.description("????????? ????????? ??????"),
						fieldWithPath("reservationInfoResForCustomer.visitDateTime").type(JsonFieldType.STRING)
							.description("????????? ??????"),
						fieldWithPath("reservationInfoResForCustomer.visitorCount").type(JsonFieldType.NUMBER)
							.description("????????? ??????"),
						fieldWithPath("reservationInfoResForCustomer.memo").type(JsonFieldType.STRING)
							.optional()
							.description("?????? ??????"),

						fieldWithPath("restaurantInfoRes").type(JsonFieldType.OBJECT).description("??????"),
						fieldWithPath("restaurantInfoRes.name").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("restaurantInfoRes.location").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("restaurantInfoRes.phone").type(JsonFieldType.STRING).description("?????? ????????????")
					)
				)
			);
	}

	@Test
	@DisplayName("????????? ????????? ????????? ????????? ??? ??????.")
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
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(customer))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-reservation-update-status-cancel",
				pathParameters(
					parameterWithName("reservationId").description("?????? ?????????")
				),
				requestFields(
					fieldWithPath("status").description("????????? ??????")
				))
			);
	}

	@Test
	@DisplayName("????????? ??????????????? ????????? ?????? ????????? ???????????? ????????? ??? ??????.")
	void get_reservation_available_dates() throws Exception {

		Member owner = memberRepository.save(DummyGenerator.OWNER);

		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurantWith2Capacity(owner));

		mockMvc.perform(
				get("/customer/api/reservations/available-dates?restaurantId=" + restaurant.getId()))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-get-available-dates-restaurant",
				responseFields(
					fieldWithPath("availableDates[]").type(JsonFieldType.ARRAY).description("?????? ?????? ?????? ?????????")
				))
			);
	}
}