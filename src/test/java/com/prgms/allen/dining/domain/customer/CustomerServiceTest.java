package com.prgms.allen.dining.domain.customer;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgms.allen.dining.domain.customer.dto.CustomerSignupRequest;
import com.prgms.allen.dining.domain.customer.entity.CustomerType;

class CustomerServiceTest {

	private final CustomerRepository customerRepository = new FakeCustomerRepository();
	private final CustomerService customerService = new CustomerService(customerRepository);

	@AfterEach
	void tearDown() {
		customerRepository.deleteAll();
	}

	@Test
	@DisplayName("사용자는 회원가입 할 수 있다.")
	public void signup() {
		// given
		final CustomerSignupRequest customerSignupRequest = new CustomerSignupRequest("닉네임", "이택승", "01012341234",
			"qwer1234!", CustomerType.CUSTOMER);

		// when
		customerService.signup(customerSignupRequest);

		// then
		final long count = customerRepository.count();
		assertThat(count)
			.isEqualTo(1L);
	}
}