<dsp:page xml="true">


<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<HTML>
<TITLE>Castorama : Call center - Rechercher une commande</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK rel="stylesheet" href="../../../css/hp.css">
<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<dsp:include page="../../common/header.jsp"/>
<P>
<!--- SEARCH ORDERS --->
	<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="commerce-csr-orders-privilege,informatique-privilege"/>				   		   		
		   		<dsp:oparam name="accesAutorise">
        
   <dsp:include page="./find_orders.jsp"/>
   
   </dsp:oparam>
   <dsp:oparam name="accesRefuse"></dsp:oparam>
   </dsp:droplet>

</BODY>
</HTML>

</dsp:page>