--liquibase formatted sql

--changeset korobko:2024-09-22-001-comments
CREATE TABLE comments (
    id bigserial,
    text varchar(255),
    book_id bigint REFERENCES books (id) ON DELETE CASCADE,
    PRIMARY KEY (id)
);

