<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />
  <dsp:importbean bean="/com/castorama/droplet/CastShippingDroplet"/>

  <c:set var="cDeliveriesCount" value=""/>
  <c:set var="cTotalWeight" value=""/>

  <dsp:droplet name="CastShippingDroplet">
    <c:choose>
      <c:when test="${mode == 'confirmation'}">
        <dsp:param bean="/atg/commerce/ShoppingCart.last.ShippingGroups" name="shippingGroups"/>
      </c:when>
      <c:otherwise>
         <dsp:param bean="/atg/commerce/ShoppingCart.currentlySelected.ShippingGroups" name="shippingGroups"/>
      </c:otherwise>
    </c:choose>

    <dsp:oparam name="output">
      <dsp:getvalueof var="expeditionPNS" param="expeditionPNS"/>
      <dsp:getvalueof var="expeditionsLDF" param="expeditionsLDF"/>
      <c:set var="expeditionPNS" value="${expeditionPNS}"/>
      <c:set var="expeditionsLDF" value="${expeditionsLDF}"/>
      <dsp:getvalueof var="deliveriesCount" param="deliveries"/>
      <dsp:getvalueof var="totalWeight" param="totalWeight"/>
      <c:set var="cDeliveriesCount" value="${deliveriesCount}"/>
      <c:set var="cTotalWeight" value="${totalWeight}"/>

      <div class="whitePopupContainer" id="enSavoirPlus">
        <div class="whitePopupContent popupFormContainer">
          <div class="whitePopupHeader">
            <h1>
              <c:if test="${deliveriesCount > 1}">
                <fmt:message key="cc.delivery.popup2">
                  <fmt:param>
                    <dsp:valueof param="deliveries" />
                  </fmt:param>
                </fmt:message>
              </c:if>
              <c:if test="${deliveriesCount == 1}">
                <fmt:message key="cc.delivery.popup1">
                  <fmt:param>
                    <fmt:formatNumber type="number" >
                      <dsp:valueof param="deliveries" />
                    </fmt:formatNumber>
                  </fmt:param>
                </fmt:message>
              </c:if>
            </h1>
            <a href="javascript:void(0)" onclick="hidePopup(this)" class="closeBut" title="<fmt:message key="castCatalog_label.close"/>">
              <span><!--~--></span>
              <fmt:message key="castCatalog_label.close"/>
            </a>
          </div>
          <div class="clear"><!--~--></div>

          <div class="popupContentContainer">
            <!--  PNS DELIVERY  -->
            <dsp:droplet name="IsNull">
              <dsp:param name="value" param="expeditionPNS" />
              <dsp:oparam name="false">
                <dsp:tomap var="PNS" param="expeditionPNS" />
                  <div class="padded"></div>
                  <div class="borderBlock2">
                    <h1>
                      <fmt:message key="cc.delivery.time.1">
                        <fmt:param>
                          <dsp:valueof param="expeditionPNS.deliveryTime" valueishtml="true" />
                        </fmt:param>
                      </fmt:message>
                    </h1>
                    <div class="padded">
                      <table cellpadding="0" cellspacing="0" class="productsTable darkTable">
                        <tr>
                          <th class="alignLeft width179px">
                            <fmt:message key="client.order.returns.code"/>
                          </th>
                          <th class="alignLeft"><fmt:message key="cc.delivery.time.intitule"/></th>
                        </tr>
                        <dsp:droplet name="ForEach">
                          <dsp:param name="array" value="${PNS.deliveryItems}" />
                          <dsp:param name="elementName" value="sku"/>
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="skuLinks" param="sku.bundleLinks"/>
                            <dsp:getvalueof var="size" value="${fn:length(skuLinks)}"/>
                            <tr>
                              <td><div class="productItemDescription"><dsp:valueof param="sku.CodeArticle" /></div></td>
                              <td>
                                <div class="productItemDescription">
                                  <dsp:getvalueof var="productName" param="sku.displayName" />
                                  <%@ include file="productName.jspf" %>
                                </div>
                              </td>
                            </tr>
                          </dsp:oparam>
                        </dsp:droplet>
                      </table>
                    </div>
                  </div>
              </dsp:oparam>
            </dsp:droplet>

          <!--  LDF DELIVERY  -->  
          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="expeditionsLDF" />
            <dsp:oparam name="output">
              <dsp:tomap var="LDF" param="element" />
              <div class="padded"></div>
              <div class="borderBlock2">
                <h1>
                  <strong>
                    <fmt:message key="cc.delivery.time.label2">
                      <fmt:param>
                        <dsp:valueof param="element.deliveryTime" valueishtml="true" />
                      </fmt:param>
                    </fmt:message>
                  </strong>
                </h1>
                <div class="padded">
                  <table cellpadding="0" cellspacing="0" class="productsTable darkTable">
                    <tr>
                      <th class="alignLeft  width179px"><fmt:message key="client.order.returns.code"/></th>
                      <th class="alignLeft"><fmt:message key="cc.delivery.time.intitule"/></th>
                    </tr>
                    <dsp:droplet name="ForEach">
                      <dsp:param name="array" value="${LDF.deliveryItems}" />
                      <dsp:param name="elementName" value="sku"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="skuLinks" param="sku.bundleLinks"/>
                        <dsp:getvalueof var="size" value="${fn:length(skuLinks)}"/>
                        <tr>
                          <td><div class="productItemDescription"><dsp:valueof param="sku.CodeArticle" /></div></td>
                          <td>
                            <div class="productItemDescription">
                              <dsp:getvalueof var="productName" param="sku.displayName" />
                              <%@ include file="productName.jspf" %>
                            </div>
                          </td>
                        </tr>  
                      </dsp:oparam>
                    </dsp:droplet>
                  </table>
                </div>
              </div>
            </dsp:oparam>
          </dsp:droplet>

          <div class="popupForm">
            <div class="clear"><!--~--></div>
          </div>
        </div>
      </div>
    </div>

  </dsp:oparam>
  </dsp:droplet>

</dsp:page>