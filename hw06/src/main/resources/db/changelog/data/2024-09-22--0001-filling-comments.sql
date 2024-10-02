--liquibase formatted sql

--changeset korobko:2024-09-22-0001-comments
insert into comments(text, book_id)
values ('Comment_1', 1), ('Comment_2', 1), ('Comment_3', 1);

