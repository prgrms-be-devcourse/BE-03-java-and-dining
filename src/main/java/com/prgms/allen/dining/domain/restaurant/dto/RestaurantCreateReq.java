package com.prgms.allen.dining.domain.restaurant.dto;

import java.time.LocalTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public record RestaurantCreateReq(

	@NotNull
	FoodType foodType,

	@NotBlank
	String name,

	int capacity,

	@NotNull
	@JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
	LocalTime openTime,

	@NotNull
	@JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
	LocalTime lastOrderTime,

	@NotBlank
	String location,

	String description,

	@NotBlank
	String phone,

	List<MenuCreateReq> menuList,

	List<ClosingDayCreateReq> closingDays
) {

	public Restaurant toEntity(Member owner) {

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
			toMenuValueObjectList(menuList),
			toClosingDayValueObjectList(closingDays)
		);
	}

	private List<ClosingDay> toClosingDayValueObjectList(List<ClosingDayCreateReq> closingDays) {
		List<ClosingDay> closingDayList = List.of();

		if (closingDays != null) {
			closingDayList = closingDays.stream()
				.map(ClosingDayCreateReq::toClosingDay)
				.toList();
		}

		return closingDayList;
	}

	private List<Menu> toMenuValueObjectList(List<MenuCreateReq> menuList) {
		List<Menu> menus = List.of();

		if (menuList != null) {
			menus = menuList.stream()
				.map(MenuCreateReq::toMenu)
				.toList();
		}

		return menus;
	}
}
