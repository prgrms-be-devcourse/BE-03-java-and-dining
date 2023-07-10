package com.prgms.allen.dining.domain.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.domain.schedule.entity.Schedule;
import com.prgms.allen.dining.domain.schedule.repository.ScheduleRepository;

public class FakeScheduleRepository implements ScheduleRepository {

	private final List<Schedule> schedules = new ArrayList<>();

	@Override
	public Optional<Schedule> findByRestaurantAndDateTime(Restaurant restaurant, LocalDateTime dateTime) {
		return schedules.stream()
			.filter(schedule -> schedule.getDateTime().equals(dateTime) && schedule.getRestaurant().equals(restaurant))
			.findFirst();
	}

	@Override
	public <S extends Schedule> S save(S entity) {
		Schedule schedule = Schedule.ofFirstSchedule(entity.getDateTime(), entity.getRestaurant(),
			entity.getRestaurant().getCapacity() - entity.getCapacity());
		schedules.add(schedule);
		return (S)schedules;
	}

	@Override
	public <S extends Schedule> List<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() {

	}

	@Override
	public <S extends Schedule> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends Schedule> List<S> saveAllAndFlush(Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Schedule> entities) {

	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> longs) {

	}

	@Override
	public void deleteAllInBatch() {

	}

	@Override
	public Schedule getOne(Long aLong) {
		return null;
	}

	@Override
	public Schedule getById(Long aLong) {
		return null;
	}

	@Override
	public Schedule getReferenceById(Long aLong) {
		return null;
	}

	@Override
	public <S extends Schedule> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends Schedule> List<S> findAll(Example<S> example) {
		return null;
	}

	@Override
	public <S extends Schedule> List<S> findAll(Example<S> example, Sort sort) {
		return null;
	}

	@Override
	public <S extends Schedule> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends Schedule> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends Schedule> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends Schedule, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

	@Override
	public Optional<Schedule> findById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean existsById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Schedule> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Schedule> findAll(Sort sort) {
		return null;
	}

	@Override
	public Page<Schedule> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public List<Schedule> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Schedule entity) {
		schedules.remove(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll(Iterable<? extends Schedule> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException();
	}
}
