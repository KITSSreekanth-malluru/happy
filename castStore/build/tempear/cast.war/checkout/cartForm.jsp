<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:getvalueof var="order" param="order"/>
  <dsp:getvalueof var="mode" param="mode"/>
  <dsp:getvalueof var="shipGroups" param="shippingGroups"/>
  <dsp:getvalueof var="currencyCode" param="curCode"/>
  <dsp:getvalueof var="isCCOrder" param="isCCOrder"/>
  <dsp:form action="cart.jsp" method="post" name="cartItemForm" id="cartItemForm" onsubmit="if (!subVar) { return false }">
    <dsp:setvalue bean="CastShoppingCartFormHandler.initForm" value=""/>
    <dsp:input bean="CastShoppingCartFormHandler.setOrderByRelationshipIdSuccessURL" type="hidden" value="cart.jsp" id="orderByRelationshipIdSuccessURL"/>
    <dsp:input bean="CastShoppingCartFormHandler.setOrderByRelationshipIdErrorURL" type="hidden" value="cart.jsp"/>
    <dsp:input bean="CastShoppingCartFormHandler.sessionExpirationURL" type="hidden" value="../home.jsp"/>
    <dsp:input bean="CastShoppingCartFormHandler.moveToPurchaseInfoByRelId" type="submit" id="moveToPurchaseInfoByRelIdButton" style="display:none"/>
    <dsp:input bean="CastShoppingCartFormHandler.acceptCarteLAtout" type="text" style="display:none" id="acceptCarteLAtoutCheckBox"/>
    <dsp:input bean="CastShoppingCartFormHandler.lAtoutChangedOnPage" type="text" style="display:none" id="lAtoutChangedOnPage"/>
    <dsp:input bean="CastShoppingCartFormHandler.acceptSalesConditions" type="text" style="display:none" id="acceptSalesConditionsCheckBox"/>
    <dsp:input bean="CastShoppingCartFormHandler.setOrderByRelationshipId" type="submit" style="display:none" id="recalculateCartButton"/>
    
    <dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
      <dsp:param name="inUrl" value="full/delivery.jsp"/>
      <dsp:oparam name="output">
        <dsp:getvalueof id="next_url" param="secureUrl" idtype="java.lang.String">
          <dsp:input bean="CastShoppingCartFormHandler.moveToPurchaseInfoByRelIdSuccessURL" value="${next_url}" type="hidden"/>
        </dsp:getvalueof>
      </dsp:oparam>
    </dsp:droplet>
    <dsp:input bean="CastShoppingCartFormHandler.moveToPurchaseInfoByRelIdErrorURL" type="hidden" value="cart.jsp"/>
    <c:choose>
      <c:when test="${storeIsCC && mode != 'confirmation'}">
        <div class="boxCartTop">
          <h3><fmt:message key="cc.doubleBasket.jeChoisis"/></h3>
          <div class="boxCartChooseWr">
            <div class="boxCartChoose boxCartChooseLeft">
              <label>
              <c:choose>
                <c:when test="${not empty shipGroupCommerceRelationships}">
                  <dsp:input bean="/atg/commerce/ShoppingCart.selectedOrder" type="radio" value="1" onclick="recalculateQuantity();"/>
                </c:when>
                <c:otherwise>
                  <input type="radio" disabled="true"/>
                </c:otherwise>
              </c:choose>
                <span>
                  <fmt:message key="cc.deliveryToHome.basketCheckboxMessage"/>
                </span>
              </label>
            </div>
            <div class="boxCartChoose boxCartChooseRight">
              <label>
                <c:choose>
                  <c:when test="${not empty shipGroupCommerceRelationshipsLocal}">
                    <dsp:input bean="/atg/commerce/ShoppingCart.selectedOrder" type="radio" value="2" onclick="recalculateQuantity();"/>
                  </c:when>
                  <c:otherwise>
                    <input type="radio" disabled="true"/>
                  </c:otherwise>
                </c:choose>
                <span>
                  <fmt:message key="cc.pickUpFromStore.basketCheckboxMessage">
                    <fmt:param>
                      ${storeNom}
                    </fmt:param>
                  </fmt:message>
                </span>
              </label>
            </div>
          </div>
          <table class="boxCartGiftWr" cellspacing="0" cellpadding="0">
            <tr>
              <td colspan="2">
                <c:if test="${itemsCount > 0}">
                  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                    <dsp:param name="value" bean="CastShoppingCartFormHandler.formExceptions"/>
                    <dsp:oparam name="false">
                      <dsp:droplet name="Switch">
                        <dsp:param name="value" bean="CastShoppingCartFormHandler.formExceptions[0].errorCode"/>
                        <dsp:oparam name="msgRemoveIllegalItems">
                          <div class="boxCartRemovedItems">
                            <p><fmt:message key="msg.cart.remove.items"/></p>
                          </div>
                        </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
                </c:if>
              </td>
            </tr>
            <tr>
              <td class="boxCartGiftLeft ${leftClass}">
                <dsp:include page="orderPromoBlock.jsp">
                  <dsp:param name="md" value="${mode}"/>
                  <dsp:param name="or" param="order"/>
                  <dsp:param name="style" value="${leftClass}"/>
                </dsp:include>
              </td>
              <td class="boxCartGiftRight ${rightClass}">
                <dsp:include page="orderPromoBlock.jsp">
                  <dsp:param name="md" value="${mode}"/>
                  <dsp:param name="or" param="orderLocal"/>
                  <dsp:param name="style" value="${rightClass}"/>
                </dsp:include>
              </td>
            </tr>
          </table>
        </div>
      </c:when>
      <c:when test="${!storeIsCC && mode != 'confirmation'}">
       <div class="boxCartTop">
          <table class="boxCartGiftWr" cellspacing="0" cellpadding="0">
            <tr>
              <td colspan="2">
                <c:if test="${itemsCount > 0}">
                  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                    <dsp:param name="value" bean="CastShoppingCartFormHandler.formExceptions"/>
                    <dsp:oparam name="false">
                      <dsp:droplet name="Switch">
                        <dsp:param name="value" bean="CastShoppingCartFormHandler.formExceptions[0].errorCode"/>
                        <dsp:oparam name="msgRemoveIllegalItems">
                          <div class="boxCartRemovedItems">
                            <p><fmt:message key="msg.cart.remove.items"/></p>
                          </div>
                        </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
                </c:if>
              </td>
            </tr>
            <tr>
              <td colspan="2" class="boxCartGiftLeft ${leftClass}">
                <dsp:include page="orderPromoBlock.jsp">
                  <dsp:param name="md" value="${mode}"/>
                  <dsp:param name="or" param="order"/>
                  <dsp:param name="style" value="${leftClass}"/>
                </dsp:include>
              </td>
            </tr>
          </table>
        </div>
      </c:when>
      <c:otherwise>
      </c:otherwise>
    </c:choose>
    <c:if test="${!storeIsCC || mode == 'confirmation'}">
      <dsp:getvalueof var="singleBasketClass" value="singleBasket" scope="request"/>
    </c:if>
    <div class="boxCartMiddle" >
      <table class="boxCartTable ${singleBasketClass}" id="productTable">
        <thead>
          <tr>
            <th class="produits"><fmt:message key="msg.cart.product"/></th>
            <th class="ccTitle ${leftClass}">
                <table class="boxCartTitle">
                  <tr>
                    <c:if test="${mode == 'confirmation'}">
                      <td><fmt:message key="msg.cart.quantity"/></td>
                    </c:if>
                    <c:if test="${!isCCOrder && mode != 'confirmation'}">
                      <td><fmt:message key="msg.cart.delivery"/></td>
                    </c:if>
                    <td><fmt:message key="msg.cart.unit-price"/></td>
                    <c:if test="${mode != 'confirmation'}">
                      <td><fmt:message key="msg.cart.quantity"/></td>
                    </c:if>
                    <td><fmt:message key="msg.cart.sub-total"/></td>
                  </tr>
                </table>
            </th>
            <c:if test="${storeIsCC && mode != 'confirmation'}">
              <th class="ccTitle ${rightClass}">
                  <table class="boxCartTitle">
                    <tr>
                      <td><fmt:message key="msg.cart.delivery"/></td>
                      <td><fmt:message key="msg.cart.unit-price"/></td>
                      <td><fmt:message key="msg.cart.quantity"/></td>
                      <td><fmt:message key="msg.cart.sub-total"/></td>
                    </tr>
                  </table>
              </th>
            </c:if>
            <c:if test="${mode != 'confirmation'}">
              <th class="remove"><!-- --></th>
            </c:if>
          </tr>
        </thead>
        <tbody>
          <dsp:getvalueof var="tagCommanderProductsInfo" value="${castCollection:column()}" scope="request"/>
          <dsp:getvalueof var="tagCommanderDiscountAmountTF" value="0" scope="request"/>
          <dsp:getvalueof var="tagCommanderDiscountAmountATI" value="0" scope="request"/>
          <dsp:getvalueof var="tagCommanderOrderAmountTFWithoutSF" value="0" scope="request"/>
          <dsp:getvalueof var="tagCommanderTotalOrderTaxTVA" value="0" scope="request"/>

          <dsp:droplet name="/com/castorama/droplet/CastDoubleBasketShippingGroupsDroplet">
            <dsp:param name="web" param="shippingGroups"/>
            <dsp:param name="local" param="shippingGroupsLocal"/>
            <dsp:oparam name="output">

              <dsp:getvalueof var="currentLocal" param="localCIRelationships"/>
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="webCIRelationships"/>
                <dsp:param name="elementName" value="CiRelationship"/>
                <dsp:param name="indexName" value="index"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="index" param="index"/>
                  <dsp:include page="cart_item.jsp">
                    <dsp:param name="relationship" param="CiRelationship"/>
                    <dsp:param name="relationshipLocal" value="${currentLocal[index]}"/>
                    <dsp:param name="mode" param="mode"/>
                    <dsp:param name="hasProductsWithDiscount" value="${hasProductsWithDiscount}"/>
                    <dsp:param name="currencyCode" value="${currencyCode}"/>
                    <dsp:param name="enabledCastCart" value="${order.payeCarteAtout}" />
                    <dsp:param name="isCCOrder" value="${isCCOrder}"/>
                    <dsp:param name="url" value="cart.jsp" />
                  </dsp:include>
                </dsp:oparam>
              </dsp:droplet>

            </dsp:oparam>
            <dsp:oparam name="error">
              <dsp:valueof param="errorMessage"/>
            </dsp:oparam>
          </dsp:droplet>
        </tbody>
      </table>
      <c:if test="${mode != 'confirmation'}">
        <script type="text/javascript">
          var rows = $("#productTable tr:gt(0)");
          var pattern=/^\s*$/;
          rows.each(function(index) {
            var rowSize = $(this).children("td").size();
            var counter = 0;
            $(this).children("td").each(function() {
              var str = $(this).html();
              if(pattern.test(str)){
                  counter++;
              };
            });
            if((counter +2) == rowSize){
                $(this).css("display", "none");
            };
          });
        </script>
      </c:if>
    </div>
  </dsp:form>
</dsp:page>