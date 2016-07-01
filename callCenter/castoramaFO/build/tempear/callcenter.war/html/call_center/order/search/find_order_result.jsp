<dsp:page xml="true">


<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/commerce/order/FindOrdersFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<%--dsp:include page="../../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include--%>



<html>
<TITLE>Castorama : Call center - Rechercher une commande</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<LINK REL="stylesheet" HREF="../../../css/hp.css">



<dsp:tomap var="laCommande" bean="FindOrdersFormHandler.orderPourAffichage"/>    



<c:if test="${laCommande == null}">
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">

<dsp:include page="../../common/header.jsp"/>
	<table border="0" width="600" align="center">
				<tr>
					<td align=center>
						<span class=marques>R&eacute;sultat de la recherche :</span><p>
						<span class=prix>Aucune r&eacute;ponse !</span>
	      			</td>
	      		</tr>
	</table>
	
</body>
</html>
</c:if>


<c:if test="${laCommande != null}">
  <c:set var="numOrder" value="${fn:substring(laCommande.id, 1, -1)}"/>
  <c:choose>
    <c:when test="${numOrder < 400000000}">
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="../show/order_migr.jsp?id=${laCommande.id}"/>
      </dsp:droplet>
    </c:when>
    <c:otherwise> 
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="../show/order.jsp?id=${laCommande.id}"/>
      </dsp:droplet>
     </c:otherwise> 
  </c:choose>
</c:if>





</dsp:page>