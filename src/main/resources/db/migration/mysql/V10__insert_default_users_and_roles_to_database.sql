INSERT INTO `user`(uuid, username, password, email) VALUES
('c8405385-1efd-4c14-8089-5cc5ca34b351', 'user', '$2y$10$55xnu/C0hSlu870m8.n3D.MlNdjYE4Y0X8DIhj7.m89zwSJQllFqy', 'user@email.com'),
('055a925f-4dc2-47c2-82d2-3fe88b17d407', 'admin', '$2y$10$vU366Dmp7ZsASly4kHF0NuCbibOnLEom9W.ocPPTSIfloWUbvnM/e', 'admin@email.com');

INSERT INTO `user_profile`(id, uuid, about, gender, interests, languages, location) VALUES
(1, '87e9b6ae-adf6-44f9-8348-f8692457f7e8', 'I am a user', 'MALE', 'I like cats', 'Polish and English', 'Poland'),
(2, 'b0544377-77c8-40b9-b824-3d4af8cdb9f0', 'I am an admin', 'FEMALE', 'I like alpacas', 'Polish and Japanese', 'Poland');

INSERT INTO `role` (uuid, name)
VALUES ('ba50fd96-8973-41f7-9866-221c66d2dd05', 'USER_ROLE'), ('f0ded723-fa8d-4b54-a4c4-95c3dc8caf0f', 'ADMIN_ROLE');

INSERT INTO `user_role`(`user_id`, `role_id`) VALUES (1, 1), (2, 1), (2, 2);