-- 	Expected
-- &1 DESTINATION ORACLE DBA NAME
-- &2 DESTINATION ORACLE TNS
-- &3 DESTINATION ORACLE MIGRATION USER NAME


CONNECT &1@&2
--AS SYSDBA

DROP USER &3 CASCADE;

DROP TABLESPACE migrationdata INCLUDING CONTENTS AND DATAFILES;

DISCONNECT;
quit;