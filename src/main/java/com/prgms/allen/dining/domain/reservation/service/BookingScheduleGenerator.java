package com.prgms.allen.dining.domain.reservation.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.BookingScheduleRepository;
import com.prgms.allen.dining.domain.reservation.DuplicatedException;
import com.prgms.allen.dining.domain.reservation.entity.BookingSchedule;

@Service
public class BookingScheduleGenerator {

	private final Logger logger = LoggerFactory.getLogger(BookingScheduleGenerator.class);

	private final BookingScheduleRepository bookingScheduleRepository;

	public BookingScheduleGenerator(BookingScheduleRepository bookingScheduleRepository) {
		this.bookingScheduleRepository = bookingScheduleRepository;
	}

	public BookingSchedule generate(Long restaurantId, LocalDateTime bookingDateTime, int restaurantCapacity) {
		BookingSchedule schedule = new BookingSchedule(restaurantId, bookingDateTime, restaurantCapacity);

		try {
			return bookingScheduleRepository.save(schedule);
		} catch (PersistenceException | DataIntegrityViolationException dataIntegrityViolationException) {
			logger.warn("{}번 레스토랑의 {}의 예약 현황 로우가 이미 생성되었기 때문에 찾아옵니다.", restaurantId, bookingDateTime);

			String errorMessage = MessageFormat.format("{0}번 레스토랑의 {1}의 예약 현황 로우가 이미 생성되었기 때문에 찾아옵니다.",
				restaurantId, bookingDateTime);

			throw new DuplicatedException(errorMessage);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	public BookingSchedule retryGenerate(Long restaurantId, LocalDateTime bookingDateTime, int seatCounts) {
		BookingSchedule schedule = bookingScheduleRepository.getByRestaurantIdAndAndBookingDateTime(
			restaurantId, bookingDateTime);

		if (schedule == null) {
			logger.warn("다시 시도한 생성에서 데이터가 없다고 판단하여 생성을 시도합니다.");
			return generate(restaurantId, bookingDateTime, seatCounts);
		}

		return schedule;

	}
}
