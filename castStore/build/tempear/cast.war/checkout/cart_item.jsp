<dsp:page>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
  
  <dsp:param name="commerceItem" param="relationship.commerceItem"/>
  <dsp:param name="commerceItemLocal" param="relationshipLocal.commerceItem"/>
  <dsp:param name="product" param="relationship.commerceItem.auxiliaryData.productRef"/>
  <dsp:param name="sku" param="relationship.commerceItem.auxiliaryData.catalogRef"/>

  <dsp:getvalueof var="rel" param="relationship" />
  <c:if test="${empty rel}">
    <dsp:param name="product" param="relationshipLocal.commerceItem.auxiliaryData.productRef"/>
    <dsp:param name="sku" param="relationshipLocal.commerceItem.auxiliaryData.catalogRef"/>
  </c:if>

  <dsp:getvalueof var="removedItems" param="orderLocal.removedItemsIds"/>
  <dsp:getvalueof var="orderSkuId"  param="commerceItem.catalogRefId"/>
  <dsp:getvalueof var="skuId"       param="sku.repositoryId"/>
  <dsp:getvalueof var="refNumber"   param="sku.CodeArticle"/>
  <dsp:getvalueof var="ImageURL"    param="sku.miniatureImage.url"/>
  <dsp:getvalueof var="productName" param="sku.displayName"/>
  <dsp:getvalueof var="childSku"    param="product.childSKUs"/>
  <dsp:getvalueof var="template"    param="product.template.url"/>
  <dsp:getvalueof var="productId"   param="product.repositoryId"/>
  <dsp:getvalueof var="skuLinks" param="sku.bundleLinks"/>
  <dsp:getvalueof var="size" value="${fn:length(skuLinks)}"/>
  <dsp:getvalueof id="relationshipId" param="relationship.id"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="hasProductsWithDiscount" param="hasProductsWithDiscount"/>
  <dsp:getvalueof var="mode"  param="mode"/>
  <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="" scope="request"/>
  <dsp:getvalueof var="url" param="url" />
  
  <tr>
    <td>
      <dsp:include page="includes/cart_item_info.jsp">
        <dsp:param name="sku" param="sku"/>
        <dsp:param name="product" param="product"/>
        <dsp:param name="itemType" value="shopping"/>
      </dsp:include>
    </td>

    <dsp:getvalueof var="ca" param="sku.codeArticle"/>
    <dsp:getvalueof var="dn" param="sku.displayName"/>
    <dsp:getvalueof var="productCategory" param="product.parentCategory.displayName" />

    <dsp:include page="cart_item_order.jsp" >
      <dsp:param name="currencyCode" param="currencyCode"/>
      <dsp:param name="productId"    value="${productId}"/>
      <dsp:param name="style"        value="${leftClass}"/>
      <dsp:param name="sku"          param="sku"/>
      <dsp:param name="commerceItem" param="commerceItem"/>
      <dsp:param name="relationship" param="relationship"/>
      <dsp:param name="delType"      param="order.deliveryType"/>
      <dsp:param name="mode"         param="mode"/>
    </dsp:include>
    <dsp:getvalueof var="qty" param="commerceItem.quantity"/>
    <dsp:getvalueof var="tagCommanderProductsInfoTemp1" value="${tagCommanderProductsInfoTemp}"/>
    <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="" scope="request"/>


    <c:if test="${storeIsCC && mode != 'confirmation'}">
      <dsp:include page="cart_item_order.jsp" >
        <dsp:param name="currencyCode" param="currencyCode"/>
        <dsp:param name="productId"    value="${productId}"/>
        <dsp:param name="style"        value="${rightClass}"/>
        <dsp:param name="isLocal"      value="${true}"/>
        <dsp:param name="sku"          param="sku"/>
        <dsp:param name="commerceItem" param="commerceItemLocal"/>
        <dsp:param name="relationship" param="relationshipLocal"/>
        <dsp:param name="delType"      param="orderLocal.deliveryType"/>
        <dsp:param name="mode"         param="mode"/>
      </dsp:include>

      <dsp:getvalueof var="qtyLoc" param="commerceItemLocal.quantity"/>
      <dsp:getvalueof var="qtyLocWasDecreased" param="commerceItemLocal.quantityWasDecreased"/>
      <dsp:getvalueof var="currentInventoryValue" param="commerceItemLocal.currentInventoryValue"/>
    </c:if>

    <c:choose>
      <c:when test="${not empty qty && (not empty qtyLoc && qty >= qtyLoc || empty qtyLoc)}">
        <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="'${ca}','${dn}'${tagCommanderProductsInfoTemp1},'${qty}',\"${productCategory}\""/>
        ${castCollection:add(tagCommanderProductsInfo,tagCommanderProductsInfoTemp)}
      </c:when>
      <c:when test="${not empty qtyLoc && (not empty qty && qty < qtyLoc || empty qty)}">
        <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="'${ca}','${dn}'${tagCommanderProductsInfoTemp},'${qtyLoc}',\"${productCategory}\""/>
        ${castCollection:add(tagCommanderProductsInfo,tagCommanderProductsInfoTemp)}
      </c:when>
    </c:choose>

    <c:if test="${mode != 'confirmation'}">
      <td class="remove">
        <dsp:getvalueof var="itemId1" param="commerceItem.id"/>
        <dsp:getvalueof var="itemId2" param="commerceItemLocal.id"/>
        <dsp:getvalueof var="itemIds" value="${itemId1},${itemId2}"/>
        <dsp:input type="hidden" bean="CastShoppingCartFormHandler.removeItemFromOrderSuccessURL" value="${url}" />
        <dsp:input type="hidden" bean="CastShoppingCartFormHandler.removeItemFromOrderErrorURL" value="${url}" />
        <dsp:input type="hidden" bean="CastShoppingCartFormHandler.continueURL" beanvalue="CastShoppingCartFormHandler.continueURL" valueishtml="true"/>
        <dsp:input bean="CastShoppingCartFormHandler.deleteItem" type="submit" onclick="subVar = true;" iclass="icoTrash" value="${itemIds} "/>

      </td>
    </c:if>
  </tr>
  <c:if test="${mode != 'confirmation'}">
  <c:if test="${qtyLocWasDecreased || castCollection:contains(removedItems, orderSkuId)}">
    <tr class="invalidQuantite">
      <td></td>
      <td></td>
      <td class="ccEnableBasket">
        <div class="boxCartInnerWr">
          <div class="boxCartPromotion">
            <div class="boxCartPromotionImg">
              <img title="" alt="" src="/images/icoInvalidQuantite.gif">
            </div>
            <p><fmt:message key="cc.cart.invalid.quantite"/><span class="value">
			<c:choose>
				<c:when test="${castCollection:contains(removedItems, orderSkuId)}">
				${castCollection:remove(removedItems, orderSkuId)}
				0
				</c:when>
				<c:otherwise>
				${currentInventoryValue}
				</c:otherwise>
			</c:choose>
			</span></p>
          </div>
        </div>
      </td>
      <td class="remove"></td>
    </tr>
  </c:if>
  </c:if>
    
  <c:if test="${mode != 'confirmation'}">
    <dsp:include page="includes/closenessQualifiers.jsp">
      <dsp:param name="commerceItem" param="commerceItem"/>
    </dsp:include>
    <tr class="productItemPromotion">
      <td><!-- --></td>
      <dsp:include page="includes/itemPromotions.jsp">
        <dsp:param name="commerceItem" param="commerceItem"/>
        <dsp:param name="style"        value="${leftClass}"/>
      </dsp:include>
      <c:if test="${storeIsCC && mode != 'confirmation'}">
        <dsp:include page="includes/itemPromotions.jsp">
          <dsp:param name="commerceItem" param="commerceItemLocal"/>
          <dsp:param name="style"        value="${rightClass}"/>
        </dsp:include>
      </c:if>
      <td class="remove"><!-- --></td>
    </tr>
  </c:if>

</dsp:page>
