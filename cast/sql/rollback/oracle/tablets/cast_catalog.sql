drop table "CAST_FOOTER_ITEM" cascade constraints;
drop table "CAST_NOS_SERVICES_ITEM" cascade constraints;

ALTER TABLE CASTO_SKU 
MODIFY GARANTIE VARCHAR2(70);

commit;
