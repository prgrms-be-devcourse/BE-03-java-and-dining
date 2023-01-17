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

import org.springframework.util.Assert;

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
	private ReservationDetail detail;

	protected Reservation() {
	}

	public Reservation(Long id, Member customer, Restaurant restaurant, ReservationStatus status,
		ReservationDetail detail) {
		this.id = id;
		this.customer = customer;
		this.restaurant = restaurant;
		this.status = status;
		this.detail = detail;
	}

	public Reservation(Member customer, Restaurant restaurant, ReservationStatus status, ReservationDetail detail) {
		this(null, customer, restaurant, status, detail);
	}


	public Reservation(Customer customer, Restaurant restaurant, ReservationDetail detail) {
		validation(customer, restaurant, detail);

		this.customer = customer;
		this.restaurant = restaurant;
		if (detail.checkVisitingToday()) {
			this.status = ReservationStatus.CONFIRMED;
		} else {
			this.status = ReservationStatus.PENDING;
		}
		this.detail = detail;
	}

	private void validation(Customer customer, Restaurant restaurant, ReservationDetail detail) {
		validateCustomer(customer);
		validateRestaurant(restaurant);
		validateReservationDetail(detail);
	}

	private void validateCustomer(Customer customer) {
		Assert.notNull(customer, "Customer must not be null.");
	}

	private void validateRestaurant(Restaurant restaurant) {
		Assert.notNull(restaurant, "Restaurant must not be null.");
	}

	private void validateReservationDetail(ReservationDetail detail) {
		Assert.notNull(detail, "ReservationDetail must not be null.");
	}

	public ReservationStatus getStatus() {
		return status;
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

	public ReservationDetail getDetail() {
		return detail;
	}

	public long getRestaurantId() {
		return restaurant.getId();
	}

	public int getVisitorCount() {
		return detail.getVisitorCount();
	}

	public String getCustomerPhone() {
		return customer.getPhone();
	}

	public String getCustomerName() {
		return customer.getName();
	}

	public LocalDateTime getVisitDateTime() {
		return detail.getVisitDateTime();
	}
}
