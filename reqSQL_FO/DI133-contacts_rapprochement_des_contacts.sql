select 'insert into scd_contact_fo (ccontact) values ('||to_char(ccontact)||');' "--Insertions"
  from scd_contact
 where dcreation > trunc(sysdate-30)
   and ccontact > 320170
