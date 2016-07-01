<dsp:page xml="true">

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:importbean bean="/castorama/pageListe/CastoStockDataPageListe" />
<dsp:importbean bean="/castorama/pageListe/CastoReinitTri" />
<%-- AP dsp:importbean bean="/castorama/settings/CastoImgPath"/--%>
<dsp:importbean bean="/castorama/order/CastoExportCdesAdminFormHandler" />

<%--AP dsp:tomap var="beanImg" bean="CastoImgPath"/--%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
   <dsp:param name="value" bean="CastoStockDataPageListe.page" />
   <dsp:oparam name="0">
      <dsp:setvalue bean="CastoStockDataPageListe.page" value="1" />
   </dsp:oparam>
</dsp:droplet>

<html>
<head>
	<TITLE>Castorama : Call center - Suivi des commandes passées par les administrateurs Call Center</TITLE>
	
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
	
	<LINK REL="stylesheet" HREF="../../../css/hp.css">
	<%-- AP link rel="stylesheet" type="text/css" href="<c:out value="${beanImg.cssPath}"/>/style.css" media="all" />
	<link rel="stylesheet" type="text/css" href="<c:out value="${beanImg.cssPath}"/>/pageliste/style.css" media="all" />
	<link rel="stylesheet" href="html/css/hp.css">
	
	<script type="text/javascript" src="<c:out value="${beanImg.cssPath}"/>/style_contenu.css"></script>	
	<script type="text/javascript" src="<c:out value="${beanImg.jsPath}"/>/fonctions.js"></script>
	<script type="text/javascript" src="<c:out value="${beanImg.jsPath}"/>/page-liste/fonctions.js"></script--%>
	
	<script type="text/javascript" >
		<!--
			function navPaginationForm(page) 
			{
				document.paginationForm.page.value = page;
				document.paginationForm.submit();
			}
			
		////////////////////////////////////////////////////////////////////////////
		//-->					
	</script>
	
</head>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">

<%-- 
------------------------------------------------------------------------------------------ 
Securisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>
<center>	<dsp:include page="../../common/header.jsp"/></center>

<%
	//Init du nombre de produits total de la catÃ©gorie
	int nombreDeProduitsTotal=0;
	//init du flux pour comparateur
	String flux = null;
%>

<%-- AP added --%>
<c:set var="count" value="0"/>

<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
	<dsp:param name="queryRQL" value="cdeAdmin=true"/>
	<dsp:param name="repository" value="/atg/commerce/order/OrderRepository"/>
	<dsp:param name="itemDescriptor" value="order"/>
	<dsp:param name="elementName" value="casto_order"/>
	<dsp:oparam name="output">
		<%
			// AP nombreDeProduitsTotal = new Integer(request.getParameter("count")).intValue();
		if (request.getParameter("count") != null) {
			nombreDeProduitsTotal = new Integer(request.getParameter("count")).intValue();
		}
		%>
		<dsp:getvalueof var="count" param="count"/>
		<%-- AP c:set var="count" value="${param['count']}"/--%>
	</dsp:oparam>
</dsp:droplet>
			
<dsp:droplet name="castorama/droplet/CastoBlocPagination">
	<dsp:param name="nombreTotalProduit" value="${count}"/>
	<dsp:param name="pageEnCours" bean="CastoStockDataPageListe.page"/>
	<dsp:param name="nbProdMax" value="25"/>
	<dsp:oparam name="output">
		<div class="pager pager_haut block_float">
			<%
				flux = request.getParameter("fluxPagination");
			%>						
			<dsp:valueof param="fluxPagination" valueishtml="true" />
		</div>
	</dsp:oparam>
