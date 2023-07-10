package com.prgms.allen.dining.domain.reservation;

class ReservationFindServiceTest {

	// private final ReservationRepository reservationRepository = new FakeReservationRepository();
	// private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	// private final MemberRepository memberRepository = new FakeMemberRepository();
	// private final MemberService memberService = new MemberService(memberRepository);
	// private final SlackNotifyService slackNotifyService = new FakeSlackNotifyService();
	// private final RestaurantProvider restaurantProvider = new RestaurantFindService(
	// 	restaurantRepository);
	// private final ReservationProvider reservationService = new ReservationInfoService(reservationRepository,
	// 	restaurantProvider);
	// private final RestaurantService restaurantService = new RestaurantService(restaurantRepository, memberService,
	// 	reservationService);
	// private final ReservationReserveService reservationReserveService = new ReservationReserveService(
	// 	reservationRepository,
	// 	restaurantProvider,
	// 	memberService,
	// 	bookingScheduleRespository, slackNotifyService);
	// private final ReservationFindService reservationFindService = new ReservationFindService(
	// 	reservationRepository,
	// 	restaurantProvider,
	// 	memberService,
	// 	reservationReserveService
	// );
	//
	// @ParameterizedTest
	// @CsvSource({"PENDING", "CONFIRMED", "VISITED", "CANCELLED", "NO_SHOW"})
	// @DisplayName("식당의 특정 상태의 예약들을 조회할 수 있다.")
	// public void getReservationsTest(String status) {
	// 	// given
	// 	Member owner = memberRepository.save(DummyGenerator.OWNER);
	// 	Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
	// 	Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
	// 	ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;
	//
	// 	Reservation reservation1 = DummyGenerator.createReservation(
	// 		customer,
	// 		restaurant,
	// 		ReservationStatus.valueOf(status),
	// 		customerInput
	// 	);
	// 	Reservation reservation2 = DummyGenerator.createReservation(
	// 		customer,
	// 		restaurant,
	// 		ReservationStatus.valueOf(status),
	// 		customerInput
	// 	);
	//
	// 	List<Reservation> savedReservations = reservationRepository.saveAll(List.of(reservation1, reservation2));
	//
	// 	PageImpl<ReservationSimpleResForOwner> expect = new PageImpl<>(
	// 		savedReservations.stream()
	// 			.map(ReservationSimpleResForOwner::new)
	// 			.toList()
	// 	);
	//
	// 	// when
	// 	Page<ReservationSimpleResForOwner> actual = reservationFindService.getReservations(
	// 		restaurant.getId(),
	// 		ReservationStatus.valueOf(status),
	// 		PageRequest.of(0, 5)
	// 	);
	//
	// 	// then
	// 	assertThat(actual)
	// 		.isEqualTo(expect);
	// }
	//
	// @ParameterizedTest
	// @CsvSource({"PLANNED", "DONE", "CANCEL"})
	// @DisplayName("구매자는 자신이 예약한 정보들을 상태별로 볼 수 있다.")
	// public void getRestaurantReservationsTest(String status) {
	// 	// given
	// 	Member owner = memberRepository.save(DummyGenerator.OWNER);
	// 	Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
	// 	Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
	//
	// 	VisitStatus visitStatus = VisitStatus.valueOf(status);
	// 	List<ReservationStatus> reservationStatuses = visitStatus.getStatuses();
	// 	ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;
	//
	// 	List<Reservation> reservations = reservationStatuses.stream()
	// 		.map(reservationStatus -> reservationRepository.save(DummyGenerator.createReservation(
	// 			customer,
	// 			restaurant,
	// 			reservationStatus,
	// 			customerInput
	// 		)))
	// 		.toList();
	//
	// 	PageImpl<ReservationSimpleResForCustomer> expect = new PageImpl<>(
	// 		reservations
	// 			.stream()
	// 			.map(reservation -> {
	// 				var restaurantInfo = RestaurantInfo.toRestaurantInfo(restaurant);
	//
	// 				return ReservationSimpleResForCustomer.from(reservation, restaurantInfo);
	// 			})
	// 			.toList());
	//
	// 	// when
	// 	Page<ReservationSimpleResForCustomer> actual =
	// 		reservationFindService.getReservations(
	// 			customer.getId(),
	// 			visitStatus,
	// 			PageRequest.of(0, 5)
	// 		);
	//
	// 	// then
	// 	assertThat(actual)
	// 		.isEqualTo(expect);
	// }
	//
	// @ParameterizedTest
	// @CsvSource({"PENDING", "CONFIRMED", "VISITED", "CANCELLED", "NO_SHOW"})
	// @DisplayName("식당의 예약 상세 조회")
	// public void getReservationDetail(String status) {
	// 	// given
	// 	Member owner = memberRepository.save(DummyGenerator.OWNER);
	// 	Restaurant restaurant = restaurantRepository.save(DummyGenerator.createRestaurant(owner));
	// 	Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
	// 	ReservationCustomerInput customerInput = DummyGenerator.CUSTOMER_INPUT;
	//
	// 	Reservation reservation = reservationRepository.save(DummyGenerator.createReservation(
	// 		customer,
	// 		restaurant,
	// 		ReservationStatus.valueOf(status),
	// 		customerInput
	// 	));
	//
	// 	RestaurantInfo restaurantInfo = RestaurantInfo.toRestaurantInfo(restaurant);
	//
	// 	ReservationDetailResForCustomer expect = new ReservationDetailResForCustomer(reservation, restaurantInfo);
	//
	// 	// when
	// 	ReservationDetailResForCustomer actual = reservationFindService.getReservationDetail(reservation.getId(),
	// 		customer.getId());
	//
	// 	// then
	// 	assertThat(actual)
	// 		.isEqualTo(expect);
	//
	// }
	//
	// @Test
	// @DisplayName("점주는 예약을 상세조회 할 수 있다.")
	// void getReservationDetail() {
	// 	// given
	// 	Member customer = memberRepository.save(new Member(
	// 		"customer",
	// 		"김환이",
	// 		"01012342345",
	// 		"asdfg123!",
	// 		MemberType.CUSTOMER
	// 	));
	//
	// 	Member owner = memberRepository.save(new Member(
	// 		"owner",
	// 		"김환이",
	// 		"01012342345",
	// 		"asdfg123!",
	// 		MemberType.OWNER
	// 	));
	//
	// 	Restaurant restaurant = restaurantRepository.save(new Restaurant(
	// 		owner,
	// 		FoodType.KOREAN,
	// 		"유명 레스토랑",
	// 		6,
	// 		LocalTime.of(11, 0),
	// 		LocalTime.of(21, 0),
	// 		"서울특별시 강남구 어딘가로 123 무슨빌딩 1층",
	// 		"우리는 유명한 한식당입니다.",
	// 		"0211112222",
	// 		List.of(new Menu("맛있는 밥", BigInteger.valueOf(10000), "맛있어용")),
	// 		List.of(new ClosingDay(DayOfWeek.MONDAY))
	// 	));
	//
	// 	ReservationCustomerInput customerInput = new ReservationCustomerInput(
	// 		LocalDateTime.of(
	// 			LocalDate.now().plusDays(1),
	// 			restaurant.getOpenTime()
	// 		),
	// 		2,
	// 		"가지 빼주세요"
	// 	);
	// 	ReservationCustomerInput customerInput1 = new ReservationCustomerInput(
	// 		LocalDateTime.of(
	// 			LocalDate.now().plusDays(1),
	// 			restaurant.getOpenTime()
	// 				.plusHours(1)
	// 		),
	// 		2,
	// 		"가지 빼주세요"
	// 	);
	// 	ReservationCustomerInput customerInput2 = new ReservationCustomerInput(
	// 		LocalDateTime.of(
	// 			LocalDate.now().plusDays(1),
	// 			restaurant.getOpenTime()
	// 				.plusHours(2)
	// 		),
	// 		2,
	// 		"가지 빼주세요"
	// 	);
	// 	ReservationCustomerInput customerInput3 = new ReservationCustomerInput(
	// 		LocalDateTime.of(
	// 			LocalDate.now().plusDays(1),
	// 			restaurant.getOpenTime()
	// 				.plusHours(3)
	// 		),
	// 		2,
	// 		"가지 빼주세요"
	// 	);
	// 	ReservationCustomerInput customerInput4 = new ReservationCustomerInput(
	// 		LocalDateTime.of(
	// 			LocalDate.now().plusDays(1),
	// 			restaurant.getOpenTime()
	// 				.plusHours(4)
	// 		),
	// 		2,
	// 		"가지 빼주세요"
	// 	);
	// 	saveReservation(
	// 		customer,
	// 		restaurant,
	// 		ReservationStatus.VISITED,
	// 		customerInput
	// 	);
	// 	saveReservation(
	// 		customer,
	// 		restaurant,
	// 		ReservationStatus.VISITED,
	// 		customerInput1
	// 	);
	// 	Reservation lastVisitedReservation = reservationRepository.save(Reservation.newTestInstance(
	// 		null,
	// 		customer,
	// 		restaurant.getId(),
	// 		ReservationStatus.VISITED,
	// 		customerInput2
	// 	));
	// 	saveReservation(
	// 		customer,
	// 		restaurant,
	// 		ReservationStatus.NO_SHOW,
	// 		customerInput3
	// 	);
	// 	saveReservation(
	// 		customer,
	// 		restaurant,
	// 		ReservationStatus.CONFIRMED,
	// 		customerInput4
	// 	);
	//
	// 	ReservationDetailResForOwner expect = new ReservationDetailResForOwner(
	// 		new CustomerReservationInfoProj(
	// 			customer.getName(),
	// 			customer.getPhone(),
	// 			3L,
	// 			1L,
	// 			lastVisitedReservation.getVisitDateTime().toString()
	// 		),
	// 		lastVisitedReservation
	// 	);
	//
	// 	// when
	// 	ReservationDetailResForOwner actual = reservationFindService.getReservationDetail(
	// 		lastVisitedReservation.getId());
	//
	// 	// then
	// 	assertThat(actual)
	// 		.isEqualTo(expect);
	// }
	//
	// private void saveReservation(
	// 	Member consumer,
	// 	Restaurant savedRestaurant,
	// 	ReservationStatus status,
	// 	ReservationCustomerInput customerInput
	// ) {
	//
	// 	reservationRepository.save(Reservation.newTestInstance(
	// 		null,
	// 		consumer,
	// 		savedRestaurant.getId(),
	// 		status,
	// 		customerInput
	// 	));
	// }
}