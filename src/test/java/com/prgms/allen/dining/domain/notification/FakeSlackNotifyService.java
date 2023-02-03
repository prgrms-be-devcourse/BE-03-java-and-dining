package com.prgms.allen.dining.domain.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public class FakeSlackNotifyService extends SlackNotifyService {

	private static final Logger log = LoggerFactory.getLogger(FakeSlackNotifyService.class);

	public FakeSlackNotifyService() {
		super(null, null, null);
		log.info(
			"FakeSlackNotifyService.constructor() called: this is class for test and service code won't be called");
	}

	@Override
	public void notifyReserve(Reservation reservation) {
		log.info("FakeSlackNotifyService.notifyReserve() called because service code doesn't need to be called");
	}

	@Override
	public void notifyConfirm(Reservation reservation) {
		log.info("FakeSlackNotifyService.notifyConfirm() called because service code doesn't need to be called");
	}

	@Override
	public void notifyCancel(Reservation reservation) {
		log.info("FakeSlackNotifyService.notifyCancel() called because service code doesn't need to be called");
	}
}
