  ALTER TABLE VTE_ENLEVEMENT DROP (
	URL_TRANSPORTEUR 
);

ALTER TABLE CASTO_EXPERIAN_REQUESTS MODIFY PHONE_NUMBER_2 VARCHAR2(16);

commit;