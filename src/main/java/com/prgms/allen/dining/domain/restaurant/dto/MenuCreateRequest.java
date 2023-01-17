package com.prgms.allen.dining.domain.restaurant.dto;

import java.math.BigDecimal;

import com.prgms.allen.dining.domain.restaurant.entity.Menu;

public record MenuCreateRequest(

	String name,
	BigDecimal price,
	String description
) {
	public Menu toEntity() {
		return new Menu(name, price, description);
	}
}
