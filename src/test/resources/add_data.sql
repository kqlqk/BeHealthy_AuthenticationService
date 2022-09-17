delete from users;

insert into users (name, email, password, age)
values ('John',
        'john@mail.com',
        '$2a$12$Q3U77paoIJHMLrxxw.7bt.38f6gcvbSVbiznWoErlRyr.AhzS7/Hm',--john123
        20);

insert into users (name, email, password, age)
values ('Max',
        'max@gmail.com',
        '$2a$12$ieL1D9YHC0ZrdzeQHYDYV.E8f4wOAyPxlurRQ.B0KTzSKCGCxMU9.',--max123
        43);