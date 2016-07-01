select 'insert into casto_contact_fo (idcontact,dateenvoyebo) values ('''||idcontact||''','||
       'to_date('''||to_char(dateenvoyebo,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'') );' "--Insertions"
  from casto_contact
 where dateenvoyebo is not null
   and dateenvoyebo > trunc(sysdate-30)
   and numcontact > 320170
