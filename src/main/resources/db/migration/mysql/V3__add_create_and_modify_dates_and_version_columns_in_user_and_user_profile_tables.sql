ALTER TABLE `user` ADD COLUMN `create_date` TIMESTAMP NOT NULL AFTER `id`;
ALTER TABLE `user` ADD COLUMN `modify_date` TIMESTAMP NOT NULL AFTER `create_date`;
ALTER TABLE `user` ADD COLUMN `version` INT NOT NULL AFTER `modify_date`;
ALTER TABLE `user_profile` ADD COLUMN `create_date` TIMESTAMP NOT NULL AFTER `id`;
ALTER TABLE `user_profile` ADD COLUMN `modify_date` TIMESTAMP NOT NULL AFTER `create_date`;
ALTER TABLE `user_profile` ADD COLUMN `version` INT NOT NULL AFTER `modify_date`;
