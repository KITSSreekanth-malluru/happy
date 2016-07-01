
<dsp:page xml="true">

<dsp:importbean bean="/castorama/order/LogsSIPSFormHandler"/>
<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<A NAME="saisieLogsSIPS"/>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
<dsp:param name="value" bean="LogsSIPSFormHandler.create"/>
<dsp:oparam name="true">
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" bean="LogsSIPSFormHandler.formError"/>
	<dsp:oparam name="false">
		<p align="center"><span class="moncasto">Le d&eacute;tail de la transaction a &eacute;t&eacute; enregistr&eacute;.</span></p>
	</dsp:oparam>
	</dsp:droplet>
</dsp:oparam>
</dsp:droplet>
<dsp:form action="order.jsp" method="post" name="formLogsSIPS">
<input type="hidden" name="saisieLogsSIPS" value="true">
<dsp:input type="hidden" bean="LogsSIPSFormHandler.requireIdOnCreate" value="false"/>
<dsp:input type="hidden" bean="LogsSIPSFormHandler.value.order_id" beanvalue="CastoOrderEditor.order.id"/>
<dsp:input type="hidden" bean="LogsSIPSFormHandler.value.profile_id" beanvalue="CastoOrderEditor.order.profileId"/>
<dsp:input type="hidden" bean="LogsSIPSFormHandler.value.devise" value="250"/>
<dsp:input type="hidden" bean="LogsSIPSFormHandler.create" value="true" priority="-10"/>
<table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
<tr bgcolor="#FFDE63">
	<td bgcolor="#FFDE63" width="1"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
    <td width="298"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
    <td width="1"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
</tr>
<tr>
    <td bgcolor="#FFDE63" width="1"><img src="/img/1pixel.gif" width="1" height="1"></td>
    <td width="298" align="center"> <br>
      <table width="280" border="0" cellspacing="0" cellpadding="0" align="CENTER">
	  	<tr align="CENTER">
			<td class="moncasto" colspan="2">Vous avez les autorisations n&eacute;cessaires pour saisir les d&eacute;tails du paiement.<br></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" colspan="2"><dsp:include page="./frag_erreurSaisieLogsSIPS.jsp"/><br></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">RESPONSE_CODE.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.response_code"/></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">CURRENT_AMOUNT.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.montant"/></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">TRANSACTION_ID.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.transaction_id"/></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">AUTHORISATION.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.authorisation_id"/></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">CARD_TYPE.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.payment_means"/></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">HASH_CARD_NUMBER.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.card_number"/></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">CERTIFICATE.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.payment_certificate"/></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">PAYMENT_TIME.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.payment_time"/></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">PAYMENT_DATE.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.payment_date"/></td>
		</tr>
		<tr align="CENTER">
			<td class="moncasto" width="50%">CAPTURE_DATE.</td>
			<td class="moncasto" width="50%"><dsp:input type="text" bean="LogsSIPSFormHandler.value.date_trans_expire"/></td>
		</tr>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
<dsp:param name="value" bean="LogsSIPSFormHandler.formError"/>
<dsp:oparam name="false">
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" bean="LogsSIPSFormHandler.create"/>
	<dsp:oparam name="false">
		<tr>
			<td colspan="2" align="center"><br><input type="image" src="../../../img/valider.gif" border=0></a></td>
		</tr>
	</dsp:oparam>
	</dsp:droplet>
</dsp:oparam>
<dsp:oparam name="true">
		<tr>
			<td colspan="2" align="center"><br><input type="image" src="../../../img/valider.gif" border=0></a></td>
		</tr>
</dsp:oparam>
</dsp:droplet>
		

      </table>
      <br>
    </td>
    <td bgcolor="#FFDE63" width="1"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
</tr>
<tr bgcolor="#FFDE63"> 
    <td bgcolor="#FFDE63" width="1"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
    <td width="298"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
    <td width="1"><img src="../../../img/1pixel.gif" width="1" height="1"></td>
</tr>
</table>
</dsp:form>


</dsp:page>