set linesize 4000
set wrap off
set pagesize 0
set trimspool on
set feed off
set termout off
spool ./pricequantitylistingdata.txt

SELECT 'store code|itemid|quantity|price|sale price|sale price effective date|availability|featured product' FROM DUAL;

SELECT 
"store code"                                      || '|' ||
"itemid"                                          || '|' ||
"quantity"                                        || '|' ||
"price"                                           || '|' ||
"sale price"                                      || '|' ||
"sale price effective date"                       || '|' ||
"availability"                                    || '|' ||
"featured product"
FROM 
(
SELECT st.store_id AS "store code",
  pr.sku_id AS "itemid",
  st.quantity AS "quantity",
  pr.listprices AS "price",
  pr.saleprices AS "sale price",
  CASE
WHEN pr.saleprices IS NOT NULL THEN
  to_char(CURRENT_DATE -1,   'YYYY-MM-DD') || 'T00:00:00/' || to_char(CURRENT_DATE,   'YYYY-MM-DD') || 'T00:00:00'
ELSE
  NULL
END AS
"sale price effective date",
  avail AS "availability",
  feachured_product AS "featured product"
FROM --stores
  (SELECT store_id,
     to_number(product_id) AS
  codearticle,
     stock_theoretical -msi.stock_reserved AS
   quantity,
     CASE
   WHEN(msi.stock_theoretical -msi.stock_reserved) > 3 THEN
    'in stock'
	WHEN msi.stock_theoretical >= 1 AND msi.stock_theoretical > msi.stock_reserved THEN
	'low availability'
   ELSE
    'out of stock'
   END AS
   avail
   FROM cast_stock.ms_inventory msi
  )
st,
  --prices
  (SELECT prc.sku_id,
     MAX(cst.codearticle) AS
  codearticle,
     SUM(decode(seq,    1,    prc.list_price,    NULL)) AS
  listprices,
     SUM(decode(seq,    2,    prc.list_price,    NULL)) AS
  saleprices,
     CASE
   WHEN prc.sku_id IN
    (SELECT sku_id
     FROM cast_catb.dcs_prd_chldsku tbl,
       cast_catb.cast_category cat
     WHERE tbl.product_id = cat.featuredproduct)
  THEN
    'y'
   ELSE
    'n'
   END AS
   feachured_product
   FROM
    (SELECT sku_id,
       price_list,
       list_price,
       row_number() over(PARTITION BY sku_id
     ORDER BY list_price DESC nulls LAST) seq
     FROM cast_catb.dcs_price
     WHERE price_list = 'listPrices' 
     OR price_list = 'salePrices')
  prc,
     cast_core.casto_tmp_searchable_products cst
   WHERE cst.sku_id = prc.sku_id
   GROUP BY prc.sku_id)
pr
WHERE st.codearticle = pr.codearticle
);

SPOOL off;
set termout on
exit