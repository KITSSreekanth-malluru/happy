ALTER TABLE CASTO_SKU RENAME COLUMN EAN TO URL_PAGE;
ALTER TABLE CASTO_SKU MODIFY URL_PAGE VARCHAR2(260);

ALTER TABLE CASTO_MAGASIN MODIFY STORE_ID VARCHAR2(40) DEFAULT '0';

commit;
