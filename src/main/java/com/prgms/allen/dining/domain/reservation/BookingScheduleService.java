package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDateTime;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.prgms.allen.dining.domain.reservation.entity.BookingSchedule;

@Service
@Transactional(readOnly = true)
public class BookingScheduleService {

	private final Logger logger = LoggerFactory.getLogger(BookingScheduleService.class);

	private final BookingScheduleRepository bookingScheduleRepository;

	private final TransactionTemplate transactionTemplate;

	public BookingScheduleService(BookingScheduleRepository bookingScheduleRepository,
		TransactionTemplate transactionTemplate) {
		this.bookingScheduleRepository = bookingScheduleRepository;
		this.transactionTemplate = transactionTemplate;
	}

	public BookingSchedule findOrInit(Long restaurantId, LocalDateTime bookingDateTime, int restaurantCapacity) {

		return bookingScheduleRepository.findByRestaurantIdAndAndBookingDateTime(restaurantId, bookingDateTime)
			.orElse(save(restaurantId, bookingDateTime, restaurantCapacity));
	}

	public BookingSchedule save(Long restaurantId, LocalDateTime bookingDateTime, int restaurantCapacity) {
		BookingSchedule schedule = new BookingSchedule(restaurantId, bookingDateTime, restaurantCapacity);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		transactionTemplate.execute(status -> {
			try {
				return bookingScheduleRepository.saveAndFlush(schedule);
			} catch (PersistenceException | DataIntegrityViolationException dataIntegrityViolationException) {
				logger.warn("예약 현황 로우가 이미 생성되었기 때문에 찾아옵니다.");

				return this.get(restaurantId, bookingDateTime);
			}

			// return null;
		});

		return this.get(restaurantId, bookingDateTime);
	}

	public void booking(BookingSchedule schedule, int visitorCount) {
		bookingScheduleRepository.save(schedule.booking(visitorCount));
	}

	// @Transactional(propagation = Propagation.REQUIRES_NEW)
	public BookingSchedule get(Long restaurantId, LocalDateTime bookingDateTime) {
		return bookingScheduleRepository.findOne(restaurantId, bookingDateTime);
	}

}
