package com.prgms.allen.dining.domain.restaurant.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Menu {

	@Id
	@Column(name = "menu_id")
	private Long id;

	@Column(name = "name", length = 30, nullable = false)
	private String name;

	@Column(name = "price", nullable = false, precision = 6)
	private BigDecimal price;

	@Lob
	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	protected Menu() {
	}

}
