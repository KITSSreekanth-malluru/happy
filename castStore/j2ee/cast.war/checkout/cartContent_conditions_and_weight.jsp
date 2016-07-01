<dsp:page>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
  <dsp:getvalueof var="cTotalWeight" param="pTotalWeight"/>
  <dsp:getvalueof var="cDeliveriesCount" param="pDeliveriesCount"/>
  <dsp:getvalueof var="orderDeliveryType" param="thisOrder.deliveryType"/>
  <dsp:getvalueof var="cssStyle" param="style"/>
  <dsp:getvalueof var="classType" param="classType"/>

  <td class="productItem_${classType} ${cssStyle}">
    <div class="boxCartInnerWr">
      <div class="summaryInfo">
        <div>
          <strong>
            <fmt:message key="msg.cart.weight"><fmt:param><c:out value="${cTotalWeight}"/></fmt:param></fmt:message>
          </strong>
        </div>

        <c:if test="${orderDeliveryType == 'deliveryToHome'}">
          <div>
            <fmt:message key="msg.cart.deliveries"><fmt:param><c:out value="${cDeliveriesCount}"/></fmt:param></fmt:message>
          </div>
        </c:if>
      </div>
      <div class="noCurrentBox"><!-- --></div>
    </div>
  </td>

</dsp:page>