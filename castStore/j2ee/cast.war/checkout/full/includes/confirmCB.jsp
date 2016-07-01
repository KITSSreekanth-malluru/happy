<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <div class="grayContainerBlock formMainBlock">
    <div class="formContent font12px">
      <p>
        <strong>
          <fmt:message key="confirmation.order.number">
            <fmt:param><dsp:valueof bean="ShoppingCart.last.id"/></fmt:param>
          </fmt:message>
        </strong>
        <br />
        <fmt:message key="confirmation.reference" /> <a href="${contextPath }/user/order.jsp?orderId=<dsp:valueof bean="ShoppingCart.last.id"/>" style="background: transparent; padding:0 0 3px 3px;"> « Mon espace client » </a>.
      </p>
      <p class="font12px">
        <strong>
          <fmt:message key="confirmation.static.1"/><br />
          <fmt:message key="confirmation.static.2"/>
        </strong>
      </p>
      <p>
        <fmt:message key="confirmation.cb.1">
          <fmt:param><dsp:valueof bean="Profile.email" /></fmt:param>
        </fmt:message>
      </p>
    </div>
  </div>
  <div class="padded">
    <p class="font12px">
      <fmt:message key="confirmation.static.3"/><br />
    </p>
  </div>
</dsp:page>