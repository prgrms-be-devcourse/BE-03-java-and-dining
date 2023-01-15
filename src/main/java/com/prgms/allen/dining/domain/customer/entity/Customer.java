package com.prgms.allen.dining.domain.customer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
}
