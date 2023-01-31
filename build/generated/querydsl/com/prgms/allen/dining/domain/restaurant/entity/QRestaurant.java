package com.prgms.allen.dining.domain.restaurant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRestaurant is a Querydsl query type for Restaurant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurant extends EntityPathBase<Restaurant> {

    private static final long serialVersionUID = -358700032L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRestaurant restaurant = new QRestaurant("restaurant");

    public final NumberPath<Integer> capacity = createNumber("capacity", Integer.class);

    public final ListPath<ClosingDay, QClosingDay> closingDays = this.<ClosingDay, QClosingDay>createList("closingDays", ClosingDay.class, QClosingDay.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final EnumPath<FoodType> foodType = createEnum("foodType", FoodType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final TimePath<java.time.LocalTime> lastOrderTime = createTime("lastOrderTime", java.time.LocalTime.class);

    public final StringPath location = createString("location");

    public final ListPath<Menu, QMenu> menu = this.<Menu, QMenu>createList("menu", Menu.class, QMenu.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final TimePath<java.time.LocalTime> openTime = createTime("openTime", java.time.LocalTime.class);

    public final com.prgms.allen.dining.domain.member.entity.QMember owner;

    public final StringPath phone = createString("phone");

    public QRestaurant(String variable) {
        this(Restaurant.class, forVariable(variable), INITS);
    }

    public QRestaurant(Path<? extends Restaurant> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRestaurant(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRestaurant(PathMetadata metadata, PathInits inits) {
        this(Restaurant.class, metadata, inits);
    }

    public QRestaurant(Class<? extends Restaurant> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.owner = inits.isInitialized("owner") ? new com.prgms.allen.dining.domain.member.entity.QMember(forProperty("owner")) : null;
    }

}

