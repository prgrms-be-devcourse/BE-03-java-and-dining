package com.prgms.allen.dining.domain.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

	boolean existsRestaurantByOwnerId(Long ownerId);

	Page<Restaurant> findAll(Pageable pageable);
}
