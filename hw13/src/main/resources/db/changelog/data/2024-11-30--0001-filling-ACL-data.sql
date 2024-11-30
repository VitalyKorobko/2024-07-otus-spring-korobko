--changeset korobko:2024-11-30-0001-users-update-data
UPDATE users SET age = 33 WHERE username = 'user';

--changeset korobko:2024-11-30-0002-roles-update-data
INSERT INTO roles(name)
VALUES ('PUBLISHER');
