package com.prgms.allen.dining.web.owner.reservation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class OwnerReservationApiTest {

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
	@CsvSource({"PENDING", "CONFIRMED", "VISITED", "CANCELLED", "NO_SHOW"})
	@DisplayName("점주는 식당의 예약 목록을 조회할 수 있다.")
	void getOwnerReservations(String status) throws Exception {
		// given
		owner = createOwner();
		customer = createCustomer();
		restaurant = createRestaurant(owner);
		createReservations(ReservationStatus.valueOf(status), restaurant, customer);
		page = "0";
		size = "5";

		mockMvc.perform(get("/owner/api/reservations")
				.param("reservationStatus", status)
				.param("restaurantId", restaurant.getId().toString())
				.param("page", page)
				.param("size", size)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(
				document("reservation-getAllByCustomer",
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
		ReservationCustomerInput customerInput = new ReservationCustomerInput(
			LocalDateTime.now()
				.plus(2, ChronoUnit.HOURS)
				.truncatedTo(ChronoUnit.HOURS),
			2,
			"맛있게 해주세요"
		);

		return reservationRepository.save(new Reservation(
			consumer,
			savedRestaurant,
			status,
			customerInput
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
			"021234123",
			List.of(new Menu("메뉴이름", BigInteger.valueOf(10000), "메모")),
			List.of(new ClosingDay(DayOfWeek.MONDAY))
		));
	}
}