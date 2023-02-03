package com.prgms.allen.dining.domain.notification;

import com.prgms.allen.dining.domain.notification.slack.SlackNotifyService;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;

public class FakeSlackNotifyService extends SlackNotifyService {

	public FakeSlackNotifyService(String token, String customerChannel, String ownerChannel) {
		super(token, customerChannel, ownerChannel);
	}

	public FakeSlackNotifyService() {
		super(null, null, null);
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
