package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoParam;
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoProj;
import com.prgms.allen.dining.domain.reservation.dto.VisitorCountPerVisitTimeProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public class FakeReservationRepository implements ReservationRepository {

	private final List<Reservation> reservations = new ArrayList<>();
	private Long id = 0L;

	@Override
	public Page<Reservation> findAllByRestaurantAndStatus(Restaurant restaurant, ReservationStatus status,
		Pageable pageable) {
		return new PageImpl<>(
			reservations.stream()
				.filter(reservation -> Objects.equals(reservation.getRestaurantId(), restaurant.getId()))
				.filter(reservation -> reservation.getStatus() == status)
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.toList()
		);
	}

	@Override
	public Page<Reservation> findAllByCustomerAndStatusIn(Member customer, List<ReservationStatus> statuses,
		Pageable pageable) {
		return new PageImpl<>(
			reservations.stream()
				.filter(reservation -> Objects.equals(reservation.getCustomerId(), customer.getId()))
				.filter(reservation -> statuses.contains(reservation.getStatus()))
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.toList());
	}

	@Override
	public Optional<Reservation> findByIdAndCustomer(Long reservationId, Member customer) {
		return reservations.stream()
			.filter(reservation -> Objects.equals(reservation.getId(), reservationId))
			.filter(reservation -> Objects.equals(reservation.getCustomer().getId(), customer.getId()))
			.findFirst();
	}

	@Override
	public List<VisitorCountPerVisitTimeProj> findVisitorCountPerVisitTime(
		Restaurant restaurant,
		LocalDate date,
		List<ReservationStatus> statuses
	) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Integer> countTotalVisitorCount(Restaurant restaurant,
		LocalDate visitDate,
		LocalTime visitTime,
		List<ReservationStatus> statuses) {
		return reservations.stream()
			.filter(reservation -> restaurant.getId().equals(reservation.getRestaurantId()))
			.filter(reservation -> reservation.getVisitDateTime()
				.toLocalDate()
				.equals(visitDate))
			.filter(reservation -> reservation.getVisitDateTime()
				.toLocalTime()
				.equals(visitTime))
			.map(Reservation::getVisitorCount)
			.reduce(Integer::sum);
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
		Reservation newReservation = Reservation.newTestInstance(
			++id,
			entity.getCustomer(),
			entity.getRestaurant(),
			entity.getStatus(),
			entity.getCustomerInput()
		);
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
		return reservations.stream()
			.filter(reservation -> Objects.equals(reservation.getId(), aLong))
			.findFirst();
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

	@Override
	public CustomerReservationInfoProj findCustomerReservationInfo(
		CustomerReservationInfoParam customerReservationInfoParam) {
		return new CustomerReservationInfoProj(
			reservations.stream()
				.filter(reservation -> reservation.getId().equals(customerReservationInfoParam.id()))
				.map(Reservation::getCustomerName)
				.findAny()
				.get(),
			reservations.stream()
				.filter(reservation -> reservation.getId().equals(customerReservationInfoParam.id()))
				.map(Reservation::getCustomerPhone)
				.findAny()
				.get(),
			reservations.stream()
				.filter(reservation -> reservation.getId().equals(customerReservationInfoParam.id()))
				.filter(reservation -> reservation.getStatus() == ReservationStatus.VISITED)
				.count(),
			reservations.stream()
				.filter(reservation -> reservation.getId().equals(customerReservationInfoParam.id()))
				.filter(reservation -> reservation.getStatus() == ReservationStatus.NO_SHOW)
				.count(),
			reservations.stream()
				.filter(reservation -> reservation.getId().equals(customerReservationInfoParam.id()))
				.filter(reservation -> reservation.getStatus() == ReservationStatus.VISITED)
				.sorted((o1, o2) -> {
					if (o1.getVisitDateTime().compareTo(o2.getVisitDateTime()) > 0) {
						return 1;
					} else if (o1.getVisitDateTime().compareTo(o2.getVisitDateTime()) < 0) {
						return -1;
					}
					return 0;
				})
				.limit(1)
				.map(Reservation::getVisitDateTime)
				.findAny()
				.get()
				.toString()
		);
	}
}
