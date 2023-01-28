create table `member`
(
    `member_id`   bigint             not null auto_increment,
    `member_type` varchar(255)       not null,
    `name`        varchar(5)         not null,
    `nickname`    varchar(20) unique not null,
    `password`    varchar(20)        not null,
    `phone`       varchar(11)        not null,
    primary key (`member_id`)
);

create table `restaurant`
(
    `restaurant_id`   bigint       not null auto_increment,
    `capacity`        integer      not null,
    `description`     longtext,
    `food_type`       varchar(255) not null,
    `last_order_time` time         not null,
    `location`        varchar(255) not null,
    `name`            varchar(30)  not null,
    `open_time`       time         not null,
    `phone`           varchar(11)  not null,
    `owner_id`        bigint,
    primary key (`restaurant_id`),
    foreign key (`owner_id`) references member (`member_id`)
);

create table `closing_day`
(
    `restaurant_id` bigint       not null,
    `day_of_week`   varchar(255) not null,
    foreign key (`restaurant_id`) references restaurant (`restaurant_id`)
);


create table `menu`
(
    `restaurant_id` bigint         not null,
    `description`   varchar(255),
    `name`          varchar(30)    not null,
    `price`         numeric(19, 2) not null,
    foreign key (`restaurant_id`) references restaurant (`restaurant_id`)
);

create table `reservation`
(
    `reservation_id` bigint       not null auto_increment,
    `created_at`     timestamp,
    `customer_memo`  longtext,
    `visit_date`     date         not null,
    `visit_time`     time         not null,
    `visitor_count`  integer      not null,
    `status`         varchar(255) not null,
    `customer_id`    bigint       not null,
    `restaurant_id`  bigint       not null,
    primary key (`reservation_id`),
    foreign key (`customer_id`) references member (`member_id`),
    foreign key (`restaurant_id`) references restaurant (`restaurant_id`)
);