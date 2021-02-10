use user_auth;
set foreign_key_checks=0;
set sql_safe_updates=0;
truncate table user;
truncate table  user_roles;
truncate table token;
set foreign_key_checks=1;
set sql_safe_updates=1;
