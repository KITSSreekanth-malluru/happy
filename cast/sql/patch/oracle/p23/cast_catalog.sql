ALTER TABLE CASTO_SKU ADD video_aux_url	VARCHAR2(2000) NULL;
ALTER TABLE CASTO_SKU ADD video_full_size NUMBER(1, 0) DEFAULT 0;

alter table CAST_CATEGORY modify info_text varchar2(1000);
alter table CAST_CATEGORY modify commerce_text varchar2(1000);

commit;
