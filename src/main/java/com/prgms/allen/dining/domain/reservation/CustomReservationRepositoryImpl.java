package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.QMember;
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoParam;
import com.prgms.allen.dining.domain.reservation.dto.CustomerReservationInfoProj;
import com.prgms.allen.dining.domain.reservation.entity.QReservation;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.entity.QRestaurant;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

@Repository
public class CustomReservationRepositoryImpl extends QuerydslRepositorySupport
	implements CustomReservationRepository {

	public static final String VISITED_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm";
	public static final String DATE_TIME_CONCAT_DELIMITER = " ";
	public static final String DATE_TIME_FORMAT_FUNCTION = "CAST(FORMATDATETIME({0}, {1}) as java.lang.String)";
	public static final String DATE_TIME_CONCAT_FUNCTION = "CAST(CONCAT_WS({0},{1}, {2}) as java.lang.String)";

	public CustomReservationRepositoryImpl() {
		super(Reservation.class);
	}

	@Override
	public CustomerReservationInfoProj findCustomerReservationInfo(
		CustomerReservationInfoParam customerReservationInfoParam
	) {

		final QReservation reservation = QReservation.reservation;
		final QMember customer = QMember.member;
		final QRestaurant restaurant = QRestaurant.restaurant;

		final JPQLQuery<Reservation> query = from(reservation);
		query.join(customer).on(reservation.customer.eq(customer))
			.join(restaurant).on(reservation.restaurant.eq(restaurant));

		return query.distinct()
			.select(Projections.fields(CustomerReservationInfoProj.class,
				customer.name,
				customer.phone,
				ExpressionUtils.as(
					selectCountByStatus(reservation, ReservationStatus.VISITED),
					"visitedCount"),
				ExpressionUtils.as(
					selectCountByStatus(reservation, ReservationStatus.NO_SHOW),
					"noShowCount"),
				ExpressionUtils.as(
					selectLastVisitedDateTime(reservation),
					"lastVisitedDateTime")))
			.from(reservation)
			.where(
				customer.eq(selectReservationCustomer(customerReservationInfoParam, reservation, customer))
					.and(
						restaurant.eq(
							selectReservationRestaurant(customerReservationInfoParam, reservation, restaurant))
					)
			)
			.fetchOne();
	}

	private JPQLQuery<Member> selectReservationCustomer(
		CustomerReservationInfoParam customerReservationInfoParam,
		QReservation reservation,
		QMember customer
	) {
		return JPAExpressions.select(customer)
			.from(reservation)
			.where(reservation.id.eq(customerReservationInfoParam.reservationId()));
	}

	private JPQLQuery<Restaurant> selectReservationRestaurant(
		CustomerReservationInfoParam customerReservationInfoParam,
		QReservation reservation,
		QRestaurant restaurant
	) {
		return JPAExpressions.select(restaurant)
			.from(reservation)
			.where(reservation.id.eq(customerReservationInfoParam.reservationId()));
	}

	private StringTemplate formatVisitedDateTime(QReservation reservation) {

		return Expressions.stringTemplate(
			DATE_TIME_FORMAT_FUNCTION,
			Expressions.dateTemplate(LocalDateTime.class,
				DATE_TIME_CONCAT_FUNCTION,
				DATE_TIME_CONCAT_DELIMITER,
				reservation.customerInput.visitDate,
				reservation.customerInput.visitTime),
			ConstantImpl.create(VISITED_DATE_TIME_FORMAT_PATTERN));
	}

	private JPQLQuery<String> selectLastVisitedDateTime(QReservation reservation) {

		final StringTemplate formattedVisitedDateTime = formatVisitedDateTime(reservation);

		return JPAExpressions.select(formattedVisitedDateTime.max())
			.from(reservation)
			.where(reservation.status.eq(ReservationStatus.VISITED))
			.groupBy(reservation.status);
	}

	private JPQLQuery<Long> selectCountByStatus(QReservation reservation, ReservationStatus status) {

		return JPAExpressions.select(reservation.id.count())
			.from(reservation)
			.groupBy(reservation.status)
			.having(reservation.status.eq(status));
	}
}
