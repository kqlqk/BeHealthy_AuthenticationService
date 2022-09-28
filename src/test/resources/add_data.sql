delete
from refresh_tokens;
delete
from users;

insert into refresh_tokens (token, expires)
values ('eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQG1haWwuY29tIiwiaWF0IjoxNjYwNDkzMzEzLCJleHAiOjk2NjMwODUzMTN9.cLIXPLbOyC-4Rt1pOTmQQ7s0gYV0u0AlwYVmc0W3TZE',--for john
        9663085313);

insert into refresh_tokens (token, expires)
values ('eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXhAbWFpbC5jb20iLCJpYXQiOjE2NjA0OTMzMTMsImV4cCI6OTY2MzA4NTMxM30.4fPaSI2prJjYi3yhIFXEVYh5Q6xx8CeNqHZBl_J1LM8', -- for max
        9663085313);


insert into users (name, email, password, refresh_token_id)
values ('John',
        'john@mail.com',
        '$2a$12$Q3U77paoIJHMLrxxw.7bt.38f6gcvbSVbiznWoErlRyr.AhzS7/Hm',--john123
        1);

insert into users (name, email, password, refresh_token_id)
values ('Max',
        'max@gmail.com',
        '$2a$12$ieL1D9YHC0ZrdzeQHYDYV.E8f4wOAyPxlurRQ.B0KTzSKCGCxMU9.',--max123
        2);