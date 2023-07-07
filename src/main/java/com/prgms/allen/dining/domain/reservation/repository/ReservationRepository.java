package com.prgms.allen.dining.domain.reservation.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, CustomReservationRepository {

	@Query("select sum(r.customerInput.visitorCount) "
		+ " from Reservation r "
		+ " where r.restaurant = :restaurant "
		+ " AND r.status In (:statuses) "
		+ " AND r.customerInput.visitDate = :visitDate"
		+ " AND r.customerInput.visitTime = :visitTime")
	Optional<Integer> countTotalVisitorCount(
		@Param("restaurant") Restaurant restaurant,
		@Param("visitDate") LocalDate visitDate,
		@Param("visitTime") LocalTime visitTime,
		@Param("statuses") List<ReservationStatus> statuses);

	Page<Reservation> findAllByRestaurantAndStatus(
		Restaurant restaurant,
		ReservationStatus status,
		Pageable pageable
	);

	Page<Reservation> findAllByCustomerAndStatusIn(
		Member customer,
		List<ReservationStatus> statuses,
		Pageable pageable
	);

	Optional<Reservation> findByIdAndCustomer(
		Long reservationId,
		Member customer
	);

	@Query(
		"select r "
			+ "from Reservation r "
			+ "where r.restaurant.id = :restaurantId "
			+ "and r.customerInput.visitDate = :date "
			+ "and r.status in (:statuses) ")
	List<Reservation> findBookingCounts(
		@Param("restaurantId") Long restaurantId,
		@Param("date") LocalDate date,
		@Param("statuses") List<ReservationStatus> statuses
	);

	@Query("select r "
		+ "from Reservation r "
		+ "where r.restaurant.id = :restaurantId "
		+ "AND r.status IN (:statues) ")
	List<Reservation> findTotalVisitorCountPerDay(
		@Param("restaurantId") Long restaurantId,
		@Param("statues") List<ReservationStatus> statuses
	);
}
