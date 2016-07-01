<dsp:page>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

	<h3><fmt:message key="payment.gift.title" /></h3>
	<p>
		<fmt:message key="payment.gift.line1" />
	</p>
	<p>
		<fmt:message key="payment.gift.line2" /><br /> 
		<fmt:message key="payment.gift.line3" />
	</p>
	
	
	<img src="${originatingRequest.contextPath}/images/payment/carte-cadeau-explain.gif" />

	<dsp:getvalueof var="order" param="order" />
	<dsp:getvalueof var="total" param="total" />
	<dsp:getvalueof var="mail" param="mail" />
	
	<div class="formButtons">
		<span class="inputButton right">
            <fmt:message var="msgSelect" key="payment.select.method" />
			<form name="pbx_gift" id="pbx_gift" action="${originatingRequest.contextPath}/checkout/full/includes/paymentPaybox.jsp" method="post">
				<input type="hidden" name="PBX_MODE" id="PBX_MODE" value="13" />
				<input type="hidden" name="PBX_TOTAL" id="PBX_TOTAL" value="${total}" />
				<input type="hidden" name="PBX_CMD" id="PBX_CMD" value="${order.id}" />
				<input type="hidden" name="PBX_TYPECARTE" id="PBX_TYPECARTE" value="SVS" />
				<input type="hidden" name="PBX_TYPEPAIEMENT" id="PBX_TYPEPAIEMENT" value="PREPAYEE" />
				<input type="hidden" name="PBX_PORTEUR" id="PBX_PORTEUR" value="${mail}" />

				<input type="submit" value="${msgSelect}"  onclick="javascript:paimentSend('pbx_gift', 'SVS')"/>
			</form>
		</span>
	</div>
  
    <dsp:getvalueof var="isCCOrder" param="isCCOrder"/>
    <c:if test="${isCCOrder}">
        <dsp:getvalueof id="index" param="index"/>
        <script>
            disable(${index});
        </script>
    </c:if>
</dsp:page>