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

import com.prgms.allen.dining.domain.member.entity.Member;

@Entity
public class Restaurant {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "restaurant_id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "owner_id")
	private Member owner;

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

	public Restaurant(Long id, Member owner, FoodType foodType, String name, int capacity, LocalTime openTime,
		LocalTime lastOrderTime, String location, String description, String phone) {
		this.id = id;
		this.owner = owner;
		this.foodType = foodType;
		this.name = name;
		this.capacity = capacity;
		this.openTime = openTime;
		this.lastOrderTime = lastOrderTime;
		this.location = location;
		this.description = description;
		this.phone = phone;
	}

	public Restaurant(Member owner, FoodType foodType, String name, int capacity, LocalTime openTime,
		LocalTime lastOrderTime, String location, String description, String phone) {
		this(null, owner, foodType, name, capacity, openTime, lastOrderTime, location, description, phone);
	}

	public Long getId() {
		return id;
	}

	public Member getOwner() {
		return owner;
	}

	public FoodType getFoodType() {
		return foodType;
	}

	public String getName() {
		return name;
	}

	public int getCapacity() {
		return capacity;
	}

	public LocalTime getOpenTime() {
		return openTime;
	}

	public LocalTime getLastOrderTime() {
		return lastOrderTime;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	public String getPhone() {
		return phone;
	}
}
