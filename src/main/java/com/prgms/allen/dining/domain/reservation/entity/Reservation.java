package com.prgms.allen.dining.domain.reservation.entity;

import static com.prgms.allen.dining.domain.reservation.entity.ReservationStatus.*;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

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

	private static final int MAX_STATUS_UPDATE_EXPIRATION_PERIOD = 30;

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

	public Reservation(Member customer, Restaurant restaurant, ReservationStatus status,
		ReservationCustomerInput customerInput) {
		this(null, customer, restaurant, status, customerInput);
	}

	public Reservation(Member customer, Restaurant restaurant, ReservationCustomerInput customerInput) {
		this(null, customer, restaurant, checkVisitingToday(customerInput), customerInput);
	}

	public Reservation(Long id, Member customer, Restaurant restaurant, ReservationStatus status,
		ReservationCustomerInput customerInput) {
		validate(customer, restaurant, customerInput);

		this.id = id;
		this.customer = customer;
		this.restaurant = restaurant;
		this.status = status;
		this.customerInput = customerInput;
	}

	private static ReservationStatus checkVisitingToday(ReservationCustomerInput customerInput) {
		if (customerInput.checkVisitingToday()) {
			return CONFIRMED;
		}
		return PENDING;
	}

	private void validate(Member customer, Restaurant restaurant, ReservationCustomerInput customerInput) {
		validateCustomer(customer);
		validateRestaurant(restaurant);
		validateReservationDetail(customerInput);
	}

	private void validateCustomer(Member customer) {
		Assert.notNull(customer, "Customer must not be null.");
	}

	private void validateRestaurant(Restaurant restaurant) {
		Assert.notNull(restaurant, "Restaurant must not be null.");
	}

	private void validateReservationDetail(ReservationCustomerInput customerInput) {
		Assert.notNull(customerInput, "ReservationDetail must not be null.");
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

	public Member getRestaurantOwner() {
		return restaurant.getOwner();
	}

	public void confirm(Long ownerId) {
		validUpdatableReservationState(ownerId, PENDING);
		customerInput.assertVisitDateAfter(LocalDate.now());
		status = CONFIRMED;
	}

	public void cancel(Long ownerId) {
		validUpdatableReservationState(ownerId, PENDING, CONFIRMED);
		customerInput.assertVisitDateAfter(LocalDate.now());
		status = CANCELLED;
	}

	public void visit(Long ownerId) {
		validUpdatableReservationState(ownerId, CONFIRMED);
		customerInput.assertVisitDateTimeBefore(LocalDateTime.now());
		customerInput.assertVisitDateWithin(LocalDate.now(), MAX_STATUS_UPDATE_EXPIRATION_PERIOD);
		status = VISITED;
	}

	public void noShow(Long ownerId) {
		validUpdatableReservationState(ownerId, CONFIRMED);
		customerInput.assertVisitDateTimeBefore(LocalDateTime.now());
		customerInput.assertVisitDateWithin(LocalDate.now(), MAX_STATUS_UPDATE_EXPIRATION_PERIOD);
		status = NO_SHOW;
	}

	private void validUpdatableReservationState(Long ownerId, ReservationStatus... validStatuses) {
		assertMatchesOwner(ownerId);
		assertReservationStatus(validStatuses);
	}

	private void assertMatchesOwner(Long ownerId) {
		Assert.state(
			getRestaurantOwner().matchesId(ownerId),
			MessageFormat.format(
				"Owner does not match. Parameter ownerId={0} but actual ownerId={1}",
				ownerId,
				getRestaurantOwner().getId()
			)
		);
	}

	private void assertReservationStatus(ReservationStatus... validStatuses) {
		Assert.state(
			Arrays.asList(validStatuses).contains(status),
			MessageFormat.format(
				"ReservationStatus should be {0} but was {1}", Arrays.toString(validStatuses), this.status
			)
		);
	}
}
