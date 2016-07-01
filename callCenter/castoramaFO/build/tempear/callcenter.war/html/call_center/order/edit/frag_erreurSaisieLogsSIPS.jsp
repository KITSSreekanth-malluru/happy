<dsp:page xml="true">

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/order/LogsSIPSFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:droplet name="ForEach">
  <dsp:param name="array" bean="LogsSIPSFormHandler.formExceptions"/>
  <dsp:param name="elementName" value="exception"/>
	<dsp:oparam name="outputStart">
		<table border=0 cellpadding="10"><tr><td align="left">
			<span class="texterou">D&eacute;sol&eacute;, des anomalies sont survenues concernant les &eacute;l&eacute;ments suivants.</span><br></td></tr>
			<tr><td align="left" class="texterou">
	</dsp:oparam>
	<dsp:oparam name="outputEnd">
		</td></tr></table>
	</dsp:oparam>
	<dsp:oparam name="output">
		<dsp:droplet name="Switch">
 			<dsp:param name="value" param="exception.errorCode"/>
 			<dsp:oparam name="existError">
				<li> Le d&eacute;tail du paiement existe d&eacute;ja.
			</dsp:oparam>
			<dsp:oparam name="orderIdError">
				<li> L'identifiant de la commande
			</dsp:oparam>
			<dsp:oparam name="responseCodeError">
				<li> RESPONSE_CODE
			</dsp:oparam>
			<dsp:oparam name="montantError">
				<li> CURRENT_AMOUNT
			</dsp:oparam>
			<dsp:oparam name="transactionIdError">
				<li> TRANSACTION_ID
			</dsp:oparam>
			<dsp:oparam name="authorisationIdError">
				<li> AUTHORISATION
			</dsp:oparam>
			<dsp:oparam name="paymentMeansError">
				<li> CARD_TYPE
			</dsp:oparam>
			<dsp:oparam name="cardNumberError">
				<li> HASH_CARD_NUMBER
			</dsp:oparam>
			<dsp:oparam name="paymentCertificateError">
				<li> CERTIFICATE
			</dsp:oparam>
			<dsp:oparam name="paymentTimeError">
				<li> PAYMENT_TIME
			</dsp:oparam>
			<dsp:oparam name="paymentDateError">
				<li> PAYMENT_DATE
			</dsp:oparam>
			<dsp:oparam name="deviseError">
				<li> La devise
			</dsp:oparam>
			<dsp:oparam name="profileError">
				<li> L'identifiant du client
			</dsp:oparam>
			<dsp:oparam name="dateTransExpireError">
				<li> CAPTURE_DATE
			</dsp:oparam>
			<dsp:oparam name="default">
				<span class="texterou"><dsp:valueof param="exception.message"/><br></span>
			</dsp:oparam>
		</dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>