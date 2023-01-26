package com.prgms.allen.dining.web.domain.owner.reservation;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.ReservationRepository;
import com.prgms.allen.dining.domain.reservation.dto.ReservationStatusUpdateReq;
import com.prgms.allen.dining.domain.reservation.entity.FakeReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

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

	@Test
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
		mockMvc.perform(patch("/owner/api/reservations/{reservationId}?ownerId=" + owner.getId(),
				reservation.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("reservation-update-status-confirm",
				pathParameters(
					parameterWithName("reservationId").description("예약 식별자")
				),
				requestFields(
					fieldWithPath("status").description("변경할 상태")
				))
			);
	}

	@Test
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
		mockMvc.perform(patch("/owner/api/reservations/{reservationId}?ownerId=" + owner.getId(),
				reservation.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("reservation-update-status-cancel",
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
		mockMvc.perform(patch("/owner/api/reservations/{reservationId}?ownerId=" + owner.getId(),
				reservation.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("reservation-update-status-visit",
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
		mockMvc.perform(patch("/owner/api/reservations/{reservationId}?ownerId=" + owner.getId(),
				reservation.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("reservation-update-status-noShow",
				pathParameters(
					parameterWithName("reservationId").description("예약 식별자")
				),
				requestFields(
					fieldWithPath("status").description("변경할 상태")
				))
			);
	}
}