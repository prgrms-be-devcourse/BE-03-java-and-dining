package com.prgms.allen.dining.domain.schedule.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Entity
@Table(uniqueConstraints = {
	@UniqueConstraint(
		name = "dateTimeRestaurantUnique",
		columnNames = {
			"date_time",
			"restaurant_id"
		}
	)
})
public class Schedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "date_time")
	private LocalDateTime dateTime;

	@Column(columnDefinition = "integer CHECK capacity >= 0 ")
	private int capacity;

	@ManyToOne
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@Version
	private Integer version;

	protected Schedule(){}

	private Schedule(LocalDateTime dateTime, Restaurant restaurant){
		this.dateTime = dateTime;
		this.capacity = restaurant.getCapacity();
		this.restaurant = restaurant;
	}

	public static Schedule ofFirstSchedule(LocalDateTime dateTime, Restaurant restaurant, int visitorCount){
		Schedule schedule = new Schedule(dateTime, restaurant);
		schedule.fix(visitorCount);
		return schedule;
	}

	public void fix(int visitorCount){
		if(capacity < visitorCount){
			System.out.println("Schedule.fix IllegalStateException");
			throw new IllegalStateException(
				"Reservation fail. Capacity of restaurant '%s' on date time '%s' is under visitorCount %d".formatted(
					restaurant.getName(), dateTime, visitorCount)
			);
		}
		capacity -= visitorCount;
	}

	public void cancel(int visitorCount){
		capacity += visitorCount;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public int getCapacity() {
		return capacity;
	}
}
