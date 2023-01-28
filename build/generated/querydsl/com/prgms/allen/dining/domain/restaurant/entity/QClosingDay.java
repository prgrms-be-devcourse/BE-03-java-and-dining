package com.prgms.allen.dining.domain.restaurant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QClosingDay is a Querydsl query type for ClosingDay
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QClosingDay extends BeanPath<ClosingDay> {

    private static final long serialVersionUID = -519841942L;

    public static final QClosingDay closingDay = new QClosingDay("closingDay");

    public final EnumPath<java.time.DayOfWeek> dayOfWeek = createEnum("dayOfWeek", java.time.DayOfWeek.class);

    public QClosingDay(String variable) {
        super(ClosingDay.class, forVariable(variable));
    }

    public QClosingDay(Path<? extends ClosingDay> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClosingDay(PathMetadata metadata) {
        super(ClosingDay.class, metadata);
    }

}

