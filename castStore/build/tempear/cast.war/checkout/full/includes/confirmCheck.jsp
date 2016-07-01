<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <div class="grayContainerBlock formMainBlock">
    <div class="formContent font12px">
      <p>
        <strong>
          <fmt:message key="confirmation.order.number" >
            <fmt:param><dsp:valueof bean="ShoppingCart.last.id"/></fmt:param>
          </fmt:message>
        </strong>
        <br />
        <fmt:message key="confirmation.reference" /><a href="${contextPath }/user/order.jsp?orderId=<dsp:valueof bean="ShoppingCart.last.id"/>" style="background: transparent; padding:0 0 3px 3px;"> « Mon espace client » </a>.
      </p>
      <p>
        <strong class="red"><fmt:message key="confirmation.check.1" /></strong><br />
        <strong class="red">
          <fmt:message key="confirmation.check.2" >
            <fmt:param><dsp:valueof bean="ShoppingCart.last.priceInfo.total" converter="euro" locale="fr_FR"/></fmt:param>
          </fmt:message>
        </strong>
      </p>
      <p>
        <fmt:message key="confirmation.check.3" />
      </p>
      <table class="horizontalForm">
        <tbody>
          <c:forEach begin="1" end="5" varStatus="status">
            <dsp:getvalueof var="index" value="${status.index}"/>
            <tr>
              <td><fmt:message key="confirmation.check.address.col.${index}" /></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>

<%@ include file="/checkout/full/includes/confirmStaticBlock.jspf"%>

</dsp:page>