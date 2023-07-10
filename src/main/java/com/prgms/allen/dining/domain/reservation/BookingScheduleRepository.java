package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgms.allen.dining.domain.reservation.entity.BookingSchedule;

public interface BookingScheduleRepository
	extends JpaRepository<BookingSchedule, Long>, BookingScheduleRepositoryCustom {

	// @Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<BookingSchedule> findByRestaurantIdAndAndBookingDateTime(Long restaurantId, LocalDateTime bookingDateTime);

}

interface BookingScheduleRepositoryCustom {

	BookingSchedule findOne(Long restaurantId, LocalDateTime bookingDateTime);

}