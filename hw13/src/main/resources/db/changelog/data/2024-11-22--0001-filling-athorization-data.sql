--changeset korobko:2024-11-22-0001-users-data
INSERT INTO users(username, password, enabled)
VALUES ('user', '$2y$13$H81FL5loLZN/s8baA6NcdOrXF1asdG4.tsf9no5a/UuVqrRtsy31i', 1);

--changeset korobko:2024-11-22-0001-roles-data
INSERT INTO roles(name)
VALUES ('USER'), ('ADMIN');

--changeset korobko:2024-11-22-0001-users-roles-data
INSERT INTO users_roles(user_id, role_id)
VALUES (1, 1), (1, 2);