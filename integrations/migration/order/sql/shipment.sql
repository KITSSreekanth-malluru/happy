SELECT TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS') AS DATETIME FROM DUAL;

insert into cast_core.casto_order_emails
  (order_id, email_type, is_sent)
select t2.ncommande_client, 'confirmationShipment' || t1.cenlevement, 1  
  from cast_core.vte_enlevement t1, cast_core.scd_vente_web t2
 where t1.cvente = t2.cvente
   and t1.cetat_ol_c651 in (5,9)
   and nvl(t1.dmaj, to_date('2010/01/15', 'yyyy/mm/dd')) <= to_date('2010/01/15', 'yyyy/mm/dd')
   and not exists (select 1 from cast_core.casto_order_emails a
                    where a.order_id = t2.ncommande_client
                      and email_type = 'confirmationShipment' || t1.cenlevement);
commit;

SELECT TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS') AS DATETIME FROM DUAL;

insert into cast_core.casto_order_emails
  (order_id, email_type, is_sent)
select t2.ncommande_client, 'shipmentPreparation' || t1.cenlevement, 1  
  from cast_core.vte_enlevement t1, cast_core.scd_vente_web t2
 where t1.cvente = t2.cvente
   and t1.cetat_ol_c651 in (3,4)
   and nvl(t1.dmaj, to_date('2010/01/15', 'yyyy/mm/dd')) <= to_date('2010/01/15', 'yyyy/mm/dd')
   and not exists (select 1 from cast_core.casto_order_emails a
                    where a.order_id = t2.ncommande_client
                      and email_type = 'shipmentPreparation' || t1.cenlevement);

commit;

SELECT TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS') AS DATETIME FROM DUAL;
