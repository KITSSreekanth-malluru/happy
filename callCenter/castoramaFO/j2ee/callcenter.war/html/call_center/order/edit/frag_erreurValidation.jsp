<dsp:page xml="true">

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>

<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:droplet name="ForEach">
  <dsp:param name="array" bean="CastoOrderEditor.formExceptions"/>
  <dsp:param name="elementName" value="exception"/>
	<dsp:oparam name="outputStart">
		<table border=0 cellpadding="10"><tr><td align="left">
			<span class="texterou">D&eacute;sol&eacute;, des anomalies sont survenues.</span><br></td></tr>
			<tr><td align="left">
	</dsp:oparam>
	<dsp:oparam name="outputEnd">
		</td></tr></table>
	</dsp:oparam>
	<dsp:oparam name="output">
		<dsp:droplet name="Switch">
 			<dsp:param name="value" param="exception.errorCode"/>
			<dsp:oparam name="numberFormatError">
			</dsp:oparam>
			<dsp:oparam name="default">
				<span class="texterou"><dsp:valueof param="exception.errorCode"/><br></span>
			</dsp:oparam>
		</dsp:droplet>
  </dsp:oparam>
</dsp:droplet>
<dsp:droplet name="ForEach">
  <dsp:param name="array" bean="/castorama/CastoShoppingCartModifier.formExceptions"/>
  <dsp:param name="elementName" value="exception"/>
	<dsp:oparam name="outputStart">
		<table border=0 cellpadding="10">
			<tr><td align="left">
	</dsp:oparam>
	<dsp:oparam name="outputEnd">
		</td></tr></table>
	</dsp:oparam>
	<dsp:oparam name="output">
		<span class="texterou"><dsp:valueof param="exception.errorCode"/><br></span>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>