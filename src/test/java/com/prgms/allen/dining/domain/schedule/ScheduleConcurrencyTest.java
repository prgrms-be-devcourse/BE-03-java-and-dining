package com.prgms.allen.dining.domain.schedule;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.domain.schedule.entity.Schedule;
import com.prgms.allen.dining.domain.schedule.repository.ScheduleRepository;
import com.prgms.allen.dining.domain.schedule.service.ScheduleServiceFacade;

@SpringBootTest
public class ScheduleConcurrencyTest {

	private static final int RESTAURANT_CAPACITY = 5;

	@Autowired
	ScheduleServiceFacade scheduleServiceFacade;

	@Autowired
	ScheduleRepository scheduleRepository;

	@Autowired
	RestaurantRepository restaurantRepository;

	@Autowired
	MemberRepository memberRepository;

	private Schedule schedule;
	private Restaurant restaurant;
	private LocalDateTime testDateTime;

	@BeforeEach
	void 데이터_세팅(){
		Member owner = memberRepository.save(
			new Member("owner", "김연아", "01098601941", "cjsak123!", MemberType.OWNER));
		this.restaurant = new Restaurant(owner, FoodType.CHINESE, "china", RESTAURANT_CAPACITY, LocalTime.of(10, 0), LocalTime.of(20, 0), "아아", "아아",
			"01093492959");
		restaurantRepository.save(restaurant);
		this.testDateTime = LocalDateTime.of(LocalDate.now().plusDays(1L), LocalTime.of(12, 0));
		this.schedule = Schedule.ofFirstSchedule(testDateTime, restaurant, 0);
		scheduleRepository.save(schedule);

	}

	@Test
	@DisplayName("10명의 사용자가 동시에 가용 인원수가 5명인 식당에 방문인원수 4명으로 스케쥴 데이터 업데이트를 시도할 경우 한 사람만 성공합니다.")
	public void ConcurrentlyFixOnlyOne() throws InterruptedException {
		//given
		CountDownLatch latch = new CountDownLatch(10);

		//when
		List<Thread> workers = Stream
			.generate(() -> new Thread(new FixWorker(testDateTime, restaurant, 4, latch)))
			.limit(10)
			.toList();

		workers.forEach(Thread::start);
		latch.await();

		//then
		Schedule expectedSchedule = scheduleRepository.findByRestaurantAndDateTime(restaurant,
			testDateTime).get();
		int expectedCapacity = expectedSchedule.getCapacity();
		assertThat(expectedCapacity).isEqualTo(1);
	}

	@Test
	@DisplayName("스케쥴 정보 수정이 동시에 발생할 경우 낙관적 락 버전 정보에 따라 실패하지만 재시도하여 두 수정 모두 성공합니다.")
	public void ConcurrentlyFixSuccess() throws InterruptedException {
		//given
		CountDownLatch latch = new CountDownLatch(2);

		//when
		List<Thread> workers = Stream
			.generate(() -> new Thread(new FixWorker(testDateTime, restaurant, 2, latch)))
			.limit(2)
			.toList();

		workers.forEach(Thread::start);
		latch.await();

		//then
		Schedule expectedSchedule = scheduleRepository.findByRestaurantAndDateTime(restaurant,
			testDateTime).get();
		int expectedCapacity = expectedSchedule.getCapacity();
		assertThat(expectedCapacity).isEqualTo(1);
	}

	private class FixWorker implements Runnable{

		private LocalDateTime dateTime;
		private Restaurant restaurant;
		private int visitorCount;
		private CountDownLatch countDownLatch;

		public FixWorker(LocalDateTime dateTime, Restaurant restaurant, int visitorCount, CountDownLatch countDownLatch) {
			this.dateTime = dateTime;
			this.restaurant = restaurant;
			this.visitorCount = visitorCount;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			try{
				scheduleServiceFacade.fix(dateTime, restaurant, visitorCount);
			} catch(IllegalStateException e){

			}
			countDownLatch.countDown();
		}
	}
}
