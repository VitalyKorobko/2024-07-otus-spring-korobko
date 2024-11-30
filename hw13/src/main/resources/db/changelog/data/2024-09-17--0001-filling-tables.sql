--liquibase formatted sql

--changeset korobko:2024-07-17-0001-authors
INSERT INTO authors(full_name)
VALUES ('Author_1'), ('Author_2'), ('Author_3');

--changeset korobko:2024-07-17-0001-genres
INSERT INTO genres(name)
VALUES ('Genre_1'), ('Genre_2'), ('Genre_3'),
       ('Genre_4'), ('Genre_5'), ('Genre_6');

--changeset korobko:2024-07-17-0001-books
INSERT INTO books(title, author_id)
VALUES ('BookTitle_1', 1), ('BookTitle_2', 2), ('BookTitle_3', 3);

--changeset korobko:2024-07-17-0001-books_genres
INSERT INTO books_genres(book_id, genre_id)
VALUES (1, 1),   (1, 2),
       (2, 3),   (2, 4),
       (3, 5),   (3, 6);