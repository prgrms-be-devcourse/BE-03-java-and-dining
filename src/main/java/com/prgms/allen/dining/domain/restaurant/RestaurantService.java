package com.prgms.allen.dining.domain.restaurant;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.restaurant.dto.ClosingDayRes;
import com.prgms.allen.dining.domain.restaurant.dto.MenuSimpleRes;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantSimpleRes;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantDetailResForCustomer;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantDetailResForOwner;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.error.ErrorCode;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;
import com.prgms.allen.dining.global.error.exception.RestaurantDuplicateCreationException;

@Service
@Transactional(readOnly = true)
public class RestaurantService {

	private final RestaurantRepository restaurantRepository;
	private final MemberService memberService;

	public RestaurantService(RestaurantRepository restaurantRepository, MemberService memberService) {
		this.restaurantRepository = restaurantRepository;
		this.memberService = memberService;
	}

	public Restaurant findById(Long restaurantId) {
		return restaurantRepository.findById(restaurantId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Restaurant entity for restaurant id = {0}", restaurantId)
			));
	}

	public void validateRestaurantExists(long restaurantId) {
		if (!restaurantRepository.existsById(restaurantId)) {
			throw new NotFoundResourceException(
				MessageFormat.format("Cannot find Restaurant entity for restaurant id = {0}", restaurantId)
			);
		}
	}

	@Transactional
	public long save(RestaurantCreateReq restaurantCreateReq, Long ownerId) {

		validAlreadyHasRestaurant(ownerId);

		final Member owner = memberService.findOwnerById(ownerId);
		final Restaurant restaurant = restaurantRepository.save(
			restaurantCreateReq.toEntity(owner)
		);

		return restaurant.getId();
	}

	public RestaurantDetailResForCustomer getRestaurant(Long restaurantId) {
		final Restaurant restaurant = findById(restaurantId);

		return new RestaurantDetailResForCustomer(restaurant,
			toMenuSimpleResList(restaurant.getMinorMenu()),
			toClosingDayResList(restaurant.getClosingDays())
		);
	}

	public RestaurantDetailResForOwner getRestaurant(Long restaurantId, Long ownerId) {
		Member owner = memberService.findOwnerById(ownerId);

		Restaurant restaurant = restaurantRepository.findByIdAndOwner(restaurantId, owner)
			.orElseThrow(() -> {
				throw new IllegalArgumentException("No Authorization");
			});

		return new RestaurantDetailResForOwner(restaurant,
			toMenuSimpleResList(restaurant.getMinorMenu()),
			toClosingDayResList(restaurant.getClosingDays())
		);
	}

	private void validAlreadyHasRestaurant(Long ownerId) {
		if (restaurantRepository.existsRestaurantByOwnerId(ownerId)) {
			throw new RestaurantDuplicateCreationException(ErrorCode.DUPLICATE_ERROR);
		}
	}

	public Page<RestaurantSimpleRes> getRestaurantList(Pageable pageable) {

		return new PageImpl<>(restaurantRepository.findAll(pageable)
			.stream()
			.map(RestaurantSimpleRes::new)
			.toList());
	}

	private List<MenuSimpleRes> toMenuSimpleResList(List<Menu> menu) {
		return menu.stream()
			.map(MenuSimpleRes::new)
			.toList();
	}

	private List<ClosingDayRes> toClosingDayResList(List<ClosingDay> closingDayList) {
		return closingDayList.stream()
			.map(ClosingDayRes::new)
			.toList();
	}
}