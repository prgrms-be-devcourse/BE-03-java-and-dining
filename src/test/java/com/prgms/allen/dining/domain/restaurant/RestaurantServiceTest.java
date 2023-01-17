package com.prgms.allen.dining.domain.restaurant;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.customer.CustomerService;
import com.prgms.allen.dining.domain.customer.FakeCustomerRepository;
import com.prgms.allen.dining.domain.customer.entity.Customer;
import com.prgms.allen.dining.domain.customer.entity.CustomerType;
import com.prgms.allen.dining.domain.restaurant.dto.ClosingDayCreateRequest;
import com.prgms.allen.dining.domain.restaurant.dto.MenuCreateRequest;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateRequest;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.fake.FakeRestaurantRepository;

@Transactional
class RestaurantServiceTest {

	private final RestaurantRepository restaurantRepository = new FakeRestaurantRepository();
	private final FakeCustomerRepository customerRepository = new FakeCustomerRepository();
	private final CustomerService customerService = new CustomerService(customerRepository);
	private final RestaurantService restaurantService = new RestaurantService(restaurantRepository, customerService);

	private Customer savedOwner;

	@BeforeEach
	void setUp() {
		final Customer owner = new Customer("nickname", "익명",
			"01011112222", "qwer1234!", CustomerType.OWNER);
		savedOwner = customerRepository.save(owner);
	}

	@AfterEach
	void clean() {
		customerRepository.deleteAll();
		restaurantRepository.deleteAll();
	}

	@Test
	@DisplayName("점주는 식당을 등록할 수 있다.")
	public void testSave() {

		List<ClosingDayCreateRequest> closingDayList = List.of(new ClosingDayCreateRequest(DayOfWeek.MONDAY));
		List<MenuCreateRequest> menuList = List.of(
			new MenuCreateRequest("맛있는 밥", BigDecimal.valueOf(10000), "맛있어용")
		);

		final RestaurantCreateRequest restaurantCreateRequest = new RestaurantCreateRequest(
			FoodType.KOREAN,
			"유명 레스토랑",
			30,
			LocalTime.of(11, 0),
			LocalTime.of(21, 0),
			"서울특별시 강남구 어딘가로 123 무슨빌딩 1층",
			"우리는 유명한 한식당입니다.",
			"0211112222",
			menuList,
			closingDayList);

		// when
		restaurantService.save(restaurantCreateRequest, savedOwner.getId());

		// then
		assertThat(restaurantRepository.count())
			.isEqualTo(1);

	}
}