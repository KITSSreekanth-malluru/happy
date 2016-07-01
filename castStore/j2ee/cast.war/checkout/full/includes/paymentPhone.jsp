<dsp:page>
  <dsp:importbean bean="/com/castorama/CastPaymentFormHandler"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <h3><fmt:message key="payment.phone.title" /></h3>
  <p>
    <fmt:message key="payment.phone.line1" /><br />
    <fmt:message key="payment.phone.line2" />
  </p>
  <p>
    <fmt:message key="payment.phone.line3" />
  </p>
  <p>
    <fmt:message key="payment.phone.line4" />
  </p>


  <div class="formButtons">
    <span class="inputButton right">
      <fmt:message var="msgSelect" key="payment.select.method" />
      <dsp:form>
        <dsp:input type="hidden" bean="CastPaymentFormHandler.cartURL" value="${contextPath }/checkout/cart.jsp" />
        <dsp:input type="hidden" bean="CastPaymentFormHandler.cancelURL" value="${contextPath }/checkout/full/delivery.jsp" />
        <dsp:input type="hidden" bean="CastPaymentFormHandler.successURL" value="${contextPath }/checkout/full/confirmation.jsp" />
        <dsp:input type="hidden" bean="CastPaymentFormHandler.commitOrderSuccessURL" value="${contextPath }/checkout/full/confirmation.jsp" />
        <dsp:input type="hidden" bean="CastPaymentFormHandler.commitOrderErrorURL" value="${contextPath }/checkout/full/delivery.jsp" />
        <input type="hidden" name="paymentMethod" value="Call-Center"/>

        <dsp:input type="submit" bean="CastPaymentFormHandler.commitOrder" value="${msgSelect}" onclick="sendOmniturePaymentInfoDisableButton(this, 'tel')"/>
        <dsp:getvalueof var="selectedOrderId" param="order.id"/>
        <dsp:input type="hidden" bean="CastPaymentFormHandler.orderId" value="${selectedOrderId}" />
      </dsp:form>
    </span>
  </div>
  <dsp:getvalueof var="disableAsynch" param="disableAsynch"/>
  <dsp:getvalueof var="isCCOrder" param="isCCOrder"/>
  <c:if test="${isCCOrder || (not empty disableAsynch && (disableAsynch == 'true' || disableAsynch))}">
    <dsp:getvalueof id="index" param="index"/>
    <script>
      disable(${index});
    </script>
  </c:if>

</dsp:page>