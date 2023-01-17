package com.prgms.allen.dining.domain.customer.entity;

import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.util.Assert;

@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "customer_id")
	private Long id;

	@Column(name = "nickname", unique = true, length = 20, nullable = false)
	private String nickname;

	@Column(name = "name", length = 5, nullable = false)
	private String name;

	@Column(name = "phone", length = 11, nullable = false)
	private String phone;

	@Column(name = "password", length = 20, nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "customer_type", nullable = false)
	private CustomerType customerType;

	protected Customer() {
	}

	public Customer(String nickname, String name, String phone, String password, CustomerType customerType) {
		validate(nickname, name, phone, password, customerType);

		this.nickname = nickname;
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.customerType = customerType;
	}

	public Customer(Long id, String nickname, String name, String phone, String password, CustomerType customerType) {
		validate(nickname, name, phone, password, customerType);

		this.id = id;
		this.nickname = nickname;
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.customerType = customerType;
	}

	public Long getId() {
		return id;
	}

	public String getNickname() {
		return nickname;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getPassword() {
		return password;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	private void validate(String nickname, String name, String phone, String password, CustomerType customerType) {
		validateNickname(nickname);
		validateName(name);
		validatePhone(phone);
		validatePassword(password);
		validateCustomerType(customerType);
	}

	private void validateNickname(String nickname) {
		Assert.hasLength(nickname, "Nickname must be not empty.");
		Assert.state(nickname.length() >= 3 && nickname.length() <= 20, "Nickname must be between 3 and 20.");
		Assert.state(Pattern.matches("^[0-9a-zA-Zㄱ-ㅎ가-힣]+$", nickname), "Nickname is invalid format");
	}

	private void validateName(String name) {
		Assert.hasLength(name, "Name must be not empty.");
		Assert.state(name.length() >= 2 && name.length() <= 5, "Name must be between 2 and 5.");
		Assert.state(Pattern.matches("^[가-힣]+$", name), "Name is invalid format");
	}

	private void validatePhone(String phone) {
		Assert.hasLength(phone, "Phone must be not empty.");
		Assert.state(phone.length() >= 9 && phone.length() <= 11, "Name must be between 2 and 5.");
		Assert.state(Pattern.matches("^[0-9]+$", phone), "Phone is invalid format");
	}

	private void validatePassword(String password) {
		Assert.hasLength(password, "Password must be not empty.");
		Assert.state(password.length() >= 8 && password.length() <= 20, "Password must be between 8 and 20.");
		Assert.state(Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$", password),
			"Password is invalid format.");
	}

	private void validateCustomerType(CustomerType customerType) {
		Assert.notNull(customerType, "customerType must be not null.");
	}

}
