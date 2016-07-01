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
<p align=center><span class="marques">Validation du panier</span><br><br>

<dsp:setvalue param="validationSuccessURL" beanvalue="CastoOrderEditor.orderId"/>
<dsp:getvalueof var="orderId" bean="CastoOrderEditor.orderId"/>
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
<dsp:droplet name="/atg/dynamo/droplet/IsNull">
<dsp:param name="value" param="saisieLogsSIPS"/>
<dsp:oparam name="false">
<dsp:include page="./frag_formLogsSIPS.jsp"/>
</dsp:oparam>
</dsp:droplet>
	<dsp:form action="order.jsp" method="post" name="formPricing">
	<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.commentaireDeValidation" name="commentaire"/>
	<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.moyenDePaiement" name="moyenDePaiement"/>
	<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.priceOrder" value="reprice" priority="-5"/>
	</dsp:form>
	<dsp:form action="order.jsp" method="post" name="formCommit">
	<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.moveToOrderCommitSuccessURL" value="${pageContext.request.contextPath}/html/call_center/order/show/order.jsp?id=${orderId}"/>
	<dsp:input type="hidden" bean="/castorama/CastoOrderEditor.moveToOrderCommit" value="valider" priority="-10"/>
	</dsp:form>
	<dsp:form action="order.jsp#commenter" method="post" name="formValidationCommande">
	<input type="hidden" name="popUpConfirmationValider" value="true">
	<table align=center border=0 borderColor="#FFFFFF" cellpadding="0" cellspacing="0" width="400">
	<tr bgcolor="#FFDF63">
		<td colspan="6"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Moyen de paiement </td>
		<td width="5%"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto">
			<dsp:select iclass="moncasto" bean="/castorama/CastoOrderEditor.moyenDePaiement" onchange="document.formPricing.moyenDePaiement.value=this.options[this.selectedIndex].value;">
				<dsp:option value="creditCard"> Par carte bancaire</dsp:option>
				<dsp:option value="Cheque"> Par carte ch&eacute;que</dsp:option>
				<dsp:option value="Call-Center"> Par carte t&eacute;l&eacute;phone</dsp:option>
			</dsp:select>
		</td>
		<td width="1" bgcolor="#FFDF63"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="/img/1pixel.gif" width="1" height="1"></td>
		<td width="5%"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
		<td width="40%" class="moncasto">Commentaires</td>
		<td width="5%"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
		<td width="50%" class="moncasto"><a name="commenter">
		<dsp:textarea bean="/castorama/CastoOrderEditor.commentaireDeValidation" cols="30" rows="5" iclass="moncasto" name="commentaire"></dsp:textarea></a></td>
		<td width="1" bgcolor="#FFDF63"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr height="30">
		<td width="1" bgcolor="#FFDF63"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
		<td colspan="4" align="center"><input type="submit" border="0" value="valider" /></td>
		<td width="1" bgcolor="#FFDF63"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr bgcolor="#FFDF63">
		<td colspan="6"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
	</tr>
	</table>
	</dsp:form>
</p><br>
	</dsp:oparam>
</dsp:droplet>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
<dsp:param name="value" param="popUpConfirmationValider"/>
<dsp:oparam name="true">
<script>valider();</script>
</dsp:oparam>
</dsp:droplet>

</dsp:page>
