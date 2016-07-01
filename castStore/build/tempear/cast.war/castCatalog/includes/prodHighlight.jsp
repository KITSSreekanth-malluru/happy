<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/dynamo/service/CurrentDate"/>
  <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceItem"/>
  
  <dsp:getvalueof var="productPromoDescription" param="product.productPromoDescription"/>
  <dsp:getvalueof var="productBenefit" param="product.productBenefit"/>
  <dsp:getvalueof var="promoFontColor" param="product.promoFontColor"/>
  <c:if test="${empty fn:trim(promoFontColor)}">
    <dsp:getvalueof var="promoFontColor" value="#FFF"/>
  </c:if>
  <dsp:getvalueof var="promoBackgroundColor" param="product.promoBackgroundColor"/>
  <dsp:getvalueof var="promoStartDate" param="product.promoStartDate"/>
  <dsp:getvalueof var="promoExpirationDate" param="product.promoExpirationDate"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="date" bean="CurrentDate"/>
  <dsp:getvalueof var="view" param="view"/>
  <dsp:getvalueof var="noBreadcrumbs" param="noBreadcrumbs"/>
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
  <dsp:getvalueof var="printVersion" param="printVersion"/>
  <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>

  <dsp:getvalueof var="isCC" bean="Profile.currentLocalStore.retraitMagasin"/>
  <dsp:getvalueof var="localPrices" bean="Profile.currentLocalStore.localPrix"/>  
  <dsp:getvalueof var="currentSkuId" param="skuId"/>
  <dsp:getvalueof var="viewProduct" param="viewProduct"/>
  <dsp:getvalueof var="childSkus" param="product.childSKUs"/>
  <dsp:getvalueof var="multiSku" value="${fn:length(childSkus)>1}"/>
  <c:choose>
    <c:when test="${(empty currentSkuId or viewProduct) and multiSku}">
      <%-- multi sku product: display PROMOTION if onSale on notCheapestSku --%>
      <dsp:droplet name="CastPriceRangeDroplet">
        <dsp:param name="productId" param="product.id"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="cheapestSkuId" param="sku.id"/>
        </dsp:oparam>
      </dsp:droplet>
      
      <dsp:droplet name="ForEach">
        <dsp:param name="elementName" value="sku"/>
        <dsp:param name="array" param="product.childSKUs"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="currentSkuId" param="sku.id"/>
          <c:if test="${cheapestSkuId != currentSkuId}">
            <dsp:droplet name="CastPriceItem">
              <dsp:param name="elementName" value="pricedItem"/>
              <dsp:param name="item" param="sku"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="onSaleParam" param="pricedItem.priceInfo.onSale"/>
                <dsp:getvalueof var="localPriceAppliedParam" param="pricedItem.priceInfo.localPriceApplied"/>
              </dsp:oparam>
            </dsp:droplet>
            <dsp:getvalueof var="onSale" value="${onSale || onSaleParam}"/>
            <dsp:getvalueof var="localPriceApplied" value="${localPriceApplied || localPriceAppliedParam}"/>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
    </c:when>
    <c:otherwise>
      <%-- sku or single sku product: display PROMOTION if onSale on current sku --%>
      <dsp:droplet name="CastPriceItem">
        <dsp:param name="elementName" value="pricedItem"/>
        <dsp:param name="item" param="sku"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="onSale" param="pricedItem.priceInfo.onSale"/>
          <dsp:getvalueof var="localPriceApplied" param="pricedItem.priceInfo.localPriceApplied"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:otherwise>
  </c:choose>
 
  <c:choose>
    <c:when test="${noBreadcrumbs || isSearchResult}">
      <dsp:getvalueof var="productPromoColor" param="product.parentCategory.style.productPromoStyle"/>
    </c:when>
    <c:otherwise>
      <dsp:droplet name="StyleLookupDroplet">
        <dsp:param name="categoryId" value="${categoryId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="productPromoColor" param="style.productPromoStyle"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:otherwise>
  </c:choose>

<c:choose>
  <c:when test="${not empty notCheapestSkuPromo and notCheapestSkuPromo}">
    <dsp:getvalueof var="productBenefit" value="PROMOTION"/>
    <dsp:getvalueof var="productPromoColor" value="redH"/>
    <%@ include file="/castCatalog/includes/productBenefit.jspf" %>
  </c:when>
  <c:when test="${onSale and localPriceApplied and (isCC or localPrices)}">
    <dsp:getvalueof var="productBenefit" value="PROMOTION"/>
    <dsp:getvalueof var="productPromoColor" value="redH"/>
    <%@ include file="/castCatalog/includes/productBenefit.jspf" %>
  </c:when>
  <c:otherwise>
  <c:choose>
    <c:when test="${not empty productPromoDescription}">
      <c:choose>
        <c:when test="${not empty promoStartDate && not empty promoExpirationDate}">
          <c:choose>
            <c:when test="${promoStartDate<=date.timeAsTimestamp && date.timeAsTimestamp<=promoExpirationDate}">
              <%@ include file="/castCatalog/includes/productPromoDescription.jspf" %>
            </c:when>
            <c:otherwise>
              <dsp:getvalueof var="productPromoColor" value="lightblueH"/>
              <%@ include file="/castCatalog/includes/productBenefit.jspf" %>
            </c:otherwise>
          </c:choose>
        </c:when>
        <c:when test="${not empty promoStartDate && empty promoExpirationDate}">
          <c:choose>
            <c:when test="${promoStartDate<=date.timeAsTimestamp}">
              <%@ include file="/castCatalog/includes/productPromoDescription.jspf" %>
            </c:when>
            <c:otherwise>
              <dsp:getvalueof var="productPromoColor" value="lightblueH"/>
              <%@ include file="/castCatalog/includes/productBenefit.jspf" %>
            </c:otherwise>
          </c:choose>
        </c:when>
        <c:when test="${empty promoStartDate && not empty promoExpirationDate}">
          <c:choose>
            <c:when test="${date.timeAsTimestamp<=promoExpirationDate}">
              <%@ include file="/castCatalog/includes/productPromoDescription.jspf" %>
            </c:when>
            <c:otherwise>
              <dsp:getvalueof var="productPromoColor" value="lightblueH"/>
              <%@ include file="/castCatalog/includes/productBenefit.jspf" %>
            </c:otherwise>
          </c:choose>
        </c:when>
        <c:otherwise>
          <%@ include file="/castCatalog/includes/productPromoDescription.jspf" %>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <dsp:getvalueof var="productPromoColor" value="lightblueH"/>
      <%@ include file="/castCatalog/includes/productBenefit.jspf" %>
    </c:otherwise>
  </c:choose>
  </c:otherwise>
</c:choose>
</dsp:page>