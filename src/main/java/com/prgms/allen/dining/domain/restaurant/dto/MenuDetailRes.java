package com.prgms.allen.dining.domain.restaurant.dto;

import java.math.BigInteger;

import com.prgms.allen.dining.domain.restaurant.entity.Menu;

public record MenuDetailRes(
	String name,
	BigInteger price,
	String description
) {
	public MenuDetailRes(Menu menu) {
		this(menu.getName(), menu.getPrice(), menu.getDescription());
	}
}
