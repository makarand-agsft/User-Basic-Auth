
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

CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `fields` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field_name` varchar(255) DEFAULT NULL,
  `field_label` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `form` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `template_name` varchar(255) DEFAULT NULL,
  `version` double DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKte03dysun5abfkd49b4kigijj` (`category_id`),
  CONSTRAINT `FKte03dysun5abfkd49b4kigijj` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `form_field` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `required` bit(1) DEFAULT NULL,
  `field_id` bigint(20) DEFAULT NULL,
  `form_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkxe3yc411wp0gkan5cuc29elg` (`field_id`),
  KEY `FKbgdx9knhcvbjyxlq9iv0x04es` (`form_id`),
  CONSTRAINT `FKbgdx9knhcvbjyxlq9iv0x04es` FOREIGN KEY (`form_id`) REFERENCES `form` (`id`),
  CONSTRAINT `FKkxe3yc411wp0gkan5cuc29elg` FOREIGN KEY (`field_id`) REFERENCES `fields` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `form_page` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `page_no` bigint(20) DEFAULT NULL,
  `template_location` varchar(255) DEFAULT NULL,
  `form_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqv51fqg4gp7aubrpfc4ged0r8` (`form_id`),
  CONSTRAINT `FKqv51fqg4gp7aubrpfc4ged0r8` FOREIGN KEY (`form_id`) REFERENCES `form` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `request_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `request_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `request_json` varchar(255) DEFAULT NULL,
  `pdf_download_path` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role` VARCHAR(45) NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` VARCHAR(45) NULL,
  `updated_by` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `user_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  INDEX `user_id_idx` (`user_id` ASC),
  INDEX `role_id_idx` (`role_id` ASC),
  CONSTRAINT `user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `role_id`
    FOREIGN KEY (`role_id`)
    REFERENCES `role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) DEFAULT NULL,
  `token_type` varchar(20) DEFAULT NULL,
  `expiry_date` datetime DEFAULT NULL,
  `is_expired` tinyint(4) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(45) DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `token_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `role` (`role`) VALUES ('ADMIN');