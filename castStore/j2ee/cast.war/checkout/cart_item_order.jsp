<dsp:page>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="mode"  param="mode"/>
  <dsp:getvalueof var="deliveryType" param="delType"/>
  <dsp:getvalueof var="cssStyle" param="style"/>

  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
    <dsp:param name="value" param="commerceItem" />
    <dsp:oparam name="true">
      <td class="${cssStyle}">
        <div class="boxCartInnerWr">
        <c:choose>
          <c:when test="${deliveryType == 'deliveryToHome'}">
            <fmt:message var="mess" key="cc.deliveryToHome.unavailable"/>
          </c:when>
          <c:otherwise> 
            <fmt:message var="mess" key="cc.pickUpFromStore.unavailable"/>
          </c:otherwise>
        </c:choose> 
        <table class="boxCartInner">
          <tbody>
            <tr>
              <td colspan="4">
                <span class="boxCartInnerDelivery">${mess}</span>
              </td>
            </tr>
          </tbody>
        </table>
          <div class="noCurrentBox"><!-- --></div>
        </div>
      </td>
    </dsp:oparam>
    <dsp:oparam name="false"> 

      <dsp:getvalueof var="currencyCode" param="currencyCode"/>
      <dsp:getvalueof var="productId" param="productId"/>
      <dsp:getvalueof var="skuId" param="commerceItem.catalogRefId"/>
      <dsp:getvalueof var="relationshipId" param="relationship.id"/>
      <dsp:getvalueof var="isLocal" param="isLocal"/>

      <td class="${cssStyle}">
        <div class="boxCartInnerWr" id="boxCartInnerWrId${relationshipId}">
          <table class="boxCartInner">
            <tr>
              <c:if test="${mode != 'confirmation'}">
                <td>
                  <c:choose>
                    <c:when test = "${isLocal!=true}">
                      <span><fmt:message key="msg.cart.delivery_at"/></span>
                      <span class="deliveryProd${relationshipId}"></span>
                    </c:when>
                    <c:otherwise>
                      <fmt:message key="cc.msg.cart.delivery_at">
                        <fmt:param>
                          ${delayTime}${displayDelayUnit}
                        </fmt:param>
                      </fmt:message>
                    </c:otherwise>
                  </c:choose>
                </td>
              </c:if>

              <c:if test="${mode == 'confirmation'}">
                <td>
                  <dsp:valueof param="commerceItem.quantity"/>
                </td>
              </c:if>
              <td <c:if test="${mode == 'confirmation'}">class="price"</c:if>>
                <dsp:include page="includes/skuPrice.jsp">
                  <dsp:param name="thisProductId" value="${productId}"/>
                  <dsp:param name="thisSku" param="sku"/>
                  <dsp:param name="commerceItem" param="commerceItem"/>
                  <dsp:param name="mode" param="mode"/>
                  <dsp:param name="hasProductsWithDiscount" value="${hasProductsWithDiscount}"/>
                </dsp:include>
              </td>

              <c:if test="${mode != 'confirmation'}">
                <td>
                  <div class="boxCartInnerHowMuch">
                    <dsp:getvalueof var="quantity" param="commerceItem.quantity"/>
                    <dsp:getvalueof var="itemId" value="${relationshipId}"/>
                    
                    <%@ include file="/includes/quantityArea.jspf" %>
                    <span class="boxCartInnerRecalculer">
                      <a class="recalculerBtn" id="RECALCULER${relationshipId}">Recalculer</a>
                    </span>
                  </div>
                </td>
              </c:if>
              <td>
                <div class="newprice">
                  <dsp:getvalueof var="itemAmount" param="commerceItem.priceInfo.amount"/>
                  <fmt:formatNumber value="${itemAmount}" type="currency" currencyCode="${currencyCode}"/>
                </div>
              </td>

            </tr>
          </table>
          <div class="noCurrentBox"><!-- --></div>
        </div>
      </td>

    </dsp:oparam>
  </dsp:droplet>
</dsp:page>