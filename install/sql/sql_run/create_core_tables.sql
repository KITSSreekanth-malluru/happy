set echo on
REM !!! create_core_tables.sql

-- DAS
@F:\ATG\ATG9.4\home/../DAS/sql/install/oracle/das_ddl.sql

-- DPS
@F:\ATG\ATG9.4\home/../DPS/sql/install/oracle/dps_ddl.sql

-- DSS
@F:\ATG\ATG9.4\home/../DSS/sql/install/oracle/dss_ddl.sql

-- DCS
@F:\ATG\ATG9.4\home/../DCS/sql/install/oracle/dcs_ddl.sql

-- Abandoned Orders
@F:\ATG\ATG9.4\home/../DCS/AbandonedOrderServices/sql/db_components/oracle/abandoned_order_views.sql

-- Custom Catalogs
@F:\ATG\ATG9.4\home/../DCS/CustomCatalogs/sql/db_components/oracle/custom_catalog_user_ddl.sql

-- DCS Search
@F:\ATG\ATG9.4\home/../DAF/Search/common/sql/db_components/oracle/refinement_ddl.sql
@F:\ATG\ATG9.4\home/../DAF/Search/Index/sql/db_components/oracle/search_ddl.sql
@F:\ATG\ATG9.4\home/../DCS/Search/common/sql/db_components/oracle/commerce_refinement_ddl.sql
@F:\ATG\ATG9.4\home/../DCS/Search/CustomCatalogs/common/sql/db_components/oracle/custom_catalog_refinement_ddl.sql

rem DAF Routing

@F:\ATG\ATG9.4\home/../DAF/Search/Routing/sql/db_components/oracle/routing_ddl.sql
@F:\ATG\ATG9.4\home/../DAF/Search/Topics/sql/db_components/oracle/search_topics_ddl.sql



-- SearchTesting
@F:\ATG\ATG9.4\home/../DAF/Search/common/sql/db_components/oracle/searchtesting_ddl.sql

-- Search Admin
@F:\ATG\ATG9.4\home/../Search9.4/SearchAdmin/common/sql/db_components/oracle/searchadmin_admin_ddl.sql
@F:\ATG\ATG9.4\home/../Search9.4/SearchAdmin/customizations/sql/db_components/oracle/searchadmin_dictionary_ddl.sql
@F:\ATG\ATG9.4\home/../Search9.4/SearchAdmin/customizations/sql/db_components/oracle/searchadmin_termweights_ddl.sql
@F:\ATG\ATG9.4\home/../Search9.4/SearchAdmin/customizations/sql/db_components/oracle/searchadmin_queryrules_ddl.sql
@F:\ATG\ATG9.4\home/../Search9.4/SearchAdmin/customizations/sql/db_components/oracle/searchadmin_tpo_ddl.sql
@F:\ATG\ATG9.4\home/../Search9.4/SearchAdmin/customizations/sql/db_components/oracle/searchadmin_facet_ddl.sql

-- Castorama
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_core_order.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_core_user.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_newsletter.sql

-- Cloud
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_cloud.sql
--Back Office
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_bo.sql
--Order message
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_order_message.sql
--Export journal
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_export_journal.sql
--Export SKU journal
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_export_sku_journal.sql
--Sips log
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_sips_log.sql
--Call center
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_log_call_center.sql
--Coupons
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_coupons.sql
--Order transaction
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_order_transaction.sql
--Order backup
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_order_backup.sql
--Log admin
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_log_admin.sql
--Mail log
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/install/oracle/cast_maillog.sql

--Patch
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p19/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p20/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p21/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p22/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/pdr/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p23/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/s2/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p28/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/fl/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p29/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p30/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/cc/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/p31/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/hmacImpl/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/inventoryCheck/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/lc/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/ds/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/lh/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/tablets/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/sl/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/sp/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/si/cast_core.sql
@F:\ATG\ATG9.4\home/../commerce/castorama/cast/sql/patch/oracle/cc/cast_core.sql