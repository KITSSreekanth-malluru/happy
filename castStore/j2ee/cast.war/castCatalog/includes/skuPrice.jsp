<dsp:page>

  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/com/castorama/droplet/CastPriceItem"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceDroplet"/>

  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="singleSkuId" param="skuId"/>
  <dsp:getvalueof var="skuId" param="skuId"/>
  <c:if test="${empty skuId}">
    <dsp:getvalueof var="skuId" param="sku.id"/>
  </c:if>
  <dsp:getvalueof var="featuredSkuId" param="featuredSkuId"/>
  <dsp:getvalueof var="featuredProduct" param="featuredProduct"/>
  <dsp:getvalueof var="multiSku" param="multiSku"/>
  <dsp:getvalueof var="pageType" param="pageType"/>
  <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo" />
  <dsp:getvalueof var="printVersion" param="printVersion" />
  <dsp:getvalueof var="displayVUMMessage" param="displayVUMMessage" />
  <dsp:getvalueof var="displayVUMMessageStyle" param="displayVUMMessageStyle"/>
  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:param name="elementName" value="prod"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="childSku" param="prod.childSKUs"/>
      <dsp:getvalueof var="multiSku" value="${fn:length(childSku)>1}"/>

      <c:choose>
        <c:when test="${fn:length(childSku)==1 || (not empty singleSkuId && multiSku) || (pageType == 'packPage')}">
          <dsp:getvalueof var="sku" param="sku"/>
        </c:when>
        <c:otherwise>
          <c:choose>
            <c:when test="${featuredProduct}">
              <c:choose>
                <c:when test="${not empty featuredSkuId}">
                  <dsp:getvalueof var="sku" param="sku"/>
                </c:when>
                <c:otherwise>
                  <%@ include file="/castCatalog/includes/cheapestSku.jspf" %>
                </c:otherwise>
              </c:choose>
            </c:when>
            <c:otherwise>
              <%@ include file="/castCatalog/includes/cheapestSku.jspf" %>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
      <dsp:getvalueof var="productType" param="prod.type"/>
      <c:if test="${productType=='casto-grouped-product'}">
        <dsp:getvalueof var="implantaMessage" param="prod.implantaMessage"/>
      </c:if>
      <dsp:getvalueof var="isEmpty" value="false"/>
      <dsp:droplet name="CastPriceItem">
        <dsp:param name="item" value="${sku}"/>
        <dsp:param name="elementName" value="pricedSku"/>
        <dsp:oparam name="empty">
          <dsp:getvalueof var="isEmpty" value="true"/>
       
          <div class="price">
            <span class="wraped priceNotAvailable">
              <fmt:message key="castCatalog_productTemplate.price_not_available"/>
            </span>
          </div>
       
        </dsp:oparam>
        <dsp:oparam name="output">
          <%@ include file="/castCatalog/includes/price.jspf" %>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
