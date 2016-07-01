<dsp:page>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
  <dsp:importbean bean="/com/castorama/commerce/order/purchase/RepriceLocalOrderDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/castorama/droplet/OrderPromotionsDroplet"/>
  
  <%--Reprice the Order total so that we can assign PaymentGroups to any CommerceIdentifier. --%>
  <dsp:droplet name="RepriceOrderDroplet">
    <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
  </dsp:droplet>
  <dsp:droplet name="RepriceLocalOrderDroplet">
    <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
  </dsp:droplet>


  <div class="monpanierBlock">
    <h2><fmt:message key="msg.cart.title" /></h2>
    <div class="rightPanelContent">
      <dsp:droplet name="OrderPromotionsDroplet">
        <dsp:param name="order" bean="ShoppingCart.current"/>
        <dsp:param name="orderLocal" bean="ShoppingCart.currentLocal"/>
        <dsp:oparam name="output">
          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="orderPromotions" />
            <dsp:oparam name="output">
              <dsp:getvalueof var="promoName" param="element.displayName"/>
              <dsp:getvalueof var="description" param="element.description"/>
              <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="element.media"/>
                  <dsp:param name="elementName" value="media"/>
                  <dsp:oparam name="output">
                      <dsp:getvalueof var="mediaUrl" param="media.url"/>
                  </dsp:oparam>
              </dsp:droplet>
              <c:if test="${not empty description}">
                <div class="livraison">
                  <c:choose>
                    <c:when test="${fn:startsWith(promoName, 'VenteFlash')==true}">
                      <img src="../images/icoTimer.gif" alt="" title="" class="icoPreShopCart" />
                    </c:when>
                    <c:when test="${empty mediaUrl}">
                      <img src="../images/icoPresent.gif" alt="" title="" class="icoPreShopCart" />
                    </c:when>
                    <c:otherwise>
                      <img src="${mediaUrl}" alt="" title="" class="icoPreShopCart" />
                    </c:otherwise>
                    </c:choose>
                  <strong><dsp:valueof param="element.description" valueishtml="true"/></strong><br />
                </div>  
              </c:if>
              <dsp:getvalueof var="mediaUrl" value=""/>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:getvalueof var="isCartEmpty" bean="ShoppingCart.cartEmpty" />
      <dsp:getvalueof var="isCartLocalEmpty" bean="ShoppingCart.cartLocalEmpty" />
      <dsp:droplet name="Switch">
        <dsp:param name="value" value="${isCartEmpty && isCartLocalEmpty}" />
        <dsp:oparam name="true">
          <div class="scEmpty">
            <fmt:message key="msg.cart.empty" />
          </div>
           <dsp:form action="${pageContext.request.contextPath}/home.jsp" method="post" formid="precartForm">
            <div class="clear"></div>
            <div class="formButtons">
              <dsp:input type="hidden" bean="CastShoppingCartFormHandler.cancelURL" value="${pageContext.request.contextPath}/home.jsp" />       
              <span class="ccBascetButton">
                <fmt:message var="cart_continue" key="msg.cart.continue" />
                <dsp:input type="submit" bean="CastShoppingCartFormHandler.cancel" iclass="ccBascetButton" value="${cart_continue}" />
              </span>
            </div>
          </dsp:form> 
        </dsp:oparam>
        <dsp:oparam name="false">
          <dsp:setvalue param="lastCommerceItem" beanvalue="ShoppingCart.lastAddedCommerceItemFromTwoOrders" />
          <dsp:setvalue param="lastCommerceItemWeb" beanvalue="ShoppingCart.lastAddedCommerceItem" />
          <dsp:setvalue param="lastCommerceItemLocal" beanvalue="ShoppingCart.lastAddedCommerceItemLocal" />
          <dsp:param name="tmpOrder" bean="ShoppingCart.current"/>
          <dsp:param name="tmpOrderLocal" bean="ShoppingCart.currentLocal"/>
          <%@ include file="prepareOrderParams.jspf" %>
          <dsp:droplet name="IsNull">
            <dsp:param name="value" param="lastCommerceItem" />
            <dsp:oparam name="false">
              <h3><fmt:message key="msg.cart.last.item.title" /></h3>
              <div class="rightPanelProduct">
                <dsp:getvalueof var="lastProduct" param="lastCommerceItem.auxiliaryData.catalogRef"/> 
                <dsp:include page="includes/cart_item_info.jsp">
                  <dsp:param name="sku" param="lastCommerceItem.auxiliaryData.catalogRef"/>
                  <dsp:param name="product" param="lastCommerceItem.auxiliaryData.productRef"/>
                  <dsp:param name="commerceItem" param="lastCommerceItemWeb"/>
                  <dsp:param name="commerceItemLocal" param="lastCommerceItemLocal"/>
                  <dsp:param name="itemType" value="preshopping"/>
                  <dsp:param name="currencyCode" param="lastCommerceItem.priceInfo.currencyCode"/>
                  <dsp:param name="showCastCardPrice" value="${true}"/>
                  <dsp:param name="enabledCastCart" bean="ShoppingCart.current.payeCarteAtout"/>
                </dsp:include>
                <dsp:getvalueof var="url" param="url" />
                <dsp:form method="post" formid="precartForm1">
                  <dsp:input type="hidden" bean="CastShoppingCartFormHandler.removeItemFromOrderSuccessURL" value="${url}" />
                  <dsp:input type="hidden" bean="CastShoppingCartFormHandler.removeItemFromOrderErrorURL" value="${url}" />
                  <dsp:getvalueof var="continueURL" bean="CastShoppingCartFormHandler.addItemToOrderErrorURL"/>
                  <c:if test="${empty continueURL}">
                    <dsp:getvalueof var="continueURL" bean="CastShoppingCartFormHandler.addMultipleItemsToOrderErrorURL"/>
                  </c:if>
                  <c:if test="${empty continueURL}">
                    <dsp:getvalueof var="continueURL" bean="CastShoppingCartFormHandler.continueURL"/>
                  </c:if>
                  <dsp:input type="hidden" bean="CastShoppingCartFormHandler.continueURL" value="${continueURL}" />
                  <dsp:getvalueof var="itemId1" param="lastCommerceItemWeb"/>
                  <dsp:getvalueof var="itemId2" param="lastCommerceItemLocal"/>
                  <dsp:getvalueof var="itemId3" param="lastCommerceItem"/>
                  <c:choose>
                    <c:when test="${itemId1.auxiliaryData.productId == itemId2.auxiliaryData.productId}">
                      <dsp:getvalueof var="itemIds" value="${itemId1.id},${itemId2.id}"/>
                    </c:when>
                    <c:when test="${itemId1.auxiliaryData.productId == itemId3.auxiliaryData.productId}">
                      <dsp:getvalueof var="itemIds" value="${itemId1.id},"/>
                    </c:when>
                    <c:when test="${itemId2.auxiliaryData.productId == itemId3.auxiliaryData.productId}">
                      <dsp:getvalueof var="itemIds" value=",${itemId2.id}"/>
                    </c:when>
                  </c:choose>
                  <fmt:message var="cart_remove" key="msg.cart.remove" />
                  <dsp:input type="submit" bean="CastShoppingCartFormHandler.deleteItem" value="${itemIds}" iclass="icoTrash" />
                </dsp:form>
              </div>
              <div class="clear"></div>
            </dsp:oparam>
          </dsp:droplet>


          <dsp:droplet name="/com/castorama/droplet/CastDoubleBasketShippingGroupsDroplet">
            <dsp:param name="web" value="${shippingGroups}"/>
            <dsp:param name="local" value="${shippingGroupsLocal}"/>
            <dsp:oparam name="output">

              <dsp:getvalueof var="currentLocal" param="localCIRelationships"/>
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="webCIRelationships"/>
                <dsp:param name="elementName" value="CiRelationship"/>
                <dsp:param name="indexName" value="index"/>
                <dsp:oparam name="outputStart">
                  <dsp:getvalueof var="secondWeb" param="webCIRelationships[1]" />
                  <dsp:getvalueof var="secondLocal" param="localCIRelationships[1]" />
                  <c:if test="${not empty secondWeb || not empty secondLocal}">
                    <h3><fmt:message key="msg.cart.items.title" /></h3>
                  </c:if>
                </dsp:oparam>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="index" param="index"/>
                  <dsp:param name="relationshipLocal" value="${currentLocal[index]}"/>
                  <dsp:param name="commerceItem" param="CiRelationship.commerceItem"/>
                  <dsp:param name="commerceItemLocal" param="relationshipLocal.commerceItem"/>
                  <dsp:param name="product" param="CiRelationship.commerceItem.auxiliaryData.productRef"/>
                  <dsp:param name="sku" param="CiRelationship.commerceItem.auxiliaryData.catalogRef"/>
                  <dsp:param name="currencyCode" param="CiRelationship.commerceItem.priceInfo.currencyCode"/>
                  <dsp:getvalueof var="productCurr" param="CiRelationship.commerceItem.auxiliaryData.catalogRef" />

                  <dsp:getvalueof var="rel" param="CiRelationship" />
                  <c:if test="${empty rel}">
                    <dsp:param name="product" param="relationshipLocal.commerceItem.auxiliaryData.productRef"/>
                    <dsp:param name="sku" param="relationshipLocal.commerceItem.auxiliaryData.catalogRef"/>
                    <dsp:param name="currencyCode" param="relationshipLocal.commerceItem.priceInfo.currencyCode"/>
                    <dsp:getvalueof var="productCurr" param="relationshipLocal.commerceItem.auxiliaryData.catalogRef" />
                  </c:if>

                  <c:if test="${lastProduct != productCurr}">
                    <div class="rightPanelProduct">
                      <dsp:include page="includes/cart_item_info.jsp">
                        <dsp:param name="sku" param="sku"/>
                        <dsp:param name="product" param="product"/>
                        <dsp:param name="commerceItem" param="commerceItem"/>
                        <dsp:param name="commerceItemLocal" param="commerceItemLocal"/>
                        <dsp:param name="itemType" value="preshopping"/>
                        <dsp:param name="currencyCode" param="element.priceInfo.currencyCode"/>
                        <dsp:param name="showCastCardPrice" value="${true}" />
                        <dsp:param name="enabledCastCart" bean="ShoppingCart.current.payeCarteAtout"/>
                      </dsp:include>
                      <dsp:getvalueof var="url" param="url" />
                      <dsp:form method="post" formid="precartForm2">
                        <dsp:input type="hidden" bean="CastShoppingCartFormHandler.removeItemFromOrderSuccessURL" value="${url}" />
                        <dsp:input type="hidden" bean="CastShoppingCartFormHandler.removeItemFromOrderErrorURL" value="${url}" />
                        <dsp:getvalueof var="continueURL" bean="CastShoppingCartFormHandler.addItemToOrderErrorURL"/>
                        <c:if test="${empty continueURL}">
                          <dsp:getvalueof var="continueURL" bean="CastShoppingCartFormHandler.addMultipleItemsToOrderErrorURL"/>
                        </c:if>
                        <c:if test="${empty continueURL}">
                          <dsp:getvalueof var="continueURL" bean="CastShoppingCartFormHandler.continueURL"/>
                        </c:if>
                        <dsp:input type="hidden" bean="CastShoppingCartFormHandler.continueURL" value="${continueURL}" />
                        <dsp:getvalueof var="itemId1" param="commerceItem.id"/>
                        <dsp:getvalueof var="itemId2" param="commerceItemLocal.id"/>
                        <dsp:getvalueof var="itemIds" value="${itemId1},${itemId2}"/>
                        <fmt:message var="cart_remove" key="msg.cart.remove" />
                        <dsp:input type="submit" bean="CastShoppingCartFormHandler.deleteItem" value="${itemIds}" iclass="icoTrash" />
                      </dsp:form>
                    </div>
                  </c:if>
                </dsp:oparam>
              </dsp:droplet>

            </dsp:oparam>
            <dsp:oparam name="error">
              <dsp:valueof param="errorMessage"/>
            </dsp:oparam>
          </dsp:droplet>

          <dsp:form action="cart.jsp" method="post" formid="precartForm3">
            <div class="formButtons">
              <dsp:input type="hidden" bean="CastShoppingCartFormHandler.cancelURL" value="cart.jsp" />
              <dsp:getvalueof var="continueURL" bean="CastShoppingCartFormHandler.addItemToOrderErrorURL"/>
              <c:if test="${empty continueURL}">
                <dsp:getvalueof var="continueURL" bean="CastShoppingCartFormHandler.addMultipleItemsToOrderErrorURL"/>
              </c:if>
              <c:if test="${empty continueURL}">
                <dsp:getvalueof var="continueURL" bean="CastShoppingCartFormHandler.continueURL"/>
              </c:if>
              <dsp:input type="hidden" bean="CastShoppingCartFormHandler.continueURL" value="${continueURL}" />
              <span class="ccBascetButton blue">
                <fmt:message var="cart_submit" key="msg.cart.submit" />
                <dsp:input type="submit" bean="CastShoppingCartFormHandler.cancel" iclass="ccBascetButton" value="${cart_submit}" />
              </span>
              <span class="ccBascetButton">
                <fmt:message var="cart_continue" key="msg.cart.continue" />
                <dsp:input type="submit" bean="CastShoppingCartFormHandler.continue" iclass="ccBascetButton" value="${cart_continue}" />
              </span>
            </div>
          </dsp:form>  
        </dsp:oparam>
      </dsp:droplet>
    </div>
    <div class="clear"></div>
  </div>

</dsp:page>