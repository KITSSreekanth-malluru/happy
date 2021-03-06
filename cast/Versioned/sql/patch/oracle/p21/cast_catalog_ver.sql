ALTER TABLE CAST_DOCUMENT ADD PAGE_TITLE_PATTERN	VARCHAR2(300)	NULL;
ALTER TABLE CAST_DOCUMENT ADD META_KEYWORDS_PATTERN	VARCHAR2(300)	NULL;
ALTER TABLE CAST_DOCUMENT ADD META_DESCRIPTION_PATTERN	VARCHAR2(300)	NULL;

ALTER TABLE CASTO_MAGASIN ADD STORE_ID VARCHAR2(40) DEFAULT '0';

ALTER TABLE CAST_PROMO_INFORMATION ADD PAGE_TITLE_PATTERN	VARCHAR2(300)	NULL;
ALTER TABLE CAST_DOCUMENT ADD PAGE_TITLE_PATTERN	VARCHAR2(300)	NULL;

ALTER TABLE CAST_PRODUCT ADD FLAG_IMAGE	VARCHAR2(100)	NULL;
ALTER TABLE CAST_PRODUCT ADD FLAG_TEXT	VARCHAR2(200)	NULL;

ALTER TABLE CASTO_ORDER_BACKUP MODIFY (
    LOGIN varchar2(60)
);

commit;