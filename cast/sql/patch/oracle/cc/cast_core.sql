/* 
=======================================================================================================
Document Information
=======================================================================================================
Auth         : EPAM Team
E-mail       : 
Created      : 04/05/2012               
Use          : Add local price lists to the user
Run As       : CORE
Duration Est : This will take less than 20 seconds to install
=======================================================================================================
*/
ALTER TABLE CASTO_USER ADD(
  LOCAL_LIST_PRICE_LIST VARCHAR2(40) NULL,
  LOCAL_SALE_PRICE_LIST VARCHAR2(40) NULL,
  CURRENT_LOCAL_STORE VARCHAR2(40) NULL
);

ALTER TABLE CASTO_ORDER ADD(
  DELIVERY_TYPE NUMBER(*,0) DEFAULT 0 NOT NULL ENABLE,
  MAGASIN_ID VARCHAR2(40) DEFAULT '999' NOT NULL ENABLE
);

/* 
    start improve mail sender functionality
*/
CREATE INDEX CASTO_MAILSUIVICOMMANDE_IDX1 ON CASTO_MAILSUIVICOMMANDE
  (
    ORDER_ID
  );
CREATE INDEX CASTO_MAILSUIVICOMMANDE_IDX2 ON CASTO_MAILSUIVICOMMANDE
  (
    QUESTIONNAIRE_ID
  );
CREATE INDEX CASTO_SIPS_LOG_IDX1 ON CASTO_SIPS_LOG
  (
    ORDER_ID
  );
/*
    end improve mail sender functionality
*/

CREATE INDEX SRCH_UPDATE_QUEUE_IDX1 ON SRCH_UPDATE_QUEUE
  (
    GENERATION
  );
  
CREATE INDEX INDEX_PROFILE_ID ON CASTO_ABONNEMENT_NEWSLETTER
  (
    PROFILE_ID
  );

CREATE INDEX SCD_CONTACT_INDEX1 ON SCD_CONTACT
  (
    CVENTE,
    DCONTACT
  );

ALTER TABLE CASTO_ORDER ADD(
  PROCESSING_FEE_NICE_WORD VARCHAR2(1000));

ALTER TABLE CASTO_ABONNEMENT_NEWSLETTER ADD (
  ACCEPT_RECONTACT NUMBER(1,0) DEFAULT 0 NOT NULL);
  
ALTER TABLE CASTO_USER ADD(
  LOCAL_PRICE_LIST VARCHAR2(40) NULL
);

ALTER TABLE CASTO_USER DROP (
  LOCAL_LIST_PRICE_LIST,
  LOCAL_SALE_PRICE_LIST
);
COMMIT;
