CREATE TABLE CAST_STORE_GEOLOCATION (
    ASSET_VERSION	            NUMBER(19)   		NOT NULL,
	ID							VARCHAR2(40)		NOT NULL,
	LONGITUDE					NUMBER(12,7)		NULL,
	LATITUDE 					NUMBER(12,7)		NULL,	
	CONSTRAINT CAST_STORE_GEOLOCATION_P PRIMARY KEY (ASSET_VERSION,ID));
COMMIT;