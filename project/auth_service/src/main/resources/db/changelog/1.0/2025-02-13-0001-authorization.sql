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
