ALTER TABLE `role` ADD COLUMN `create_date` TIMESTAMP NOT NULL AFTER `id`;
ALTER TABLE `role` ADD COLUMN `modify_date` TIMESTAMP NOT NULL AFTER `create_date`;
ALTER TABLE `role` ADD COLUMN `version` INT NOT NULL AFTER `modify_date`;