<dsp:page>
	<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

	<h3><fmt:message key="payment.bank.title" /></h3>
	<p>
		<fmt:message key="payment.bank.line1" /> 
	</p>
	
	<dsp:getvalueof var="order" param="order" />
	<dsp:getvalueof var="total" param="total" />
	<dsp:getvalueof var="mail" param="mail" />
	
	<form name="pbx" id="pbx" action="${originatingRequest.contextPath}/checkout/full/includes/paymentPaybox.jsp" method="post">
		<input type="hidden" name="PBX_MODE" id="PBX_MODE" value="13" />
		<input type="hidden" name="PBX_TOTAL" id="PBX_TOTAL" value="${total}" />
		<input type="hidden" name="PBX_CMD" id="PBX_CMD" value="${order.id}" />
		<input type="hidden" name="PBX_TYPECARTE" id="PBX_TYPECARTE" value="VISA" />
		<input type="hidden" name="PBX_PORTEUR" id="PBX_PORTEUR" value="${mail}" />
		<input type="hidden" name="PBX_TYPEPAIEMENT" id="PBX_TYPEPAIEMENT" value="CARTE" />
		
		<ul class="chooseCC">
			<li><a title="Carte bleue" onclick="javascript:paimentSend('pbx', 'CB');"><img style="cursor:pointer;" alt="Carte bleue" src="${originatingRequest.contextPath}/images/cclogos/card-bleue.gif"/></a></li>
			<li><a title="Carte Visa" onclick="javascript:paimentSend('pbx', 'VISA');"><img style="cursor:pointer;" alt="Carte Visa" src="${originatingRequest.contextPath}/images/cclogos/card-visa.gif"/></a></li>
			<li><a title="Carte Eurocard Mastercard" onclick="javascript:paimentSend('pbx', 'EUROCARD_MASTERCARD');"><img style="cursor:pointer;" alt="Carte Eurocard Mastercard" src="${originatingRequest.contextPath}/images/cclogos/card-mastercard.gif"/></a></li>
			<li><a title="Carte American Express" onclick="javascript:paimentSend('pbx', 'AMEX');"><img style="cursor:pointer;" alt="Carte American Express" src="${originatingRequest.contextPath}/images/cclogos/card-american-express.gif"/></a></li>
			<%--<li><a title="Service Receive &amp; pay" href="javascript:paimentSend('pbx', '?');"><img alt="Service Receive &amp; pay" src="${originatingRequest.contextPath}/images/cclogos/card-receive-and-pay.gif"/></a></li>--%>
		</ul>
	</form>
	
    <p><fmt:message key="payment.bank.line2" /><br><fmt:message key="payment.bank.line3" /></p>
      <div class="carteBancaire">
        <img src="/images/secure_code2.png" alt="secure_code"> 
        <span><fmt:message key="payment.bank.line5" /> (<a onclick="showPopup('secure3D')" class="secure" href="#"><fmt:message key="payment.bank.line6" /></a>)</span>
      </div>
    <p><fmt:message key="payment.bank.line4" /></p>
</dsp:page>
