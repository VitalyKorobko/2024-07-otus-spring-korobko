--liquibase formatted sql

--changeset korobko:2025-02-17-005-users-data
insert into users(email, enabled, password, username)
values ('email@email.com', true, '$2y$13$H81FL5loLZN/s8baA6NcdOrXF1asdG4.tsf9no5a/UuVqrRtsy31i', 'user'),
       ('prod@email.com', true, '$2y$13$H81FL5loLZN/s8baA6NcdOrXF1asdG4.tsf9no5a/UuVqrRtsy31i', 'admin');

--changeset korobko:2025-02-17-006-roles-data
insert into user_role(user_id, roles)
values (1, 'USER'), (2, 'ADMIN');
