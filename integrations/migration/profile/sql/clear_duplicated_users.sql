-- 	Expected
-- &1 MIGRATION SCHEMA NAME

-- Duplicated login users.

 DROP TABLE "&1".CASTO_USER_MAPPED;

 CREATE TABLE "&1".CASTO_USER_MAPPED
 (
   USER_ID VARCHAR2 (40) NOT NULL,
   USER_MAPPED_ID VARCHAR2 (40) NOT NULL,
   CONSTRAINT CASTO_USER_MAPPED_PK PRIMARY KEY (USER_ID,USER_MAPPED_ID)
 );

 insert into "&1".CASTO_USER_MAPPED(USER_ID,USER_MAPPED_ID)
 select A.id,B.min_id from "&1".dps_user A left outer join (
 SELECT min(id) as min_id,login
 FROM "&1".dps_user
 group by login having count(login) > 1) B
 on A.login = B.login WHERE B.min_id IS NOT NULL order by A.login;

 delete from "&1".casto_other_billship_addr where user_id in
 (select user_id from "&1".CASTO_USER_MAPPED where user_id <> user_mapped_id);

 delete from "&1".casto_other_billing_addr where user_id in
 (select user_id from "&1".CASTO_USER_MAPPED where user_id <> user_mapped_id);

 delete from "&1".CASTO_USER where id in
 (select user_id from "&1".CASTO_USER_MAPPED where user_id <> user_mapped_id);

 delete from "&1".dps_other_addr where user_id in
 (select user_id from "&1".CASTO_USER_MAPPED where user_id <> user_mapped_id);

 delete from "&1".dcs_user where user_id in
 (select user_id from "&1".CASTO_USER_MAPPED where user_id <> user_mapped_id);

 delete from "&1".dps_user_address where id in
 (select user_id from "&1".CASTO_USER_MAPPED where user_id <> user_mapped_id);

 DELETE FROM "&1".CASTO_USER_NEWSLETTER WHERE ID IN
 (select user_id from "&1".CASTO_USER_MAPPED where user_id <> user_mapped_id);

 delete from "&1".dps_user where id in
 (select user_id from "&1".CASTO_USER_MAPPED where user_id <> user_mapped_id);

COMMIT;

DISCONNECT;
quit;

 -- Finish duplicated login users.
