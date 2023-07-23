package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;
import com.prgms.allen.dining.domain.reservation.repository.ReservationRepository;
import com.prgms.allen.dining.domain.reservation.service.ReservationReserveService;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.generator.DummyGenerator;

@SpringBootTest
@Transactional
public class ConcurrentReserveTest {

	private final int THREAD_NUMS = 3;
	private final int CAPACITY = 4;

	@Autowired
	private PlatformTransactionManager platformTransactionManager;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private ReservationReserveService reservationReserveService;
	@Autowired
	private ReservationRepository reservationRepository;

	private TransactionTemplate transactionTemplate;

	private Member owner;

	private Restaurant restaurant;

	private List<Member> customers;

	@BeforeEach
	void setup() {
		transactionTemplate = new TransactionTemplate(platformTransactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		owner = transactionTemplate.execute(status -> memberRepository.save(DummyGenerator.createOwner()));
		restaurant = transactionTemplate.execute(
			status -> restaurantRepository.save(DummyGenerator.createRestaurant(owner, CAPACITY)));

		List<Member> customerList = IntStream.range(0, THREAD_NUMS)
			.boxed()
			.map(num -> DummyGenerator.createCustomer("customer" + num))
			.toList();

		customers = transactionTemplate.execute(
			status -> memberRepository.saveAll(customerList)
		);

	}

	@AfterEach
	void clean() {
		transactionTemplate.executeWithoutResult(
			status -> reservationRepository.deleteAll()
		);
		transactionTemplate.executeWithoutResult(
			status -> restaurantRepository.deleteAll()
		);
		transactionTemplate.executeWithoutResult(
			status -> memberRepository.deleteAll()
		);
	}

	@Test
	@DisplayName("동시에 같은 날짜, 같은 시간에 3개의 예약을 요청하면 2개만 성공한다.")
	void test_concurrent_reservation() throws InterruptedException {

		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMS);
		CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUMS);

		for (int i = 0; i < THREAD_NUMS; i++) {
			Member customer = customers.get(i);
			LocalDateTime visitTime = LocalDateTime.of(
				LocalDate.now().plusDays(1L),
				LocalTime.of(13, 0)
			);
			int visitCount = 2;

			executorService.execute(() -> {
				try {
					ReservationCreateReq reservationInfo = DummyGenerator.createReservationInfo(
						customer,
						restaurant.getId(),
						visitTime,
						visitCount
					);

					transactionTemplate.execute(
						status -> reservationReserveService.reserve(customer.getId(), reservationInfo));
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		int reservationSize = reservationRepository.findAll().size();
		int answer = 2;

		assertThat(reservationSize).isEqualTo(answer);
	}
}
