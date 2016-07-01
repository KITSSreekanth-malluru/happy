DROP TABLE casto_doc_filter_tag_cats cascade constraints;

DROP TABLE casto_doc_filter_tags cascade constraints;

DROP TABLE cast_tag_docs cascade constraints;

DROP TABLE casto_doc_types cascade constraints;

DROP TABLE casto_doc_subcats cascade constraints;

DROP TABLE casto_fast_lab_configs cascade constraints;

ALTER TABLE cast_document drop column subcat_id;
ALTER TABLE cast_document drop column sub_type_id;
ALTER TABLE cast_document drop column cf_description;
ALTER TABLE cast_document drop column cf_r_col_dis_link_name;

ALTER TABLE cast_category drop column dp_promo;
ALTER TABLE cast_category drop column dp_promo_width;
ALTER TABLE cast_category drop column dp_promo_height;
ALTER TABLE cast_category drop column min_cat_number_per_col;

ALTER TABLE cast_product drop column flag_bg;

commit;
