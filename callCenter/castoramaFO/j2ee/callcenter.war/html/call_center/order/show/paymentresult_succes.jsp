<%@page import="atg.servlet.DynamoHttpServletRequest"%>
<%@page import="atg.servlet.DynamoHttpServletResponse"%>
<dsp:page xml="true">


<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/BeanSessionOrder"/>
<dsp:importbean bean="/castorama/OrderFormHandler"/>
<dsp:importbean bean="/castorama/droplet/commande/paiement/CastoMercanetInfos"/>
<dsp:importbean bean="/castorama/droplet/commande/paiement/CastoMercanetReponseManuelle"/>
	<dsp:importbean bean="/castorama/droplet/commande/paiement/CastoMercanetInfos" />
<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../../common/protocolChange.jsp"> <dsp:param name="protocol" value="secure" /> </dsp:include>

<html> <head>
<link rel="stylesheet" href="../../../css/hp.css">
<title></title>
</head>

<body bgcolor="#FFFFFF">

<dsp:include page="../../common/header.jsp"/>

<dsp:droplet name="CastoMercanetInfos" >
	<dsp:param name="data" param="DATA" />
	<dsp:param name="keys" value="order_id" />
	<dsp:oparam name="output">	
	<c:set var="orderId" value="${param['order_id']}"/>	

<dsp:droplet name="CastoMercanetReponseManuelle">
	<dsp:param name="DATA" param="DATA" />
</dsp:droplet>




