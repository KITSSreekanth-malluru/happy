set echo on
REM !!! create_pub_tables.sql
-- BIZUI
@F:\ATG\ATG9.4\home/../BIZUI/sql/install/oracle/bizui_all_ddl.sql

-- DAS
@F:\ATG\ATG9.4\home/../DAS/Versioned/sql/install/oracle/das_versioned_ddl.sql

-- DCS
@F:\ATG\ATG9.4\home/../DCS/Versioned/sql/install/oracle/dcs_versioned_ddl.sql
@F:\ATG\ATG9.4\home/../DCS/CustomCatalogs/Versioned/sql/db_components/oracle/versioned_custom_catalog_ddl.sql
@F:\ATG\ATG9.4\home/../DCS/CustomCatalogs/sql/db_components/oracle/custom_catalog_user_ddl.sql

-- DAF Search
@F:\ATG\ATG9.4\home/../DAF/Search/Index/sql/db_components/oracle/search_ddl.sql
@F:\ATG\ATG9.4\home/../DAF/Search/Versioned/sql/db_components/oracle/versioned_refinement_ddl.sql

-- TO ADD
@F:\ATG\ATG9.4\home/../DAF/Search/Topics/sql/db_components/oracle/search_topics_ddl.sql
@F:\ATG\ATG9.4\home/../DAF/Search/Routing/sql/db_components/oracle/routing_ddl.sql

-- DCS Search
@F:\ATG\ATG9.4\home/../DCS/Search/Versioned/sql/db_components/oracle/versioned_commerce_refinement_ddl.sql
@F:\ATG\ATG9.4\home/../DCS/Search/CustomCatalogs/Versioned/sql/db_components/oracle/versioned_custom_catalog_refinement_ddl.sql

-- SearchTesting
@F:\ATG\ATG9.4\home/../DAF/Search/common/sql/db_components/oracle/searchtesting_ddl.sql


-- Castorama
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_core_order.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_core_user.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_newsletter.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/install/oracle/cast_catalog_ver.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/install/oracle/cast_magasin_ver.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_bo.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_order_message.sql

-- Castorama iPhone
@F:\ATG\ATG9.4\home/../commerce/castorama/castIphone/Versioned/sql/install/oracle/cast_iphone_catalog_vers.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/castIphone/Versioned/sql/install/oracle/cast_iphone_catalog_retina_vers.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/castIphone/Versioned/sql/install/oracle/cast_iphone_R7_catalog_vers.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/castIphone/Versioned/sql/install/oracle/cast_iphone_store_vers.sql

--Order backup
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_order_backup.sql
--Call center
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_log_call_center.sql

-- Patch
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/p19/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/p20/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/p21/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/dsp/cast_magasin.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p22/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p22/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/ik/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/mth/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/pdr/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/p23/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/s2/cast_catalog_ver.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/p27/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/p28/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/fl/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/p29/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/p30/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/Versioned/sql/patch/oracle/cc/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p31/cast_catalog.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/hmacImpl/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/inventoryCheck/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/lc/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/ds/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/tablets/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/sl/cast_pub.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/si/cast_pub.sql
