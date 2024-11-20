--changeset korobko:2024-11-22-001-users
CREATE TABLE users (
    id bigserial,
    username varchar(255),
    password varchar(255),
    enabled bit,
    PRIMARY KEY (id)
);

--changeset korobko:2024-11-22-001-roles
CREATE TABLE roles (
    id bigserial,
    name varchar(255),
    PRIMARY KEY (id)
);

--changeset korobko:2024-11-22-001-users_roles
CREATE TABLE users_roles (
    user_id bigint REFERENCES users(id) ON DELETE CASCADE,
    role_id bigint REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);