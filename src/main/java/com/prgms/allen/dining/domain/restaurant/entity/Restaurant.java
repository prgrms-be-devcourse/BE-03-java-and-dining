package com.prgms.allen.dining.domain.restaurant.entity;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.springframework.util.Assert;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;

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

	@ElementCollection
	@CollectionTable(name = "Menu", joinColumns = @JoinColumn(name = "restaurant_id"))
	private List<Menu> menu = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "ClosingDay", joinColumns = @JoinColumn(name = "restaurant_id"))
	private List<ClosingDay> closingDays = new ArrayList<>();

	protected Restaurant() {
	}

	public Restaurant(Member owner, FoodType foodType, String name, int capacity, LocalTime openTime,
		LocalTime lastOrderTime, String location, String description, String phone) {
		validate(owner, name, capacity, phone, openTime, lastOrderTime, location);
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
		LocalTime lastOrderTime, String location, String description, String phone, List<Menu> menuList,
		List<ClosingDay> closingDays) {
		this(null, owner, foodType, name, capacity, openTime, lastOrderTime, location, description, phone,
			menuList, closingDays);
	}

	public Restaurant(Long id, Member owner, FoodType foodType, String name, int capacity, LocalTime openTime,
		LocalTime lastOrderTime, String location, String description, String phone, List<Menu> menuList,
		List<ClosingDay> closingDays) {
		validate(owner, name, capacity, phone, openTime, lastOrderTime, location);
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
		this.menu = menuList;
		this.closingDays = closingDays;
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

	public List<Menu> getMenu() {
		return List.copyOf(menu);
	}

	public List<ClosingDay> getClosingDays() {
		return List.copyOf(closingDays);
	}

	public List<Menu> getMinorMenu() {
		if (menu.size() < 5) {
			return this.getMenu();
		}
		return this.menu.subList(0, 4);
	}

	public boolean isAvailable(int totalCount, int requestCount) {
		return this.capacity - totalCount >= requestCount;
	}

	public boolean isNotReserveAvailableForDay(long totalCount) {
		long availableTotalCapacity = (long)(this.lastOrderTime.getHour() - this.openTime.getHour() + 1) * capacity;
		return availableTotalCapacity - totalCount < 2;
	}

	public boolean isClosingDay(LocalDate requestDate) {
		return this.closingDays.stream()
			.map(ClosingDay::getDayOfWeek)
			.anyMatch(dayOfWeek -> dayOfWeek.equals(requestDate.getDayOfWeek()));
	}

	public void validate(Member owner, String name, int capacity, String phone, LocalTime openTime,
		LocalTime lastOrderTime, String location) {
		validateOwnerType(owner);
		validateName(name);
		validateCapacity(capacity);
		validatePhone(phone);
		validateTimes(openTime, lastOrderTime);
		validateLocation(location);
	}

	private void validateOwnerType(Member owner) {
		Assert.notNull(owner, "Owner must not be empty");
		Assert.isTrue(MemberType.OWNER.equals(owner.getMemberType()),
			MessageFormat.format("member id: {0} is not owner, actually type is customer", owner.getId()));
	}

	private void validateName(String name) {
		Assert.isTrue(name.length() >= 1, "Length of name must over than 0");
		Assert.isTrue(name.length() <= 30, "Length of name must less than 31");
	}

	private void validateCapacity(int capacity) {
		Assert.isTrue(capacity >= 2, "Capacity must over than 1");
	}

	private void validatePhone(String phone) {
		Assert.hasLength(phone, "Phone must be not empty.");
		Assert.isTrue(phone.length() >= 9 && phone.length() <= 11, "Phone must between 9 and 11");
		Assert.isTrue(Pattern.matches("^[0-9]+$", phone), "Phone is invalid format");
	}

	private void validateTimes(LocalTime openTime, LocalTime lastOrderTime) {
		Assert.notNull(openTime, "openTime must not be empty");
		Assert.notNull(lastOrderTime, "lastOrderTime must not by empty");
	}

	private void validateLocation(String location) {
		Assert.hasLength(location, "Location must be not empty.");
	}
}
