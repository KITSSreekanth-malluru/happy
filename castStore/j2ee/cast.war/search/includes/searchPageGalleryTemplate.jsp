<dsp:page>
 
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />

  <div>
    <c:set var="productsList" value=""  scope="request"/>
    <dsp:droplet name="ForEach">
  	<dsp:param name="array" param="results"/>
  	<dsp:oparam name="output">
  	     <c:if test="${not empty productsList }">
            <c:set var="productsList" value="${productsList}," scope="request"/>
          </c:if>
        <dsp:getvalueof var="size" param="size"/>
        <dsp:getvalueof var="index" param="index"/>
          <c:choose>
            <c:when test="${index == 0 || index mod 10 == 0}">
              <div class="productsRow hasHighlight">
            </c:when>
            <c:otherwise>
              <c:if test="${index mod 5 == 0}">
                <div class="productsRow hasHighlight">
              </c:if>
            </c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${(index + 1) mod 5 == 0 }">
              <div class="productItem piLast" style="z-index: 5;">
            </c:when>
            <c:otherwise>
              <div class="productItem" style="z-index: 5;">
            </c:otherwise>
          </c:choose>      
  
          <dsp:include page="/castCatalog/includes/productViewTemplate.jsp" flush="true">
            <dsp:param name="productId" param="element.document.properties.$repositoryId"/>
            <dsp:param name="navAction" param="navAction"/>
            <dsp:param name="navCount" param="navCount"/>
            <dsp:param name="categoryId" param="categoryId"/>
            <dsp:param name="draggable" value="true"/>
            <dsp:param name="isSearchResult" param="isSearchResult"/>
          </dsp:include>
    	
          <dsp:droplet name="ProductLookup">
            <dsp:param name="id" param="element.document.properties.$repositoryId"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="childSku" param="element.childSKUs"/>
              <dsp:getvalueof var="productId" param="element.repositoryId"/>
              <dsp:droplet name="CastPriceRangeDroplet">
                <dsp:param name="productId" value="${productId}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="sku" param="sku"/>
                </dsp:oparam>
              </dsp:droplet>
              <dsp:getvalueof var="skuCodeArticle" param="sku.CodeArticle"/>
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
            </dsp:oparam>
          </dsp:droplet>
          <dsp:include page="../../castCatalog/includes/addToCartSmall.jsp">
            <dsp:param name="sku" value="${sku}"/>
            <dsp:param name="productId" value="${productId}"/>
            <dsp:param name="childSku" value="${childSku}"/>
            <dsp:param name="url" value="${contextPath}${templateUrl}"/>
          </dsp:include>
        </div>
        <c:if test="${(((index + 1) == size) && (size mod 5 != 0)) || ((index + 1) mod 5 == 0)}">
          <div class="clear"></div>
          </div>
        </c:if>
          
          
      </dsp:oparam>
    </dsp:droplet>
  </div>
  <c:set var="omnitureProducts" value="${productsList}" scope="request"/>
</dsp:page>