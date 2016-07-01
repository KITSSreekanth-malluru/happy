<dsp:page>
  <dsp:importbean bean="/com/castorama/CastPaymentFormHandler"/>
    <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
    
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <h3><fmt:message key="payment.check.title" /></h3>
  <p>
    <fmt:message key="payment.check.line1" /><br />
    <fmt:message key="payment.check.line2" />
  </p>
  <p>
    <fmt:message key="payment.check.line3" />
  </p>
  <p>
    <fmt:message key="payment.check.line4" />
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

       <input type="hidden" name="paymentMethod" value="Cheque"/>

       <dsp:input type="submit" bean="CastPaymentFormHandler.commitOrder" value="${msgSelect}" onclick="sendOmniturePaymentInfoDisableButton(this, 'cheque')"/>
       <dsp:getvalueof var="selectedOrderId" param="order.id"/>
       <dsp:input type="hidden" bean="CastPaymentFormHandler.orderId" value="${selectedOrderId}" />
     </dsp:form>
    </span>
  </div>
  <dsp:getvalueof id="price" bean="/atg/commerce/ShoppingCart.currentlySelected.priceInfo.total"/>
  <dsp:getvalueof var="disableAsynch" param="disableAsynch"/>
    <dsp:getvalueof var="isCCOrder" param="isCCOrder"/>
  <c:if test="${isCCOrder || price < 150 || (not empty disableAsynch && (disableAsynch == 'true' || disableAsynch))}">
    <dsp:getvalueof id="index" param="index"/>
    <script>
      disable(${index});
    </script>
  </c:if>

</dsp:page>