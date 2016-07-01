<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

<div class="grayContainerBlock formMainBlock">
  <div class="formContent font12px">
    <p>
      <strong>
        <fmt:message key="confirmation.reservation.number">
          <fmt:param><dsp:valueof bean="ShoppingCart.last.id"/></fmt:param>
        </fmt:message>
      </strong><br />
    </p>
    <p>
      <strong><fmt:message key="confirmation.cc.1"/></strong>
    </p>
    <p>
      <fmt:message key="confirmation.cc.2">
        <fmt:param><dsp:valueof bean="Profile.billingAddress.phoneNumber"/></fmt:param>
      </fmt:message>
    </p>
  </div>
</div>

</dsp:page>