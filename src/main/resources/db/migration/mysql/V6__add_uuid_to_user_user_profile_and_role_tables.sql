ALTER TABLE `user` ADD COLUMN `uuid` VARCHAR(36) NOT NULL AFTER `id`;
ALTER TABLE `user_profile` ADD COLUMN `uuid` VARCHAR(36) NOT NULL AFTER `id`;
ALTER TABLE `role` ADD COLUMN `uuid` VARCHAR(36) NOT NULL AFTER `id`;