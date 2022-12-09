delete
from users;
delete
from refresh_tokens;


insert into users (name, email, password)
values ('John',
        'john@mail.com',
        '$2a$12$9y21BQaQmb3VYgy4ziZu2.8.ZdwLbFDHcuXMdtX6ilmEhAQV9mcyC');--Test1234

insert into users (name, email, password)
values ('Max',
        'max@gmail.com',
        '$2a$12$Mf1buarB/YXGkB183.LNJulVNQ86Zhv/giLN8K8TDnZg1GsMAuyla');--Test12345


insert into refresh_tokens(user_id, token)
values (1,
        'eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQG1haWwuY29tIiwiZXhwIjoxNjczMTc3MzQ2fQ.-2omlZ2c_TPjbBVUb2ODBm_z8cvuknyLRwFZc47aXvHDjurWAQeqksAYKjmYr_nb');

