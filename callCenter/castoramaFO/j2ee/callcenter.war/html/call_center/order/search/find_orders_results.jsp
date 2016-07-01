<dsp:page xml="true">

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/commerce/order/FindOrdersFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>


<html>
<head>
<TITLE>Castorama : Call center - Rechercher une commande</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<LINK REL="stylesheet" HREF="../../../css/hp.css">
</head>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">

<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>


<dsp:include page="../../common/header.jsp"/>
<table border=0 width=600 align=center>
	<tr>
		<td align=center>
<span class=marques>R&eacute;sultat de la recherche :</span><p>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
 <dsp:param name="value" bean="FindOrdersFormHandler.orderIds"/>
   <dsp:oparam name="unset">
      <span class=prix>Aucune r&eacute;ponse !</span>
   </dsp:oparam>
   <dsp:oparam name="default">
    <dsp:include page="./find_orders_results_list.jsp">
      <param name="ids" bean="FindOrdersFormHandler.orderIds"/>
    </dsp:include>
   </dsp:oparam>
</dsp:droplet>
		</td>
	</tr>
</table>
<dsp:include page="../../common/menuBas.jsp"/>
</body>
</html>

</dsp:page>