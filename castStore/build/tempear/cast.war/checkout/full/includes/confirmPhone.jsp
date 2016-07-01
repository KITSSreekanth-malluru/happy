<%@ page contentType="text/html; charset=UTF-8"%>

<dsp:page>

  <dsp:importbean bean="/atg/commerce/ShoppingCart" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />


  <div class="grayContainerBlock formMainBlock">
    <div class="formContent font12px">
      <p>
        <strong> 
          <fmt:message key="confirmation.order.number">
            <fmt:param><dsp:valueof bean="ShoppingCart.last.id" /></fmt:param>
          </fmt:message>
        </strong><br />
        <fmt:message key="confirmation.reference" /><a href="${contextPath }/user/order.jsp?orderId=<dsp:valueof bean="ShoppingCart.last.id"/>" style="background: transparent; padding: 0 0 3px 0px;"> « Mon espace client » </a>.
      </p>
      <p>
        <strong class="red"><fmt:message key="confirmation.phone.1"/></strong>
      </p>
      <span style="display: block; text-align: center;">
        <img src="/images/techSupport.png" width="176" height="23" alt="Nos conseillers a votre écoute" /><br />
      </span>
      <p><fmt:message key="confirmation.phone.2"/></p>
    </div>
  </div>
  <%@ include file="/checkout/full/includes/confirmStaticBlock.jspf"%>

</dsp:page>