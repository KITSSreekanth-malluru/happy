/* 
=======================================================================================================
Document Information
=======================================================================================================
Auth         : EPAM Team
E-mail       : 
Created      : 04/05/2012               
Use          : Add local price lists to the store
Run As       : PUBLISHING
Duration Est : This will take less than 20 seconds to install
=======================================================================================================
*/

ALTER TABLE CAST_CATALOG_ADDITION_PROP ADD (
  DISPLAY_BANNER NUMBER(1,0) DEFAULT 1,
  DISPLAY_STORES NUMBER(1,0) DEFAULT 1,
  COOKIE_AGE NUMBER(*,0) DEFAULT 180 NOT NULL,
  POPUP_TITLE VARCHAR2(1000) DEFAULT UNISTR('S\00e9lectionnez votre magasin pour visualiser le prix et la disponibilit\00e9 de vos produits :'),
  HEADER_TEXT VARCHAR2(1000) DEFAULT UNISTR('Consulter le prix et la disponibilit\00e9 de mes produits'),
  CC_PROMO_INFO VARCHAR2(40) DEFAULT 'prin200000');

ALTER TABLE CASTO_MAGASIN ADD (
  CC_DELAY_PERIOD_UNIT NUMBER(*,0) DEFAULT 0 NOT NULL,
  CC_DELAY_PERIOD NUMBER(*,0) DEFAULT 2 NOT NULL,
  CC_PROMO_INFO VARCHAR2(40),
  CC_TOP_PROMO_INFO VARCHAR2(40),
  LOCAL_LIST_PRICE_LIST VARCHAR2(40) NULL,
  LOCAL_SALE_PRICE_LIST VARCHAR2(40) NULL,
  RETRAIT_MAGASIN NUMBER(1,0) DEFAULT 0,
  LOCAL_PRIX NUMBER(1,0) DEFAULT 0);

ALTER TABLE CAST_CATALOG_ADDITION_PROP ADD (
  DELIVERY_TOOLTIP VARCHAR2(1000),
  CC_TOOLTIP VARCHAR2(1000));

ALTER TABLE CASTO_MAGASIN ADD (
  CC_PHONE VARCHAR2(1000));

ALTER TABLE CAST_CATALOG_ADDITION_PROP ADD (
  PROCESSING_FEE_NICE_WORD VARCHAR2(1000));

ALTER TABLE CASTO_MAGASIN ADD (
  CC_DELIVERY_MESSAGE VARCHAR2(1000));

ALTER TABLE CASTO_ORDER ADD(
  DELIVERY_TYPE NUMBER(*,0) DEFAULT 0 NOT NULL ENABLE,
  MAGASIN_ID VARCHAR2(40) DEFAULT '999' NOT NULL ENABLE
);

ALTER TABLE CASTO_ORDER ADD(
  PROCESSING_FEE_NICE_WORD VARCHAR2(1000));

ALTER TABLE CAST_CATALOG_ADDITION_PROP ADD (
  SHOPPING_CART_D2H_BANNER VARCHAR2(40) DEFAULT '1400037',
  SHOPPING_CART_CC_BANNER VARCHAR2(40) DEFAULT '1400038');
  
ALTER TABLE CASTO_MAGASIN ADD (
  PBX_SITE VARCHAR2(20),
  PBX_RANG VARCHAR2(20),
  PBX_IDENTIFIANT VARCHAR2(20));

ALTER TABLE CASTO_ABONNEMENT_NEWSLETTER ADD (
  ACCEPT_RECONTACT NUMBER(1,0) DEFAULT 0 NOT NULL);
  
ALTER TABLE CASTO_MAGASIN ADD (
  LOCAL_PRICE_LIST VARCHAR2(40) NULL);
  
ALTER TABLE CASTO_MAGASIN DROP (
  LOCAL_LIST_PRICE_LIST,
  LOCAL_SALE_PRICE_LIST
);

CREATE INDEX PERF_DCS_PROMOTION_IDX ON DCS_PROMOTION (PROMOTION_TYPE, ASSET_VERSION);

ALTER TABLE CASTO_MAGASIN ADD (
  PBX_SITE_SOFINCO VARCHAR2(20),
  PBX_RANG_SOFINCO VARCHAR2(20));

ALTER TABLE CASTO_MAGASIN ADD (
  PBX_IDENTIFIANT_SOFINCO VARCHAR2(20));
  
COMMIT;