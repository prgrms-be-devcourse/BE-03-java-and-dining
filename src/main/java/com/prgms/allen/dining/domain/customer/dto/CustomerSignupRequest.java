package com.prgms.allen.dining.domain.customer.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.prgms.allen.dining.domain.customer.entity.Customer;
import com.prgms.allen.dining.domain.customer.entity.CustomerType;

public record CustomerSignupRequest(

	@NotBlank
	String nickname,

	@NotBlank
	String name,

	@NotBlank
	String phone,

	@NotBlank
	String password,

	@NotNull
	CustomerType customerType
) {

	public Customer toEntity() {
		return new Customer(nickname, name, phone, password, customerType);
	}
}
