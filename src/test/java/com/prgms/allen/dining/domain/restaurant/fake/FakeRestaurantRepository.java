package com.prgms.allen.dining.domain.restaurant.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public class FakeRestaurantRepository implements RestaurantRepository {

	private final List<Restaurant> restaurants = new ArrayList<>();

	@Override
	public List<Restaurant> findAll() {
		return restaurants;
	}

	@Override
	public List<Restaurant> findAll(Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Page<Restaurant> findAll(Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Restaurant> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		return restaurants.size();
	}

	@Override
	public void deleteById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Restaurant entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll(Iterable<? extends Restaurant> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		restaurants.clear();
	}

	@Override
	public <S extends Restaurant> S save(S entity) {
		Restaurant restaurant = new Restaurant(
			(long)count() + 1,
			entity.getOwner(),
			entity.getFoodType(),
			entity.getName(),
			entity.getCapacity(),
			entity.getOpenTime(),
			entity.getLastOrderTime(),
			entity.getLocation(),
			entity.getDescription(),
			entity.getPhone(),
			entity.getMenu(),
			entity.getClosingDays()
		);
		restaurants.add(restaurant);
		return (S)restaurant;
	}

	@Override
	public <S extends Restaurant> List<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Restaurant> findById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean existsById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Restaurant> S saveAndFlush(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Restaurant> List<S> saveAllAndFlush(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllInBatch(Iterable<Restaurant> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllInBatch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Restaurant getOne(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Restaurant getById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Restaurant getReferenceById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Restaurant> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends Restaurant> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Restaurant> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Restaurant> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Restaurant> long count(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Restaurant> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Restaurant, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean existsRestaurantByOwner_Id(Long ownerId) {
		return restaurants.stream()
			.filter(restaurant -> ownerId.equals(restaurant.getOwner().getId())).isParallel();
	}
}