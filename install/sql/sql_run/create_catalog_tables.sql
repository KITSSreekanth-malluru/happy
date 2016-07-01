set echo on
REM !!! create_catalog_tables.sql

-- DCS
@F:\ATG\ATG9.4\home/../DCS/sql/db_components/oracle/priceLists_ddl.sql
@F:\ATG\ATG9.4\home/../DCS/sql/db_components/oracle/product_catalog_ddl.sql
@F:\ATG\ATG9.4\home/../DCS/sql/db_components/oracle/promotion_ddl.sql

-- Custom Catalogs
@F:\ATG\ATG9.4\home/../DCS/CustomCatalogs/sql/db_components/oracle/custom_catalog_ddl.sql

-- Search
@F:\ATG\ATG9.4\home/../DAF/Search/common/sql/db_components/oracle/refinement_ddl.sql
@F:\ATG\ATG9.4\home/../DAF/Search/Index/sql/db_components/oracle/search_ddl.sql
@F:\ATG\ATG9.4\home/../DCS/Search/common/sql/db_components/oracle/commerce_refinement_ddl.sql
@F:\ATG\ATG9.4\home/../DCS/Search/CustomCatalogs/common/sql/db_components/oracle/custom_catalog_refinement_ddl.sql


-- Castorama
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_magasin.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_catalog.sql

-- Castorama iPhone
@F:\ATG\ATG9.4\home/../commerce/castorama/castIphone/sql/install/oracle/cast_iphone_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/castIphone/sql/install/oracle/cast_iphone_catalog_retina.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/castIphone/sql/install/oracle/cast_iphone_R7_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/castIphone/sql/install/oracle/cast_iphone_store.sql

-- Patch
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p19/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p20/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p21/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p22/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/ik/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/mth/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/iphone_r8/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p23/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/s2/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p27/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p28/cast_catalog.sql

@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/fl/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p29/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p30/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/cc/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p31/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/hmacImpl/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/inventoryCheck/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/lc/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/ds/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/tablets/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/sl/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/sp/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/si/cast_catalog.sql




