<dsp:page>
    <dsp:importbean bean="/atg/dynamo/service/CurrentDate"/>
    <dsp:importbean bean="/com/castorama/droplet/CastPriceItem"/>
    <dsp:importbean bean="/com/castorama/droplet/CastPriceDroplet"/>
    <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
    <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
    
    <dsp:getvalueof var="name" param="sku.displayName"/>
    <dsp:getvalueof var="comparatorImage" param="sku.comparatorImage.url"/>
    <dsp:getvalueof var="categoryId" param="product.parentCategory.repositoryId"/>
    <dsp:getvalueof var="productId" param="product.repositoryId"/>
    <dsp:getvalueof var="productPromoDescription" param="product.productPromoDescription"/>
    <dsp:getvalueof var="productBenefit" param="product.productBenefit"/>
    <dsp:getvalueof var="promoFontColor" param="product.promoFontColor"/>
    <dsp:getvalueof var="promoBackgroundColor" param="product.promoBackgroundColor"/>
    <dsp:getvalueof var="promoStartDate" param="product.promoStartDate"/>
    <dsp:getvalueof var="promoExpirationDate" param="product.promoExpirationDate"/>
    <dsp:getvalueof var="date" bean="CurrentDate"/>
    <dsp:getvalueof var="view" value="galeryView"/>
    <dsp:getvalueof var="productPromoColor" param="product.parentCategory.style.productPromoStyle"/>
	<dsp:getvalueof var="skuId" param="sku.id"/>
    
    <dsp:getvalueof var="isCC" bean="Profile.currentLocalStore.retraitMagasin"/>
    <dsp:getvalueof var="localPrices" bean="Profile.currentLocalStore.localPrix"/>  
    <dsp:droplet name="CastPriceItem">
      <dsp:param name="elementName" value="pricedItem"/>
      <dsp:param name="item" param="sku"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="onSale" param="pricedItem.priceInfo.onSale"/>
        <dsp:getvalueof var="localPriceApplied" param="pricedItem.priceInfo.localPriceApplied"/>
      </dsp:oparam>
    </dsp:droplet>

    <div class="hasHighlight">
        <div class="productImageHolder">
            <c:choose>
                <c:when test="${not empty  comparatorImage}">
                    <img title="${name}" alt="${name}" src="${comparatorImage}"/>
                </c:when>
                <c:otherwise>
                  <img title="${name}" alt="${name}" src="/default_images/e_no_img.jpg"/>
                </c:otherwise>
            </c:choose>
			<fmt:message key="castCatalog_camparateur.remove" var="removeLabel"/>
			<a class="campRemove" title="${removeLabel}" href="javascript:void(0)" id="popup_${skuId}">${removeLabel}</a>
              <c:choose>
                <c:when test="${onSale and localPriceApplied and (isCC or localPrices)}">
                  <dsp:getvalueof var="productBenefit" value="PROMOTION"/>
                  <dsp:getvalueof var="productPromoColor" value="redH"/>
                  <%@ include file="/castCatalog/includes/productBenefit.jspf" %>
                </c:when>
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
            
              <dsp:droplet name="CastPriceItem">
                <dsp:param name="item" param="sku"/>
                <dsp:param name="elementName" value="pricedSku"/>
                <dsp:oparam name="output">
                  <dsp:param name="pricedItem" param="pricedSku"/>
                  <dsp:getvalueof var="listPrice" param="pricedItem.priceInfo.listPrice"/>
                  <dsp:getvalueof var="salePrice" param="pricedItem.priceInfo.amount"/>
                  <dsp:getvalueof var="currencyCode" vartype="java.lang.String" param="pricedItem.priceInfo.currencyCode"/>  
                  <dsp:droplet name="CastPriceDroplet">
                    <dsp:param name="listPrice" value="${listPrice}"/>
                    <dsp:param name="salePrice" value="${salePrice}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="percent" param="percent"/>
                      <dsp:getvalueof var="onDiscount" param="onDiscount"/>
                      <dsp:getvalueof var="onSaleParam" param="pricedItem.priceInfo.onSale"/>
                      <dsp:getvalueof var="onSaleDiscountDisplay" param="pricedItem.priceInfo.onSaleDiscountDisplay"/>
                        <c:if test="${onDiscount && ((not empty onSaleDiscountDisplay && onSaleDiscountDisplay) || (not empty onSaleParam && not onSaleParam))}">
                          <div class="relative">
                            <div class="discount"><dsp:valueof value="${percent}"/></div>
                          </div>
                        </c:if>
                        
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:oparam>
              </dsp:droplet>
        </div>
    </div>
</dsp:page>