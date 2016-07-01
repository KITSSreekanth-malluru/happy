ALTER TABLE CASTO_USER ADD(
	FAILED_ATTEMPTS NUMBER(1,0) DEFAULT 0,
	FIRST_ATTEMPT_DATE DATE,
	LOCKOUT_DATE DATE);
  
CREATE TABLE CASTO_PASSWORD_LINK(
	ID VARCHAR2(40) NOT NULL,
	DATE_EXP DATE,
	KEY VARCHAR2(200),
	PRIMARY KEY (ID));

commit;