UPDATE CAST_PROMO_INFORMATION 
SET PAGE_TITLE_PATTERN = '{title} - CASTORAMA' WHERE PAGE_TITLE_PATTERN IS NULL;

ALTER TABLE CAST_PRODUCT ADD KITCHEN_CONTENT_HTML	VARCHAR2(200)	NULL;
ALTER TABLE DCS_PROMOTION MODIFY DISPLAY_NAME VARCHAR2(200);

ALTER TABLE CASTO_SKU MODIFY CONTRAINTESUTILISATION VARCHAR2(1500);
ALTER TABLE CASTO_SKU MODIFY MENTIONSLEGALESOBLIGATOIRES VARCHAR2(1500);
ALTER TABLE CASTO_SKU MODIFY PLUSDUPRODUIT VARCHAR2(1500);
ALTER TABLE CASTO_SKU MODIFY LIBELLECLIENTLONG VARCHAR2(1500);

commit;