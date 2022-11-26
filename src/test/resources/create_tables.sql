drop table if exists refresh_tokens;
drop table if exists users;

create table users
(
    id       bigint       not null unique auto_increment,
    name     varchar(100) not null,
    email    varchar(100) not null unique,
    password varchar      not null,

    primary key (id)
);

create table refresh_tokens
(
    id      bigint  not null unique auto_increment,
    user_id bigint  not null unique,
    token   varchar not null,

    primary key (id),
    foreign key (user_id) references users (id)
        on delete cascade
);
