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
        <br/>
        <fmt:message key="confirmation.reference" /><a href="${contextPath }/user/order.jsp?orderId=<dsp:valueof bean="ShoppingCart.last.id"/>" style="background: transparent; padding:0 0 3px 3px;"> « Mon espace client » </a>.
      </p>
      <p>
        <strong><fmt:message key="confirmation.transfer.1" /></strong><br />
        <strong class="red"><fmt:message key="confirmation.transfer.2" /></strong>
      </p>
      
      <table class="horizontalForm">
        <thead>
          <tr>
            <th class="firstCell">30004</th>
            <th>00515</th>
            <th>00010220594</th>
            <th class="lastCell">07</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><fmt:message key="confirmation.transfer.account.col.1" /></td>
            <td><fmt:message key="confirmation.transfer.account.col.2" /></td>
            <td><fmt:message key="confirmation.transfer.account.col.3" /></td>
            <td><fmt:message key="confirmation.transfer.account.col.4" /></td>
          </tr>
        </tbody>
      </table>
      
      <p>
        <fmt:message key="confirmation.transfer.3" >
          <fmt:param><strong>0 810 104 104</strong></fmt:param>
        </fmt:message>
      </p>
    </div>
  </div>

  <%@ include file="/checkout/full/includes/confirmStaticBlock.jspf" %>

</dsp:page>