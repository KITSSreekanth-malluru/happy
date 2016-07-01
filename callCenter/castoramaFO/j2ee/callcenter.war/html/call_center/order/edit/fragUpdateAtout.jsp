
<%@page import="atg.servlet.DynamoHttpServletRequest"%>
<dsp:page xml="true">


<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/castorama/OrderFormHandler"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

		<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="atout-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">
    

<table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
<tr>
	<td width="1" bgcolor="#003399"><img src="${contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
</tr>
</table>
<p align=center><span class="marques">Validation du paiement par carte l'Atout</span><br><br>

<%--AP dsp:setvalue param="atoutSuccessURL" beanvalue="CastoOrderEditor.orderId"/--%>
<dsp:getvalueof var="atoutSuccessURL" bean="CastoOrderEditor.orderId" />

<%// AP ((DynamoHttpServletRequest)request).setParameter("atoutSuccessURL", "../show/order.jsp?id=" + (String)((DynamoHttpServletRequest)request).getParameter("atoutSuccessURL") );%>
	<dsp:include page="./erreurs_carte_atout.jsp"/>
	<dsp:form action="./order.jsp" method="post" name="formAtout">
	<dsp:input type="hidden" bean="OrderFormHandler.requireIdOnCreate" value="true"/>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
			<dsp:param name="value" bean="OrderFormHandler.formError"/>
		<dsp:oparam name="false">
	<dsp:setvalue bean="OrderFormHandler.repositoryId" beanvalue="CastoOrderEditor.orderId"/>
	<dsp:setvalue bean="OrderFormHandler.extractDefaultValuesFromItem" value="true"/>
	<dsp:input type="hidden" bean="OrderFormHandler.extractDefaultValuesFromItem" value="true"/>
		</dsp:oparam>
	</dsp:droplet>
	<%--AP dsp:input type="hidden" bean="OrderFormHandler.updateSuccessURL" name="updateSuccessURL" paramvalue="atoutSuccessURL"/--%>
	<dsp:input type="hidden" bean="OrderFormHandler.updateSuccessURL" name="updateSuccessURL" value="${contextPath}/html/call_center/order/show/order.jsp?id=${atoutSuccessURL}"/>
	<dsp:input type="hidden" bean="OrderFormHandler.updateErrorURL" name="updateErrorURL" value="order.jsp"/>
	<dsp:input type="hidden" bean="OrderFormHandler.repositoryId" beanvalue="CastoOrderEditor.orderId"/>
	<dsp:input type="hidden" bean="OrderFormHandler.value.paymentGroups[0].libelleBanque" value=""/>
	<dsp:input type="hidden" bean="OrderFormHandler.value.paymentGroups[0].numcheque" value=""/>
	<%--AP dsp:input type="hidden" bean="OrderFormHandler.value.paymentGroups[0].montantChequeFrancs" value="0"/--%>
	<dsp:input type="hidden" bean="OrderFormHandler.value.paymentGroups[0].montantChequeEuros" value="0"/>
	<dsp:input type="hidden" bean="OrderFormHandler.value.paymentGroups[0].paymentMethod" value="Atout"/>
	<dsp:input type="hidden" bean="OrderFormHandler.validAtout" value="true"/>
	<table align=center border=0 borderColor="#FFFFFF" cellpadding="0" cellspacing="0" width="400">
	<tr bgcolor="#FFDF63">
		<td colspan="6"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Num&eacute;ro de carte l'Atout</td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
		<dsp:input type="text" bean="OrderFormHandler.codeBin" size="6" disabled="true" readonly="true" beanvalue="OrderFormHandler.codeBin"/>
		<dsp:input type="text" size="13" maxlength="13" name="numAtout" bean="OrderFormHandler.value.numCarteAtout"/></td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Date de validit&eacute; de la carte l'Atout (jj/mm/aaaa)</td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
			<dsp:input type="text" bean="OrderFormHandler.value.dateValidAtout" converter="date" date="dd/MM/yyyy" name="dateValid"/>
		</td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Option de paiement</td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
			<dsp:select bean="OrderFormHandler.value.optionPaiementAtout_avtCodeReview">
				<dsp:option value="1">Paiement en une fois</dsp:option>
				<dsp:option value="3">Paiement en petites mensualites</dsp:option>
			
				<dsp:droplet name="/castorama/atout/CastoCarteAtout" var="cca">
					<dsp:param name="elementName" value="moyenDePaiement" />
					<dsp:oparam name="output">
						<c:set var="dropletName" value="/castorama/atout/${cca['moyenDePaiementName']}" />
						<dsp:droplet name="${dropletName}">
							<dsp:param name="montant" param="order.priceInfo.total"/>
							<dsp:param name="date" param="order.submittedDate"/>
							<dsp:oparam name="output">
								<dsp:option paramvalue="moyenDePaiement.code">
									<dsp:valueof param="moyenDePaiement.libelle"/>
								</dsp:option>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:select>
		</td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		
		<input type="hidden" name="profId" value="<dsp:valueof param="order.profileId"/>"/>
		
					<%-- GY : stabilisation : journalisation--%>
						<input name="userId" type="hidden" value="<dsp:valueof bean="Profile.repositoryId"/>"/>
						<input name="userLogin" type="hidden" value="<dsp:valueof bean="Profile.login"/>"/>
						<input name="numeroCommande" type="hidden" value="<dsp:valueof bean="CastoOrderEditor.orderId"/>"/>
					<%-- / GY : stabilisation --%>
		
		<td colspan="4" align="center"><dsp:input type="image" src="${contextPath}/html/img/valider.gif" bean="OrderFormHandler.updateAtout"/></td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr bgcolor="#FFDF63">
		<td colspan="6"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	</table>
	</dsp:form>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" bean="OrderFormHandler.formError"/>
	<dsp:oparam name="false">
<script language="javascript">
window.document.formAtout.numAtout.value="<dsp:valueof bean='OrderFormHandler.numeroCarteAtoutWithoutCodeBin'></dsp:valueof>";
</script>
	</dsp:oparam>
</dsp:droplet>
</p><br>

</dsp:oparam>
<dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>

</dsp:page>
