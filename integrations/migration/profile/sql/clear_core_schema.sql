--     Expected
-- &1 DESTINATION SCHEMA NAME

DELETE FROM "&1".CASTO_ABONNEMENT_NEWSLETTER;
COMMIT;

DELETE FROM "&1".dps_other_addr;
COMMIT;

DELETE FROM "&1".CASTO_USER;
COMMIT;

DELETE FROM "&1".dcs_user;
COMMIT;

DELETE FROM "&1".dps_user_address;
COMMIT;

DELETE FROM "&1".dps_user;
COMMIT;

DELETE FROM "&1".CASTO_CONTACT_INFO;
COMMIT;

DELETE FROM "&1".dps_contact_info;
COMMIT;

DISCONNECT;
quit;