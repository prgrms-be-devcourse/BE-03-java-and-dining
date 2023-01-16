package com.prgms.allen.dining.domain.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.customer.dto.CustomerSignupRequest;
import com.prgms.allen.dining.domain.customer.entity.Customer;

@Service
@Transactional(readOnly = true)
public class CustomerService {

	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Transactional
	public void signup(CustomerSignupRequest signupRequest) {
		final Customer newCustomer = signupRequest.toEntity();
		customerRepository.save(newCustomer);
	}
}
