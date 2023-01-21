package com.prgms.allen.dining.domain.restaurant;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

	boolean existsRestaurantByOwnerId(Long ownerId);

	Optional<Restaurant> findByIdAndOwner(Long id, Member owner);

	Page<Restaurant> findAll(Pageable pageable);
}