<dsp:droplet name="/atg/dynamo/droplet/SQLQueryForEach">
	<dsp:param name="dataSource" bean="/atg/dynamo/service/jdbc/JTDataSource"/>
	  <dsp:param name="querySQL" value="select count(1) from CASTO_ORDER_TRANSACTION where ORDER_ID='${param['order_id']}'"/>
		<dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>	
		<dsp:param name="elementName" value="rs"/>
		<dsp:oparam name="output">
			<c:set var="rs"  value="${param['rs.column[0]']}"/>
		<dsp:droplet name="/atg/dynamo/droplet/Switch">
		  <dsp:param name="value" value="${rs}"/>
		  <dsp:oparam name="1">		  
		  		<center><font face="Tahoma, Arial, Helvetica, sans-serif" size="3" color="#003399">La transaction s'est d&eacute;roul&eacute;e correctement.<br></font></center>
				<br><br>
		  		<dsp:droplet name="/atg/dynamo/droplet/SQLQueryForEach">
				<dsp:param name="dataSource" bean="/atg/dynamo/service/jdbc/JTDataSource"/>
		  		<dsp:param name="querySQL" value="select transaction_id,authorisation_id,payment_means,card_number,payment_certificate,payment_time,payment_date,montant from CASTO_SIPS_LOG where order_id='${param['order_id']}' and response_code=00"/>
				<dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>	
				<dsp:param name="elementName" value="sips"/>
				<dsp:oparam name="output">  
				  <center>
				  <table width="600" border="0" cellspacing="0" cellpadding="0">
		              <tr align="center"> 
		                <td class="texteb" width="1" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
		                <td class="texteb" width="198" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
		                <td class="texteb" width="1" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
						<td width="400"></td>
		              </tr>
		              <tr align="center"> 
		                <td class="texteb" bgcolor="#FFDE63" width="1" bgcolor="#E7E6E4"><img src="/img/1pixel.gif" width="1" height="1"></td>
		                <td class="texteb" width="198" bgcolor="#E7E6E4">votre transaction</td>
		                <td class="texteb" bgcolor="#FFDE63" width="1" ><img src="/img/1pixel.gif" width="1" height="1"></td>
						<td width="400"></td>
		              </tr>
		            </table> 
			
					<table width="600" border="0" cellspacing="0" cellpadding="0">
						<tr> 
						  <td class="texte" width="1" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td class="texte" bgcolor="#FFDE63" width="175"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td class="prix" bgcolor="#FFDE63" width="124"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td class="texte" bgcolor="#FFDE63" width="175"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td class="texte" bgcolor="#FFDE63" width="124"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td class="texte" bgcolor="#FFDE63" width="1"><img src="/img/1pixel.gif" width="1" height="1"></td>
						</tr>
						<tr> 
						  <td height="30" class="texte" bgcolor="#FFDE63" width="1"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td height="30" class="texte" width="175">&nbsp;Votre r&eacute;f&eacute;rence de transaction :</td>
						  <td height="30" class="prix" align="left" width="124"><dsp:valueof param="sips.column[0]"/></td>
						  <%
							String l_strDate = request.getParameter("sips.column[6]");
							String l_strDateFinale = l_strDate.substring(6,8) + "/" + l_strDate.substring(4,6) + "/" + l_strDate.substring(0,4);
						  	((DynamoHttpServletRequest)request).setParameter("datetransaction",l_strDateFinale);
						  %>
						  <td height="30" class="texte" width="175">Date de votre transaction :</td>
						  <td height="30" class="prix" width="124"><dsp:valueof param="datetransaction"/></td>
						  <td height="30" class="texte" bgcolor="#FFDE63" width="1"><img src="/img/1pixel.gif" width="1" height="1"></td>
						</tr>
						<tr> 
						  <td width="1" class="texte" bgcolor="#FFDE63" height="30"><img src="/img/1pixel.gif" width="1" height="1"></td>
				  		  <td width="175" class="texte" height="30">&nbsp;Certificat de la transaction :</td>
						  <td width="124" class="prix" align="left" height="30"><dsp:valueof param="sips.column[4]"/></td>
						  <td width="175" class="texte" height="30">Autorisation :</td>
						  <td width="124" class="prix" height="30"><dsp:valueof param="sips.column[1]"/></td>
						  <td width="1" class="texte" bgcolor="#FFDE63" height="30"><img src="/img/1pixel.gif" width="1" height="1"></td>
						</tr>
						<tr> 
						 <%
							String l_strCarte = request.getParameter("sips.column[3]");
							String l_strNumcarte = l_strCarte.substring(0,4)+"XXXXXXXXXX"+l_strCarte.substring(5,6);
						  	((DynamoHttpServletRequest)request).setParameter("numcarte",l_strNumcarte);
						 %>
						  <td width="1" class="texte" bgcolor="#FFDE63" height="30"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td width="175" class="texte" height="30">&nbsp;Num&eacute;ro de la carte :</td>
						  <td width="124" class="prix" height="30"><dsp:valueof param="numcarte"/></td>
				  		  <td width="175" class="texte" height="30">Type de carte :</td>
						  <td width="124" class="prix" align="left" height="30"><dsp:valueof param="sips.column[2]"/></td>
						  <td width="1" class="texte" bgcolor="#FFDE63" height="30"><img src="/img/1pixel.gif" width="1" height="1"></td>
						</tr>
						<tr> 
						 <%
							String l_strMtt = request.getParameter("sips.column[7]");
							String l_strMttDecimal = l_strMtt.substring(0,(l_strMtt.length()-2))+","+l_strMtt.substring((l_strMtt.length()-2),(l_strMtt.length()))+" F";
							((DynamoHttpServletRequest)request).setParameter("montant",l_strMttDecimal);
						 %>
						  <td width="1" class="texte" bgcolor="#FFDE63" height="30"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td width="175" class="texte" height="30">&nbsp;Montant :</td>
						  <td width="124" class="prix" height="30"><dsp:valueof param="montant"/></td>
				  		  <td width="175" class="texte" height="30">&nbsp;Num&eacute;ro de commande</td>
						  <td width="124" class="prix" align="left" height="30"><c:out value="${param['order_id']}"/></td>
						  <td width="1" class="texte" bgcolor="#FFDE63" height="30"><img src="/img/1pixel.gif" width="1" height="1"></td>
						</tr>
						<tr> 
						  <td width="1" class="texte" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td width="175" class="texte" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td width="124" class="prix" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td width="175" class="texte" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td width="124" class="texte" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
						  <td width="1" class="texte" bgcolor="#FFDE63"><img src="/img/1pixel.gif" width="1" height="1"></td>
						</tr>
					  </table>
				  	  </dsp:oparam>
				</dsp:droplet>
			</center>
		  </dsp:oparam>
	  	  <dsp:oparam name="0">
		  	<center>
		    <font face="Tahoma, Arial, Helvetica, sans-serif" size="3" color="#003399">Votre transaction n'a pas &eacute;t&eacute; accept&eacute;e par votre<br>
			&eacute;tablissement financier<br><br></font>
			<a href="/call_center/login_success.jsp">Retour au menu g&eacute;n&eacute;ral</a>
			</center>
		  </dsp:oparam>
		</dsp:droplet>
	  </dsp:oparam>
	</dsp:droplet>
	

<dsp:setvalue bean="BeanSessionOrder.orderId" value=""/>
<%/******* Mise a jour de la commande *******/
//******  Log de paiement par carte banquaire  *****
com.castorama.BeanCreateLogAdmin.createLog(((DynamoHttpServletRequest)request),((DynamoHttpServletResponse)response),"CallCenter","Modification","Paiement par carte banquaire pour la commande "+request.getParameter("orderId"));
%>
<dsp:setvalue bean="OrderFormHandler.updateOrderAfterSIPSCallCenter" value="${param['order_id']}"/>


</dsp:oparam>
</dsp:droplet>	
</body> </html>

</dsp:page>