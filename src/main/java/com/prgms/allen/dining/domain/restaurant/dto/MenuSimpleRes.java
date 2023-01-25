package com.prgms.allen.dining.domain.restaurant.dto;

import java.math.BigInteger;

import com.prgms.allen.dining.domain.restaurant.entity.Menu;

public record MenuSimpleRes(
	String name,
	BigInteger price
) {
	public MenuSimpleRes(Menu menu) {
		this(
			menu.getName(),
			menu.getPrice()
		);
	}
}
