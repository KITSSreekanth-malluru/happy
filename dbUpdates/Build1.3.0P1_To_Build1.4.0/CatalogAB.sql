ALTER TABLE CAST_SKU ADD INFO_TEXT VARCHAR2(500)       NULL;
ALTER TABLE CAST_SKU ADD COMMERCE_TEXT VARCHAR2(500)       NULL,

CREATE TABLE CAST_SKU_INSTRUCTIONS (
    SKU_ID                                  VARCHAR2(40)        NOT NULL REFERENCES DCS_SKU(SKU_ID),
    MEDIA_ID                                VARCHAR2(40)        NOT NULL REFERENCES DCS_MEDIA(MEDIA_ID),
    INSTRUCTION_NAME                        VARCHAR2(100)       NOT NULL,
    PRIMARY KEY(SKU_ID, MEDIA_ID));


ALTER TABLE CASTO_SKU_MAGASIN ADD FOREIGN KEY (SKU_ID) REFERENCES DCS_SKU (SKU_ID) ON DELETE CASCADE;
