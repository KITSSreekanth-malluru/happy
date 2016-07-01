<dsp:page xml="true">


<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/RechercheCommandeParDateFormHandler"/>


<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>



<html>
<head>
<TITLE>Castorama : Call center - Rechercher un contact</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<LINK REL="stylesheet" HREF="../../../css/hp.css">
</head>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">


<dsp:include page="../../common/header.jsp"/>




<table align=center width=600 border=0 cellpadding=0 cellspacing=0>
<tr><td><span class=prix align=center>Resultat de la recherche :</span></td></tr>
<tr><td><span align=right><img src="<%=request.getContextPath()%>/html/img/flecheb_retrait.gif" border=0>&nbsp;<a class=moncasto href="javascript:history.back();">retour</a></span></td></tr>
</table>
<br>
<table cellpadding="0"  cellspacing="0" border=1 bordercolor="#CC0033" width=450 align=center>
	<tr>
		<td align=center>
			<span class=prix>Cliquer sur le num&eacute;ro de la commande pour la voir ou la modifier.</span>
		</td>
	</tr>
</table><br>

<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">  
  <dsp:oparam name="false">
   <dsp:droplet name="/atg/dynamo/droplet/TableRange">
   <dsp:param name="numColumns" value="1"/>
   <dsp:param name="howMany" value="25"/>
    <dsp:param name="array" bean="RechercheCommandeParDateFormHandler.resultatRecherche"/>
    <dsp:oparam name="outputStart">
    <table width=600 align="center" cellspacing=0 cellpadding=3 border=0>
       <tr align="center" valign="middle" bgcolor="#FFDF63">
       	<td align="center" width=100 align="center" class=moncasto>Commande</td>
        <td align="center" width=100 class=moncasto align=center>Internaute</td>
        <td align="center" width=150 class=moncasto align=center>Date</td>
       <td align="center" width=250 class=moncasto align=center>Etat</td>
       </tr>
       
  </dsp:oparam>
  <dsp:oparam name="outputEnd">
   </table>
		<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
		<tr>
		<td align=center colspan=8>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" param="hasPrev"/>
				<dsp:oparam name="true">
					<a class="moncasto" href="<c:out value="${pageContext.request.requestURI}"/>?start=<dsp:valueof param="prevStart"/>">
						Pr&eacute;c&eacute;dent&nbsp;<dsp:valueof param="prevHowMany"/>
					</a>&nbsp;&nbsp;&nbsp;&nbsp;
				</dsp:oparam>
			</dsp:droplet>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" param="hasNext"/>
				<dsp:oparam name="true">
					<a class=moncasto href="<c:out value="${pageContext.request.requestURI}"/>?start=<dsp:valueof param="nextStart"/>">
						Suivant&nbsp;<dsp:valueof param="nextHowMany"/>
					</a>
				</dsp:oparam>
			</dsp:droplet>
		</TD>
		</tr>
		</TABLE>
  </dsp:oparam>
  <dsp:oparam name="outputRowStart">
    <tr>
  </dsp:oparam>
  <dsp:oparam name="outputRowEnd">
    </tr>
  </dsp:oparam>
    <dsp:oparam name="output">
	<dsp:droplet name="/atg/dynamo/droplet/IsNull">
        <dsp:param name="value" param="element.submittedDate"/>
		<dsp:oparam name="false">
			<tr>
				<td align="center" class="moncasto">
   <dsp:getvalueof var="orderId" param="element.repositoryId"/>
  <c:set var="numOrder" value="${fn:substring(orderId, 1, -1)}"/>
  <c:choose>
    <c:when test="${numOrder < 400000000}">
					<a href="../show/order_migr.jsp?id=<dsp:valueof param="element.repositoryId"/>">
						<dsp:valueof param="element.repositoryId"/></a>
    </c:when>
    <c:otherwise> 
					<a href="../show/order.jsp?id=<dsp:valueof param="element.repositoryId"/>">
						<dsp:valueof param="element.repositoryId"/></a>
     </c:otherwise> 
  </c:choose>
				</td>
		       	<td align="center" class="moncasto"><dsp:valueof param="element.profileId"/></td>
				<td align="center" class="moncasto"><dsp:valueof param="element.submittedDate" date="dd/M/yyyy HH:mm:ss"><i>non renseign&eacute;e</i></dsp:valueof></td>
				<td align="center" class="moncasto">
				<dsp:setvalue param="etat" paramvalue="element.BOState"/>
				<dsp:include src="../../common/fragmentEtat.jsp"><dsp:param name="state" param="etat"/></dsp:include>
				</td>
			</tr>
		</dsp:oparam>
		</dsp:droplet>		
	</dsp:oparam>
    <dsp:oparam name="empty">
      <span class=prix>Il n'y a aucun r&eacute;sultat!</span>
    </dsp:oparam>
  </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="true">
	<table align=center width=600 border=0 cellpadding=0 cellspacing=0>
		<tr><td>
  	<span class=prix>Il n'y a aucun r&eacute;sultat !</span>
		</td></tr>
		</table>
  </dsp:oparam>
  <dsp:param name="value" bean="RechercheCommandeParDateFormHandler.resultatRecherche"/>
</dsp:droplet>
<dsp:include page="../../common/menuBas.jsp"/>

</BODY> </HTML>


</dsp:page>