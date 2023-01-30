package com.prgms.allen.dining.event;

import com.prgms.allen.dining.domain.reservation.dto.ReservationCreateReq;

public interface NotificationService {

	Long reserve(Long customerId, ReservationCreateReq createReq);

}
