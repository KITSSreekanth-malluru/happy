<%@page import="atg.servlet.DynamoHttpServletRequest"%>
<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>

	<dsp:importbean bean="/castorama/CastoOrderEditor" />
	<dsp:importbean bean="/castorama/BeanSetDateOrder" />



	<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../../common/protocolChange.jsp">
		<dsp:param name="protocol" value="secure" />
	</dsp:include>




	<html>
	<TITLE>Castorama : Call center - Supprimer une commande</TITLE>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
	<LINK REL="stylesheet" HREF="../../../css/hp.css">
	<BODY BGCOLOR="#FFFFFF" LEFTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0"
		MARGINHEIGHT="0">

	<dsp:include page="../../common/header.jsp" />
	<p>
	<table border=0 width=600 align=center>
		<tr>
			<td align=center>
			<dsp:droplet name="/castorama/SetStateOrder">
				<dsp:param name="ID" bean="CastoOrderEditor.order.id" />
				<dsp:param name="State" value="PENDING_REMOVE" />
				<dsp:param name="StateNum" value="14" />
				<dsp:param name="Detail" value="En attente de suppression" />
				<dsp:oparam name="OUTPUT">
				</dsp:oparam>
			</dsp:droplet> 
			
			<dsp:setvalue bean="BeanSetDateOrder.OrderID" beanvalue="CastoOrderEditor.order.id" />
			<dsp:setvalue bean="BeanSetDateOrder.DateOrder" value="suppression" /> 
			<span class="prix">La commande est en &eacute;tat de suppression !</span>
			</td>
		<tr>
	</table>
	</body>
	</html>

</dsp:page>
