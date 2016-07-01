CREATE TABLE "CAST_FOOTER_ITEM" 
   (
   "ASSET_VERSION" NUMBER(19,0) NOT NULL ENABLE, 
	"WORKSPACE_ID" VARCHAR2(40 BYTE) NOT NULL ENABLE, 
	"BRANCH_ID" VARCHAR2(40 BYTE) NOT NULL ENABLE, 
	"IS_HEAD" NUMBER(1,0) NOT NULL ENABLE, 
	"VERSION_DELETED" NUMBER(1,0) NOT NULL ENABLE, 
	"VERSION_EDITABLE" NUMBER(1,0) NOT NULL ENABLE, 
	"PRED_VERSION" NUMBER(19,0), 
	"CHECKIN_DATE" DATE,
    "LINK_ID" VARCHAR2(40 BYTE) NOT NULL ENABLE,
	"TITLE" VARCHAR2(254 BYTE), 	
    "VERSION" NUMBER(*,0) NOT NULL ENABLE,
	"URL" VARCHAR2(2048 BYTE),
	"IS_ACTIVE" NUMBER(1,0) DEFAULT 0 NOT NULL,
    CONSTRAINT "CAST_FOOTER_ITEM_P" PRIMARY KEY ("LINK_ID","ASSET_VERSION")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "CAST_USERS"  ENABLE
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "CAST_USERS" ;


CREATE TABLE "CAST_NOS_SERVICES_ITEM" 
   (
   "ASSET_VERSION" NUMBER(19,0) NOT NULL ENABLE, 
	"WORKSPACE_ID" VARCHAR2(40 BYTE) NOT NULL ENABLE, 
	"BRANCH_ID" VARCHAR2(40 BYTE) NOT NULL ENABLE, 
	"IS_HEAD" NUMBER(1,0) NOT NULL ENABLE, 
	"VERSION_DELETED" NUMBER(1,0) NOT NULL ENABLE, 
	"VERSION_EDITABLE" NUMBER(1,0) NOT NULL ENABLE, 
	"PRED_VERSION" NUMBER(19,0), 
	"CHECKIN_DATE" DATE,
    "LINK_ID" VARCHAR2(40 BYTE) NOT NULL ENABLE, 
	"TITLE" VARCHAR2(254 BYTE), 
    "VERSION" NUMBER(*,0) NOT NULL ENABLE,
	"URL" VARCHAR2(2048 BYTE),
	"IS_ACTIVE" NUMBER(1,0) DEFAULT 0 NOT NULL,	
	CONSTRAINT "CAST_NOS_SERVICES_ITEM_P" PRIMARY KEY ("LINK_ID","ASSET_VERSION")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "CAST_USERS"  ENABLE
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "CAST_USERS" ;

ALTER TABLE CASTO_SKU 
MODIFY GARANTIE VARCHAR2(2000);
commit;