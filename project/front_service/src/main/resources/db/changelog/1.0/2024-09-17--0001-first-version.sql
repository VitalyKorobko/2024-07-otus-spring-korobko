--liquibase formatted sql

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

----changeset korobko:2025-02-17-003-products
--CREATE TABLE products (
--    id bigserial,
--    author varchar(255),
--    description text,
--    image varchar(255),
--    price bigint,
--    title varchar(255),
--    user_id bigserial REFERENCES users (id),
--    PRIMARY KEY (id)
--);
--
----changeset korobko:2025-02-17-004-orders
--CREATE TABLE orders (
--    id bigserial,
--    end_date date,
--    order_field varchar(255),
--    start_date date,
--    status bigint,
--    user_id bigserial REFERENCES users (id),
--    PRIMARY KEY (id)
--);











