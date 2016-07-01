<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>  	
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/LinkedSkuTabNamesIteratorDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>

  <dsp:getvalueof var="isRobot" value="false"/>
  <dsp:droplet name="/com/castorama/droplet/IsRobotDroplet">
    <dsp:oparam name="true">
      <dsp:getvalueof var="isRobot" value="true"/>
    </dsp:oparam>           
  </dsp:droplet>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" value="${productId}"/>
    <dsp:oparam name="output">
      <dsp:param name="product" param="element"/>
      <dsp:droplet name="CastPriceRangeDroplet">
        <dsp:param name="productId" value="${productId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>
          <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
          <dsp:getvalueof var="skuCodeArticle" param="sku.CodeArticle"/>
          
          <%-- Following two lines of code are added to support the Omniture functionality --%>
          <dsp:getvalueof var="refNumber" param="sku.CodeArticle"/>
          <c:set var="productsList" value="${productsList}${refNumber}" scope="request"/>
          <td>
            <div class="productItemImage">
              <dsp:getvalueof var="name" param="product.displayName"/>
              <dsp:getvalueof var="carouselImage" param="sku.carouselImage.url"/>
              <dsp:droplet name="CastProductLinkDroplet">
                <dsp:param name="productId" value="${productId}"/>
                <dsp:param name="categoryId" value="${categoryId}" />	
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
              <dsp:droplet name="PriceDroplet">
                <dsp:param name="sku" param="sku"/> 
                <dsp:oparam name="output">
                  <dsp:getvalueof var="listPrice" param="price.listPrice"/> 
                </dsp:oparam> 
              </dsp:droplet>
              <dsp:getvalueof var="productType" param="product.type"/>
              <dsp:getvalueof var="isMultySKU" param="product.childSKUs"/>
              <dsp:a href="${contextPath}${templateUrl}">
                <c:choose>
                  <c:when test="${(fn:length(isMultySKU) > 1) or (empty listPrice)}">
                    <c:choose>
                      <c:when test="${not empty carouselImage}">
                        <img src="${carouselImage}" alt="${name}" title="${name}"/> 
                      </c:when>
                      <c:otherwise>
                        <img src="/default_images/b_no_img.jpg" alt="${name}"  title="${name}"/>
                      </c:otherwise>
                    </c:choose> 	
                  </c:when>
                  <c:when test="${productType=='casto-grouped-product'}">
                    <%@ include file="/shoppingList/includes/groupedProductInfo.jspf"%>
                    <c:choose>
                      <c:when test="${not empty carouselImage}">
                        <img class="slPrdMarker" id="${skuList}" src="${carouselImage}" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}" onload="jQuery(this).css('visibility', 'visible')"/>
                      </c:when>
                      <c:otherwise>
                        <img class="slPrdMarker" id="${skuList}" src="/default_images/b_no_img.jpg" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}" onload="jQuery(this).css('visibility', 'visible')"/>
                      </c:otherwise>
                    </c:choose>
                  </c:when>
                  <c:otherwise>
                    <dsp:getvalueof var="skuId" value="${skuId}"/>
                    <c:choose>
                      <c:when test="${not empty carouselImage}">
                        <img src="${carouselImage}" alt="${name}" title="${name}" class="prdMarker slPrdMarker" id="${skuId}" productId="${productId}"  skuCodeArticle="${skuCodeArticle }"/> 
                      </c:when>
                      <c:otherwise>
                        <img src="/default_images/b_no_img.jpg" alt="${name}"  title="${name}" class="prdMarker slPrdMarker" id="${skuId}" productId="${productId}"  skuCodeArticle="${skuCodeArticle }"/>
                      </c:otherwise>
                    </c:choose> 
                  </c:otherwise>
                </c:choose>
              </dsp:a>
            </div>
          </td>
          <td>
            <div class="productItemDescription">
              <dsp:include page="includes/prodHighlight.jsp">
                <dsp:param name="product" param="product"/>
                <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}"/>
                <dsp:param name="categoryId" value="${categoryId}"/>
                <dsp:param name="view" value="listView"/>
                <dsp:param name="isSearchResult" param="isSearchResult"/>
              </dsp:include>
              <dsp:a href="${contextPath}${templateUrl}">
                <dsp:valueof param="product.displayName" valueishtml="true"/>
              </dsp:a>
            </div>
          </td>
          <td>
            <dsp:include page="includes/brandLink.jsp">
              <dsp:param name="isProductListingPage" value="true"/>
              <dsp:param name="product" param="product"/>
              <dsp:param name="navAction" param="jump"/>
              <dsp:param name="className" value="greyLink"/>
              <dsp:param name="showImage" value="${true}"/>
            </dsp:include>
          </td>
          <td>
            <dsp:getvalueof var="skuId" value="${skuId}"/>
            <dsp:include page="includes/skuPrice.jsp">
              <dsp:param name="pageType" value="catalog"/>
              <dsp:param name="productId" value="${productId}"/>
              <dsp:param name="sku" param="sku"/>
            </dsp:include>
          </td>
          <td class="lastCell">
            <dsp:include page="includes/addToCartSmall.jsp">
              <dsp:param name="sku" param="sku"/>
              <dsp:param name="productId" value="${productId}"/>
              <dsp:param name="childSku" value="${isMultySKU}"/>
              <dsp:param name="url" value="${contextPath}${templateUrl}"/>
            </dsp:include>
          </td>
        </dsp:oparam>
      </dsp:droplet>    
    </dsp:oparam>
  </dsp:droplet>  
</dsp:page>
