delete
from users;
delete
from refresh_tokens;


insert into users (name, email, password)
values ('John',
        'john@mail.com',
        '$2a$12$Q3U77paoIJHMLrxxw.7bt.38f6gcvbSVbiznWoErlRyr.AhzS7/Hm');--john123

insert into users (name, email, password)
values ('Max',
        'max@gmail.com',
        '$2a$12$ieL1D9YHC0ZrdzeQHYDYV.E8f4wOAyPxlurRQ.B0KTzSKCGCxMU9.');--max123


insert into refresh_tokens(user_id, token)
values (1, 'someToken1234');

