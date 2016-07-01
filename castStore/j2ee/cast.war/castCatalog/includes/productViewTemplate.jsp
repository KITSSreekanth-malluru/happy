<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>  
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/IsRobotDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/LinkedSkuTabNamesIteratorDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  

  <dsp:getvalueof var="isRobot" value="false"/>
  <dsp:droplet name="IsRobotDroplet">
    <dsp:oparam name="true">
      <dsp:getvalueof var="isRobot" value="true"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="noBreadcrumbs" param="noBreadcrumbs"/>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" value="${productId}"/>
    <dsp:oparam name="output">
      <dsp:param name="product" param="element"/>
      <!-- !!!<dsp:getvalueof var="templateUrl" param="product.template.url"/> -->
      <dsp:droplet name="CastProductLinkDroplet">
        <dsp:param name="categoryId" value=""/>
        <dsp:param name="productId" value="${productId}"/>
        <dsp:param name="navAction" param="navAction"/>
        <dsp:param name="navCount" param="navCount"/>
        <dsp:param name="isSearchResult" param="isSearchResult"/>
        <dsp:param name="sortByValue" param="sortByValue"/>
        <dsp:param name="ba" value="${baFakeContext}"/>
        <dsp:param name="hideBreadcrumbs" value="${bonnesAffaires}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="templateUrl" param="url"/>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:droplet name="CastPriceRangeDroplet">
        <dsp:param name="productId" value="${productId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>
          <%-- Following two lines of code are added to support the Omniture functionality --%>
          <dsp:getvalueof var="refNumber" param="sku.CodeArticle"/>
          <c:set var="productsList" value="${productsList}${refNumber}" scope="request"/>
          <dsp:droplet name="PriceDroplet">
            <dsp:param name="sku" param="sku"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="listPrice" param="price.listPrice"/>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:getvalueof var="comparatorImg" param="sku.comparatorImage.url" idtype="java.lang.String" />
          <dsp:getvalueof var="name" param="product.displayName"/>
          <div class="illustration">
            <dsp:getvalueof var="productType" param="product.type"/>
            <dsp:getvalueof var="childSKUs" param="product.childSKUs"/>
            <dsp:a href="${contextPath}${templateUrl}">
              <c:choose>
                <c:when test="${(fn:length(childSKUs) > 1) or (empty listPrice)}">
                  <c:choose>
                    <c:when test="${not empty comparatorImg}">
                      <dsp:img src="${comparatorImg}" width="138" height="138" alt="${name}" title="${name}" />
                    </c:when>
                    <c:otherwise>
                      <dsp:img src="/default_images/e_no_img.jpg" width="138" height="138" alt="${name}"  title="${name}" />
                    </c:otherwise>
                  </c:choose>   	
                </c:when>
                <c:when test="${productType=='casto-grouped-product'}">
                  <%@ include file="/shoppingList/includes/groupedProductInfo.jspf"%>
                  <c:choose>
                    <c:when test="${not empty comparatorImg}">
                      <img class="slPrdMarker" id="${skuList}" src="${comparatorImg}" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}" onload="jQuery(this).css('visibility', 'visible')" width="138" height="138"/>
                    </c:when>
                    <c:otherwise>
                      <img class="slPrdMarker" id="${skuList}" src="/default_images/h_no_img.jpg" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}" onload="jQuery(this).css('visibility', 'visible')" width="138" height="138" />
                    </c:otherwise>
                  </c:choose>
                </c:when>
                <c:otherwise>
                  <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
                  <c:choose>
                    <c:when test="${not empty comparatorImg}">
                      <img src="${comparatorImg}" width="138" height="138" alt="${name}" title="${name}" class="prdMarker slPrdMarker" id="${skuId}" productId="${productId}" skuCodeArticle="${refNumber }"/>
                    </c:when>
                    <c:otherwise>
                      <img src="/default_images/e_no_img.jpg" width="138" height="138" alt="${name}"  title="${name}" class="prdMarker slPrdMarker" id="${skuId}" productId="${productId}" skuCodeArticle="${refNumber }"/>
                    </c:otherwise>
                  </c:choose>
                </c:otherwise>
              </c:choose>
            </dsp:a>
          </div>
             <div class="prodDecription">
            <dsp:include page="prodHighlight.jsp">
              <dsp:param name="product" param="product"/>
              <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}"/>
              <dsp:param name="categoryId" value="${categoryId}"/>
              <dsp:param name="view" value="galeryView"/>
              <dsp:param name="noBreadcrumbs" value="${noBreadcrumbs}"/>
              <dsp:param name="isSearchResult" param="isSearchResult"/>
            </dsp:include>
            <dsp:a iclass="prodDecriptionName" href="${contextPath}${templateUrl}">
              <dsp:valueof param="product.displayName"/>
            </dsp:a>
            <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
            <dsp:getvalueof var="storeId" bean="/atg/userprofiling/Profile.currentLocalStore.id"/>
            <dsp:droplet name="/com/castorama/droplet/ProductPriceCache">
              <dsp:param name="key" value="prod_price_${productId}_${skuId}_${isRobot}_${storeId}" />
              <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSecondsLong" />
              <dsp:oparam name="output">
                <dsp:include page="skuPrice.jsp">
                  <dsp:param name="pageType" value="catalog"/>
                  <dsp:param name="productId" value="${productId}" />
                  <dsp:param name="sku" param="sku" />
                </dsp:include>
              </dsp:oparam>
            </dsp:droplet>
            
          </div>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>