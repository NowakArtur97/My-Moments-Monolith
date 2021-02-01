CREATE TABLE `user_role`(
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
    
    PRIMARY KEY(`user_id`, `role_id`),
    CONSTRAINT `FK_USER_ROLE` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `FK_ROLE_USER` FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
)ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8MB4;