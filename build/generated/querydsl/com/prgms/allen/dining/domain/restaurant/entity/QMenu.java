package com.prgms.allen.dining.domain.restaurant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMenu is a Querydsl query type for Menu
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QMenu extends BeanPath<Menu> {

    private static final long serialVersionUID = -193723294L;

    public static final QMenu menu = new QMenu("menu");

    public final StringPath description = createString("description");

    public final StringPath name = createString("name");

    public final NumberPath<java.math.BigInteger> price = createNumber("price", java.math.BigInteger.class);

    public QMenu(String variable) {
        super(Menu.class, forVariable(variable));
    }

    public QMenu(Path<? extends Menu> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMenu(PathMetadata metadata) {
        super(Menu.class, metadata);
    }

}

