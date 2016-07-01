<%@page import="atg.servlet.DynamoHttpServletRequest"%>
<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>


<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/atg/commerce/order/FindOrdersFormHandler"/>


<%-- 
	stabilisation
--%>
<dsp:importbean bean="/atg/userprofiling/Profile"/>




	<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../../common/protocolChange.jsp"> <dsp:param name="protocol" value="secure" /> </dsp:include>


<html>
<head>
<TITLE>Castorama : Call center - Supprimer une commande</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<LINK REL="stylesheet" HREF="../../../css/hp.css">
</head>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">

<dsp:include page="../../common/header.jsp"/>

<p>
<table border=0 width=600 align=center>
<tr>
	<td align=center>
	<%
		//com.castorama.CastoOrderModifierFormHandler l_strOrderEditor = (com.castorama.CastoOrderModifierFormHandler)((DynamoHttpServletRequest)request).resolveName("/castorama/CastoOrderEditor");
		//String l_strOrderId = l_strOrderEditor.getOrder().getId();
		//String l_strCommentaire = "La commande "+l_strOrderId+ " a ete supprimee";
		//System.out.println("l_strCommentaire = "+l_strCommentaire);
	%>
<dsp:form action="../../login_success.jsp" method="POST">
	<dsp:input type="hidden" bean="CastoOrderEditor.type" value="Contact CallCenter"/>
	<dsp:input type="hidden" bean="CastoOrderEditor.action" value="Suppression"/>
	<dsp:input type="hidden" bean="CastoOrderEditor.commentaire" value="La commande ${CastoOrderEditor.order.id} a ete supprimee"/>
	<dsp:input type="hidden" bean="CastoOrderEditor.ip" beanvalue="/atg/dynamo/servlet/pipeline/BrowserAttributes.remoteAddr"/>
	<%--<droplet name="/atg/dynamo/security/CurrentUser">
    <oparam name="output">
	  <input type="hidden" bean="CastoOrderEditor.nomLogin" value="param:username">
	</oparam>
    <oparam name="empty"></oparam>
    <oparam name="error">error</oparam>
	</droplet>--%>
	
	
	<dsp:tomap var="order" bean="CastoOrderEditor.order"/>
	
	
     <span class=marques>  Confirmez-vous la suppression de la commande :<span class=prix> <dsp:valueof bean="CastoOrderEditor.order.id"><i>none</i></dsp:valueof> ??</span>
     </span>
     <p>&nbsp;<br>

       <dsp:input type="hidden" bean="CastoOrderEditor.cancelOrderSuccessURL" value="./order/edit/order_cancelled.jsp"/>
       <dsp:input type="hidden" bean="CastoOrderEditor.cancelOrderErrorURL" value="./order/edit/order_cancelled.jsp"/>
      
<%-- GY : stabilisation : journalisation--%>
				<input name="userId" type="hidden" value="<dsp:valueof bean="Profile.repositoryId"/>"/>
				<input name="userLogin" type="hidden" value="<dsp:valueof bean="Profile.login"/>"/>
				<input name="numeroCommande" type="hidden" value="<dsp:valueof bean="CastoOrderEditor.order.id"/>"/>
<%-- / GY : stabilisation --%>
       
       
       
       <dsp:input type="submit" bean="CastoOrderEditor.SupprimeOrder" value="  Oui  "/>
&nbsp; &nbsp;
  <%--<droplet name="/atg/dynamo/droplet/URLArgument">
     <param name="url" value="/call_center/order/show/order.jhtml">
     <param name="argName" value="id">
     <param name="argValue" value="bean:CastoOrderEditor.order.id">
     <oparam name="output">
       <input type="hidden" bean="CastoOrderEditor.forwardSuccessURL" value="bean:/atg/dynamo/droplet/URLArgument.url">
     </oparam>
  </droplet>--%>
  <dsp:input type="hidden" bean="CastoOrderEditor.forwardSuccessURL" value="./order/show/order.jsp?id=${order.id}"/>

       <dsp:input type="submit" bean="CastoOrderEditor.forward" value="  Non  "/>
</dsp:form>
	</td>
<tr>
</table>
<p>
</BODY>
</HTML>

</dsp:page>