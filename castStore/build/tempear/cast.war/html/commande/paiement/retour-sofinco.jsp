<dsp:page>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  <dsp:getvalueof var="host" bean="/com/castorama/util/ServerSetting.secureHost" />

  <dsp:getvalueof var="pbx_codefamille" param="op"/>
  
  <dsp:getvalueof var="orderId" param="rc"/>
  <c:if test="${not empty orderId}">
    <c:if test="${fn:endsWith(orderId, '_')}">
	<script type="text/javascript">
	<!--
		window.location="${host}/adminFO/html/call_center/order/payment/retour-sofinco.jsp?lb=${pbx_codefamille}";
	//-->
	</script>
  </c:if>
  </c:if>
	
	<dsp:getvalueof var="mail" bean="/atg/userprofiling/Profile.eMail" />
	<dsp:getvalueof var="order" bean="/atg/commerce/ShoppingCart.currentlySelected" />

<body onload="document.pbx_sofinco.submit();">

<form name="pbx_sofinco" id="pbx_sofinco" action="${originatingRequest.contextPath}/checkout/full/includes/paymentPaybox.jsp" method="post" target="_parent">
	<input type="hidden" name="PBX_MODE" id="PBX_MODE" value="13" />
	<fmt:formatNumber var="total" value="${order.priceInfo.total*100}" maxFractionDigits="0" groupingUsed="false" />
	<input type="hidden" name="PBX_TOTAL" id="PBX_TOTAL" value="${total}" />
	<input type="hidden" name="PBX_CMD" id="PBX_CMD" value="${order.id}" />
	<input type="hidden" name="PBX_TYPECARTE" id="PBX_TYPECARTE" value="SOFINCO" />
	<input type="hidden" name="PBX_TYPEPAIEMENT" id="PBX_TYPEPAIEMENT" value="CARTE" />
	<input type="hidden" name="PBX_PORTEUR" id="PBX_PORTEUR" value="${mail}" />
	<input type="hidden" name="PBX_CODEFAMILLE" id="PBX_CODEFAMILLE" value="${pbx_codefamille}" />
	
</form>

</body>
</dsp:page>