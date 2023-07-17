package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.entity.BookingSchedule;
import com.prgms.allen.dining.domain.reservation.service.BookingScheduleGenerator;

@Service
@Transactional(readOnly = true)
public class BookingScheduleService {

	private final Logger logger = LoggerFactory.getLogger(BookingScheduleService.class);

	private final BookingScheduleRepository bookingScheduleRepository;

	private final BookingScheduleGenerator bookingScheduleGenerator;

	public BookingScheduleService(BookingScheduleRepository bookingScheduleRepository,
		BookingScheduleGenerator bookingScheduleGenerator) {
		this.bookingScheduleRepository = bookingScheduleRepository;
		this.bookingScheduleGenerator = bookingScheduleGenerator;
	}

	public BookingSchedule findSchedule(Long restaurantId, LocalDateTime bookingDateTime, int seatCount) {
		return bookingScheduleRepository.findByRestaurantIdAndAndBookingDateTime(restaurantId, bookingDateTime)
			.orElse(generateSchedule(restaurantId, bookingDateTime, seatCount));
	}

	private BookingSchedule generateSchedule(Long restaurantId, LocalDateTime bookingDateTime, int seatCount) {
		try {
			return bookingScheduleGenerator.generate(restaurantId, bookingDateTime, seatCount);
		} catch (DuplicatedException duplicatedException) {
			logger.warn("생성에 실패하여 다시 시도합니다.");
			return bookingScheduleGenerator.retryGenerate(restaurantId, bookingDateTime, seatCount);
		}
	}

	public void booking(BookingSchedule schedule, int visitorCount) {
		bookingScheduleRepository.save(schedule.booking(visitorCount));
	}

}
