package com.prgms.allen.dining.domain.restaurant.entity;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Menu {

	@Column(name = "name", length = 30, nullable = false)
	private String name;

	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@Column(name = "description")
	private String description;

	protected Menu() {
	}

	public Menu(String name, BigDecimal price, String description) {
		this.name = name;
		this.price = price;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Menu menu = (Menu)o;
		return Objects.equals(name, menu.name) && Objects.equals(price, menu.price)
			&& Objects.equals(description, menu.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, price, description);
	}
}
