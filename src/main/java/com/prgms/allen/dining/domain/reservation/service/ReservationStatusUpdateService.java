package com.prgms.allen.dining.domain.reservation.service;

import com.prgms.allen.dining.domain.reservation.dto.ReservationStatusUpdateReq;

public interface ReservationStatusUpdateService {

	void update(Long reservationId, Long memberId, ReservationStatusUpdateReq statusUpdateReq);
}
