select sku_id from dcs_prd_chldsku where PRODUCT_ID in (
  select child_prd_id from dcs_cat_chldprd where category_id in 
  ('cat300001','cat300002','cat300003','cat300004','cat300005',
  'cat300006','cat300007','cat300008','cat300009','cat300010',
  'cat300011','cat300000')
);