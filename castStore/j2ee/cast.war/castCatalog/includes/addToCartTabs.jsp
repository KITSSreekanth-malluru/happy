<dsp:page>

  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/com/castorama/stockvisualization/StockVisualizationDroplet" />
  <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/castorama/stockvisualization/StockModelHolder" />

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />
  <dsp:getvalueof var="childSku" param="childSku"/>
  <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
  <dsp:getvalueof var="codeArticle" param="sku.codeArticle"/>
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="pack" param="pack"/>
  <dsp:getvalueof var="skuParam" param="skuParam" />
  <dsp:getvalueof var="templateUrl" param="templateUrl" />

  <dsp:droplet name="PriceDroplet">
    <c:choose>
      <c:when test="${pack}">
        <dsp:param name="sku" param="skuParam"/>
      </c:when>
      <c:otherwise>
        <dsp:param name="sku" param="sku"/>
      </c:otherwise>
    </c:choose>
    <dsp:oparam name="output">
      <dsp:getvalueof var="listPrice" param="price.listPrice"/>
    </dsp:oparam> 
  </dsp:droplet>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" value="${productId}"/>
    <dsp:param name="elementName" value="prod" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="productType" param="prod.type"/>
      <dsp:param name="element" param="prod"/>
      <dsp:getvalueof var="pageType" param="pageType" />
      <c:choose>
        <c:when test="${empty listPrice}">
          <c:choose>
            <c:when test="${pageType == 'documentPage'}">
              <div class="fPrix documentPagePriceBlock storeIsCCtrue priceNotAvailable">
                <div class="productPrix">
                  <fmt:message key="castCatalog_productTemplate.price_not_available"/>
                </div>
                <div class="blockLastCell">
                  <div class="minHeightWrap"></div>
                </div>
              </div>
            </c:when>
            <c:otherwise>
              <div class="fPrix productPageTabsPriceBlock">
                <div class="numbAndPriceV2"></div>
                <div class="blockLastCell">
                  <div class="minHeightWrap">
                    <div class="priceNotAvailable">
                      <fmt:message key="castCatalog_productTemplate.price_not_available"/>
                    </div>
                  </div>
                </div>
              </div>
            </c:otherwise>
          </c:choose>
        </c:when>
        <c:when test="${pack}">
          <%@ include file="/castCatalog/includes/addToCartTabPack.jspf" %>
        </c:when>
        <c:otherwise>
          <dsp:getvalueof var="localStore" bean="Profile.currentLocalStore"/>
          <dsp:getvalueof var="localStoreId" bean="Profile.currentLocalStore.id"/>
          <c:if test="${(not empty localStore) and (localStoreId != '999') and fn:contains(requestURI, 'ocumentTemplate.jsp')}">
            <dsp:droplet name="StockVisualizationDroplet">
              <dsp:param name="queryCondition" value="byDefinedStore" />
              <dsp:param name="prodId" value="${codeArticle}" />
              <dsp:param name="magasinId" value="${localStoreId}" />
            </dsp:droplet>
          </c:if>
          <%@ include file="/castCatalog/includes/addToCartArea.jspf" %>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
  
</dsp:page>