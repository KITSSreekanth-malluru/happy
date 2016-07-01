<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/castorama/order/LogsSIPSFormHandler"/>


	<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../../common/protocolChange.jsp"> <dsp:param name="protocol" value="secure" /> </dsp:include>


<dsp:setvalue bean="LogsSIPSFormHandler.orderId" beanvalue="CastoOrderEditor.order.id"/>
<html>
<head>
<title>Castorama : magasins bricolage, jardinage, d&eacute;coration.</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta http-equiv="PRAGMA" content="NO-CACHE"/>
<meta http-equiv="EXPIRES" content="-1"/>
<link rel="stylesheet" href="../../../css/hp.css">
</head>
<body bgcolor="#FFFFFF" leftmargin="0">

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
<dsp:droplet name="/atg/dynamo/droplet/IsNull">
<dsp:param name="value" bean="CastoOrderEditor.order.priceInfo"/>
<dsp:oparam name="true">
	  	<tr align="CENTER">
			<td class="texterou" colspan="2">Le total de la commande n'a pas &eacute;t&eacute; calcul&eacute;.<br>
			<a href="javascript:window.opener.document.formPricing.commentaire.value=window.opener.document.formValidationCommande.commentaire.value;window.opener.document.formPricing.submit();window.close();">Cliquez ici</a> pour effectuer le total.<br></td>
	   </tr>
</dsp:oparam>
<dsp:oparam name="false">
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" bean="/castorama/CastoOrderEditor.moyenDePaiement"/>
	<dsp:oparam name="creditCard">
		<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" bean="LogsSIPSFormHandler.exists"/>
		<dsp:oparam name="false">
			<tr align="CENTER">
				<td class="texterou" colspan="2">Aucune trace de paiement par carte bancaire n'a &eacute;t&eacute; trouv&eacute;e.<br>
				Le client a peut &eacute;tre utilis&eacute; un autre moyen de paiement, autrement vous pouvez saisir les d&eacute;tails du paiement par carte bancaire en 
				<a href="javascript:window.opener.document.location='order.jsp?saisieLogsSIPS=true#saisieLogsSIPS';window.close();">cliquant ici</a>.
				<br></td>
			</tr>
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:include page="./frag_confirmValider.jsp"/>
		</dsp:oparam>
		</dsp:droplet>
	</dsp:oparam>
	<dsp:oparam name="default">
		<dsp:include page="./frag_confirmValider.jsp"/>
	</dsp:oparam>
	</dsp:droplet>
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
</body>
</html>


</dsp:page>