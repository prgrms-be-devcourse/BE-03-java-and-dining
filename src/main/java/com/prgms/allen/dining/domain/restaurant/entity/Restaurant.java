package com.prgms.allen.dining.domain.restaurant.entity;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import com.prgms.allen.dining.domain.customer.entity.Customer;

@Entity
public class Restaurant {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "restaurant_id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "customer_id")
	private Customer owner;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "food_type", nullable = false)
	private FoodType foodType;

	@Column(name = "name", length = 30, nullable = false)
	private String name;

	@Column(name = "capacity", nullable = false)
	private int capacity;

	@Column(name = "open_time", nullable = false)
	private LocalTime openTime;

	@Column(name = "last_order_time", nullable = false)
	private LocalTime lastOrderTime;

	@Column(name = "location", nullable = false)
	private String location;

	@Lob
	@Column(name = "description")
	private String description;

	@Column(name = "phone", length = 11, nullable = false)
	private String phone;

	protected Restaurant() {
	}
}
