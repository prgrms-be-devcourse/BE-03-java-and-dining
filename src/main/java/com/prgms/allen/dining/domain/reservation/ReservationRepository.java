package com.prgms.allen.dining.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
