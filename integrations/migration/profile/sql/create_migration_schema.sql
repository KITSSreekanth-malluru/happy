-- 	Expected
-- &1 DESTINATION ORACLE DBA NAME
-- &2 DESTINATION ORACLE TNS
-- &3 DESTINATION ORACLE MIGRATION USER NAME
-- &4 DESTINATION ORACLE MIGRATION USER PASSWORD
-- &5 DESTINATION ORACLE MIGRATION TABLESPACE DATA FILE

CONNECT &1@&2
-- AS SYSDBA

CREATE TABLESPACE migrationdata DATAFILE
'&5' SIZE 1024M 
AUTOEXTEND ON
BLOCKSIZE 8192
FORCE LOGGING
EXTENT MANAGEMENT LOCAL UNIFORM SIZE 256K
FLASHBACK ON;

CREATE USER &3 IDENTIFIED BY &4 DEFAULT TABLESPACE migrationdata;
GRANT DBA TO &3;

DISCONNECT;

quit;