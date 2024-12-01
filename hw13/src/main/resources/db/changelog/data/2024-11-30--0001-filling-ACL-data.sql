--changeset korobko:2024-11-30-0001-users-update-data
UPDATE users SET age = 33 WHERE username = 'admin';

--changeset korobko:2024-11-30-0002-roles-update-data
INSERT INTO roles(name)
VALUES ('PUBLISHER');

--changeset korobko:2024-11-30-0003-ACL-tables-data
INSERT INTO acl_sid (id, principal, sid) VALUES
(1, 1, 'admin'),
(2, 0, 'ADMIN'),
(3, 0, 'PUBLISHER'),
(4, 0, 'USER');

INSERT INTO acl_class (id, class) VALUES
(1, 'ru.otus.hw.models.Book'), (2, 'ru.otus.hw.models.Comment');

INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
(1, 1, 1, NULL, 1, 1),
(2, 1, 2, NULL, 1, 0),
(3, 1, 3, NULL, 1, 0),
(4, 2, 1, 1, 1, 0),
(5, 2, 2, 1, 1, 0),
(6, 2, 3, 1, 1, 0);


INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask,
                       granting, audit_success, audit_failure) VALUES
(1, 1, 1, 4, 0, 1, 1, 0),
(2, 1, 2, 3, 0, 1, 1, 1),
(3, 1, 3, 3, 1, 1, 1, 1),
(4, 1, 4, 3, 2, 1, 1, 1),
(5, 1, 5, 1, 4, 1, 1, 1),
(6, 1, 6, 2, 4, 1, 1, 1),
(7, 2, 1, 4, 0, 1, 1, 0),
(8, 2, 2, 3, 0, 1, 1, 1),
(9, 2, 3, 3, 1, 1, 1, 1),
(10, 2, 4, 3, 2, 1, 1, 1),
(11, 2, 5, 1, 4, 1, 1, 1),
(12, 2, 6, 2, 4, 1, 1, 1),
(13, 3, 1, 4, 0, 1, 1, 1),
(14, 3, 2, 3, 0, 1, 1, 1),
(15, 3, 3, 3, 1, 1, 1, 1),
(16, 3, 4, 3, 2, 1, 1, 1),
(17, 3, 5, 1, 4, 1, 1, 1),
(18, 3, 6, 2, 4, 1, 1, 1),
(19, 4, 1, 4, 0, 1, 1, 1),
(20, 4, 2, 4, 2, 1, 1, 1),
(21, 4, 3, 3, 0, 1, 1, 1),
(22, 4, 4, 3, 1, 1, 1, 1),
(23, 4, 5, 3, 2, 1, 1, 1),
(24, 4, 6, 1, 4, 1, 1, 1),
(25, 4, 7, 2, 4, 1, 1, 1),
(26, 5, 1, 4, 0, 1, 1, 1),
(27, 5, 2, 4, 2, 1, 1, 1),
(28, 5, 3, 3, 0, 1, 1, 1),
(29, 5, 4, 3, 1, 1, 1, 1),
(30, 5, 5, 3, 2, 1, 1, 1),
(31, 5, 6, 1, 4, 1, 1, 1),
(32, 5, 7, 2, 4, 1, 1, 1),
(33, 6, 1, 4, 0, 1, 1, 1),
(34, 6, 2, 4, 2, 1, 1, 1),
(35, 6, 3, 3, 0, 1, 1, 1),
(36, 6, 4, 3, 1, 1, 1, 1),
(37, 6, 5, 3, 2, 1, 1, 1),
(38, 6, 6, 1, 4, 1, 1, 1),
(39, 6, 7, 2, 4, 1, 1, 1);