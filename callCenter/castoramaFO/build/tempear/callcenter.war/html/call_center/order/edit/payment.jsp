<dsp:page xml="true">
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
<dsp:getvalueof var="sofincoRang" bean="/castorama/payment/PaymentConfiguration.sofincoRang" />

<script type="text/javascript">
var sofincoWind = null;
		function paimentSend(formId, cardType) {
			var paimentForm = document.getElementById(formId);
			paimentForm['PBX_TYPECARTE'].value = cardType; 
			return paimentForm.submit();
		}
		function showSofinco(link) {
			if ( "" != link ) {
				if (sofincoWind && !sofincoWind.closed) {
					sofincoWind.focus();
				} else {
					sofincoWind = centerWindow(link,650,520);
				}
			}
		}
function centerWindow(new_URL,new_width,new_height) {
	var xMax = 0;
	var yMax = 0;
	if (document.all) {
		xMax = screen.width;
		 yMax = screen.height;
	} else {
		if (document.layers) {
			xMax = window.outerWidth;
			 yMax = window.outerHeight;
		} else {
			xMax = 800;
			yMax=600;
		}
	}
	var xOffset = (xMax - new_width)/2, yOffset = (yMax - new_height)/2;
	return window.open(new_URL,'','scrollbars=yes,toolbar=0,menubar=0,resizable=0,width='+new_width+',height='+new_height+',screenX='+xOffset+',screenY='+yOffset+',top='+yOffset+',left='+xOffset+'');
}		
</script>

<dsp:getvalueof var="amount" param="amount" />
<dsp:getvalueof var="orderId" param="orderId" />

    <dsp:droplet name="/atg/targeting/RepositoryLookup">
		<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
		<dsp:param name="itemDescriptor" value="user" />
		<dsp:param name="id" param="profileId" />		
		<dsp:param name="elementName" value="profile"/>							
		<dsp:oparam name="output">
	 		<dsp:droplet name="/atg/dynamo/droplet/IsNull">
	          <dsp:param name="value" param="profile"/>
	          <dsp:oparam name="true">
					<dsp:getvalueof var="mail" value="" />
	          </dsp:oparam> 
	          <dsp:oparam name="false">	           	 
					<dsp:getvalueof var="mail" param="profile.eMail" />
	          </dsp:oparam> 
	      	</dsp:droplet>
     	</dsp:oparam>
	</dsp:droplet>

<dsp:getvalueof var="host" bean="/com/castorama/util/ServerSetting.host" />
<c:set var="shost" value="${host}${originatingRequest.contextPath}" />
<dsp:getvalueof var="sid" value="${castCollection:encode(pageContext.session.id)}" />
<fmt:formatNumber var="total" value="${amount*100}" maxFractionDigits="0" groupingUsed="false" />

<dsp:droplet name="/castorama/droplet/SofincoDroplet" >
	<dsp:param name="orderAmount" value="${amount}" />
  	<dsp:param name="orderReference" value="${orderId}_" />
  	<dsp:oparam name="output">
  		  <dsp:getvalueof var="sofincoLink" param="urlString" />
  	</dsp:oparam>
	<dsp:oparam name="empty">
  	  <c:set var="sofincoLink" value="" />
  	 </dsp:oparam>
</dsp:droplet>

<table width="600" border="0" cellspacing="0" cellpadding="0" align="center">
<tr bgcolor="#003399"> 
	<td><img src="./../../img/1pixel.gif" width="1" height="1"></td>
</tr>
<tr>
	<td align="center">
		<br><span class="marques">Paiement Call Center :</span><br><br>
		
<dsp:include page="../payment/paymentBank.jsp">
	<dsp:param name="orderId" value="${orderId}"/>
	<dsp:param name="total" value="${total}"/>
	<dsp:param name="mail" value="${mail}"/>
</dsp:include>
	</td>
</tr>
<tr>
	<td align="center">
		<a title="Carte l'Atout" href="javascript:showSofinco('${sofincoLink}');"><img alt="Carte L'atout" src="${originatingRequest.contextPath}/html/img/payment/bg-clatout.jpg"/></a>

<dsp:include page="../payment/paymentGift.jsp">
	<dsp:param name="orderId" value="${orderId}"/>
	<dsp:param name="total" value="${total}"/>
	<dsp:param name="mail" value="${mail}"/>
</dsp:include>
	</td>
</tr>
</table>
 
<form name="pbx_sofinco" id="pbx_sofinco" action="${originatingRequest.contextPath}/html/call_center/order/payment/paymentPaybox.jsp" method="post" target="_parent">
	<input type="hidden" name="PBX_MODE" id="PBX_MODE" value="13" />
	<input type="hidden" name="PBX_TOTAL" id="PBX_TOTAL" value="${total}" />
	<input type="hidden" name="PBX_CMD" id="PBX_CMD" value="${orderId}" />
	<input type="hidden" name="PBX_RANG" id="PBX_RANG" value="${sofincoRang}" />
	<input type="hidden" name="PBX_TYPECARTE" id="PBX_TYPECARTE" value="SOFINCO" />
	<input type="hidden" name="PBX_TYPEPAIEMENT" id="PBX_TYPEPAIEMENT" value="CARTE" />
	<input type="hidden" name="PBX_PORTEUR" id="PBX_PORTEUR" value="${mail}" />
	<input type="hidden" name="PBX_CODEFAMILLE" id="PBX_CODEFAMILLE" value="" />
</form>

</dsp:page>
