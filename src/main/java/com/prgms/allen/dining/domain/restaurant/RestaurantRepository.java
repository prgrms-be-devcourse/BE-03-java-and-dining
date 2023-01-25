package com.prgms.allen.dining.domain.restaurant;

import java.util.List;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

	boolean existsRestaurantByOwnerId(Long ownerId);

	Optional<Restaurant> findByIdAndOwner(Long id, Member owner);

	Page<Restaurant> findAll(Pageable pageable);

	Page<Restaurant> findAllByNameContains(Pageable pageable, String restaurantName);

	@Query("select m "
		+ "from Restaurant r "
		+ "join r.menu m "
		+ "where r.id = :id")
	List<Menu> getMenus(Pageable pageable, @Param("id") Long id);
}
