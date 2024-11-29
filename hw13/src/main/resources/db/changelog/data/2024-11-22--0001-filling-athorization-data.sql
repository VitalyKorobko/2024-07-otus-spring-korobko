--changeset korobko:2024-11-22-0001-users-data
insert into users(username, password, enabled)
values ('user', '$2y$13$H81FL5loLZN/s8baA6NcdOrXF1asdG4.tsf9no5a/UuVqrRtsy31i', 1);

--changeset korobko:2024-11-22-0001-roles-data
insert into roles(name)
values ('USER'), ('ADMIN');

--changeset korobko:2024-11-22-0001-users-roles-data
insert into users_roles(user_id, role_id)
values (1, 1), (1,2);