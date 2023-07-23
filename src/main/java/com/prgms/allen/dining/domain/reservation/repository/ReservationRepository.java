package com.prgms.allen.dining.domain.reservation.repository;

import java.time.LocalDate;
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

public interface ReservationRepository extends JpaRepository<Reservation, Long>, CustomReservationRepository {

	Page<Reservation> findAllByRestaurantIdAndStatus(
		Long restaurantId,
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
			+ "where r.restaurantId = :restaurantId "
			+ "and r.customerInput.visitDate = :date "
			+ "and r.status in (:statuses) ")
	List<Reservation> findAllByDateAndStatus(
		@Param("restaurantId") Long restaurantId,
		@Param("date") LocalDate date,
		@Param("statuses") List<ReservationStatus> statuses
	);

	@Query("select r "
		+ "from Reservation r "
		+ "where r.restaurantId = :restaurantId "
		+ "AND r.status IN (:statues) ")
	List<Reservation> findAllByRestaurantIdAndStatus(
		@Param("restaurantId") Long restaurantId,
		@Param("statues") List<ReservationStatus> statuses
	);
}
