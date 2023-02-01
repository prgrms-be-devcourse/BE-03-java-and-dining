package com.prgms.allen.dining.domain.reservation.entity;

import static com.prgms.allen.dining.domain.member.entity.MemberType.*;
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
import com.prgms.allen.dining.domain.member.entity.MemberType;
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

	public Reservation(
		Member customer,
		Restaurant restaurant,
		ReservationCustomerInput customerInput
	) {
		this(null, customer, restaurant, checkVisitingToday(customerInput), customerInput);
	}

	private Reservation(
		Long id,
		Member customer,
		Restaurant restaurant,
		ReservationStatus status,
		ReservationCustomerInput customerInput
	) {
		validate(customer, restaurant, customerInput);

		this.id = id;
		this.customer = customer;
		this.restaurant = restaurant;
		this.status = status;
		this.customerInput = customerInput;
	}

	public static Reservation newTestInstance(
		Long id,
		Member customer,
		Restaurant restaurant,
		ReservationStatus status,
		ReservationCustomerInput customerInput
	) {
		return new Reservation(id, customer, restaurant, status, customerInput);
	}

	public static ReservationStatus checkVisitingToday(ReservationCustomerInput customerInput) {
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

	public String getMemo() {
		return customerInput.getCustomerMemo();
	}

	public void confirm(Long ownerId) {
		assertMatchesOwner(ownerId);
		assertReservationStatusOneOf(PENDING, CONFIRMED);
		assertVisitDateAfterCurrentDate();
		status = CONFIRMED;
	}

	public void cancel(MemberType memberType, Long memberId) {
		assertMatchesMember(memberType, memberId);
		assertReservationStatusOneOf(PENDING, CONFIRMED);
		assertVisitDateAfterCurrentDate();
		status = CANCELLED;
	}

	public void visit(Long ownerId) {
		assertMatchesOwner(ownerId);
		assertReservationStatusOneOf(CONFIRMED);
		assertVisitDateTimeBeforeCurrentDateTime();
		assertDaysBetweenVisitDateAndCurrentDateWithin(MAX_STATUS_UPDATE_EXPIRATION_PERIOD);
		status = VISITED;
	}

	public void noShow(Long ownerId) {
		assertMatchesOwner(ownerId);
		assertReservationStatusOneOf(CONFIRMED);
		assertVisitDateTimeBeforeCurrentDateTime();
		assertDaysBetweenVisitDateAndCurrentDateWithin(MAX_STATUS_UPDATE_EXPIRATION_PERIOD);
		status = NO_SHOW;
	}

	private void assertMatchesMember(MemberType memberType, Long memberId) {
		if (memberType == CUSTOMER) {
			assertMatchesCustomer(memberId);
			return;
		}
		assertMatchesOwner(memberId);
	}

	private void assertMatchesCustomer(Long customerId) {
		Assert.state(
			customer.matchesId(customerId),
			MessageFormat.format(
				"Customer does not match. Parameter customerId={0} but actual customerId={1}",
				customerId,
				customer.getId()
			)
		);
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

	private void assertReservationStatusOneOf(ReservationStatus... validStatuses) {
		Assert.state(
			Arrays.asList(validStatuses).contains(status),
			MessageFormat.format(
				"ReservationStatus should be {0} but was {1}", Arrays.toString(validStatuses), this.status
			)
		);
	}

	private void assertVisitDateTimeBeforeCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();

		Assert.state(
			customerInput.isVisitDateTimeBefore(currentDateTime),
			MessageFormat.format(
				"visitDateTime={0} should be before dateTime={1}", getVisitDateTime(), currentDateTime
			)
		);
	}

	public void assertVisitDateAfterCurrentDate() {
		LocalDate currentDate = LocalDate.now();

		Assert.state(
			customerInput.isVisitDateAfter(currentDate),
			MessageFormat.format(
				"visitDate={0} should be after date={1}", customerInput.getVisitDate(), currentDate
			)
		);
	}

	private void assertDaysBetweenVisitDateAndCurrentDateWithin(int days) {
		LocalDate currentDate = LocalDate.now();

		Assert.state(
			customerInput.isVisitDateTimeWithin(currentDate, days),
			MessageFormat.format(
				"Days between visitDate={0} and currentDate={1} should be within {2} days.",
				customerInput.getVisitDate(),
				currentDate,
				days
			)
		);
	}

	public Long getCustomerId() {
		return customer.getId();
	}

	public String getRestaurantName() {
		return restaurant.getName();
	}

	public String getRestaurantAddress() {
		return restaurant.getLocation();
	}
}
