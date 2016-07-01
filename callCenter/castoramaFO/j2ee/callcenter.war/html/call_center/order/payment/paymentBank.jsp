<dsp:page>
    <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

    <dsp:getvalueof var="orderId" param="orderId" />
    <dsp:getvalueof var="total" param="total" />
    <dsp:getvalueof var="mail" param="mail" />

    <form name="pbx" id="pbx" action="${originatingRequest.contextPath}/html/call_center/order/payment/paymentPaybox.jsp" method="post">
        <input type="hidden" name="PBX_MODE" id="PBX_MODE" value="13" />
        <input type="hidden" name="PBX_TOTAL" id="PBX_TOTAL" value="${total}" />
        <input type="hidden" name="PBX_CMD" id="PBX_CMD" value="${orderId}" />
        <input type="hidden" name="PBX_TYPECARTE" id="PBX_TYPECARTE" value="VISA" />
        <input type="hidden" name="PBX_PORTEUR" id="PBX_PORTEUR" value="${mail}" />
        <input type="hidden" name="PBX_TYPEPAIEMENT" id="PBX_TYPEPAIEMENT" value="CARTE" />

        <ul class="chooseCC">
            <li><a title="Carte bleue" href="javascript:paimentSend('pbx', 'CB');"><img alt="Carte bleue" src="${originatingRequest.contextPath}/html/img/cclogos/card-bleue.gif"/></a></li>
            <li><a title="Carte Visa" href="javascript:paimentSend('pbx', 'VISA');"><img alt="Carte Visa" src="${originatingRequest.contextPath}/html/img/cclogos/card-visa.gif"/></a></li>
            <li><a title="Carte Eurocard Mastercard" href="javascript:paimentSend('pbx', 'EUROCARD_MASTERCARD');"><img alt="Carte Eurocard Mastercard" src="${originatingRequest.contextPath}/html/img/cclogos/card-mastercard.gif"/></a></li>
            <li><a title="Carte American Express" href="javascript:paimentSend('pbx', 'AMEX');"><img alt="Carte American Express" src="${originatingRequest.contextPath}/html/img/cclogos/card-american-express.gif"/></a></li>
            <%--<li><a title="Service Receive &amp; pay" href="javascript:paimentSend('pbx', '?');"><img alt="Service Receive &amp; pay" src="${originatingRequest.contextPath}/images/cclogos/card-receive-and-pay.gif"/></a></li>--%>
        </ul>
    </form>
</dsp:page>