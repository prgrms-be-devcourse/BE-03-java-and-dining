package com.prgms.allen.dining.domain.schedule.service;

import java.time.LocalDateTime;
import java.util.Optional;

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

	public void fix(LocalDateTime dateTime, Restaurant restaurant, int visitorCount) {
		scheduleRepository.findByRestaurantAndDateTime(restaurant, dateTime)
			.ifPresentOrElse(schedule -> schedule.fix(visitorCount),
				() -> scheduleRepository.save(Schedule.ofFirstSchedule(dateTime, restaurant, visitorCount))
			);
	}

	public void cancel(Restaurant restaurant, LocalDateTime dateTime, int visitorCount) {
		Schedule schedule = scheduleRepository.findByRestaurantAndDateTime(restaurant, dateTime)
			.orElseThrow(() -> new IllegalArgumentException(
				"Schedule for Restaurant %s and dateTime %s not found.".formatted(restaurant.getName(), dateTime)));

		schedule.cancel(visitorCount);
	}
}
