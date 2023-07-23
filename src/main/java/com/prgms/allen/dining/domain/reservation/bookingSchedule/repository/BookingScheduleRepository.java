package com.prgms.allen.dining.domain.reservation.bookingSchedule.repository;

import static org.hibernate.annotations.QueryHints.*;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import com.prgms.allen.dining.domain.reservation.bookingSchedule.entity.BookingSchedule;

public interface BookingScheduleRepository
	extends JpaRepository<BookingSchedule, Long>, BookingScheduleRepositoryCustom {

	Optional<BookingSchedule> findByRestaurantIdAndAndBookingDateTime(Long restaurantId, LocalDateTime bookingDateTime);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints(value = {@QueryHint(name = FLUSH_MODE, value = "COMMIT")})
	BookingSchedule getByRestaurantIdAndAndBookingDateTime(Long restaurantId, LocalDateTime bookingDateTime);

}

interface BookingScheduleRepositoryCustom {

	BookingSchedule findOne(Long restaurantId, LocalDateTime bookingDateTime);

}