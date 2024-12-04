--liquibase formatted sql

--changeset korobko:2024-09-22-0001-comments
INSERT INTO comments(text, book_id)
VALUES ('Comment_1', 1), ('Comment_2', 1), ('Comment_3', 1);

