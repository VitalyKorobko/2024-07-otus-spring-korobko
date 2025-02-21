--changeset korobko:2025-02-17-001-users
CREATE TABLE users (
    id bigserial,
    email varchar(255),
    enabled boolean,
    password varchar(255),
    username varchar(255),
    PRIMARY KEY (id)
);

--changeset korobko:2025-02-17-002-user_role
CREATE TABLE user_role (
    user_id bigserial REFERENCES users (id),
    roles varchar(255)
);




----changeset korobko:2025-02-13-001-users
--CREATE TABLE users (
--    id bigserial,
--    username varchar(255),
--    password varchar(255),
--    email varchar(255),
--    enabled boolean,
--    PRIMARY KEY (id)
--);
--
----changeset korobko:2025-02-13-001-roles
--CREATE TABLE roles (
--    id bigserial,
--    name varchar(255),
--    PRIMARY KEY (id)
--);
--
----changeset korobko:2025-02-13-001-users_roles
--CREATE TABLE users_roles (
--    user_id bigint REFERENCES users(id) ON DELETE CASCADE,
--    role_id bigint REFERENCES roles(id) ON DELETE CASCADE,
--    PRIMARY KEY (user_id, role_id)
--);