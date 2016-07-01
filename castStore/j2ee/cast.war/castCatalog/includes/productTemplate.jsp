<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/For"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>  
  <dsp:importbean bean="/com/castorama/droplet/IsRobotDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>

  <dsp:getvalueof var="isRobot" value="false"/>
  <dsp:droplet name="IsRobotDroplet">
    <dsp:oparam name="true">
      <dsp:getvalueof var="isRobot" value="true"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" value="${productId}"/>
    <dsp:oparam name="output">
      <dsp:param name="product" param="element"/>
      <dsp:getvalueof var="categoryId" param="element.parentCategory.repositoryId"/>
         
      <dsp:droplet name="CastPriceRangeDroplet">
        <dsp:param name="productId" value="${productId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>
          <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
          <dsp:getvalueof var="codeArticle" param="sku.codeArticle"/>
          <dsp:getvalueof var="supportingImg" param="sku.supportingImage.url" idtype="java.lang.String"/>
          <div class="illustration" onmouseover="elargeImage(this, true)" onmouseout="elargeImage(this, false)"
            id="PT_<dsp:valueof param="product.repositoryId"/>">
            <dsp:getvalueof var="name" param="product.displayName"/>
            <dsp:droplet name="CastProductLinkDroplet">
              <dsp:param name="productId" param="product.repositoryId"/>
              <dsp:param name="navAction" param="navAction"/>
              <dsp:param name="navCount" param="navCount"/>
              <dsp:param name="ba" value="${baFakeContext}"/>
              <dsp:param name="hideBreadcrumbs" value="${bonnesAffaires}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="templateUrl" param="url"/>
              </dsp:oparam>
            </dsp:droplet>
            
            <dsp:getvalueof var="sku" param="sku"/>
            <dsp:droplet name="PriceDroplet">
              <dsp:param name="sku" value="${sku}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="listPrice" param="price.listPrice"/>
              </dsp:oparam>
            </dsp:droplet>
            <dsp:getvalueof var="productType" param="product.type"/>
            <dsp:getvalueof var="isMultySKU" param="product.childSKUs"/>
              <c:choose>
                <c:when test="${(fn:length(isMultySKU) > 1) or (empty listPrice)}">
                  <dsp:a href="${contextPath}${templateUrl}">
                    <c:choose>
                      <c:when test="${not empty supportingImg}">
                        <img src="${supportingImg}" width="125" height="125" alt="${name}"  title="${name}" /> 
                      </c:when>
                      <c:otherwise>
                        <img src="/default_images/d_no_img.jpg" width="125" height="125" alt="${name}"  title="${name}" />
                      </c:otherwise>
                    </c:choose>
                  </dsp:a>
                  <dsp:include page="/castCatalog/productZoomTemplate.jsp">
                    <dsp:param name="product" param="product"/>
                    <dsp:param name="sku" param="sku"/>
                    <dsp:param name="templateUrl" value="${templateUrl}"/>
                    <dsp:param name="isMultySKU" value="${isMultySKU}"/>
                    <dsp:param name="listPrice" value="${listPrice}"/>
                  </dsp:include>  
                </c:when>
                <c:when test="${productType=='casto-grouped-product'}">
                 <%@ include file="/shoppingList/includes/groupedProductInfo.jspf"%>
                 <dsp:a href="${contextPath}${templateUrl}">
                  <c:choose>
                    <c:when test="${not empty supportingImg}">
                      <img class="slPrdMarker" id="${skuList}" src="${supportingImg}" id="${skuList}" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}"  width="125" height="125"/>
                    </c:when>
                    <c:otherwise>
                      <img class="slPrdMarker" id="${skuList}" src="/default_images/d_no_img.jpg" id="${skuList}" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}"  width="125" height="125"/>
                    </c:otherwise>
                  </c:choose>
                </dsp:a>
                <dsp:include page="/castCatalog/productZoomTemplate.jsp">
                  <dsp:param name="product" param="product"/>
                  <dsp:param name="sku" param="sku"/>
                  <dsp:param name="skuList" value="${skuList}"/>
                  <dsp:param name="skuCodeArticleList" value="${skuCodeArticleList}"/>
                  <dsp:param name="imgSrcList" value="${imgSrcList}"/>
                  <dsp:param name="namesList" value="${namesList}"/>
                  <dsp:param name="productList" value="${productList}"/>
                  <dsp:param name="templateUrl" value="${templateUrl}"/>
                </dsp:include>
              </c:when>
              <c:otherwise>
                <dsp:a href="${contextPath}${templateUrl}">
                    <c:choose>
                      <c:when test="${not empty supportingImg}">
                        <img class="slPrdMarker" src="${supportingImg}" width="125" height="125" id="${skuId}" skuCodeArticle="${codeArticle}" productId="${productId}" alt="${name}"  title="${name}" /> 
                      </c:when>
                      <c:otherwise>
                        <img class="slPrdMarker" src="/default_images/d_no_img.jpg" width="125" height="125" id="${skuId}" skuCodeArticle="${codeArticle}" productId="${productId}" alt="${name}"  title="${name}" />
                      </c:otherwise>
                    </c:choose>
                </dsp:a>
                <dsp:include page="/castCatalog/productZoomTemplate.jsp">
                  <dsp:param name="product" param="product"/>
                  <dsp:param name="sku" param="sku"/>
                  <dsp:param name="templateUrl" value="${templateUrl}"/>
                </dsp:include>  
              </c:otherwise>
            </c:choose>
          </div>
          <div class="prodDecription">
            <dsp:include page="prodHighlight.jsp">
              <dsp:param name="product" param="product"/>
              <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}"/>
              <dsp:param name="categoryId" value="${categoryId}"/>
              <dsp:param name="view" value="galeryView"/>
              <dsp:param name="noBreadcrumbs" value="true"/>
            </dsp:include>
            <dsp:a href="${contextPath}${templateUrl}">
              <dsp:valueof param="product.displayName"/>
            </dsp:a>
            <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
            <dsp:getvalueof var="storeId" bean="Profile.currentLocalStore.id"/>
            <dsp:droplet name="/com/castorama/droplet/ProductPriceCache">
              <dsp:param name="key" value="prod_price_${productId}_${skuId}_${isRobot}_${storeId}"/>
              <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSecondsLong"/>
              <dsp:oparam name="output">
                <dsp:include page="skuPrice.jsp">
                  <dsp:param name="pageType" value="catalog"/>
                  <dsp:param name="productId" value="${productId}"/>
                  <dsp:param name="sku" param="sku"/>
                </dsp:include>
              </dsp:oparam>
            </dsp:droplet>
           </div>
        </dsp:oparam>
      </dsp:droplet>  <%-- end  CastPriceRangeDroplet --%>
      
    </dsp:oparam>
  </dsp:droplet>  <%-- end  ProductLookup  --%>
</dsp:page>
