package com.prgms.allen.dining.domain.reservation.concurrency;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.ReserveFailException;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCustomerInputCreateReq;
import com.prgms.allen.dining.domain.reservation.dto.ReservationSimpleResForOwner;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.reservation.service.ReservationFindService;
import com.prgms.allen.dining.domain.reservation.service.ReservationService;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.domain.schedule.entity.Schedule;
import com.prgms.allen.dining.domain.schedule.repository.ScheduleRepository;

@SpringBootTest
public class ReservationConcurrencyTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	RestaurantRepository restaurantRepository;

	@Autowired
	ReservationService reservationService;

	@Autowired
	ReservationFindService reservationFindService;

	@Autowired
	ScheduleRepository scheduleRepository;

	@Test
	@DisplayName("예약 가능 인원수가 꽉 찬 식당에 예약하려는 경우 예약에 실패합니다.")
	public void reservationConcurrency() throws InterruptedException, ExecutionException {
		//given
		Member firstMember = memberRepository.save(
			new Member("firstCustomer", "홍길동", "01098601941", "cjsak123!", MemberType.CUSTOMER));
		Member secondMember = memberRepository.save(
			new Member("secondCustomer", "박지성", "01098601941", "cjsak123!", MemberType.CUSTOMER));
		Member owner = memberRepository.save(
			new Member("owner", "김연아", "01098601941", "cjsak123!", MemberType.OWNER));
		Restaurant restaurant = restaurantRepository.save(
			new Restaurant(owner, FoodType.CHINESE, "china", 5, LocalTime.of(10, 0), LocalTime.of(20, 0), "아아", "아아",
				"01093492959"));

		CountDownLatch latch = new CountDownLatch(2);

		AtomicLong reservationId1 = new AtomicLong();
		AtomicLong reservationId2 = new AtomicLong();

		scheduleRepository.saveAndFlush(Schedule.ofFirstSchedule(LocalDateTime.of(LocalDate.now().plusDays(1L), LocalTime.of(12, 0)), restaurant, 0));

		CompletableFuture.runAsync(() -> {
			reservationId1.set(reservationService.reserveWithSchedule(firstMember.getId(),
				new ReservationCreateReq(restaurant.getId(), new ReservationCustomerInputCreateReq(
					LocalDateTime.of(LocalDate.now().plusDays(1L), LocalTime.of(12, 0)), 4, "첫번째로 예약"))));
			latch.countDown();
		}).exceptionally(e -> {
			System.out.println("e = " + e);
			latch.countDown();
			return null;
		});
		CompletableFuture.runAsync(() -> {
			reservationId2.set(reservationService.reserveWithSchedule(secondMember.getId(),
				new ReservationCreateReq(restaurant.getId(), new ReservationCustomerInputCreateReq(
					LocalDateTime.of(LocalDate.now().plusDays(1L), LocalTime.of(12, 0)), 4, "두번째로 예약"))));
			latch.countDown();
		}).exceptionally(e -> {
			System.out.println("e = " + e);
			latch.countDown();
			return null;
		});

		latch.await();

		//then
		Schedule foundSchedule = scheduleRepository.findByRestaurantAndDateTime(restaurant,
			LocalDateTime.of(LocalDate.now().plusDays(1L), LocalTime.of(12, 0))).get();

		assertThat(foundSchedule.getCapacity()).isEqualTo(1);

	}
}
