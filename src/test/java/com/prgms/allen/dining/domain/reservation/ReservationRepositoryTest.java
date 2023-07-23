package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class ReservationRepositoryTest {

	Logger log = LoggerFactory.getLogger(ReservationRepositoryTest.class);

	private final int CAPACITY = 6;

	private Member customer;
	private Member owner1;
	private Member owner2;
	private Restaurant restaurant;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@BeforeEach
	void initMembersAndRestaurant() {
		customer = memberRepository.save(DummyGenerator.createCustomer("customer1"));
		owner1 = memberRepository.save(DummyGenerator.createOwner("owner1"));
		restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner1, CAPACITY));

	}

	@Test
	@DisplayName("예약 상태 별로 예약을 조회할 수 있다.")
	void find_visitor_counts_per_visit_time() {
		int visitorCount = 3;

		Reservation reservation = DummyGenerator.createReservation(
			customer,
			restaurant.getId(),
			DummyGenerator.createCustomerInput(customer, visitorCount)
		);
		reservationRepository.save(
			reservation
		);

		// when
		List<Reservation> pendingReservations = reservationRepository
			.findAllByRestaurantIdAndStatus(
				restaurant.getId(),
				List.of(ReservationStatus.PENDING)
			);

		// then
		int actualReservationCount = 1;
		int expectedReservationCount = pendingReservations.size();
		assertThat(actualReservationCount).isEqualTo(expectedReservationCount);
	}

}
