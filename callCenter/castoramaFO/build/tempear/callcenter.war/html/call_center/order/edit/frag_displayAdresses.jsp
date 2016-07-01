<dsp:page xml="true">


<table align=CENTER border=0 width=300 cellspacing =0 cellpadding=0>
<tr>
	<td class="texte" align="LEFT" colspan="2">
	<b>Adresse de Facturation :</b>
	</td>
</tr>
<tr>
	<td width="20">&nbsp;</td>
	<td class="texte" align="LEFT" colspan="">
	<dsp:droplet name="/atg/dynamo/droplet/ForEach"> 
	<dsp:param name="array" param="order.PaymentGroups"/> 
	<dsp:param name="elementName" value="paymentGroup"/> 
	<dsp:param name="indexName" value="paymentGroupIndex"/> 
	<dsp:oparam name="output">
		<dsp:valueof param="paymentGroup.billingAddress.firstName"/>&nbsp;
		<dsp:valueof param="paymentGroup.billingAddress.lastName"/><br>
		<dsp:valueof param="paymentGroup.billingAddress.faxNumber"/><br>
		<dsp:valueof param="paymentGroup.billingAddress.address1"/><br>
		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
			<dsp:param name="value" param="paymentGroup.billingAddress.address2"/>
			<dsp:oparam name="false">
				<dsp:valueof param="paymentGroup.billingAddress.address2"/><br>
			</dsp:oparam>
		</dsp:droplet>
		<dsp:valueof param="paymentGroup.billingAddress.postalCode"/>&nbsp;
		<dsp:valueof param="paymentGroup.billingAddress.City"/>&nbsp;
		<dsp:valueof param="paymentGroup.billingAddress.Country"/>
	</dsp:oparam>
	</dsp:droplet>	<br><br>
	</td>
</tr>
<tr>
	<td class="texte" align="LEFT" colspan="2">
	<b>Adresse de Livraison :</b>
	</td>
</tr>
<tr>
	<td width="20">&nbsp;</td>
	<td class="texte" align="LEFT" colspan="">	
	<dsp:droplet name="/atg/dynamo/droplet/ForEach"> 
	<dsp:param name="array" param="order.ShippingGroups"/> 
	<dsp:param name="elementName" value="shippingGroup"/>
	<dsp:param name="indexName" value="shippingGroupIndex"/> 
	<dsp:oparam name="output">
		<dsp:valueof param="shippingGroup.shippingAddress.firstName"/>&nbsp;
		<dsp:valueof param="shippingGroup.shippingAddress.lastName"/><br>
		<dsp:valueof param="shippingGroup.shippingAddress.faxNumber"/><br>
		<dsp:valueof param="shippingGroup.shippingAddress.address1"/><br>
		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
			<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
			<dsp:oparam name="false">
				<dsp:valueof param="shippingGroup.shippingAddress.address2"/><br>
			</dsp:oparam>
		</dsp:droplet>
		<dsp:valueof param="shippingGroup.shippingAddress.postalCode"/>&nbsp;
		<dsp:valueof param="shippingGroup.shippingAddress.City"/>&nbsp;
		<dsp:valueof param="shippingGroup.shippingAddress.Country"/>
	</dsp:oparam>
	</dsp:droplet>
	</td>
</tr>
</table>

</dsp:page>