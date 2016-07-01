<dsp:page xml="true">

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>

<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/castorama/OrderFormHandler"/>
<dsp:importbean bean="/castorama/order/LogsSIPSFormHandler"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
			<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="validation-commandes-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">



<script language="javascript">
function centerWindow(new_URL,new_width,new_height) {
if (document.all)
var xMax = screen.width, yMax = screen.height;
else
if (document.layers)
var xMax = window.outerWidth, yMax = window.outerHeight;
else
var xMax = 800, yMax=600;
var xOffset = (xMax - new_width)/2, yOffset = (yMax - new_height)/2;
return window.open(new_URL,'','scrollbars=yes,toolbar=0,menubar=0,resizable=0,width='+new_width+',height='+new_height+',screenX='+xOffset+',screenY='+yOffset+',top='+yOffset+',left='+xOffset+'');
}
</script>


<p align=center><span class="marques">Validation de la commande</span><br><br>

<dsp:setvalue param="validationSuccessURL" beanvalue="CastoOrderEditor.orderId"/>
<dsp:getvalueof var="orderId" bean="CastoOrderEditor.orderId"/>
	<dsp:include src="./erreurs.jsp"/>
<script language="javascript">
function valider(){
if(window.document.formValidationCommande.commentaire.value.length==0){
window.document.formValidationCommande.commentaire.focus();
alert("Veuillez saisir un commentaire justifiant la validation de la commande.");
}else{
centerWindow('popConfirmValider.jsp',350,120);
}
}
</script>
	<dsp:form action="./order.jsp" method="post" name="formPricing">
		<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.commentaireDeValidation" name="commentaire"/>
		<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.priceOrder" value="reprice" priority="-5"/>
	</dsp:form>
	
	<dsp:form action="./order.jsp" method="post" name="formCommit">
		<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.validationSuccessURL" value="../show/order.jsp?id=${orderId}"/>
		
					<%-- GY : stabilisation : journalisation--%>
						<input name="userId" type="hidden" value="<dsp:valueof bean="Profile.repositoryId"/>"/>
						<input name="userLogin" type="hidden" value="<dsp:valueof bean="Profile.login"/>"/>
						<input name="numeroCommande" type="hidden" value="<dsp:valueof bean="CastoOrderEditor.orderId"/>"/>
						<input name="EtatBOCommande" type="hidden" value="<dsp:valueof param="order.repositoryItem.BOState"/>"/>
					<%-- / GY : stabilisation --%>		
		
		
		<dsp:include page="./frag_erreurValidation.jsp"/>
		<dsp:droplet name="/atg/dynamo/droplet/Switch">
			<dsp:param name="value" param="order.repositoryItem.BOState"/>
			<dsp:oparam name="unset">
				<dsp:droplet name="/atg/dynamo/droplet/Switch">
					<dsp:param name="value" param="order.repositoryItem.state"/>
					<dsp:oparam name="INCOMPLETE">
						!!! La commande ne devrait pas &ecirc;tre en &eacute;tat INCOMPLETE !!!
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
			<%--<dsp:oparam name="ERROR_COMMIT_SIPS">
				<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.moveToOrderCommit" value="valider" priority="-10"/>
			</dsp:oparam>--%>
			<dsp:oparam name="VALIDE">
				<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.exporter" value="exporter" priority="-10"/>
			</dsp:oparam>
			<dsp:oparam name="PENDING_CALL_CENTER">
				<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.valider" value="valider" priority="-10"/>
			</dsp:oparam>
			<dsp:oparam name="PENDING_VIREMENT">
				<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.valider" value="valider" priority="-10"/>
			</dsp:oparam>
			<dsp:oparam name="A_CONTROLER">
				<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.valider" value="valider" priority="-10"/>
			</dsp:oparam>
			<dsp:oparam name="PENDING_CHEQUE">
				<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.valider" value="valider" priority="-10"/>
			</dsp:oparam>
		</dsp:droplet>
	</dsp:form>
	
	<dsp:form action="./order.jsp" method="post" name="formValidationCommande">
		<input type="hidden" name="popUpConfirmationValider" value="true">
		<table align=center border=0 bordercolor="#FFFFFF" cellpadding="0" cellspacing="0" width="400">
			<tr bgcolor="#FFDF63">
				<td colspan="6"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
			</tr>
			<tr height="30">
				<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
				<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
				<td width="40%" class="moncasto">Commentaires</td>
				<td width="5%"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
				<td width="50%" class="moncasto"><a name="commenter">
					<dsp:textarea bean="/castorama/CastoOrderEditor.commentaireDeValidation" cols="30" rows="5" iclass="moncasto" name="commentaire"></dsp:textarea></a></td>
				<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
			</tr>
			<tr height="30">
				<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
				<td colspan="4" align="center"><input type="image" border="0" src="../../../img/valider.gif"/></td>
				<td width="1" bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
			</tr>
			<tr bgcolor="#FFDF63">
				<td colspan="6"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
			</tr>
		</table>
	</dsp:form>
	
</p><br>


<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" param="popUpConfirmationValider"/>
	<dsp:oparam name="true">
		<script>valider();</script>
	</dsp:oparam>
</dsp:droplet>

</dsp:oparam>
<dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>

</dsp:page>