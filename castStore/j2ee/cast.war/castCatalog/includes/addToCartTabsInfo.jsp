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
    <dsp:param name="sku" param="skuParam"/>
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
        <c:if test="${empty listPrice}">          
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
        </c:if>
        <%@ include file="/castCatalog/includes/addToCartTabPackInfo.jspf" %>
    </dsp:oparam>
  </dsp:droplet>
  
</dsp:page>