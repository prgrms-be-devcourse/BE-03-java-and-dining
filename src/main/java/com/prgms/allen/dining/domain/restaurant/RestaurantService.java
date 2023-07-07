package com.prgms.allen.dining.domain.restaurant;

import static com.prgms.allen.dining.domain.restaurant.dto.RestaurantOperationInfo.*;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.common.NotFoundResourceException;
import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.service.ReservationProvider;
import com.prgms.allen.dining.domain.restaurant.dto.ClosingDayRes;
import com.prgms.allen.dining.domain.restaurant.dto.MenuDetailRes;
import com.prgms.allen.dining.domain.restaurant.dto.MenuSimpleRes;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantCreateReq;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantDetailResForCustomer;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantDetailResForOwner;
import com.prgms.allen.dining.domain.restaurant.dto.RestaurantSimpleRes;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Service
@Transactional(readOnly = true)
public class RestaurantService {

	private final RestaurantRepository restaurantRepository;
	private final MemberService memberService;
	private final ReservationProvider reservationProvider;

	public RestaurantService(RestaurantRepository restaurantRepository, MemberService memberService,
		ReservationProvider reservationProvider) {
		this.restaurantRepository = restaurantRepository;
		this.memberService = memberService;
		this.reservationProvider = reservationProvider;
	}

	public Restaurant findById(Long restaurantId) {
		return restaurantRepository.findById(restaurantId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Restaurant entity for restaurant id = {0}", restaurantId)
			));
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
		Restaurant restaurant = findById(restaurantId);

		return new RestaurantDetailResForCustomer(restaurant,
			toMenuSimpleResList(restaurant.getMenu()),
			toClosingDayResList(restaurant.getClosingDays())
		);
	}

	public RestaurantDetailResForOwner getRestaurant(Long restaurantId, Long ownerId) {

		Restaurant restaurant = restaurantRepository.findByIdAndOwner(restaurantId, ownerId)
			.orElseThrow(() -> {
				throw new NotFoundResourceException(
					MessageFormat.format("Cannot find Restaurant entity for restaurant id = {0}, owner id = {1}",
						restaurantId, ownerId)
				);
			});

		return new RestaurantDetailResForOwner(restaurant,
			toMenuSimpleResList(restaurant.getMinorMenu()),
			toClosingDayResList(restaurant.getClosingDays())
		);
	}

	private void validAlreadyHasRestaurant(Long ownerId) {
		if (restaurantRepository.existsRestaurantByOwnerId(ownerId)) {
			throw new RestaurantDuplicateCreationException(
				MessageFormat.format(
					"Owner id: {0} try to create restaurant, but already has the restaurant", ownerId));
		}
	}

	public Page<RestaurantSimpleRes> getRestaurantList(Pageable pageable) {

		return new PageImpl<>(restaurantRepository.findAll(pageable)
			.stream()
			.map(restaurant -> {
				var operationInfo = toOperationInfo(restaurant);
				var reservationAvailableTimesRes = reservationProvider.getAvailableTimes(operationInfo);

				return RestaurantSimpleRes.toDto(restaurant, reservationAvailableTimesRes);
			})
			.toList());
	}

	public Page<RestaurantSimpleRes> getRestaurantsContains(Pageable pageable, String restaurantName) {

		return new PageImpl<>(restaurantRepository.findAllByNameContains(pageable, restaurantName)
			.stream()
			.map(restaurant -> {
				var operationInfo = toOperationInfo(restaurant);
				var reservationAvailableTimesRes = reservationProvider.getAvailableTimes(operationInfo);

				return RestaurantSimpleRes.toDto(restaurant, reservationAvailableTimesRes);
			})
			.toList());
	}

	public Page<MenuDetailRes> getMenus(Pageable pageable, Long id) {

		return new PageImpl<>(restaurantRepository.getMenus(pageable, id)
			.stream()
			.map(MenuDetailRes::new)
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