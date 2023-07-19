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
		bookingScheduleRepository.findByRestaurantIdAndAndBookingDateTime(restaurantId, bookingDateTime)
			.orElse(generateSchedule(restaurantId, bookingDateTime, seatCount));

		return bookingScheduleRepository.getByRestaurantIdAndAndBookingDateTime(restaurantId, bookingDateTime);
	}

	private BookingSchedule generateSchedule(Long restaurantId, LocalDateTime bookingDateTime, int seatCount) {
		logger.warn("find로 못찾았기 때문에 없어서 생성하러 감");
		try {
			return bookingScheduleGenerator.generate(restaurantId, bookingDateTime, seatCount);
		} catch (DuplicatedException duplicatedException) {
			logger.warn("중복 생성 시도로 오류가 발생하여 찾아옵니다.");
			return bookingScheduleGenerator.retryGenerate(restaurantId, bookingDateTime, seatCount);
		}
	}

	@Transactional
	public void booking(BookingSchedule schedule, int visitorCount) {
		logger.warn("예약 진행 전 가능 인원수 : {}", schedule.getRemainCounts());
		schedule.booking(visitorCount);
		logger.warn("예약 가능 인원수 : {}", schedule.getRemainCounts());
	}

}
