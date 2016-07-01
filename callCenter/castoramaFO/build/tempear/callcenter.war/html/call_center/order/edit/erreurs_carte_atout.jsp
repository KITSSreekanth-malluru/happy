<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/OrderFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>


<dsp:droplet name="IsEmpty">
	<dsp:param name="value" param="OrderFormHandler.formExceptions"/>
	<dsp:oparam name="false">
		<dsp:droplet name="ForEach">
		  <param name="array" bean="OrderFormHandler.formExceptions"/>
		  <param name="elementName" value="exception"/>
			<dsp:oparam name="outputStart">
				<table border=0 cellpadding="10"><tr><td align="left">
					<span class="texterou">D&eacute;sol&eacute;, des anomalies sont survenues.<br>Merci de v&eacute;rifier si les champs ci-dessous sont correctement renseign&eacute;s.</span><br></td></tr>
					<tr><td align="left">
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
				</td></tr></table>
			</dsp:oparam>
			<dsp:oparam name="output">
				<dsp:droplet name="Switch">
		 			<dsp:param name="value" param="exception.errorCode"/>
					<dsp:oparam name="numCarteAtoutError">
						<span class="texterou"><img src="/img/puce.gif" border=0>&nbsp;Le num&eacute;ro de votre carte l'Atout<br></span>
					</dsp:oparam>
					<dsp:oparam name="numCarteAtoutNbChiffresError">
						<span class="texterou"><img src="/img/puce.gif" border=0>&nbsp;Le num&eacute;ro de votre carte l'Atout<br></span>
					</dsp:oparam>
					<dsp:oparam name="numCarteAtoutNonNumeric">
						<span class="texterou"><img src="/img/puce.gif" border=0>&nbsp;Le num&eacute;ro de votre carte l'Atout<br></span>
					</dsp:oparam>
					<dsp:oparam name="numCarteAtoutCoemetteursError">
						<span class="texterou"><img src="/img/puce.gif" border=0>&nbsp;Le num&eacute;ro de votre carte l'Atout<br></span>
					</dsp:oparam>
					<dsp:oparam name="dateValidAtoutCoemetteursError">
						<span class="texterou"><img src="/img/puce.gif" border=0>&nbsp;Le date de validit&eacute; de votre carte l'Atout<br></span>
					</dsp:oparam>
					<dsp:oparam name="default">
						<%--<span class="texterou"><dsp:valueof param="exception.errorCode"/><br></span>--%>
					</dsp:oparam>
				</dsp:droplet>
		  </dsp:oparam>
		</dsp:droplet>
</dsp:oparam>
</dsp:droplet>

</dsp:page>