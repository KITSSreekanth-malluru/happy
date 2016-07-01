<dsp:page xml="true">
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<html>
<TITLE>Castorama : Call center - Rechercher une commande</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<LINK REL="stylesheet" HREF="/css/hp.css">

<BODY BGCOLOR="#FFFFFF" LEFTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">
			<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="commerce-csr-orders-privilege,informatique-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">

<dsp:droplet name="/atg/commerce/order/FindOrdersFormHandler">
  <dsp:param name="searchType" value="byProfileLogin"/>
  <dsp:param name="login" param="login"/>
  <dsp:oparam name="output">
   <dsp:include page="./find_orders_results.jsp">
     <dsp:param name="ids" bean="/atg/commerce/order/FindOrdersFormHandler.orderIds"/>
   </dsp:include>
  </dsp:oparam>
</dsp:droplet>

</dsp:oparam>
<dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>
</body>
</html>

</dsp:page>