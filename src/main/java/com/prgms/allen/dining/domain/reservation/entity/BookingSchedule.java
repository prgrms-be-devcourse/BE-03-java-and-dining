package com.prgms.allen.dining.domain.reservation.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "BOOKING_SCHEDULE",
	uniqueConstraints = {
		@UniqueConstraint(name = "restaurant_id_booking_date_time", columnNames = {"restaurant_id",
			"booking_date_time"})
	})
public class BookingSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "restaurant_id")
	private Long restaurantId;

	@Column(name = "booking_date_time")
	private LocalDateTime bookingDateTime;

	private Boolean isAvailableBooking;

	private int remainCounts;

	private int restaurantCapacity;

	protected BookingSchedule() {
	}

	public BookingSchedule(Long restaurantId, LocalDateTime bookingDateTime, int restaurantCapacity) {
		this.restaurantId = restaurantId;
		this.bookingDateTime = bookingDateTime;
		this.remainCounts = restaurantCapacity;
		this.restaurantCapacity = restaurantCapacity;
		this.isAvailableBooking = isOverThanRemainCounts();
	}

	private boolean isOverThanRemainCounts() {
		return remainCounts > 0;
	}

	public Long getId() {
		return id;
	}

	public Long getRestaurantId() {
		return restaurantId;
	}

	public LocalDateTime getBookingDateTime() {
		return bookingDateTime;
	}

	public Boolean getAvailableBooking() {
		return isAvailableBooking;
	}

	public int getRemainCounts() {
		return remainCounts;
	}

	public int getRestaurantCapacity() {
		return restaurantCapacity;
	}

	@Override
	public String toString() {
		return "BookingSchedule{" +
			"id=" + id +
			", restaurantId=" + restaurantId +
			", bookingDateTime=" + bookingDateTime +
			", isAvailableBooking=" + isAvailableBooking +
			", remainCounts=" + remainCounts +
			", restaurantCapacity=" + restaurantCapacity +
			'}';
	}

	public BookingSchedule booking(int bookingCounts) {
		int result = this.remainCounts - bookingCounts;

		if (result < 0) {
			throw new IllegalArgumentException("예약 허용 인원수 초과");
		}

		this.remainCounts = result;

		isAvailableBooking = isOverThanRemainCounts();

		return this;
	}

	public boolean isLowerThanRemainCounts(int visitorCount) {
		boolean isLowerThanRemainCounts = remainCounts - visitorCount >= 0;

		boolean hasRemainBookingCount = this.isAvailableBooking && isLowerThanRemainCounts;

		return hasRemainBookingCount;
	}
}
