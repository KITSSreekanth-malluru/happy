<dsp:page>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
    <a title="Carte Cadeau" href="javascript:paimentSend('pbx_gift', 'SVS');"><img alt="Carte Cadeau" src="${originatingRequest.contextPath}/html/img/payment/bg-ccadeau.jpg"/></a>

    <dsp:getvalueof var="orderId" param="orderId" />
    <dsp:getvalueof var="total" param="total" />
    <dsp:getvalueof var="mail" param="mail" />

    <div class="formButtons">
        <span class="inputButton right">
            <form name="pbx_gift" id="pbx_gift" action="${originatingRequest.contextPath}/html/call_center/order/payment/paymentPaybox.jsp" method="post">
                <input type="hidden" name="PBX_MODE" id="PBX_MODE" value="13" />
                <input type="hidden" name="PBX_TOTAL" id="PBX_TOTAL" value="${total}" />
                <input type="hidden" name="PBX_CMD" id="PBX_CMD" value="${orderId}" />
                <input type="hidden" name="PBX_TYPECARTE" id="PBX_TYPECARTE" value="SVS" />
                <input type="hidden" name="PBX_TYPEPAIEMENT" id="PBX_TYPEPAIEMENT" value="PREPAYEE" />
                <input type="hidden" name="PBX_PORTEUR" id="PBX_PORTEUR" value="${mail}" />
            </form>
        </span>
    </div>
</dsp:page>