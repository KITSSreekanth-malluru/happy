/*
=======================================================================================================
Document Information
=======================================================================================================
Auth         : EPAM Team
E-mail       :
Created      : 04/05/2012
Use          : ROLLBACK local price lists & prices
Run As       : Local price scheme
Duration Est : This will take less than 20 seconds to install
=======================================================================================================
*/

DROP TABLE ECOTAX_INFO;
DROP TABLE PROMO_INFO;
DROP TABLE PROCESSING_FEE;
DROP TABLE CLICK_COLLECT;
DROP TABLE NOT_ALLOWED_SKUS;
DROP TABLE DISCOUNT;
DROP TABLE CAST_PRICE;
DROP TABLE CAST_PRICE_LIST;
DROP TABLE LOCAL_PRICE_TO_STORE;

COMMIT;