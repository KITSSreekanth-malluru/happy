
<dsp:page xml="true">

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/CastoJournalisationFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<html>
<head>
<TITLE>Castorama : Call center - Historique des actions de commande</TITLE>
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

<center>

<dsp:form method="post" action="${pageContext.request.requestURI}" name="formCommande">

	<table width=600 border=0 cellpadding=5 cellspacing=5>
		<tr>
		<td align="right" class="texte">Num&eacute;ro de commande : </td>
		<td align="left"><dsp:input type="text" size="25" bean="CastoJournalisationFormHandler.numeroCommande" iclass="moncasto"/></td>
		<td align="left"><dsp:input type="image" src="../../../img/valider.gif" border="0" bean="CastoJournalisationFormHandler.findJournalisation" value=" Valider "/></td>
		</tr>
	</table>
	
		<dsp:input type="hidden" bean="CastoJournalisationFormHandler.successURL" value="${pageContext.request.requestURI}" />
		<dsp:input type="hidden" bean="CastoJournalisationFormHandler.errorURL" value="${pageContext.request.requestURI}" />	

		
		
		<dsp:droplet name="IsNull">
			<dsp:param name="value" param="success"/>
			<dsp:oparam name="false">
				<dsp:droplet name="Switch">
					<dsp:param name="value" bean="CastoJournalisationFormHandler.existeJournalisation"/>
					<dsp:oparam name="true">
						<%int cpt = 0; %>
						<dsp:droplet name="ForEach">
							<dsp:param name="array" bean="CastoJournalisationFormHandler.journalisation"/>
							<dsp:param name="sortProperties" value="-dateAction"/>
							<dsp:oparam name="outputStart">
								<br/>
								<br/>
								<br/>
								
								<table width=600 border=1 cellpadding=0 cellspacing=0>
									<tr style="background-color: gray; ">
										<td align="center">Date</td>
										<td align="center">Utilisateur</td>
										<td align="center">Identifiant utilisateur</td>
										<td align="center">Action</td>
									</tr>
							</dsp:oparam>
							<dsp:oparam name="outputEnd">
								</table>
							</dsp:oparam>
							<dsp:oparam name="output">
								
								<%
									if (cpt % 2 == 0)
									{
								%>
									<tr>
								<%
									}
									else
									{
								%>
									<tr style="background-color: gray;">
								<%
									}
								cpt = cpt + 1;
								%>
								
									<td><dsp:valueof param="element.dateAction"/></td>
									<td><dsp:valueof param="element.login"/></td>
									<td><dsp:valueof param="element.userId"/></td>
									<td><dsp:valueof param="element.action"/></td>
								
								</tr>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
					<dsp:oparam name="false">
						Aucune action n'a &eacute;t&eacute; faite sur cette commande dans le cadre du Call-Center.
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>




	
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	
	<dsp:droplet name="ErrorMessageForEach">
			<dsp:param bean="CastoJournalisationFormHandler.formExceptions" name="exceptions"/>
			<dsp:oparam name="output">
				<div style="background-color: red;">
					<SPAN><img src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0><dsp:valueof param="message" /></span><br />		
				</div>
			</dsp:oparam>
	</dsp:droplet>
	
</dsp:form>
</center>



<dsp:include page="../../common/menuBas.jsp"/>
</body>
</html>

</dsp:page>