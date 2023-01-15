package com.prgms.allen.dining.web.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.customer.CustomerService;
import com.prgms.allen.dining.domain.customer.dto.CustomerSignupRequest;

@RestController
@RequestMapping("/api/customers/")
public class CustomerApi {

	private final CustomerService customerService;

	public CustomerApi(CustomerService customerService) {
		this.customerService = customerService;
	}

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(CustomerSignupRequest customerSignupRequest) {
		customerService.signup(customerSignupRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.build();
	}
}
