<dsp:page>

  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />
  <dsp:importbean bean="/com/castorama/CastConfiguration"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/For" />	
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  
  <dsp:getvalueof var="ownerCategoryId" param="ownerCategoryId" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />
  <dsp:getvalueof var="queryString" bean="/OriginatingRequest.queryString" />
  <dsp:getvalueof var="categoryId" param="categoryId" />
  <dsp:getvalueof var="documentId" param="documentId" />
  <dsp:getvalueof var="productId" param="productId" />
  <dsp:getvalueof var="isSearchResult" param="isSearchResult" />
  <dsp:getvalueof var="isFeaturedProduct" param="isFeaturedProduct" />
  <dsp:getvalueof var="carouselProductCount" bean="CastConfiguration.carouselProductsCount"/>
    
  <script type="text/javascript" src="${contextPath}/js/jquery.carousel.js"></script>
  <c:choose>
    <c:when test="${((not empty documentId && empty productId) || not empty categoryId || (not empty isSearchResult && isSearchResult)) && !isFeaturedProduct}">
    
      <%@ include file="/castCatalog/includes/carouselFacetedSearch.jspf" %>
     
    </c:when>
    <c:otherwise>
    
      <%@ include file="/castCatalog/includes/carouselMotherCategory.jspf" %>
    
    </c:otherwise>
  </c:choose>

</dsp:page>
