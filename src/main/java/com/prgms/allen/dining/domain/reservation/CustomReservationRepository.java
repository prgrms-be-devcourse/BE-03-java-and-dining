package com.prgms.allen.dining.domain.reservation;

import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoParam;
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoProj;

public interface CustomReservationRepository {

	CustomerReservationInfoProj findCustomerReservationInfo(CustomerReservationInfoParam customerReservationInfoParam);
}
