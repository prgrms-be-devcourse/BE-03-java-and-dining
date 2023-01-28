package com.prgms.allen.dining.domain.reservation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReservation is a Querydsl query type for Reservation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservation extends EntityPathBase<Reservation> {

    private static final long serialVersionUID = 411775852L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReservation reservation = new QReservation("reservation");

    public final com.prgms.allen.dining.domain.common.entity.QBaseEntity _super = new com.prgms.allen.dining.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.prgms.allen.dining.domain.member.entity.QMember customer;

    public final QReservationCustomerInput customerInput;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.prgms.allen.dining.domain.restaurant.entity.QRestaurant restaurant;

    public final EnumPath<ReservationStatus> status = createEnum("status", ReservationStatus.class);

    public QReservation(String variable) {
        this(Reservation.class, forVariable(variable), INITS);
    }

    public QReservation(Path<? extends Reservation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReservation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReservation(PathMetadata metadata, PathInits inits) {
        this(Reservation.class, metadata, inits);
    }

    public QReservation(Class<? extends Reservation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new com.prgms.allen.dining.domain.member.entity.QMember(forProperty("customer")) : null;
        this.customerInput = inits.isInitialized("customerInput") ? new QReservationCustomerInput(forProperty("customerInput")) : null;
        this.restaurant = inits.isInitialized("restaurant") ? new com.prgms.allen.dining.domain.restaurant.entity.QRestaurant(forProperty("restaurant"), inits.get("restaurant")) : null;
    }

}

