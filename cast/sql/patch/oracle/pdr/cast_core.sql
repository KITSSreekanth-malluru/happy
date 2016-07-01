CREATE TABLE CASTO_EXPERIAN_REQUESTS(
    ID                           VARCHAR2(40) NOT NULL,
    CIVILITE                     NUMBER(*,0),
    FIRST_NAME                   VARCHAR2(64),  
    LAST_NAME                    VARCHAR2(64),
    EMAIL                        VARCHAR2(255 BYTE),
    ADDRESS1                     VARCHAR2(120),
    ADDRESS2                     VARCHAR2(120),
    ADDRESS3                     VARCHAR2(100),
    ADDRESS4                     VARCHAR2(100),
    POSTAL_CODE                  VARCHAR2(16),
    CITY                         VARCHAR2(100),
    PAYS                         VARCHAR2(40 BYTE),
    RECEIVE_EMAIL                NUMBER(*,0) default 0 not null,
    RESEIVE_OFFERS               NUMBER(1,0) default 0 not null,
    SOURCE_INSCRIPTION           VARCHAR2(75),
    DATEINSCRIPTION              DATE,
    DATEDESINCRIPTION            DATE,
    DATE_OFFERS_INSCRIPTION      DATE,
    DATE_OFFERS_DESINCRPTION     DATE,
    PHONE_NUMBER                 VARCHAR2(50),
    PHONE_NUMBER_2               VARCHAR2(15),
    CUSTOMER_CASTO               NUMBER(1,0),
    STORE_REFERENCE              VARCHAR2(50),
    CARTEATOUT                   NUMBER,
    JARDIN                       NUMBER,
    MAISON                       NUMBER,
    PROPRIETAIRE_LOCATAIRE       VARCHAR2(20 BYTE),
    MAISONCAMPAGNE               NUMBER,
    NBPERSONNES                  VARCHAR2(10 BYTE),
    USER_ID                      VARCHAR2(40 BYTE),
    DATE_OF_BIRTH                DATE,
    REGISTRATION_DATE            DATE,
    DATEMAJPROFIL                DATE,
    TYPEMAJPROFIL                NUMBER,
    REQUEST_SOURCE               NUMBER,
    PRIMARY KEY (ID)
);

CREATE TABLE CASTO_EXPERIAN_HISTORY (
	EXPORT_DATE                  DATE,
    ID                           VARCHAR2(40) NOT NULL,
    CIVILITE                     NUMBER(*,0),
    FIRST_NAME                   VARCHAR2(64),  
    LAST_NAME                    VARCHAR2(64),
    EMAIL                        VARCHAR2(255 BYTE),
    ADDRESS1                     VARCHAR2(120),
    ADDRESS2                     VARCHAR2(120),
    ADDRESS3                     VARCHAR2(100),
    ADDRESS4                     VARCHAR2(100),
    POSTAL_CODE                  VARCHAR2(16),
    CITY                         VARCHAR2(100),
    PAYS                         VARCHAR2(40 BYTE),
    RECEIVE_EMAIL                NUMBER(*,0) default 0 not null,
    RESEIVE_OFFERS               NUMBER(1,0) default 0 not null,
    SOURCE_INSCRIPTION           VARCHAR2(75),
    DATEINSCRIPTION              DATE,
    DATEDESINCRIPTION            DATE,
    DATE_OFFERS_INSCRIPTION      DATE,
    DATE_OFFERS_DESINCRPTION     DATE,
    PHONE_NUMBER                 VARCHAR2(50),
    PHONE_NUMBER_2               VARCHAR2(15),
    CUSTOMER_CASTO               NUMBER(1,0),
    STORE_REFERENCE              VARCHAR2(50),
    CARTEATOUT                   NUMBER,
    JARDIN                       NUMBER,
    MAISON                       NUMBER,
    PROPRIETAIRE_LOCATAIRE       VARCHAR2(20 BYTE),
    MAISONCAMPAGNE               NUMBER,
    NBPERSONNES                  VARCHAR2(10 BYTE),
    USER_ID                      VARCHAR2(40 BYTE),
    DATE_OF_BIRTH                DATE,
    REGISTRATION_DATE            DATE,
    DATEMAJPROFIL                DATE,
    TYPEMAJPROFIL                NUMBER,
    REQUEST_SOURCE               NUMBER,
    PRIMARY KEY (ID)
);

alter table CASTO_ABONNEMENT_NEWSLETTER add SOURCE_INSCRIPTION VARCHAR2(75 BYTE);

COMMIT;