  CREATE TABLE "CASTO_LOG_CALL_CENTER" 
   (
	"ID_JOURNALISATION" VARCHAR2(40 BYTE), 
	"USER_ID" VARCHAR2(40 BYTE), 
	"LOGIN" VARCHAR2(40 BYTE), 
	"DATE_ACTION" DATE, 
	"ACTION" VARCHAR2(40 BYTE), 
	"ORDER_ID" VARCHAR2(40 BYTE), 
	 PRIMARY KEY ("ID_JOURNALISATION")
	);