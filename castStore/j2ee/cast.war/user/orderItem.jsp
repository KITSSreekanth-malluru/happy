<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <dsp:getvalueof var="skuId"        param="commerceItem.catalogRefId" />
  <dsp:getvalueof var="productId"    param="commerceItem.productId" />
  <dsp:getvalueof var="currencyCode" param="currencyCode"/>
  <dsp:getvalueof var="listPrice"    param="commerceItem.priceInfo.listPrice"/>
  <dsp:getvalueof var="itemAmount"   param="commerceItem.priceInfo.amount"/>
  <dsp:getvalueof var="itemQuantity" param="commerceItem.quantity"/>
  <dsp:getvalueof var="salePrice"    value="${itemAmount/itemQuantity}"/>
  <dsp:getvalueof var="adjustments"  param="commerceItem.priceInfo.adjustments"/>
    <dsp:getvalueof var="onSaleParam"  param="commerceItem.priceInfo.onSale"/>
    <dsp:getvalueof var="onSaleDiscountDisplay" param="commerceItem.priceInfo.onSaleDiscountDisplay"/>
    <dsp:getvalueof var="orderDeliveryType" param="orderDeliveryType"/>
  <dsp:droplet name="/atg/commerce/catalog/SKULookup">
    <dsp:param name="id" value="${skuId}" />
    <dsp:param name="elementName" value="sku" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="sku"         param="sku" />
    <dsp:getvalueof var="skuLinks"    param="sku.bundleLinks" />
      <dsp:getvalueof var="refNumber"   param="sku.CodeArticle" />
      <dsp:getvalueof var="ImageURL"    param="sku.miniatureImage.url" />
      <dsp:getvalueof var="productName" param="sku.displayName" />

      <dsp:getvalueof var="size" value="${fn:length(skuLinks)}"/>
      <c:if test="${not empty size && size > 0}">
        <dsp:getvalueof var="ImageURL" param="sku.bundleLinks[0].item.miniatureImage.url" />
        <c:set var="descriptionTmp" value="" />
        <dsp:droplet name="ForEach">
          <dsp:param name="array" param="sku.bundleLinks"/>
          <dsp:param name="elementName" value="skuLink"/>
          <dsp:getvalueof var="count" param="count"/>    
          <dsp:oparam name="output">
            <dsp:getvalueof var="quantity" param="skuLink.quantity"/>
            <dsp:getvalueof var="displayName" param="skuLink.item.displayName"/>
            <c:if test="${quantity > 1}"><c:set var="descriptionTmp" value="${descriptionTmp} ${quantity}"/></c:if> 
            <c:set var="descriptionTmp" value="${descriptionTmp} ${displayName}"/>
            <c:if test="${size != count}"><c:set var="descriptionTmp" value="${descriptionTmp} + "/></c:if>
          </dsp:oparam>
        </dsp:droplet>
        <dsp:droplet name="/com/castorama/droplet/DocumentDescriptionBraker">
          <dsp:param name="description" value="${descriptionTmp}"/>
          <dsp:param name="length" value="70"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="productName" param="updatedDescription" />
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>

  <c:if test="${null == productName}">
    <dsp:getvalueof var="productName" param="commerceItem.displayName"/>
  </c:if>

  <dsp:droplet name="/com/castorama/droplet/ProductNameDroplet">
    <dsp:param name="url" value="${ImageURL}" />
    <dsp:param name="name" value="${productName}" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="ImageURL"    param="outURL"/>
      <dsp:getvalueof var="productName" param="outName"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <c:set var="itemDiscount" value="0"/>
  <dsp:droplet name="ForEach">
    <dsp:param name="array" param="commerceItem.priceInfo.adjustments"/>
    <dsp:param name="elementName" value="adjustment"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="totalAdjustment" param="adjustment.TotalAdjustment"/>
      <dsp:getvalueof var="adjustmentDescription" param="adjustment.adjustmentDescription"/>
      <c:if test="${totalAdjustment < 0 && ((not empty onSaleDiscountDisplay && onSaleDiscountDisplay) || (not empty onSaleParam && not onSaleParam))}">
        <c:set var="itemDiscount" value="${itemDiscount + totalAdjustment}"/>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>
  <c:set var="itemDiscount" value="${fn:replace(itemDiscount, '-', '')}" />
  

  <tr>
    <td class="orderHistoryProdectItem">
      <div class="productItemImage">
        <dsp:img src="${ImageURL}" alt="" title="${productName}" height="43" width="43" />
      </div>  
      <div class="productItemDescription">
        <h3>${productName}</h3>
        <div class="refNum">
          <c:if test="${null != refNumber}" >
            R&eacute;f&nbsp; <c:out value="${refNumber}" />
          </c:if>
        </div>
      </div>
    </td>
    <td class="noBorderRight">
      <table class="boxCartTitle innerBoxCartTitle">
        <tbody>
          <tr>

            <c:if test="${orderDeliveryType == 'deliveryToHome'}">
              <td>
                <span><fmt:message key="msg.cart.delivery_at"/></span> 
                <dsp:droplet name="/com/castorama/droplet/DeliveryPeriodDroplet">
                  <dsp:param name="elementName" value="productDelivery" />
                  <dsp:param name="commerceItem" param="commerceItem" />
                  <dsp:oparam name="output">
                    <dsp:valueof param="productDelivery" valueishtml="true"/>
                  </dsp:oparam>
                </dsp:droplet>
              </td>
            </c:if>
            <td>
              ${itemQuantity}
            </td>
            <c:choose>
              <c:when test="${null == salePrice || 0 == salePrice || listPrice == salePrice}">
                <td>
                  <div class="price">
                    <dsp:valueof value="${listPrice}" converter="euro" symbol="&euro;" locale="fr_FR" />
                  </div>
                </td>
              </c:when>
              <c:when test="${null == listPrice || 0 == listPrice || onSaleParam && (empty onSaleDiscountDisplay || not onSaleDiscountDisplay)}">
                <td>
                  <div class="price">
                    <dsp:valueof value="${salePrice}" converter="euro" symbol="&euro;" locale="fr_FR" />
                  </div>
                </td>
              </c:when>
              <c:otherwise>
                <td>
                  <div class="oldprice">
                    <dsp:valueof value="${listPrice}" converter="euro" symbol="&euro;" locale="fr_FR" />
                  </div>
                  <div class="newprice">
                    <dsp:valueof value="${salePrice}" converter="euro" symbol="&euro;" locale="fr_FR" />
                  </div>
                </td>
              </c:otherwise>
            </c:choose>
            <td class="innerSous-total">
              <div class="newprice">
                <fmt:formatNumber value="${itemAmount}" type="currency" currencyCode="${currencyCode}"/>
              </div>
            </td>

          </tr>
        </tbody>
      </table>
    </td>
  </tr>

    <tr class="productItemPromotion">
        <dsp:include page="orderItemPromotions.jsp">
            <dsp:param name="commerceItemObject" param="commerceItemObject"/>
        </dsp:include>
    </tr>
  
</dsp:page>