use user_auth;
set foreign_key_checks=0;
set sql_safe_updates=0;

insert into patch_version(patch_no,depend_patch,is_success) values(4,3,0);
ALTER TABLE `user_auth`.`user`
ADD COLUMN `is_reset` bit(1) NULL AFTER `password`;
insert into patch_version(patch_no,depend_patch,is_success) values(4,3,1);
set foreign_key_checks=1;
set sql_safe_updates=1;