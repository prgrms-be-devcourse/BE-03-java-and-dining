package com.prgms.allen.dining.domain.reservation.bookingSchedule.repository;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.prgms.allen.dining.domain.reservation.bookingSchedule.entity.BookingSchedule;

public class BookingScheduleRepositoryCustomImpl implements BookingScheduleRepositoryCustom {

	private final EntityManager entityManager;

	public BookingScheduleRepositoryCustomImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public BookingSchedule findOne(Long restaurantId, LocalDateTime bookingDateTime) {
		CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
		CriteriaQuery<BookingSchedule> query = criteria.createQuery(BookingSchedule.class);
		Root<BookingSchedule> bookingSchedule = query.from(BookingSchedule.class);

		return entityManager.createQuery(
				query.select(bookingSchedule)
					.where(criteria.equal(bookingSchedule.get("restaurantId"), restaurantId),
						criteria.equal(bookingSchedule.get("bookingDateTime"), bookingDateTime))
			)
			.setFlushMode(FlushModeType.COMMIT)
			.getResultList()
			.stream()
			.findFirst()
			.orElse(null);
	}

}
