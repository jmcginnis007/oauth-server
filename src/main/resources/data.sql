insert into users (user_id, username, password, enabled) values ('DCF0653A-3E2C-460E-A649-4B74E96C3C47', 'admin', '{noop}nimda', 1);
insert into users (user_id, username, password, enabled) values ('648A3ECA-5C3D-4F6E-854D-37E99844E0E7', 'user', '{noop}pass', 1);
insert into authorities (user_id, authority) values ('DCF0653A-3E2C-460E-A649-4B74E96C3C47', 'ROLE_ADMIN');
insert into authorities (user_id, authority) values ('648A3ECA-5C3D-4F6E-854D-37E99844E0E7', 'ROLE_USER');
