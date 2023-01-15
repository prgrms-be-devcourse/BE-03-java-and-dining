package com.prgms.allen.dining.domain.restaurant.entity;

public enum FoodType {

	KOREAN("한식"),
	CHINESE("중식"),
	JAPANESE("일식"),
	WESTERN("양식");

	private final String foodType;

	FoodType(String foodType) {
		this.foodType = foodType;
	}
}
