insert into users (user_id, username, password, enabled) values (1, 'admin', '{noop}nimda', 1);
insert into users (user_id, username, password, enabled) values (2, 'user', '{noop}pass', 1);
insert into authorities (user_id, authority) values (1, 'ROLE_ADMIN');
insert into authorities (user_id, authority) values (2, 'ROLE_USER');
