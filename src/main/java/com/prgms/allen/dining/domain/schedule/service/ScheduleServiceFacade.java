package com.prgms.allen.dining.domain.schedule.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Component
public class ScheduleServiceFacade {

	private static final Logger log = LoggerFactory.getLogger(ScheduleServiceFacade.class);

	private final ScheduleService scheduleService;

	public ScheduleServiceFacade(ScheduleService scheduleService) {
		this.scheduleService = scheduleService;
	}

	public void fix(LocalDateTime dateTime, Restaurant restaurant, int visitorCount){
		while(true){
			try{
				scheduleService.fix(dateTime, restaurant, visitorCount);
				break;
			} catch(ObjectOptimisticLockingFailureException e){
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {
					log.info(ex.getMessage());
				}
			}
		}
	}
}
