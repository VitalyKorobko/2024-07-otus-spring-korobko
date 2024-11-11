--liquibase formatted sql

--changeset korobko:2024-09-17-001-authors
CREATE TABLE authors (
    id bigserial,
    full_name varchar(255),
    PRIMARY KEY (id)
);

--changeset korobko:2024-09-17-001-genres
CREATE TABLE genres (
    id bigserial,
    name varchar(255),
    PRIMARY KEY (id)
);

--changeset korobko:2024-09-17-001-books
CREATE TABLE books (
    id bigserial,
    title varchar(255),
    author_id bigint REFERENCES authors (id) ON DELETE CASCADE,
    PRIMARY KEY (id)
);

--changeset korobko:2024-09-17-001-books_genres
CREATE TABLE books_genres (
    book_id bigint REFERENCES books(id) ON DELETE CASCADE,
    genre_id bigint REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);







