package com.prgms.allen.dining.web.customer.reservation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
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
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.ReservationRepository;
import com.prgms.allen.dining.domain.reservation.dto.VisitStatus;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationDetail;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class CustomerReservationApiTest {

	private static Member owner;
	private static Member customer;
	private static Restaurant restaurant;
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
		owner = createOwner();
		customer = createCustomer();
		restaurant = createRestaurant(owner);
		createReservations(statuses.get(0), restaurant, customer);
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

	private Member createCustomer() {
		return memberRepository.save(new Member(
			"customer",
			"구매자",
			"01012341234",
			"qwer1234!",
			MemberType.CUSTOMER
		));
	}

	private Member createOwner() {
		return memberRepository.save(new Member(
			"owner",
			"오너",
			"01012341234",
			"qwer1234!",
			MemberType.OWNER
		));
	}

	private List<Reservation> createReservations(ReservationStatus status, Restaurant restaurant, Member consumer) {
		Reservation reservation1 = createReservation(
			status,
			consumer,
			restaurant
		);

		Reservation reservation2 = createReservation(
			status,
			consumer,
			restaurant
		);

		return List.of(reservation1, reservation2);
	}

	private Reservation createReservation(ReservationStatus status, Member consumer, Restaurant savedRestaurant) {
		ReservationDetail detail = new ReservationDetail(
			LocalDate.of(2023, 1, 16),
			LocalTime.of(16, 59), 2,
			"단무지는 빼주세요"
		);

		return reservationRepository.save(new Reservation(
			consumer,
			savedRestaurant,
			status,
			detail
		));
	}

	private Restaurant createRestaurant(Member owner) {
		return restaurantRepository.save(new Restaurant(
			owner,
			FoodType.KOREAN,
			"장충동국밥",
			100,
			LocalTime.of(9, 0),
			LocalTime.of(23, 0),
			"서울특별시 서초구 어디길11 2층",
			"실망시키지 않는 맛집",
			"021234123"
		));
	}
}