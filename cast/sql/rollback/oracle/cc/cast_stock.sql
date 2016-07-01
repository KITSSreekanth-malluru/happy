/*
=======================================================================================================
Document Information
=======================================================================================================
Auth         : EPAM Team
Created      : 02/10/2012
Use          : Rollback adding new column STOCK_AVAILABLE and removing unnecessary columns
=======================================================================================================
*/

ALTER TABLE MS_INVENTORY DROP (
  STOCK_AVAILABLE
);

ALTER TABLE MS_INVENTORY ADD(
  STOCK_THEORETICAL NUMBER(12,2),
  STOCK_RESERVED NUMBER(12,2)
);

ALTER TABLE MS_INVENTORY_SYNC_JOURNAL RENAME TO MS_SYNC_JOURNAL;
DROP TABLE MS_LOCAL_PRICES_SYNC_JOURNAL;
DROP TABLE MS_ORDERS_SYNC_JOURNAL;
DROP SEQUENCE MS_LPRICES_SYNC_JOURNAL_SEQ;
DROP SEQUENCE MS_ORDERS_SYNC_JOURNAL_SEQ;

COMMIT;