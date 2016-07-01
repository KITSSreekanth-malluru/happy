ALTER TABLE CAST_SKU ADD INFO_TEXT VARCHAR2(500)       NULL;
ALTER TABLE CAST_SKU ADD COMMERCE_TEXT VARCHAR2(500)       NULL,

CREATE TABLE CAST_SKU_INSTRUCTIONS (
    ASSET_VERSION                           NUMBER(19)          NOT NULL,
    SKU_ID                                  VARCHAR2(40)        NOT NULL,
    MEDIA_ID                                VARCHAR2(40)        NOT NULL,
    INSTRUCTION_NAME                        VARCHAR2(100)       NOT NULL,
    PRIMARY KEY(ASSET_VERSION, SKU_ID, MEDIA_ID));