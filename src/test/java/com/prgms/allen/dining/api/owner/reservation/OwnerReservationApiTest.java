package com.prgms.allen.dining.api.owner.reservation;

import static com.prgms.allen.dining.domain.reservation.entity.ReservationStatus.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

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
import com.prgms.allen.dining.domain.reservation.dto.ReservationStatusUpdateReq;
import com.prgms.allen.dining.domain.reservation.entity.FakeReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
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
class OwnerReservationApiTest {

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
	@DisplayName("????????? ?????? ?????? ?????? ????????? ????????? ??? ??????.")
	void confirm_reservation() throws Exception {
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
		Reservation reservation = reservationRepository.save(
			new Reservation(customer, restaurant, customerInput)
		);

		ReservationStatusUpdateReq statusUpdateReq = new ReservationStatusUpdateReq(CONFIRMED);

		// when & then
		mockMvc.perform(patch("/owner/api/reservations/{reservationId}", reservation.getId())
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(owner))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("owner-reservation-update-status-confirm",
				pathParameters(
					parameterWithName("reservationId").description("?????? ?????????")
				),
				requestFields(
					fieldWithPath("status").description("????????? ??????")
				))
			);
	}

	@Test
	@DisplayName("????????? ????????? ?????? ????????? ??? ??????.")
	void getReservationDetail() throws Exception {
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
		Reservation reservation = reservationRepository.save(
			new Reservation(customer, restaurant, customerInput)
		);

		// when & then
		mockMvc.perform(get("/owner/api/reservations/{reservationId}", reservation.getId())
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(owner)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("owner-reservation-get-detail",
				pathParameters(
					parameterWithName("reservationId").description("?????? ?????????")
				),
				responseFields(
					fieldWithPath("customerInfoRes").description("?????? ??????"),
					fieldWithPath("customerInfoRes.name").description("?????? ??????"),
					fieldWithPath("customerInfoRes.phone").description("?????? ????????????"),
					fieldWithPath("customerInfoRes.visitedCount").description("?????? ??????"),
					fieldWithPath("customerInfoRes.noShowCount").description("?????? ??????"),
					fieldWithPath("customerInfoRes.lastVisitedDateTime").description("????????? ????????????"),
					fieldWithPath("reservationInfoResForOwner").description("?????? ??????"),
					fieldWithPath("reservationInfoResForOwner.visitDateTime").description("?????? ??????"),
					fieldWithPath("reservationInfoResForOwner.visitorCount").description("?????? ?????????"),
					fieldWithPath("reservationInfoResForOwner.memo").description("?????? ??????")
				))
			);
	}

	@Test
	@DisplayName("????????? ?????? ?????? ?????? ????????? ????????? ??? ??????.")
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
		mockMvc.perform(patch("/owner/api/reservations/{reservationId}", reservation.getId())
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(owner))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("owner-reservation-update-status-cancel",
				pathParameters(
					parameterWithName("reservationId").description("?????? ?????????")
				),
				requestFields(
					fieldWithPath("status").description("????????? ??????")
				))
			);
	}

	@Test
	@DisplayName("????????? ????????? ????????? ?????? ?????? ?????? ??????????????? ????????? ??? ??????.")
	void visit_reservation() throws Exception {
		// given
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		ReservationCustomerInput customerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.minusDays(1),
			LocalTime.now()
				.truncatedTo(ChronoUnit.HOURS),
			2
		);
		Reservation reservation = reservationRepository.save(
			Reservation.newTestInstance(null, customer, restaurant, CONFIRMED, customerInput)
		);

		ReservationStatusUpdateReq statusUpdateReq = new ReservationStatusUpdateReq(VISITED);

		// when & then
		mockMvc.perform(patch("/owner/api/reservations/{reservationId}", reservation.getId())
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(owner))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("owner-reservation-update-status-visit",
				pathParameters(
					parameterWithName("reservationId").description("?????? ?????????")
				),
				requestFields(
					fieldWithPath("status").description("????????? ??????")
				))
			);
	}

	@Test
	@DisplayName("????????? ????????? ????????? ?????? ?????? ?????? ????????? ????????? ??? ??????.")
	void noShow_reservation() throws Exception {
		// given
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		ReservationCustomerInput customerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.minusDays(1),
			LocalTime.now()
				.truncatedTo(ChronoUnit.HOURS),
			2
		);
		Reservation reservation = reservationRepository.save(
			Reservation.newTestInstance(null, customer, restaurant, CONFIRMED, customerInput)
		);

		ReservationStatusUpdateReq statusUpdateReq = new ReservationStatusUpdateReq(NO_SHOW);

		// when & then
		mockMvc.perform(patch("/owner/api/reservations/{reservationId}", reservation.getId())
				.header(HeaderValue.AUTHORIZATION.getValue(), jwtGenerator.getToken(owner))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("owner-reservation-update-status-noShow",
				pathParameters(
					parameterWithName("reservationId").description("?????? ?????????")
				),
				requestFields(
					fieldWithPath("status").description("????????? ??????")
				))
			);
	}

	@ParameterizedTest
	@CsvSource({"PENDING", "CONFIRMED", "VISITED", "CANCELLED", "NO_SHOW"})
	@DisplayName("????????? ????????? ?????? ????????? ????????? ??? ??????.")
	void getOwnerReservations(String status) throws Exception {
		// given
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		Member owner = memberRepository.save(DummyGenerator.OWNER);
		Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
		ReservationCustomerInput customerInput = new FakeReservationCustomerInput(
			LocalDate.now()
				.minusDays(1),
			LocalTime.now()
				.truncatedTo(ChronoUnit.HOURS),
			2
		);
		Reservation reservation = reservationRepository.save(
			Reservation.newTestInstance(null, customer, restaurant, ReservationStatus.valueOf(status), customerInput)
		);
		String page = "0";
		String size = "5";

		mockMvc.perform(get("/owner/api/reservations")
				.param("reservationStatus", status)
				.param("restaurantId", restaurant.getId().toString())
				.param("page", page)
				.param("size", size)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(
				document("owner-reservation-get-by-reservationStatus",
					requestParameters(
						parameterWithName("reservationStatus").description("????????? ??????"),
						parameterWithName("restaurantId").description("??????????????? ??????????????? ?????????"),
						parameterWithName("page").description("????????? ??????"),
						parameterWithName("size").description("??? ????????? ??? ?????? ??????")
					),
					responseFields(
						fieldWithPath("content[]").type(JsonFieldType.ARRAY).description("????????? ?????????"),
						fieldWithPath("content[].visitorName").type(JsonFieldType.STRING).description("????????? ??????"),
						fieldWithPath("content[].phone").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
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
}