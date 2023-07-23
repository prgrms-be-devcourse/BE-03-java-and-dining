package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.bookingSchedule.entity.BookingSchedule;
import com.prgms.allen.dining.domain.reservation.bookingSchedule.repository.BookingScheduleRepository;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class BookingScheduleRepositoryTest {

	@Autowired
	private BookingScheduleRepository bookingScheduleRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MemberRepository memberRepository;

	private Member owner;

	private Restaurant restaurant;

	@BeforeEach
	void setup() {
		owner = memberRepository.save(DummyGenerator.createOwner());
		restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
	}

	@Test
	@DisplayName("생성되어 있는 BookingSchedule 객체를 찾아올 수 있다.")
	void test_get_booking_schedule_entity() {
		LocalDateTime bookingDateTime = LocalDateTime.of(
			LocalDate.now().plusDays(1L),
			LocalTime.of(13, 0)
		);

		BookingSchedule schedule = bookingScheduleRepository.save(
			new BookingSchedule(restaurant.getId(), bookingDateTime, restaurant.getCapacity())
		);

		BookingSchedule findSchedule = bookingScheduleRepository.findByRestaurantIdAndAndBookingDateTime(
			restaurant.getId(), bookingDateTime).get();

		assertThat(findSchedule.getId()).isEqualTo(schedule.getId());
	}
}