package com.prgms.allen.dining.domain.reservation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReservationCustomerInput is a Querydsl query type for ReservationCustomerInput
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QReservationCustomerInput extends BeanPath<ReservationCustomerInput> {

    private static final long serialVersionUID = -2001596800L;

    public static final QReservationCustomerInput reservationCustomerInput = new QReservationCustomerInput("reservationCustomerInput");

    public final StringPath customerMemo = createString("customerMemo");

    public final DatePath<java.time.LocalDate> visitDate = createDate("visitDate", java.time.LocalDate.class);

    public final NumberPath<Integer> visitorCount = createNumber("visitorCount", Integer.class);

    public final TimePath<java.time.LocalTime> visitTime = createTime("visitTime", java.time.LocalTime.class);

    public QReservationCustomerInput(String variable) {
        super(ReservationCustomerInput.class, forVariable(variable));
    }

    public QReservationCustomerInput(Path<? extends ReservationCustomerInput> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReservationCustomerInput(PathMetadata metadata) {
        super(ReservationCustomerInput.class, metadata);
    }

}

