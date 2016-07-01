<dsp:page xml="true">

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/OrderFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:droplet name="ForEach">
  <dsp:param name="array" bean="OrderFormHandler.formExceptions"/>
  <dsp:param name="elementName" value="exception"/>
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
			<dsp:oparam name="numberFormatError">
			</dsp:oparam>
			<dsp:oparam name="numchequeSyntaxError">
				<span class="texterou"><img src="<%=request.getContextPath()%>/html/img/puce.gif" border=0>&nbsp;Le num&eacute;ro du ch&egrave;que<br></span>
			</dsp:oparam>
			<dsp:oparam name="libelleBanqueSyntaxError">
				<span class="texterou"><img src="<%=request.getContextPath()%>/html/img/puce.gif" border=0>&nbsp;La banque<br></span>
			</dsp:oparam>
			<dsp:oparam name="montantChequeFrancsError">
				<span class="texterou"><img src="<%=request.getContextPath()%>/html/img/puce.gif" border=0>&nbsp;Le montant du ch&egrave;que<br></span>
			</dsp:oparam>
			<dsp:oparam name="montantChequeEurosError">
				<span class="texterou"><img src="<%=request.getContextPath()%>/html/img/puce.gif" border=0>&nbsp;Le montant du ch&egrave;que<br></span>
			</dsp:oparam>
			<dsp:oparam name="montantChequeError">
				<span class="texterou"><img src="<%=request.getContextPath()%>/html/img/puce.gif" border=0>&nbsp;Le montant du ch&egrave;que<br></span>
			</dsp:oparam>
			<dsp:oparam name="default">
				<!--<SPAN CLASS="texterou"><valueof param="exception.errorCode"/><br></span>-->
			</dsp:oparam>
		</dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>