<dsp:page>

  <dsp:importbean bean="/atg/commerce/search/refinement/CommerceFacetTrailDroplet"/>
  <dsp:importbean bean="/atg/search/repository/FacetSearchTools"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/ProductDescPreProcessorDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="categoryId" param="categoryId" />
  <dsp:getvalueof var="featuredSkuId" param="featuredSkuId" />

  <dsp:droplet name="StyleLookupDroplet">
    <dsp:param name="categoryId" value="${categoryId}" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="listColor" param="style.listStyle" /> 
    </dsp:oparam>
  </dsp:droplet> 
            
  <dsp:droplet name="CommerceFacetTrailDroplet">
    <dsp:setvalue param="trail" beanvalue="FacetSearchTools.facetTrail" />
    <dsp:param name="refineConfig" value="${refineConfigObj}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="facetTrail" param="facetTrail" />
      <c:if test="${fn:length(facetTrail.facetValues) == 1}">
        <dsp:droplet name="CategoryLookup">
          <dsp:param name="id" value="${categoryId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="product" param="element.featuredProduct"/>
            <dsp:getvalueof var="productId" param="element.featuredProduct.repositoryId"/>
            <dsp:getvalueof var="childSku" param="element.featuredProduct.childSKUs"/> 
            <c:if test="${not empty product}">
              <div class="featuredProduct">
                <c:choose>
                  <c:when test="${fn:length(childSku)>1}">
                    <c:choose>
                      <c:when test="${not empty featuredSkuId}">
                        <dsp:droplet name="SKULookup">
                          <dsp:param name="id" value="${featuredSkuId}"/>
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="sku" param="element"/>
                            <dsp:getvalueof var="singleSku" value="true"/>
                          </dsp:oparam>
                        </dsp:droplet>
                        <dsp:droplet name="CastPriceRangeDroplet">
                          <dsp:param name="productId" value="${productId}"/>
                          <dsp:oparam name="output">              
                            <dsp:getvalueof var="cheapestSku" param="sku"/>
                            <dsp:getvalueof var="notCheapestSkuPromoExists" param="notCheapestSkuPromo"/>
                            <c:if test="${(featuredSkuId != cheapestSku.repositoryId) and not empty notCheapestSkuPromoExists 
                                and notCheapestSkuPromoExists}">
                              <dsp:getvalueof var="notCheapestSkuPromoChosen" value="true"/>
                              <dsp:getvalueof var="notCheapestSkuPromo" value="false"/>
                            </c:if>
                          </dsp:oparam>
                        </dsp:droplet>
                      </c:when>
                      <c:otherwise>
                        <dsp:droplet name="CastPriceRangeDroplet">
                          <dsp:param name="productId" value="${productId}"/>
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="sku" param="sku"/>
                            <dsp:getvalueof var="singleSku" value="false"/>
                            <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>
                          </dsp:oparam>
                        </dsp:droplet>  
                      </c:otherwise>
                    </c:choose>
                  </c:when>
                  <c:otherwise>
                    <dsp:droplet name="CastPriceRangeDroplet">
                      <dsp:param name="productId" value="${productId}"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="sku" param="sku"/>
                        <dsp:getvalueof var="singleSku" value="true"/>
                      </dsp:oparam>
                    </dsp:droplet>  
                  </c:otherwise>
                </c:choose>  
                <div class="fIllustration">
                  <dsp:param name="sku" value="${sku}"/>
                  <dsp:getvalueof var="highlitedProduct" param="sku.CodeArticle" scope="request"/>
                  <c:choose>
                    <c:when test="${singleSku}">
                      <dsp:getvalueof var="name" param="sku.displayName"/>
                      <dsp:getvalueof var="showSeeMore" value="false" vartype="boolean"/>
                      <dsp:droplet name="ProductDescPreProcessorDroplet">
                        <dsp:param name="description" param="sku.LibelleClientLong"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="description" param="result"/>
                          <dsp:getvalueof var="showSeeMore" param="showSeeMore"/>
                        </dsp:oparam>
                      </dsp:droplet>
                    </c:when>
                    <c:otherwise>
                      <dsp:getvalueof var="name" param="element.featuredProduct.displayName"/>
                      <dsp:getvalueof var="showSeeMore" value="false" vartype="boolean"/>
                      <dsp:droplet name="ProductDescPreProcessorDroplet">
                        <dsp:param name="description" param="element.featuredProduct.longDescription"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="description" param="result"/>
                          <dsp:getvalueof var="showSeeMore" param="showSeeMore"/>
                        </dsp:oparam>
                      </dsp:droplet>
                    </c:otherwise>
                  </c:choose>
                  <dsp:getvalueof var="smallImage" param="sku.smallImage.url"/>
                  <dsp:getvalueof var="skuId" param="sku.id"/>
                  <dsp:droplet name="CastProductLinkDroplet">
                    <dsp:param name="productId" value="${productId}"/>
                    <dsp:param name="categoryId" param="element.featuredProduct.parentCategory.repositoryId"/>
                    <dsp:param name="navAction" value="jump"/>
                    <dsp:param name="navCount" param="navCount"/>
                    <dsp:param name="skuId" value="${featuredSkuId}"/>
                    <dsp:param name="isFeaturedProduct" value="true"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="templateUrl" param="url"/>
                    </dsp:oparam>
                  </dsp:droplet>
                  <dsp:a title="${name}" href="${contextPath}${templateUrl}">
                    <c:choose>
                      <c:when test="${not empty smallImage}">
                        <img id="${skuId}" class="slPrdMarker" src="${smallImage}" srcList="${smallImage}" alt="${name}" title="${name}" productId="${productId}"/> 
                      </c:when>
                      <c:otherwise>
                        <img id="${skuId}" class="slPrdMarker" src="/default_images/g_no_img.jpg" srcList="/default_images/g_no_img.jpg" alt="${name}"  title="${name}" productId="${productId}"/>
                      </c:otherwise>
                    </c:choose>
                  </dsp:a>
                </div>
 
                <div class="fDescription">
                  
					  <dsp:droplet name="PriceDroplet">
		                <dsp:param name="sku" value="${sku}"/> 
		                <dsp:oparam name="output">
		                  <dsp:getvalueof var="listPrice" param="price.listPrice"/> 
		                </dsp:oparam> 
		              </dsp:droplet>
		              
		              <c:if test="${empty listPrice}">   
		                <dsp:getvalueof var="notCheapestSkuPromo" value="false"/>
		                <dsp:getvalueof var="notCheapestSkuPromoChosen" value="false"/>
		              </c:if>
                  
                    <dsp:include page="../castCatalog/includes/prodHighlight.jsp">
                      <dsp:param name="product" value="${product}"/>
                      <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}"/>
                      <dsp:param name="categoryId" value="${categoryId}"/>
                      <dsp:param name="view" value="listView"/>
                    </dsp:include>
                <br/>
                  <dsp:a title="${name}" href="${contextPath}${templateUrl}">
                    <strong>
                      <dsp:valueof value="${name}"/>
                    </strong>
                  </dsp:a>
                  <ul class="${listColor}">
                    <dsp:valueof value="${description}" valueishtml="true"/>
                    <c:if test="${showSeeMore}">
                      ...
                    </c:if>
                  </ul>
                  <dsp:include page="/castCatalog/includes/brandLink.jsp">
                  <dsp:param name="isProductListingPage" value="true"/>
                    <dsp:param name="product" param="element.featuredProduct"/>
                    <dsp:param name="className" value="labelFlag"/>
                    <dsp:param name="showImage" value="${true}"/>
                  </dsp:include>
                </div>
                <dsp:include page="../castCatalog/includes/addToCartArea.jsp">
                  <dsp:param name="sku" value="${sku}"/>
                  <dsp:param name="pageType" value="featuredProduct"/>
                  <dsp:param name="productId" param="element.featuredProduct.repositoryId"/>
                  <dsp:param name="childSku" value="${childSku}"/>
                  <dsp:param name="featuredProduct" value="${true}"/>
                  <dsp:param name="categoryId" value="${categoryId}"/>
                  <dsp:param name="featuredSkuId" value="${featuredSkuId}"/>
                  <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromoChosen}"/>
                </dsp:include>
                <div class="clear"></div>
              </div>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>