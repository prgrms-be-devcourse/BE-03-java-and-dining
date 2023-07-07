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

	// @Test
	@DisplayName("점주는 확정 대기 중인 예약을 확정할 수 있다.")
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
					parameterWithName("reservationId").description("예약 식별자")
				),
				requestFields(
					fieldWithPath("status").description("변경할 상태")
				))
			);
	}

	@Test
	@DisplayName("점주는 예약을 상세 조회할 수 있다.")
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
					parameterWithName("reservationId").description("예약 식별자")
				),
				responseFields(
					fieldWithPath("customerInfoRes").description("고객 정보"),
					fieldWithPath("customerInfoRes.name").description("고객 이름"),
					fieldWithPath("customerInfoRes.phone").description("고객 전화번호"),
					fieldWithPath("customerInfoRes.visitedCount").description("방문 횟수"),
					fieldWithPath("customerInfoRes.noShowCount").description("노쇼 횟수"),
					fieldWithPath("customerInfoRes.lastVisitedDateTime").description("마지막 방문일시"),
					fieldWithPath("reservationInfoResForOwner").description("예약 정보"),
					fieldWithPath("reservationInfoResForOwner.visitDateTime").description("예약 일시"),
					fieldWithPath("reservationInfoResForOwner.visitorCount").description("방문 인원수"),
					fieldWithPath("reservationInfoResForOwner.memo").description("예약 메모")
				))
			);
	}

	// @Test
	@DisplayName("점주는 확정 대기 중인 예약을 취소할 수 있다.")
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
					parameterWithName("reservationId").description("예약 식별자")
				),
				requestFields(
					fieldWithPath("status").description("변경할 상태")
				))
			);
	}

	@Test
	@DisplayName("점주는 확정된 예약을 방문 시간 이후 방문완료로 변경할 수 있다.")
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
					parameterWithName("reservationId").description("예약 식별자")
				),
				requestFields(
					fieldWithPath("status").description("변경할 상태")
				))
			);
	}

	@Test
	@DisplayName("점주는 확정된 예약을 방문 시간 이후 노쇼로 변경할 수 있다.")
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
					parameterWithName("reservationId").description("예약 식별자")
				),
				requestFields(
					fieldWithPath("status").description("변경할 상태")
				))
			);
	}

	@ParameterizedTest
	@CsvSource({"PENDING", "CONFIRMED", "VISITED", "CANCELLED", "NO_SHOW"})
	@DisplayName("점주는 식당의 예약 목록을 조회할 수 있다.")
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
						parameterWithName("reservationStatus").description("조회할 상태"),
						parameterWithName("restaurantId").description("조회하려는 레스토랑의 아이디"),
						parameterWithName("page").description("페이지 번호"),
						parameterWithName("size").description("한 페이지 당 조회 개수")
					),
					responseFields(
						fieldWithPath("content[]").type(JsonFieldType.ARRAY).description("조회된 예약들"),
						fieldWithPath("content[].visitorName").type(JsonFieldType.STRING).description("방문자 이름"),
						fieldWithPath("content[].phone").type(JsonFieldType.STRING).description("방문자 핸드폰 번호"),
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
}