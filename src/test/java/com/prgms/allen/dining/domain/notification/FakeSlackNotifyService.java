package com.prgms.allen.dining.domain.notification;

import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public class FakeSlackNotifyService extends SlackNotifyService {

	@Override
	public void setToken(String value) {
	}

	@Override
	public void notifyReserve(Reservation reservation) {
	}

	@Override
	public void notifyConfirm(Reservation reservation) {
	}

	@Override
	public void notifyCancel(Reservation reservation) {
	}
}
