select d D ,
       sum("1" ) Col1 ,
       sum("3" ) Col3 , 
       sum("5" ) Col5 , 
       sum("6" ) Col6 , 
       sum("9")  Col9 , 
       sum("10") Col10,
       sum("11") Col11,
       sum("12") Col12

from ( select 
	d , 
	"1" * n + "1 J-1" * n1 + "1 J-2" * n2 + "1 J-3" * n3 + "1 J-4" * n4 + "1 J-5" * n5 + "1 J-6" * n6 + "1 J-7" * n7 "1" ,
	0 "3" ,
	0 "5" ,
	0 "6" , 
	0 "9" , 
	0 "10", 
	0 "11", 
	0 "12" 
	from ( 
		select 
			sum(Dossier7) "1 J-7", 
			sum(Dossier6) "1 J-6", 
			sum(Dossier5) "1 J-5", 
			sum(Dossier4) "1 J-4", 
			sum(Dossier3) "1 J-3", 
			sum(Dossier2) "1 J-2", 
			sum(Dossier1) "1 J-1", 
			sum(Dossier0) "1" 
		from ( 
			select trunc(dcreation) DCreation,
			 decode(sign(trunc(dcreation) - trunc(sysdate) + 8),1,0,1) Dossier7,
			 decode(sign(trunc(dcreation) - trunc(sysdate) + 7),1,0,1) Dossier6,
			 decode(sign(trunc(dcreation) - trunc(sysdate) + 6),1,0,1) Dossier5,
			 decode(sign(trunc(dcreation) - trunc(sysdate) + 5),1,0,1) Dossier4,
			 decode(sign(trunc(dcreation) - trunc(sysdate) + 4),1,0,1) Dossier3,
			 decode(sign(trunc(dcreation) - trunc(sysdate) + 3),1,0,1) Dossier2,
			 decode(sign(trunc(dcreation) - trunc(sysdate) + 2),1,0,1) Dossier1,
			 1 Dossier0
			from scd_reclamation
			where dcreation >= to_date('01092006','DDMMYYYY') 
			and dcreation < trunc(sysdate) and creclamation > 33572 
		     )
		), 
	( 
	  select trunc(sysdate)-8 d , 0 n, 0 n1, 0 n2, 0 n3, 0 n4, 0 n5, 0 n6, 1 n7 from dual union all 
	  select trunc(sysdate)-7 d , 0 n, 0 n1, 0 n2, 0 n3, 0 n4, 0 n5, 1 n6, 0 n7 from dual union all
	  select trunc(sysdate)-6 d , 0 n, 0 n1, 0 n2, 0 n3, 0 n4, 1 n5, 0 n6, 0 n7 from dual union all
	  select trunc(sysdate)-5 d , 0 n, 0 n1, 0 n2, 0 n3, 1 n4, 0 n5, 0 n6, 0 n7 from dual union all
	  select trunc(sysdate)-4 d , 0 n, 0 n1, 0 n2, 1 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual union all 
	  select trunc(sysdate)-3 d , 0 n, 0 n1, 1 n2, 0 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual union all 
	  select trunc(sysdate)-2 d , 0 n, 1 n1, 0 n2, 0 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual union all 
	  select trunc(sysdate)-1 d , 1 n, 0 n1, 0 n2, 0 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual 
	) 
	union all 
	select 
		d ,
		0 "1" ,
		"3" * n + "3 J-1" * n1 + "3 J-2" * n2 + "3 J-3" * n3 + "3 J-4" * n4 + "3 J-5" * n5 + "3 J-6" * n6 + "3 J-7" * n7 "3" ,
		0 "5" ,
		0 "6" ,
		0 "9" ,
		0 "10",
		0 "11",
		0 "12"
	from ( select 
		sum(CventeNonNull*Inf7) "3 J-7",
		sum(CventeNonNull*Inf6) "3 J-6",
		sum(CventeNonNull*Inf5) "3 J-5",
		sum(CventeNonNull*Inf4) "3 J-4",
		sum(CventeNonNull*Inf3) "3 J-3",
		sum(CventeNonNull*Inf2) "3 J-2",
		sum(CventeNonNull*Inf1) "3 J-1",
		sum(CventeNonNull*Nb) "3"
	      from (
	      		select trunc(dcreation) DCreation ,
		      	       1 Nb ,
		      	       decode(cvente,null,0,1) CventeNonNull ,
		      	       decode(sign(trunc(dcreation) - trunc(sysdate) + 2),1,0,1) Inf1 ,
		      	       decode(sign(trunc(dcreation) - trunc(sysdate) + 3),1,0,1) Inf2 ,
		      	       decode(sign(trunc(dcreation) - trunc(sysdate) + 4),1,0,1) Inf3 ,
		      	       decode(sign(trunc(dcreation) - trunc(sysdate) + 5),1,0,1) Inf4 ,
		      	       decode(sign(trunc(dcreation) - trunc(sysdate) + 6),1,0,1) Inf5 ,
		      	       decode(sign(trunc(dcreation) - trunc(sysdate) + 7),1,0,1) Inf6 ,
		      	       decode(sign(trunc(dcreation) - trunc(sysdate) + 8),1,0,1) Inf7 
		      	from scd_contact 
		      	where dcreation >= to_date('01092006','DDMMYYYY') 
		      	and dcreation < trunc(sysdate) and ccontact > 320170 
	      	    )
	     ),
	     ( 
	       select trunc(sysdate)-8 d , 0 n, 0 n1, 0 n2, 0 n3, 0 n4, 0 n5, 0 n6, 1 n7 from dual union all 
	       select trunc(sysdate)-7 d , 0 n, 0 n1, 0 n2, 0 n3, 0 n4, 0 n5, 1 n6, 0 n7 from dual union all 
	       select trunc(sysdate)-6 d , 0 n, 0 n1, 0 n2, 0 n3, 0 n4, 1 n5, 0 n6, 0 n7 from dual union all 
	       select trunc(sysdate)-5 d , 0 n, 0 n1, 0 n2, 0 n3, 1 n4, 0 n5, 0 n6, 0 n7 from dual union all 
	       select trunc(sysdate)-4 d , 0 n, 0 n1, 0 n2, 1 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual union all 
	       select trunc(sysdate)-3 d , 0 n, 0 n1, 1 n2, 0 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual union all
	       select trunc(sysdate)-2 d , 0 n, 1 n1, 0 n2, 0 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual union all
	       select trunc(sysdate)-1 d , 1 n, 0 n1, 0 n2, 0 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual 
	     ) 
	union all
	select 
		trunc(dcreation) d ,
		0 "1" ,
		0 "3" ,
		0 "5" ,
		0 "6" ,
		count(*) "9" ,
		0 "10", 
		0 "11",
		0 "12"
	FROM scd_contact 
	WHERE trunc(dcreation) >= trunc(sysdate)- 8
	and trunc(dcreation) < trunc(sysdate) 
	and ccontact > 320170 GROUP BY trunc(dcreation) 
	
	union all 
	select d ,
	       0 "1" ,
	       0 "3" ,
	       "5" * n + "5 J-1" * n1 + "5 J-2" * n2 + "5 J-3" * n3 + "5 J-4" * n4 + "5 J-5" * n5 + "5 J-6" * n6 + "5 J-7" * n7 "5" ,
	       0 "6" ,
	       0 "9" ,
	       0 "10",
	       0 "11",
	       0 "12"
	 from (
	  select 
	  	sum(Egal8) "5 J-7",
	  	sum(Egal7) "5 J-6",
	  	sum(Egal6) "5 J-5",
	  	sum(Egal5) "5 J-4",
	  	sum(Egal4) "5 J-3",
	   	sum(Egal3) "5 J-2",
	   	sum(Egal2) "5 J-1",
	   	sum(Egal1) "5"
	   from (
	    select trunc(datecontact) datecontact,
	    	 decode(trunc(datecontact) - trunc(sysdate) +1 ,0,1,0) Egal1 ,
	    	 decode(trunc(datecontact) - trunc(sysdate) +2 ,0,1,0) Egal2 ,
	    	 decode(trunc(datecontact) - trunc(sysdate) +3 ,0,1,0) Egal3 ,
	    	 decode(trunc(datecontact) - trunc(sysdate) +4 ,0,1,0) Egal4 ,
	    	 decode(trunc(datecontact) - trunc(sysdate) +5 ,0,1,0) Egal5 ,
	    	 decode(trunc(datecontact) - trunc(sysdate) +6 ,0,1,0) Egal6 ,
	    	 decode(trunc(datecontact) - trunc(sysdate) +7 ,0,1,0) Egal7 ,
	    	 decode(trunc(datecontact) - trunc(sysdate) +8 ,0,1,0) Egal8 
	    from casto_contact 
	    where datecontact >= to_date('27052006','DDMMYYYY') 
	    and datecontact < trunc(sysdate) and numdossier is null )
	     ),
	     ( 
	     	select trunc(sysdate)-8 d , 0 n, 0 n1, 0 n2, 0 n3, 0 n4, 0 n5, 0 n6, 1 n7 from dual union all
	     	select trunc(sysdate)-7 d , 0 n, 0 n1, 0 n2, 0 n3, 0 n4, 0 n5, 1 n6, 0 n7 from dual union all
	     	select trunc(sysdate)-6 d , 0 n, 0 n1, 0 n2, 0 n3, 0 n4, 1 n5, 0 n6, 0 n7 from dual union all
	     	select trunc(sysdate)-5 d , 0 n, 0 n1, 0 n2, 0 n3, 1 n4, 0 n5, 0 n6, 0 n7 from dual union all
	       	select trunc(sysdate)-4 d , 0 n, 0 n1, 0 n2, 1 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual union all
	       	select trunc(sysdate)-3 d , 0 n, 0 n1, 1 n2, 0 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual union all
	       	select trunc(sysdate)-2 d , 0 n, 1 n1, 0 n2, 0 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual union all
	       	select trunc(sysdate)-1 d , 1 n, 0 n1, 0 n2, 0 n3, 0 n4, 0 n5, 0 n6, 0 n7 from dual 
	     ) 
	union all
	select 
	  trunc(dateenvoyebo) d ,
	  0 "1" ,
	  0 "3" ,
	  0 "5" ,
	  count(*) "6" ,
	  0 "9" ,
	  0 "10",
	  0 "11",
	  0 "12"
	 from casto_contact
	 WHERE datecontact > to_date('27052006','DDMMYYYY') 
	 AND dateenvoyebo is not null 
	 AND ( INITIATEURCONTACT_C659 = 2 OR TYPECONTACT_C632 = 3) 
	 and trunc(dateenvoyebo) >= trunc(sysdate)- 8
	 and trunc(dateenvoyebo) < trunc(sysdate) 
	 group by trunc(dateenvoyebo) 
	 
	union all 
	select 
		trunc(date_envoie-.25) d ,
		0 "1" ,
		0 "3" ,
		0 "5" ,
		0 "6" ,
		0 "9" ,
		sum(decode(questionnaire_id,'AVIS_MESSAGE' ,1,0)) "10",
		sum(decode(questionnaire_id,'AVIS_PREPA' ,1,0)) "11",
		sum(decode(questionnaire_id,'AVIS_EXPEDITION',1,0)) "12"
	from casto_mailsuivicommande 
	where trunc(date_envoie-.25) >= trunc(sysdate)- 8
	 and trunc(date_envoie-.25) < trunc(sysdate)
	  GROUP BY trunc(date_envoie-.25) 
) group by d order by d 