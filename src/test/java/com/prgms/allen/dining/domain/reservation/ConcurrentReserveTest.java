package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	private final int numberOfThreads = 3;
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
	private ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

	private CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

	private Member owner;

	private Restaurant restaurant;

	private List<Member> customers;

	private int capacity = 2;

	@BeforeEach
	void setup() {
		transactionTemplate = new TransactionTemplate(platformTransactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		owner = transactionTemplate.execute(status -> memberRepository.save(DummyGenerator.createOwner()));
		restaurant = transactionTemplate.execute(
			status -> restaurantRepository.save(DummyGenerator.createRestaurant(owner, capacity)));

		List<Member> customerList = List.of(
			DummyGenerator.createCustomer("customer1"),
			DummyGenerator.createCustomer("customer2"),
			DummyGenerator.createCustomer("customer3"),
			DummyGenerator.createCustomer("customer4"),
			DummyGenerator.createCustomer("customer5")
		);

		customers = transactionTemplate.execute(
			status -> memberRepository.saveAll(customerList)
		);
	}

	@Test
	@DisplayName("동시에 같은 날짜, 같은 시간에 예약을 요청하면 맨 처음 이외에 예약 생성이 실패한다.")
	void test_concurrent_reservation() throws InterruptedException {

		for (int i = 0; i < numberOfThreads; i++) {
			Member customer = customers.get(i);
			LocalDateTime visitTime = LocalDateTime.of(
				LocalDate.now().plusDays(1L),
				LocalTime.of(13, 0)
			);
			int visitCount = capacity;

			executorService.execute(() -> {
				ReservationCreateReq reservationInfo = DummyGenerator.createReservationInfo(
					customer,
					restaurant.getId(),
					visitTime,
					visitCount
				);

				transactionTemplate.execute(
					status -> reservationReserveService.reserve(customer.getId(), reservationInfo));
				countDownLatch.countDown();
			});
		}
		countDownLatch.await();

		int reservationSize = reservationRepository.findAll().size();
		int answer = 1;

		assertThat(reservationSize).isEqualTo(answer);
	}
}
