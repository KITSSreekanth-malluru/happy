<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
  <dsp:importbean bean="/com/castorama/droplet/ShoppingCartDiscountDroplet"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
  
  <dsp:getvalueof var="orderDeliveryType" bean="/atg/commerce/ShoppingCart.currentlySelected.deliveryType"/>
  
  <dsp:droplet name="RepriceOrderDroplet">
      <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
  </dsp:droplet>

  <c:choose>
    <c:when test="${mode == 'confirmation'}">
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.last" var="order"/>
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.last.TotalCommerceItemCount" var="itemsCount"/>  
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.last.ShippingGroups" var="shippingGroups"/>
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.last.priceInfo.rawSubTotal" var="rawSubTotal"/>
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.last.priceInfo.total" var="total"/>
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.last.priceInfo.currencyCode" var="currencyCode" vartype="java.lang.String" />
    </c:when>
    <c:otherwise>
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.currentlySelected" var="order"/>
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.currentlySelected.TotalCommerceItemCount" var="itemsCount"/>  
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.currentlySelected.ShippingGroups" var="shippingGroups"/>
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.currentlySelected.priceInfo.rawSubTotal" var="rawSubTotal"/>
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.currentlySelected.priceInfo.total" var="total"/>
      <dsp:getvalueof bean="/atg/commerce/ShoppingCart.currentlySelected.priceInfo.currencyCode" var="currencyCode" vartype="java.lang.String" />
    </c:otherwise>
  </c:choose>

  <dsp:droplet name="ShoppingCartDiscountDroplet">
      <dsp:param name="order" value="${order}"/>
    <dsp:oparam name="output">
        <dsp:getvalueof var="hasProductsWithDiscount" param="hasProductsWithDiscount"/>
        <dsp:getvalueof var="totalDiscount" param="totalDiscount"/>    
    </dsp:oparam>
  </dsp:droplet>

  <h2 class="rightPahelTitle"><fmt:message key="msg.cart.title" /></h2>
  <div class="rightPanelContent cart_info">
    <dsp:droplet name="ForEach">
      <dsp:param name="array" value="${shippingGroups}"/>
      <dsp:param name="elementName" value="ShippingGroup"/>
      <dsp:param name="indexName" value="shippingGroupIndex"/>
      <dsp:oparam name="output">
        <dsp:droplet name="ForEach">
          <dsp:param name="array" param="ShippingGroup.CommerceItemRelationships"/>
          <dsp:param name="elementName" value="CiRelationship"/>
          <dsp:param name="indexName" value="index"/>
          <dsp:oparam name="output">
            <div class="rightPanelProduct">
              <dsp:include page="includes/cart_item_info.jsp">
                <dsp:param name="commerceItem" param="CiRelationship.commerceItem"/>
                <dsp:param name="product" param="CiRelationship.commerceItem.auxiliaryData.productRef"/>
                <dsp:param name="sku" param="CiRelationship.commerceItem.auxiliaryData.catalogRef"/>
                <dsp:param name="itemType" value="delivery"/>
                <dsp:param name="hasProductsWithDiscount" value="false"/>
                <dsp:param name="currencyCode" value="${currencyCode}"/>
                <dsp:param name="enabledCastCart" value="${order.payeCarteAtout}" />
              </dsp:include>
            </div>
          </dsp:oparam>
         </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>

    <c:set var="mode" value="confirmation"/>
    <table class="boxCartTable ${singleBasketClass}">
      <tbody class="boxCartTableSummary">
        <tr>
          <%@ include file="includes/prepareSummary.jspf" %>
          
          <dsp:include page="includes/cart_total_td.jsp">
            <dsp:param name="labelsList" value="${labelsList}"/>
          </dsp:include>
          
          <td>
            <dsp:getvalueof var="pricesList" value="${d2hPricesList}"/>
            <dsp:param name="order" value="${order}"/>
            <%@ include file="includes/cartTotalTable.jspf" %>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div class="totalChkS">
    <strong>
      <fmt:message key="msg.cart.weight">
        <fmt:param>
          <dsp:valueof param="cTotalWeight" />
        </fmt:param>
      </fmt:message>
    </strong>
    <br />
    <c:if test="${orderDeliveryType=='deliveryToHome'}">
      <fmt:message key="msg.cart.deliveries">
        <fmt:param>
          <dsp:valueof param="cDeliveriesCount" />
        </fmt:param>
      </fmt:message>
    </c:if>
  </div>
  <div class="clear"><!--~--></div>

</dsp:page>