CREATE TABLE CAST_ITEM_PRICE_INFO (
  ID VARCHAR2 (40) NOT NULL REFERENCES DCSPP_AMOUNT_INFO (AMOUNT_INFO_ID),
  ON_SALE_DISCOUNT_DISPLAY NUMBER(1,0) DEFAULT 0 NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE CAST_APPLLIED_COUPONS(
  USER_ID VARCHAR2(4000 BYTE) NOT NULL ENABLE, 
  PROMOTION_ID VARCHAR2(4000 BYTE)
);
commit;