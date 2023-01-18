package com.prgms.allen.dining.domain.reservation.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.prgms.allen.dining.domain.common.entity.BaseEntity;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Entity
public class Reservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reservation_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Member customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReservationStatus status;

	@Embedded
	@Column(name = "detail", nullable = false)
	private ReservationCustomerInput customerInput;

	protected Reservation() {
	}

	public Reservation(Long id, Member customer, Restaurant restaurant, ReservationStatus status,
		ReservationCustomerInput customerInput) {
		this.id = id;
		this.customer = customer;
		this.restaurant = restaurant;
		this.status = status;
		this.customerInput = customerInput;
	}

	public Reservation(Member customer, Restaurant restaurant, ReservationStatus status,
		ReservationCustomerInput customerInput) {
		this(null, customer, restaurant, status, customerInput);
	}

	public Long getId() {
		return id;
	}

	public Member getCustomer() {
		return customer;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public ReservationCustomerInput getCustomerInput() {
		return customerInput;
	}

	public long getRestaurantId() {
		return restaurant.getId();
	}

	public int getVisitorCount() {
		return customerInput.getVisitorCount();
	}

	public String getCustomerPhone() {
		return customer.getPhone();
	}

	public String getCustomerName() {
		return customer.getName();
	}

	public LocalDateTime getVisitDateTime() {
		return customerInput.getVisitDateTime();
	}

	public boolean isStatus(ReservationStatus status) {
		return this.status == status;
	}

	public void updateStatus(ReservationStatus status) {
		this.status = status;
	}
}
