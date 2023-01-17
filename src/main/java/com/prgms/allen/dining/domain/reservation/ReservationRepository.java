package com.prgms.allen.dining.domain.reservation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	Page<Reservation> findAllByRestaurantIdAndStatus(long restaurantId, ReservationStatus status, Pageable pageable);

	Page<Reservation> findAllByCustomerAndStatusIn(
		Member customer,
		List<ReservationStatus> statuses,
		Pageable pageable
	);
}