</dsp:droplet>
		
	<br/>	
	<br/>
	<br/>
	<br/>
	<center>		
		<table border="1">
			<tr>
				<td>Compte de l&acute;admin</td>
				<td>Nom du d&eacute;tenteur du compte</td>
				<td>Contenu de la commande</td>
				<td>Date d&acute;export de la commande</td>
				<td>Montant</td>
				<td>T&eacute;l&eacute;phone du client</td>
				<td>Mode de Paiement</td>
			</tr>
 			
			<dsp:droplet name="/castorama/droplet/CastoListerCommandesAdmin">
				<dsp:param name="pageEnCours" bean="CastoStockDataPageListe.page" />
				<dsp:param name="nombreTotalProduit" value="${count}" />
				<dsp:oparam name="output">
				
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param="lesProduits" />
						<dsp:param name="elementName" value="casto_order"/>
						<dsp:oparam name="output">
							
							<dsp:droplet name="/atg/targeting/RepositoryLookup">
							  	<dsp:param bean="/atg/userprofiling/ProfileAdapterRepository" name="repository"/>
						    	<dsp:param name="itemDescriptor" value="user"/>
						    	<dsp:param name="id" param="casto_order.profileId"/>
						    	<dsp:param name="elementName" value="profile"/>
						    	<dsp:oparam name="output">
                                    <dsp:getvalueof var="login" param="profile.login"/>
                                    <dsp:getvalueof var="phoneNumber" param="profile.billingAddress.phoneNumber"/>
   					    	</dsp:oparam>
						  	</dsp:droplet>
						  	
						  	<dsp:droplet name="ForEach">
								<dsp:param name="array" param="casto_order.paymentGroups" />
								<dsp:param name="elementName" value="paymentGroup"/>
								<dsp:oparam name="output">
                                    <dsp:getvalueof var="amount" param="paymentGroup.amount"/>
                                    <dsp:getvalueof var="paymentMethod" param="paymentGroup.paymentMethod"/>
								</dsp:oparam>
							</dsp:droplet>
							
					  		<tr>
					  			<td>
					  				<dsp:valueof param="casto_order.adminLogin" />
					  			</td>
								<td>
                                    <dsp:valueof value="${login}"/>
								</td>
								<td>
									<dsp:valueof param="casto_order.id"/> : <br/>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="casto_order.commerceItems" />
										<dsp:param name="elementName" value="cce"/>
										<dsp:oparam name="output">
											-> <dsp:valueof param="cce.catalogRefId"/>*<dsp:valueof param="cce.quantity"/><br/>
										</dsp:oparam>
									</dsp:droplet>
								</td>
								<td><dsp:valueof param="casto_order.exportdate" /></td>
								<td>
									<c:out value="${amount}"/>
								</td>
								<td>
								  	<c:out value="${phoneNumber}"/>
								</td>
								<td>
									<c:out value="${paymentMethod}"/>
								</td>
							</tr>
							
						</dsp:oparam>
					</dsp:droplet>
					
				</dsp:oparam>
			</dsp:droplet>
		
		</table>
</center>
<dsp:form formid="paginationForm" name="paginationForm" method="post" action="${urlReecrite}">
	<dsp:input bean="CastoStockDataPageListe.page" name="page" type="hidden" />
</dsp:form>

<br/>
<!-- DEBUT pagination -->	
<div class="pager pager_bas block_float">
	<%if (flux != null) out.print(flux);%>
</div>
<!-- FIN pagination -->

<br/><br/><br/><br/>

<center>
	<dsp:form method="post" action="/adminFO/html/rattrapageCde/login_success.jsp" name="formUser">
			<dsp:input type="hidden" bean="CastoExportCdesAdminFormHandler.successURL" value="/adminFO/html/call_center/order/show/suivi-commande.jsp" />
			<dsp:input type="hidden" bean="CastoExportCdesAdminFormHandler.errorURL" value="/adminFO/html/call_center/order/show/suivi-commande.jsp" />

			<table width="500" border="0" cellspacing="5">
				<tr>
					<td colspan="3" align="center">
						Export des commandes :
					</td>
				</tr>
				<tr>
					<td align="right">
						Date de d&eacute;but : 
					</td>
					<td align="left">
						<dsp:input bean="CastoExportCdesAdminFormHandler.dateDebut" type="text"/><i>* format des dates : jj/mm/aaaa</i>				
					</td>			
					<td rowspan="2" align="center">
						<%-- AP dsp:input bean="CastoExportCdesAdminFormHandler.update" src="${beanImg.imgPath}/bg-bt-valider.gif" type="image" /--%>
						<dsp:input bean="CastoExportCdesAdminFormHandler.update" src="${pageContext.request.contextPath}/html/img/valider.gif" type="image" />
					</td>		
				</tr>
				<tr>
					<td align="right">
						Date de fin : 
					</td>
					<td align="left">
						<dsp:input bean="CastoExportCdesAdminFormHandler.dateFin" type="text"/><i>* format des dates : jj/mm/aaaa</i>
					</td>
				</tr>
			</table>
			<dsp:droplet name="ErrorMessageForEach">
				<dsp:param bean="CastoExportCdesAdminFormHandler.formExceptions" name="exceptions"/>
				<dsp:oparam name="output">
					<div class="erreur">
						<SPAN><img src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0><dsp:valueof param="message" /></span><br />		
					</div>
				</dsp:oparam>
			</dsp:droplet>
			
			<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
				<dsp:param name="value" bean="CastoExportCdesAdminFormHandler.fichier"/>
				<dsp:oparam name="false">
					<SPAN class="texteb">
						<IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" width="8" height="7" border="0" />
						<%-- en dur sur gsecat mais dynamique sur preprod et prod href="${pageContext.request.contextPath} --%>
						<A href="${pageContext.request.contextPath}<dsp:valueof  bean="CastoExportCdesAdminFormHandler.urlResultat"/>">
								cliquer sur :&nbsp; <dsp:valueof  bean="CastoExportCdesAdminFormHandler.nomFichierResultat"/> &nbsp; pour sauvegarder le fichier.
						</A>	
					</SPAN>
				</dsp:oparam>
			</dsp:droplet>
			
	</dsp:form>
</center>

<center>	
	<dsp:include page="../../common/menuBas.jsp"/>
</center>	
</body>
</html>


</dsp:page>