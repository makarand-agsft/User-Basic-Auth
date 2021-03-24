use user_auth;
set foreign_key_checks=0;
set sql_safe_updates=0;

insert into patch_version(patch_no,depend_patch,is_success) values(2,1,0);
ALTER TABLE `user_auth`.`token`
CHANGE COLUMN `token` `token` LONGTEXT NULL DEFAULT NULL ;
insert into patch_version(patch_no,depend_patch,is_success) values(2,1,1);
set foreign_key_checks=1;
set sql_safe_updates=1;
