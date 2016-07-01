<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ProtocolChange"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
  <dsp:importbean bean="/com/castorama/commerce/order/purchase/RepriceLocalOrderDroplet"/>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
  <dsp:importbean bean="/com/castorama/droplet/ShoppingCartDiscountDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/OrderPromotionsDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/OrderTotalWeightDroplet"/>

  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
  <dsp:getvalueof var="mode"  param="mode"/>
  <dsp:getvalueof var="invalidOrder" param="invalidOrder"/>
  <dsp:getvalueof var="isCCOrder" param="isCCOrder"/>

  <!-- delivery popup -->
  <%@ include file="includes/delivery_popup.jsp" %>


  <%--Reprice the Order total so that we can assign PaymentGroups to any CommerceIdentifier. --%>
  <dsp:droplet name="RepriceOrderDroplet">
    <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
  </dsp:droplet>
  <dsp:droplet name="RepriceLocalOrderDroplet">
    <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
  </dsp:droplet>

  <div class="content">

    <c:if test="${mode != 'confirmation'}">
      <div class="pageTitle"><h1><fmt:message key="msg.cart.title"/></h1></div>
    </c:if>
    <dsp:setvalue bean="CastShoppingCartFormHandler.initForm" value=""/>
    <c:choose>
      <c:when test="${mode == 'confirmation'}">
        <dsp:param name="tmpOrder" bean="/atg/commerce/ShoppingCart.last"/>
        <%@ include file="prepareOrderParams.jspf" %>
      </c:when>
      <c:otherwise>
        <dsp:param name="tmpOrder" bean="/atg/commerce/ShoppingCart.current"/>
        <dsp:param name="tmpOrderLocal" bean="/atg/commerce/ShoppingCart.currentLocal"/>
        <%@ include file="prepareOrderParams.jspf" %>
      </c:otherwise>
    </c:choose>
    <c:if test="${itemsCount > 0 and invalidOrder}">
      <div class="grayCorner grayCornerGray preMessageLayer">
        <div class="grayBlockBackground"><!--~--></div>
        <div class="cornerBorder cornerTopLeft"><!--~--></div>
        <div class="cornerBorder cornerTopRight"><!--~--></div>
        <div class="cornerBorder cornerBottomLeft"><!--~--></div>
        <div class="cornerBorder cornerBottomRight"><!--~--></div>
        <div class="preMessage">
          <table cellpadding="0" cellspacing="0" class="emilateValignCenter">
            <tr>
              <td class="center darkRed">
                <fmt:message key="msg.cart.invalidOrder"/>
              </td>
            </tr>
          </table>
        </div>
      </div>
    </c:if>
    <dsp:droplet name="/atg/dynamo/droplet/Switch">
      <dsp:param name="value" value="${itemsCount}"/>
      <dsp:oparam name="0">
        <%@ include file="/checkout/full/includes/emptyShoppingMessageBlock.jspf" %>
      </dsp:oparam>
      <dsp:oparam name="default">
        <!-- Shopping Cart with items -->
        <c:if test="${mode == 'confirmation'}">
          <dsp:getvalueof var="confirmClass" value="confirmation"/>
        </c:if>
        <div class="boxCartWr ${confirmClass}">
          <div class="whitePopupContainer" id="unacceptedConditions">
            <div class="whitePopupContent nonTandC">
                  <p><strong><fmt:message key="msg.cart.unaccepted.conditions"/></strong></p>
                  <span class="ccBlueButton"><button type="submit" onclick="hidePopup(this)" class="ccBlueButton">OK</button></span>
            </div>
          </div>
          
          <c:if test="${mode != 'confirmation'}">
            <div class="whitePopupContainer" id="termsAndConditions" style="position: absolute;">
              <div class="whitePopupContent">
                <div class="whitePopupHeader">
                  <fmt:message key="castCatalog_label.close" var="fermer"/>
                  <a href="javascript:void(0);" onclick="hidePopup(this)" class="closeBut" title="${fermer}">
                    <span><!--~--></span>
                    ${fermer}
                  </a>
                </div>
                &nbsp;
              </div>
              <div class="clear"><!--~--></div>
              <div class="popupContentContainer">
                <div class="popupForm">
                  <div class="formMainBlock questionContent">
                    <c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/legal-notice.html"/>
                  </div>
                  <div class="clear"><!--~--></div>
                </div>
              </div>
            </div>
          </c:if>

        <dsp:getvalueof var="shipGroupCommerceRelationships" param="tmpOrder.shippingGroups[0].CommerceItemRelationships" scope="request"/>
        <dsp:getvalueof var="shipGroupCommerceRelationshipsLocal" param="tmpOrderLocal.shippingGroups[0].CommerceItemRelationships" scope="request"/>
        <c:if test="${empty shipGroupCommerceRelationships || empty shipGroupCommerceRelationshipsLocal}">
          <c:choose>
            <c:when test="${not empty shipGroupCommerceRelationships}">
              <dsp:getvalueof var="selectedOrderValue" value="1"/>
              <dsp:setvalue bean="/atg/commerce/ShoppingCart.selectedOrder" value="${selectedOrderValue}"/>
            </c:when>
            <c:when test="${not empty shipGroupCommerceRelationshipsLocal}">
              <dsp:getvalueof var="selectedOrderValue" value="2"/>
              <dsp:setvalue bean="/atg/commerce/ShoppingCart.selectedOrder" value="${selectedOrderValue}"/>
            </c:when>
          </c:choose>
        </c:if>
          <dsp:droplet name="Switch">
            <dsp:param name="value" bean="/atg/commerce/ShoppingCart.selectedOrder"/>
            <dsp:oparam name="1">
              <dsp:getvalueof var="leftClass" value="ccEnableBasket" scope="request"/>
              <dsp:getvalueof var="rightClass" value="" scope="request"/>
            </dsp:oparam>
            <dsp:oparam name="2">
              <dsp:getvalueof var="leftClass" value="" scope="request"/>
              <dsp:getvalueof var="rightClass" value="ccEnableBasket" scope="request"/>
            </dsp:oparam>
            <dsp:oparam name="default">
              <dsp:getvalueof var="leftClass" value="" scope="request"/>
              <dsp:getvalueof var="rightClass" value="" scope="request"/>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:include page="cartForm.jsp">
            <dsp:param name="order" value="${order}"/>
            <dsp:param name="orderLocal" value="${orderLocal}"/>
            <dsp:param name="mode" param="mode"/>
            <dsp:param name="orderPromotions" param="orderPromotions"/>
            <dsp:param name="shippingGroups" value="${shippingGroups}"/>
            <dsp:param name="shippingGroupsLocal" value="${shippingGroupsLocal}"/>
            <dsp:param name="curCode" value="${currencyCode}"/>
            <dsp:param name="isCCOrder" value="${isCCOrder}"/>
          </dsp:include>

          <div class="boxCartMiddle boxCartBot">
            <table class="boxCartTable ${singleBasketClass}">
              <c:if test="${mode != 'confirmation'}">
                <%@ include file="includes/coupon_and_cast_cart.jsp" %>
              </c:if>
              <tbody class="boxCartTableSummary">
                <tr>
                  <%@ include file="includes/prepareSummary.jspf" %>

                  <dsp:include page="includes/cart_total_td.jsp">
                    <dsp:param name="labelsList" value="${labelsList}"/>
                  </dsp:include>

                  <dsp:droplet name="OrderTotalWeightDroplet">
                    <dsp:param name="order" value="${order}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="webWeight" param="totalWeight"/>
                    </dsp:oparam>
                  </dsp:droplet>

                  <dsp:include page="cartContent_summary.jsp">
                    <dsp:param name="thisOrder" value="${order}"/>
                    <dsp:param name="pricesList" value="${d2hPricesList}"/>
                    <dsp:param name="currencyCode" value="${currencyCode}"/>
                    <dsp:param name="cssStyle" value="${leftClass}"/>
                  </dsp:include>

                  <c:if test="${storeIsCC && mode != 'confirmation'}">
                    <dsp:droplet name="OrderTotalWeightDroplet">
                      <dsp:param name="order" value="${orderLocal}"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="localWeight" param="totalWeight"/>
                      </dsp:oparam>
                    </dsp:droplet>
                  
                    <dsp:include page="cartContent_summary.jsp">
                      <dsp:param name="thisOrder" value="${orderLocal}"/>
                      <dsp:param name="pricesList" value="${ccPricesList}"/>
                      <dsp:param name="currencyCode" value="${currencyCodeLocal}"/>
                      <dsp:param name="cssStyle" value="${rightClass}"/>
                    </dsp:include>
                  </c:if>
                </tr>
              </tbody>
              <tbody class="boxCartTableSummaryInfo">
                <tr>
                  <td><!-- --></td>
                  <td colspan="2">
                      <c:if test="${mode != 'confirmation'}">
                          <div class="agreement">
                              <input id="summAgree" type="checkbox" onclick="setAgreeCheckBox(this);"/>
                              <span class="darkRed">
                                  <fmt:message key="msg.cart.i-accept1"/>
                              </span>
                              <dsp:a href="javascript:void(0);" onclick="showTermsPopup();">
                                  <fmt:message key="msg.cart.i-accept2"/>
                              </dsp:a>
                              <span class="darkRed">
                                  <fmt:message key="msg.cart.i-accept3"/>
                              </span>
                          </div>
                      </c:if>
                  </td>
                </tr>
                <c:if test="${mode != 'confirmation'}">
                  <tr>
                    <td><!-- --></td>
                    <dsp:include page="cartContent_conditions_and_weight.jsp">
                      <dsp:param name="thisOrder" value="${order}"/>
                      <dsp:param name="pTotalWeight" value="${webWeight}"/>
                      <dsp:param name="pDeliveriesCount" value="${cDeliveriesCount}" />
                      <dsp:param name="classType" value="left "/>
                      <dsp:param name="style" value="${leftClass}"/>
                    </dsp:include>
                    <c:if test="${storeIsCC}">
                      <dsp:include page="cartContent_conditions_and_weight.jsp">
                        <dsp:param name="thisOrder" value="${orderLocal}"/>
                        <dsp:param name="pTotalWeight" value="${localWeight}"/>
                        <dsp:param name="classType" value="right "/>
                        <dsp:param name="style" value="${rightClass}"/>
                      </dsp:include>
                    </c:if>
                  </tr>
                </c:if>
              </tbody>
            </table>
            
            <c:if test="${mode != 'confirmation'}">
              <div class="ccButtonCase">
                <div class="ccButtonCaseLeft">
                  <span class="ccBascetButton">
                    <fmt:message var="cart_continue" key="msg.cart.continue" />
                    <dsp:form action="cart.jsp" method="post" formid="cartContinueForm">
                      <dsp:input type="hidden" bean="CastShoppingCartFormHandler.continueURL" beanvalue="CastShoppingCartFormHandler.continueURL" valueishtml="true"/>
                      <dsp:input type="submit" bean="CastShoppingCartFormHandler.continue" iclass="ccBascetButton" value="${cart_continue}" />
                    </dsp:form>
                  </span>
                </div>
                <div class="ccButtonCaseRight">
                  <c:choose>
                    <c:when test="${not empty leftClass}">
                      <dsp:getvalueof var="butActionLeft" value="javascript:submitform();"/>
                      <dsp:getvalueof var="spanClassLeft" value="blue"/>
                      <dsp:getvalueof var="spanClassRight" value="disable"/>
                    </c:when>
                    <c:when test="${not empty rightClass}">
                      <dsp:getvalueof var="butActionRight" value="javascript:submitform();"/>
                      <dsp:getvalueof var="spanClassRight" value="blue"/>
                      <dsp:getvalueof var="spanClassLeft" value="disable"/>
                    </c:when>
                    <c:when test="${empty rightClass && empty leftClass}">
                      <dsp:getvalueof var="butActionRight" value="javascript:void(0);"/>
                      <dsp:getvalueof var="spanClassRight" value="disable"/>
                      <dsp:getvalueof var="spanClassLeft" value="disable"/>
                    </c:when>
                  </c:choose>
                  <span class="ccBascetButton ${spanClassLeft}"><button type="submit" class="ccBascetButton " onclick="${butActionLeft}"><fmt:message key="msg.cart.submit"/></button></span>
                  <c:if test="${storeIsCC}">
                    <span class="ccBascetButton ${spanClassRight}"><button type="submit" class="ccBascetButton " onclick="${butActionRight}"><fmt:message key="msg.cart.submit"/></button></span>
                  </c:if>
                </div>
              </div>
            </c:if>
            
          </div>
          <c:if test="${mode != 'confirmation'}">
            <div class="ccDeliveryInfoWr">
              <div class="ccDeliveryCommerce">
                <dsp:include page="/castCommon/promoInformationTargeter.jsp">
                  <dsp:param name="homePagePromoBean" bean="/atg/registry/Slots/ShoppingCartLeftBottomSlot"/>
                  <dsp:param name="flashId" value="ShoppingCartLeftBottomSlot"/>
                </dsp:include>
              </div>
              <div class="ccDeliveryInfo">
                <div class="deliveryToHome <c:if test='${not storeIsCC}'>only</c:if>">
                  <dsp:getvalueof var="promoInformation" bean="Profile.catalog.shoppingCartD2HBanner"/>
                  <dsp:include page="/castCatalog/includes/catalogPromoTemplates/Shopping cart delivery mode.jsp" flush="true">
                    <dsp:param name="promoInformation" value="${promoInformation}"/>
                  </dsp:include>
                </div>
                <c:if test="${storeIsCC}">
                  <div class="clickAndCollect">
                    <dsp:getvalueof var="promoInformation" bean="Profile.catalog.shoppingCartCCBanner"/>
                    <dsp:include page="/castCatalog/includes/catalogPromoTemplates/Shopping cart delivery mode.jsp" flush="true">
                      <dsp:param name="promoInformation" value="${promoInformation}"/>
                    </dsp:include>
                  </div>
                </c:if>
              </div>
            </div>
          </c:if>
        </div>
      </dsp:oparam>
    </dsp:droplet>

    <div class="clear"></div>
  </div>

</dsp:page>
