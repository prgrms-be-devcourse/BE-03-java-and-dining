package com.prgms.allen.dining.domain.restaurant.dto;

import java.time.LocalTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public record RestaurantCreateRequest(

	@NotBlank
	FoodType foodType,

	@NotBlank
	String name,

	@NotNull
	int capacity,

	@NotNull
	LocalTime openTime,

	@NotNull
	LocalTime lastOrderTime,

	@NotBlank
	String location,

	String description,

	@NotBlank
	String phone,

	List<MenuCreateRequest> menuList,

	List<ClosingDayCreateRequest> closingDays
) {

	public Restaurant toEntity(Member owner) {
		List<Menu> menus = menuList.stream()
			.map(MenuCreateRequest::toEntity)
			.toList();
		List<ClosingDay> closingDayList = closingDays.stream()
			.map(ClosingDayCreateRequest::toClosingDay)
			.toList();

		return new Restaurant(
			owner,
			foodType,
			name,
			capacity,
			openTime,
			lastOrderTime,
			location,
			description,
			phone,
			menus,
			closingDayList);
	}
}
