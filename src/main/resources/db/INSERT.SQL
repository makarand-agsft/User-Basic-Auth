
insert into role(role) values('ADMIN');
insert into role(role) values('USER');

/* Password : admin@123 */
INSERT INTO user (`id`,`email`, `is_deleted`, `password`) VALUES (1,'admin.user@yopmail.com', FALSE, '$2a$10$zwZenh9EtUw4qOPIZa6ILupC72nMnD.KOXe9.g/Y9K9U2L0pAkrqG');
INSERT INTO user_profile (`id`,`created_by`,`created_date`, `first_name`, `is_active`, `last_name`, `mobile`, `user_id`) VALUES ('1','Admin User','2021-11-02 11:35:00' , 'Admin', TRUE, 'User', '9011545454', '1');
INSERT INTO address (`id`, `address_string`, `address_type`, `city`, `country`, `pincode`, `state`, `user_id`) VALUES ('1', 'baner', 'OFFICE', 'pune', 'india', '411061', 'maharashtra', '1');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
