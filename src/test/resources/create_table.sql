drop table if exists users;

create table users(
    id       bigint       not null unique auto_increment,
    name     varchar(100) not null,
    email    varchar(100) not null unique,
    password varchar      not null,
    age      smallint     not null,

    primary key (id)
)
