CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant` varchar(45) NOT NULL,
  `email` varchar(100) NOT NULL,
  `is_active` tinyint(4) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `gender` varchar(45) DEFAULT NULL,
  `mobile` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT '0',
  `password` varchar(255) DEFAULT NULL,
  `is_delete` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `formz_master`.`role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role` VARCHAR(45) NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` VARCHAR(45) NULL,
  `updated_by` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `formz_master`.`user_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  INDEX `user_id_idx` (`user_id` ASC),
  INDEX `role_id_idx` (`role_id` ASC),
  CONSTRAINT `user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `formz_master`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `role_id`
    FOREIGN KEY (`role_id`)
    REFERENCES `formz_master`.`role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` text,
  `is_expired` tinyint(4) DEFAULT NULL,
  `expiry_date` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(45) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  `token_type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id_idx` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `formz_master`.`role` (`role`) VALUES ('SUPER_ADMIN');
INSERT INTO `formz_master`.`account` (`tenant`, `email`, `is_active`, `created_at`) VALUES ('master', 'master@yopmail.com', '0', NULL);
INSERT INTO `formz_master`.`user` (`name`, `email`, `gender`, `is_active`,`password`) VALUES ('MASTER USER', 'master@yopmail.com', 'MALE', '1','$2a$10$zwZenh9EtUw4qOPIZa6ILupC72nMnD.KOXe9.g/Y9K9U2L0pAkrqG');
INSERT INTO `formz_master`.`user_role` VALUES(1,1);