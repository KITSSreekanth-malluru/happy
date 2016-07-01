ALTER TABLE CAST_CATEGORY ADD (
	DEFAULT_TYPE_SORT NUMBER(1, 0) NULL
);
ALTER TABLE casto_fast_lab_configs ADD (
card_castorama_block VARCHAR2(40)
);

commit;