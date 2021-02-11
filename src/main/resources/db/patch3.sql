use user_auth;
set foreign_key_checks=0;
set sql_safe_updates=0;

insert into patch_version(patch_no,depend_patch,is_success) values(3,2,0);
ALTER TABLE `user_auth`.`user`
ADD COLUMN `is_deleted` bit(1) NULL AFTER `password`;
insert into patch_version(patch_no,depend_patch,is_success) values(3,2,1);
set foreign_key_checks=1;
set sql_safe_updates=1;
