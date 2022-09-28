drop table if exists users;
drop table if exists refresh_tokens;

create table refresh_tokens
(
    id      bigint  not null unique auto_increment,
    token   varchar not null,
    expires bigint  not null,

    primary key (id)
);

create table users
(
    id               bigint       not null unique auto_increment,
    name             varchar(100) not null,
    email            varchar(100) not null unique,
    password         varchar      not null,
    refresh_token_id bigint unique,

    primary key (id),
    foreign key (refresh_token_id)
        references refresh_tokens (id)
        on update cascade
        on delete cascade
);
