package com.prgms.allen.dining.domain.schedule.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.domain.schedule.entity.Schedule;
import com.prgms.allen.dining.domain.schedule.repository.ScheduleRepository;

@Service
@Transactional(readOnly = true)
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;

	public ScheduleService(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
	}

	@Transactional
	public void fix(LocalDateTime dateTime, Restaurant restaurant, int visitorCount){
		Schedule schedule = scheduleRepository.findByRestaurantAndDateTime(restaurant, dateTime)
			.orElseThrow(() -> new RuntimeException(
				"Schedule data isn't exist. Member can't reserve restaurant before Schedule data inserted."
			));
		schedule.fix(visitorCount);
	}

	@Transactional
	public void cancel(Restaurant restaurant, LocalDateTime dateTime, int visitorCount) {
		Schedule schedule = scheduleRepository.findByRestaurantAndDateTime(restaurant, dateTime)
			.orElseThrow(() -> new IllegalArgumentException(
				"Schedule for Restaurant %s and dateTime %s not found.".formatted(restaurant.getName(), dateTime)));

		schedule.cancel(visitorCount);
	}
}
