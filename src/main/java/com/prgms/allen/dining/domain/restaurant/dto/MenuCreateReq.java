package com.prgms.allen.dining.domain.restaurant.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.prgms.allen.dining.domain.restaurant.entity.Menu;

public record MenuCreateReq(

	@NotBlank
	String name,

	@NotNull
	BigDecimal price,

	String description
) {
	public Menu toMenu() {
		return new Menu(name, price, description);
	}
}
