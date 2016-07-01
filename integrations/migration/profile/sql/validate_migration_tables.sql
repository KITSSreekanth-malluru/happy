-- 	Expected
-- &1 MIGRATION SCHEMA NAME
-- &2 CATALOG SCHEMA NAME
-- &3 DESTINATION SCHEMA NAME

-- fix login: to be sure that all logins are valid
UPDATE "&1".DPS_USER SET LOGIN = REPLACE(LOGIN, '#', '@');

COMMIT;

UPDATE "&3".DPS_USER SET LOGIN = REPLACE(LOGIN, '#', '@');

COMMIT;
-- fix login: end

SELECT 'Duplicated accounts:' FROM DUAL;

SELECT * 
  FROM 
  (SELECT LOGIN, COUNT(1) AS TOTAL 
     FROM "&1".DPS_USER
    GROUP BY LOGIN)
 WHERE TOTAL > 1
 ORDER BY TOTAL DESC; 

SELECT 'Already exists accounts:' FROM DUAL;

SELECT A.LOGIN, A.ID AS TEMP_ID, B.ID AS TARGET_ID
  FROM "&1".DPS_USER A, 
       "&3".DPS_USER B       
 WHERE A.LOGIN = B.LOGIN
   AND A.ID <> B.ID
 ORDER BY A.LOGIN;

SELECT 'Wrong account references in DPS_OTHER_ADDR' FROM DUAL;

SELECT *
  FROM "&1".DPS_OTHER_ADDR a
 WHERE NOT EXISTS (SELECT 1 FROM "&1".DPS_USER b 
                    WHERE a.USER_ID = b.ID);

SELECT 'Wrong account references in DPS_USER_ADDRESS' FROM DUAL;

SELECT *
  FROM "&1".DPS_USER_ADDRESS a
 WHERE NOT EXISTS (SELECT 1 FROM "&1".DPS_USER b 
                    WHERE a.ID = b.ID);

SELECT 'Wrong account references in CASTO_OTHER_BILLING_ADDR' FROM DUAL;

SELECT *
  FROM "&1".CASTO_OTHER_BILLING_ADDR a
 WHERE NOT EXISTS (SELECT 1 FROM "&1".DPS_USER b 
                    WHERE a.USER_ID = b.ID);

SELECT 'Wrong account references in CASTO_OTHER_BILLSHIP_ADDR' FROM DUAL;

SELECT *
  FROM "&1".CASTO_OTHER_BILLSHIP_ADDR a
 WHERE NOT EXISTS (SELECT 1 FROM "&1".DPS_USER b 
                    WHERE a.USER_ID = b.ID);

SELECT 'Wrong store references in CASTO_USER:' FROM DUAL;

SELECT ID, SUBSTR(ID_MAGASIN_REF, 1, 50) AS ID_MAGAZIN_REF
  FROM "&1".CASTO_USER a
 WHERE NOT EXISTS (SELECT 1 FROM "&2".CASTO_MAGASIN b
                    WHERE a.ID_MAGASIN_REF = TO_CHAR(b.ID))
   AND ID_MAGASIN_REF IS NOT NULL;

SELECT 'Wrong store names in CASTO_ABONNEMENT_NEWSLETTER:' FROM DUAL;

SELECT SUBSTR(EMAIL, 1, 50) AS EMAIL_ADDRESS, SUBSTR(MAGASIN, 1, 50) AS MAGAZIN
  FROM "&1".CASTO_ABONNEMENT_NEWSLETTER a
 WHERE NOT EXISTS (SELECT 1 FROM "&2".CASTO_MAGASIN b
                    WHERE UPPER(a.MAGASIN) = UPPER(b.NOM))
   AND MAGASIN IS NOT NULL;

SELECT 'Wrong newsletter references:' FROM DUAL;

SELECT * 
  FROM              
    (SELECT SUBSTR(ABONNEMENT_NEWSLETTER_ID, 1, 30) AS EMAIL, COUNT(1) AS TOTAL
       FROM "&1".CASTO_USER_NEWSLETTER
      GROUP BY ABONNEMENT_NEWSLETTER_ID
      ORDER BY 2 DESC)
 WHERE TOTAL > 1;

SELECT 'Wrong number of addresses:' FROM DUAL;

SELECT * FROM
(SELECT USER_ID, COUNT(1) AS TOTAL
   FROM
   (SELECT A1.USER_ID, A1.ADDRESS_ID 
      FROM "&1".DPS_OTHER_ADDR A1 
     INNER JOIN "&1".DPS_CONTACT_INFO C1 ON A1.ADDRESS_ID = C1.ID 
     WHERE C1.ADDRESS1 IS NOT NULL OR C1.LAST_NAME IS NOT NULL
    UNION 
    SELECT A2.ID AS USER_ID, A2.HOME_ADDR_ID AS ADDRESS_ID
      FROM "&1".DPS_USER_ADDRESS A2 
     INNER JOIN "&1".DPS_CONTACT_INFO C2 ON A2.HOME_ADDR_ID = C2.ID 
     WHERE C2.ADDRESS1 IS NOT NULL OR C2.LAST_NAME IS NOT NULL
    UNION
    SELECT A3.USER_ID, A3.ADDRESS_ID 
      FROM "&1".CASTO_OTHER_BILLING_ADDR A3 
     INNER JOIN "&1".DPS_CONTACT_INFO C3 ON A3.ADDRESS_ID = C3.ID 
     WHERE C3.ADDRESS1 IS NOT NULL OR C3.LAST_NAME IS NOT NULL
    UNION
   SELECT A4.USER_ID, A4.ADDRESS_ID
     FROM "&1".CASTO_OTHER_BILLSHIP_ADDR A4 
    INNER JOIN "&1".DPS_CONTACT_INFO C4 ON A4.ADDRESS_ID = C4.ID 
    WHERE C4.ADDRESS1 IS NOT NULL AND C4.LAST_NAME IS NOT NULL)
  GROUP BY USER_ID)
 WHERE TOTAL > 3;

DISCONNECT;
quit;