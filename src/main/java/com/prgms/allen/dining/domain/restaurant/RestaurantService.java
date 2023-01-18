package com.prgms.allen.dining.domain.restaurant;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateReq;
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

	public void validateRestaurantExists(long restaurantId) {
		if (!restaurantRepository.existsById(restaurantId)) {
			throw new NotFoundResourceException(
				ErrorCode.NOT_FOUND_RESOURCE,
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

	private void validAlreadyHasRestaurant(Long ownerId) {
		if (restaurantRepository.existsRestaurantByOwner_Id(ownerId)) {
			throw new RestaurantDuplicateCreationException(ErrorCode.DUPLICATE_ERROR);
		}
	}
}