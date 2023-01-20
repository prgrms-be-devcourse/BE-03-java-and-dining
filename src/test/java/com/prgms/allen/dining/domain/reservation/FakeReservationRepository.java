package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.prgms.allen.dining.domain.reservation.dto.VisitorCountsPerVisitTimeProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;

public class FakeReservationRepository implements ReservationRepository {

	private final List<Reservation> reservations = new ArrayList<>();

	@Override
	public Page<Reservation> findAllByRestaurantIdAndStatus(long restaurantId, ReservationStatus status,
		Pageable pageable) {
		return new PageImpl<>(
			reservations.stream()
				.filter(reservation -> reservation.getRestaurantId() == restaurantId)
				.filter(reservation -> reservation.getStatus() == status)
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.toList()
		);
	}

	@Override
	public List<VisitorCountsPerVisitTimeProj> findVisitorCountsPerVisitTime(LocalDate date,
		List<ReservationStatus> statuses) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Reservation> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Reservation> findAll(Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Page<Reservation> findAll(Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Reservation> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		return reservations.size();
	}

	@Override
	public void deleteById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Reservation entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll(Iterable<? extends Reservation> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		reservations.clear();
	}

	@Override
	public <S extends Reservation> S save(S entity) {
		Reservation newReservation = new Reservation(count(), entity.getCustomer(), entity.getRestaurant(),
			entity.getStatus(), entity.getDetail());
		reservations.add(newReservation);
		return (S)newReservation;
	}

	@Override
	public <S extends Reservation> List<S> saveAll(Iterable<S> entities) {
		reservations.addAll((Collection<? extends Reservation>)entities);
		return (List<S>)entities;
	}

	@Override
	public Optional<Reservation> findById(Long aLong) {
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
	public <S extends Reservation> S saveAndFlush(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Reservation> List<S> saveAllAndFlush(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllInBatch(Iterable<Reservation> entities) {
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
	public Reservation getOne(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reservation getById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reservation getReferenceById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Reservation> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Reservation> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Reservation> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Reservation> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Reservation> long count(Example<S> example) {
		return reservations.size();
	}

	@Override
	public <S extends Reservation> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Reservation, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException();
	}
}
