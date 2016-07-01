CREATE TABLE casto_doc_filter_tag_cats (
	tag_cat_id VARCHAR2(40) NOT NULL,
	tag_cat_title VARCHAR2(255) NOT NULL,
	tag_cat_image VARCHAR2(40),
	tag_cat_number NUMBER,
	PRIMARY KEY (tag_cat_id));

CREATE TABLE casto_doc_filter_tags (
	tag_id VARCHAR2(40) NOT NULL,
	tag_title VARCHAR2(255) NOT NULL,
	tag_category VARCHAR2(40),
	tag_promo VARCHAR2(40),
	tag_number NUMBER,
	PRIMARY KEY (tag_id));

CREATE TABLE cast_tag_docs (
	tag_id VARCHAR2(40) NOT NULL REFERENCES casto_doc_filter_tags(tag_id),
	document_id VARCHAR2(40) NOT NULL REFERENCES cast_document(document_id),
	sequence_num INTEGER NOT NULL ENABLE,
	PRIMARY KEY (tag_id,sequence_num));

CREATE TABLE casto_doc_types (
	type_id VARCHAR2(40) NOT NULL,
	type_title VARCHAR2(255) NOT NULL,
	type_descr VARCHAR2(500),
	type_number NUMBER,
	type_def_promo VARCHAR2(40),
	PRIMARY KEY (type_id));

CREATE TABLE casto_doc_subcats (
	subcat_id VARCHAR2(40) NOT NULL,
	subcat_title VARCHAR2(255) NOT NULL,
	PRIMARY KEY (subcat_id));

CREATE TABLE casto_fast_lab_configs (
	id VARCHAR2(40) NOT NULL,
	fb_link_title VARCHAR2(255),
	fb_link_value VARCHAR2(255),
	fd_link_title VARCHAR2(255),
	fd_link_value VARCHAR2(255),
	eg_link_title VARCHAR2(255),
	eg_link_value VARCHAR2(255),
	dis_def_doc VARCHAR2(40),
	prod_dis_fb_link_title VARCHAR2(255),
	prod_dis_fd_link_title VARCHAR2(255),
	prod_dis_eg_link_title VARCHAR2(255),
	cf_enable_new NUMBER(1,0),
	cf_page_title VARCHAR2(255),
	cf_page_descr VARCHAR2(500),
	cf_def_bottom_banner VARCHAR2(40),
	cf_get_help_title VARCHAR2(255),
	cf_get_help_descr VARCHAR2(500),
	cf_get_help_home VARCHAR2(40),
	cf_get_help_filtered VARCHAR2(40),
	reinsurance_section VARCHAR2(40),
	reinsurance_section_flap VARCHAR2(40),
	PRIMARY KEY (id));

ALTER TABLE cast_document ADD sub_type_id VARCHAR2(40);
ALTER TABLE cast_document ADD subcat_id VARCHAR2(40);
ALTER TABLE cast_document ADD cf_description VARCHAR2(2000);
ALTER TABLE cast_document ADD cf_r_col_dis_link_name VARCHAR2(255);

ALTER TABLE cast_category ADD dp_promo VARCHAR2(40);
ALTER TABLE cast_category ADD dp_promo_width NUMBER;
ALTER TABLE cast_category ADD dp_promo_height NUMBER;
ALTER TABLE cast_category ADD min_cat_number_per_col NUMBER;

ALTER TABLE cast_product ADD flag_bg VARCHAR2(30);

commit;
