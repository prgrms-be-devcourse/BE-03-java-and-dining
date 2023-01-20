package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgms.allen.dining.domain.reservation.dto.VisitorCountsPerVisitTimeDto;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	Page<Reservation> findAllByRestaurantIdAndStatus(long restaurantId, ReservationStatus status, Pageable pageable);

	@Query(
		"select new com.prgms.allen.dining.domain.reservation.dto.VisitorCountsPerVisitTimeDto(r.customerInput.visitTime, sum(r.customerInput.visitorCount)) "
			+ "from Reservation r "
			+ "where r.customerInput.visitDate = :date "
			+ "and r.status in (:statuses) "
			+ "group by r.customerInput.visitTime")
	List<VisitorCountsPerVisitTimeDto> findVisitorCountsPerVisitTime(
		@Param("date") LocalDate date,
		@Param("statuses") List<ReservationStatus> statuses
	);
}
