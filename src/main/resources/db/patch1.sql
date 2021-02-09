use user_auth;
set foreign_key_checks=0;
set sql_safe_updates=0;
create table patch_version(id int primary key AUTO_INCREMENT, patch_no int,depend_patch int,is_success bit);
set foreign_key_checks=1;
set sql_safe_updates=1;
