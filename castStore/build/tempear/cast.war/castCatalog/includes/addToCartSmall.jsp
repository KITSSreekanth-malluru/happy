<dsp:page>

  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup" />
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />
  <dsp:getvalueof var="childSku" param="childSku"/>
  <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="url" param="url"/>
  <dsp:getvalueof var="showDeliveryMsg" param="showDeliveryMsg"/>
  <dsp:getvalueof var="multiDeliveryMsg" param="multiDeliveryMsg"/>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" value="${productId}" />
    <dsp:param name="elementName" value="prod" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="productType" param="prod.type" />
      <dsp:getvalueof var="product" param="prod" />
      <dsp:getvalueof var="prodIdS" param="prod.repositoryId"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="PriceDroplet">
    <dsp:param name="product" value="${product}"/> 
    <dsp:oparam name="output">
      <dsp:getvalueof var="listPrice" param="price.listPrice"/> 
    </dsp:oparam> 
  </dsp:droplet>

  <c:choose>
    <c:when test="${not empty listPrice}"><%-- begin of not empty list price--%>

      <c:if test="${fn:length(childSku)>1}">
        <dsp:getvalueof var="showUnavailableMessage" value="${false}"/>
        <dsp:getvalueof var="showButtonForm" value="${true}"/>


        <dsp:droplet name="/com/castorama/droplet/StockAvailabilityDroplet">
          <dsp:param name="product" value="${product}" />
          <dsp:param name="store" bean="Profile.currentLocalStore" />
          <dsp:param name="svAvailableMap" value="${svAvailableMap}" />
          <dsp:param name="isMultiSkuProduct" value="true" />
          <dsp:param name="isModifiedVersion" value="true" />
          <dsp:oparam name="immidiateWithdrawal"><!-- SAS ${prodIdS} IW -->
            <dsp:getvalueof var="showUnavailableMessage" value="${true}"/>
            <dsp:getvalueof var="showButtonForm" value="${false}"/>
          </dsp:oparam>
          <dsp:oparam name="remainingStock"><!-- SAS ${prodIdS} RS -->
          </dsp:oparam>
          <dsp:oparam name="soldOnlyInStore"><!-- SAS ${prodIdS} SOIS -->
            <dsp:getvalueof var="showButtonForm" value="${false}"/>
			<dsp:getvalueof var="displayVUMMessage" value="${true}"/>
          </dsp:oparam>
          <dsp:oparam name="deliveryTime"><!-- SAS ${prodIdS} DT -->
          </dsp:oparam>
          <dsp:oparam name="ccRemainingStock"><!-- SAS ${prodIdS} CCRS -->
          </dsp:oparam>
          <dsp:oparam name="ccSoldOnlyInStore"><!-- SAS ${prodIdS} CCSOIS -->
          </dsp:oparam>
          <dsp:oparam name="ccDeliveryTime"><!-- SAS ${prodIdS} CCDT -->
          </dsp:oparam>
        </dsp:droplet> 
        <!-- draw price block for add to cart small for multiy sku start for ${prodIdS} -->
        <c:if test="${showUnavailableMessage || showButtonForm}">
          <div class="buttonCartContainer">
            <c:if test="${showUnavailableMessage}">
              <input class="buttonCart grey" type="button" value="${cartAdd}" onclick="return false"/>
            </c:if>
            <c:if test="${showButtonForm}">
              <dsp:a href="${url}" iclass="buttonCart">
                <dsp:param name="isAddToCart" value="true" />
              </dsp:a>
            </c:if>
          </div>
        </c:if>
        <!-- draw price block for add to cart small for multy sku end for ${prodIdS} -->
      </c:if>
      <c:if test="${fn:length(childSku)==1}">
        <dsp:droplet name="ProductLookup">
          <dsp:param name="id" value="${productId}" />
          <dsp:param name="elementName" value="prod" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="productType" param="prod.type" />
            <c:choose>
              <c:when test="${productType=='casto-grouped-product'}">
                <%@ include
                  file="/castCatalog/includes/addToCartSmallGroupedProduct.jspf"%>
              </c:when>
              <c:otherwise>
                <%@ include
                  file="/castCatalog/includes/addToCartSmall.jspf"%>
              </c:otherwise>
            </c:choose>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
    </c:when>
    <c:otherwise>
    </c:otherwise>
  </c:choose>
</dsp:page>