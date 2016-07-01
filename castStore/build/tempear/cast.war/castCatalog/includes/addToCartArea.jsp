<dsp:page>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup" />
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/castorama/stockvisualization/StockVisualizationDroplet" />
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />
  <dsp:getvalueof var="childSku" param="childSku"/>
  <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
  <dsp:getvalueof var="codeArticle" param="sku.codeArticle"/>
  <dsp:getvalueof var="featuredSkuId" param="featuredSkuId"/>
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="featuredProduct" param="featuredProduct"/>
  <dsp:getvalueof var="pageType" param="pageType"/>
  <dsp:getvalueof var="multiSku" param="multiSku"/>
  <dsp:getvalueof var="categoryId" param="categoryId" />
  <dsp:getvalueof var="featuredSkuId" param="featuredSkuId" />
  <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo" />
  <dsp:getvalueof var="asJS" param="asJS"/>
  <dsp:getvalueof var="divId" param="divId"/>

  <dsp:droplet name="PriceDroplet">
    <dsp:param name="sku" param="sku"/> 
    <dsp:oparam name="output">
      <dsp:getvalueof var="listPrice" param="price.listPrice"/> 
    </dsp:oparam> 
  </dsp:droplet>
  
  <c:choose>
    <c:when test="${not empty listPrice}"><%-- begin of not empty list price--%>
      <dsp:droplet name="ProductLookup">
        <dsp:param name="id" param="productId"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="localStore" bean="Profile.currentLocalStore"/>
          <dsp:getvalueof var="localStoreId" bean="Profile.currentLocalStore.id"/>
          <c:if test="${(not empty localStore) and (localStoreId != '999')}">
            <dsp:droplet name="StockVisualizationDroplet">
              <dsp:param name="queryCondition" value="byDefinedStore" />
              <dsp:param name="prodId" value="${codeArticle}" />
              <dsp:param name="magasinId" value="${localStoreId}" />
            </dsp:droplet>
          </c:if>
          <%@ include file="/castCatalog/includes/addToCartArea.jspf" %>
        </dsp:oparam>
      </dsp:droplet>
    </c:when><%-- end of not empty list price--%>
    <c:otherwise>
      <c:choose>
        <c:when test="${featuredProduct}">
          <div class="fPrix featuredProductPriceBlock showAddToCartButtonfalse">
            <div class="featuredProductWrap priceNotAvailable">
              <span class="fPrixDesc">
                <fmt:message key="castCatalog_productTemplate.price_not_available"/>
              </span>
              
              <c:if test="${fn:length(childSku)>1}"> 
                <dsp:include page="skuDropDown.jsp" flush="true">
                  <dsp:param name="productId" value="${productId}"/>
                  <dsp:param name="featuredProduct" value="${featuredProduct}"/>
                  <dsp:param name="isMultiSku" value="true"/>
                </dsp:include>
              </c:if>
            </div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="fPrix productDetailsPriceBlock showAddToCartButtonfalse">
            <span class="fPrixDesc">
              <fmt:message key="castCatalog_productTemplate.price_not_available"/>
            </span>
          </div>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
</dsp:page>