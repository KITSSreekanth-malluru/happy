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
			   	<dsp:param name="requis" value="cheques-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">



<table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
<tr>
	<td width="1" bgcolor="#003399"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
</tr>
</table>
<p align=center><span class="marques">R&eacute;ception du ch&egrave;que</span><br><br>

<%--AP dsp:setvalue param="chequeSuccessURL" beanvalue="CastoOrderEditor.orderId"/ --%>
<dsp:getvalueof var="urlchequeSuccess" bean="CastoOrderEditor.orderId" />
<%//((DynamoHttpServletRequest)request).setParameter("chequeSuccessURL", "../show/order.jsp?id=" + (String)((DynamoHttpServletRequest)request).getParameter("chequeSuccessURL") );%>
	<dsp:include page="./erreurs.jsp"/>
	
	<dsp:form action="./order.jsp" method="post" name="formCheque">
	<dsp:input type="hidden" bean="OrderFormHandler.requireIdOnCreate" value="true"/>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
			<dsp:param name="value" bean="OrderFormHandler.formError"/>
		<dsp:oparam name="false">
			<dsp:setvalue bean="OrderFormHandler.repositoryId" beanvalue="CastoOrderEditor.orderId"/>
			<dsp:input type="hidden" bean="OrderFormHandler.extractDefaultValuesFromItem" value="true"/>
		</dsp:oparam>
	</dsp:droplet>
	<%--APdsp:input type="hidden" bean="OrderFormHandler.updateSuccessURL" name="updateSuccessURL" paramvalue="chequeSuccessURL"/--%>
	<dsp:input type="hidden" bean="OrderFormHandler.updateSuccessURL" name="updateSuccessURL" value="${contextPath}/html/call_center/order/show/order.jsp?id=${urlchequeSuccess}"/>
	<dsp:input type="hidden" bean="OrderFormHandler.updateErrorURL" name="updateErrorURL" value="order.jsp"/>
	<dsp:input type="hidden" bean="OrderFormHandler.repositoryId" beanvalue="CastoOrderEditor.orderId"/>
	<dsp:input type="hidden" bean="OrderFormHandler.value.paymentGroups[0].paymentMethod" value="Cheque"/>
	<dsp:input type="hidden" bean="OrderFormHandler.value.numCarteAtout_avtCodeReview" value=""/>
	<dsp:input type="hidden" bean="OrderFormHandler.value.paymentGroups[0].dateValidAtout" value=""/>
	<table align=center border=0 borderColor="#FFFFFF" cellpadding="0" cellspacing="0" width="400">
	<tr bgcolor="#FFDF63">
		<td colspan="6"><img src="<%=request.getContextPath()%>/html<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Banque</td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
		<dsp:input type="text" bean="OrderFormHandler.value.paymentGroups[0].libelleBanque" maxlength="30" iclass="moncasto"/></td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Num&eacute;ro du ch&egrave;que</td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
			<dsp:input type="text" bean="OrderFormHandler.value.paymentGroups[0].numcheque" maxlength="50" iclass="moncasto"/></td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<%--AP tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Montant du ch&egrave;que</td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
		<dsp:input type="text" name="francs" bean="OrderFormHandler.value.paymentGroups[0].montantChequeFrancs" maxlength="15" iclass="moncasto" onchange="javascript:updateEuros();"/> Francs</td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr> AP--%>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Montant du ch&egrave;que</td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
		<dsp:input type="text" name="euros" bean="OrderFormHandler.value.paymentGroups[0].montantChequeEuros" maxlength="15" iclass="moncasto" onchange="javascript:updateFrancs();"/> Euros</td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Le ch&egrave;que n'est pas valide</td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
		<dsp:input type="checkbox" bean="OrderFormHandler.value.paymentGroups[0].chequeNonValide"/></td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Commentaires sur le ch&egrave;que</td>
		<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
			<dsp:textarea bean="OrderFormHandler.value.paymentGroups[0].commentaireCheque" cols="30" rows="5" iclass="moncasto"></dsp:textarea></td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td colspan="4" align="center">
		
			<%-- GY : stabilisation : journalisation--%>
				<input name="userId" type="hidden" value="<dsp:valueof bean="Profile.repositoryId"/>"/>
				<input name="userLogin" type="hidden" value="<dsp:valueof bean="Profile.login"/>"/>
				<input name="numeroCommande" type="hidden" value="<dsp:valueof bean="CastoOrderEditor.orderId"/>"/>
			<%-- / GY : stabilisation --%>
		
			<dsp:input type="image" src="../../../img/valider.gif" bean="OrderFormHandler.updateCheque"/></td>
		<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr bgcolor="#FFDF63">
		<td colspan="6"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	</table>
	</dsp:form>
</p><br>

</dsp:oparam>
<dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>


</dsp:page>
