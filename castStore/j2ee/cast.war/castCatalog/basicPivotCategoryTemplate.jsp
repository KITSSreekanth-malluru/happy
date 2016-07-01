<dsp:page>

  <dsp:getvalueof var="wrapAllowed" param="wrap" />
  <dsp:getvalueof var="sortByValueFromURL" param="sortByValue" />
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />
  <dsp:droplet name="CategoryLookup">  
    <dsp:param name="id" param="categoryId" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="customPageLink" param="element.customPageLink" />
      <dsp:getvalueof var="sortType" param="element.typeSort" scope="request"/>
    </dsp:oparam>
  </dsp:droplet>  

<c:choose>
                                  <c:when test="${sortType == '0'}">
                                      <c:set var="sortBy" value="relevance"/>
                                  </c:when>
                                  <c:when test="${sortType == '1'}">
                                      <c:set var="sortBy" value="lowHighPrice"/>
                                  </c:when>
                                  <c:when test="${sortType == '2'}">
                                      <c:set var="sortBy" value="highLowPrice"/>
                                  </c:when>
                                  <c:otherwise>
                                      <c:set var="sortBy" value=""/>
                                  </c:otherwise>
              </c:choose>
<dsp:getvalueof var="sortByValueFromBcc" value="${sortBy}" />

  <c:choose>

    <c:when test="${not empty wrapAllowed and wrapAllowed and not empty customPageLink and customPageLink != ''}">
        
  <cast:pageContainer>
    <jsp:attribute name="metaInfoInclude">/global/meta.jsp?id=<dsp:valueof param="categoryId"/>&item=category</jsp:attribute>
    <jsp:attribute name="bodyContent">
      <div class="content">
        <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
        <c:import charEncoding="utf-8" url="${staticContentPath}${customPageLink}"/>
      </div>
    </jsp:attribute>
    <jsp:attribute name="canonicalUrl">
      <dsp:importbean bean="/com/castorama/droplet/CanonicalLinkDroplet"/>
      <dsp:droplet name="CanonicalLinkDroplet">
        <dsp:param name="type" value="pivotCategory"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="canonicalUrl" param="url"/>
        </dsp:oparam>
      </dsp:droplet>
      ${canonicalUrl}
    </jsp:attribute>
  </cast:pageContainer>
          
    </c:when>
    <c:otherwise>

  <%@ include file="../search/includes/resubmitSearchRequest.jspf" %>
  
  <dsp:include page="breadcrumbsCollector.jsp" >
    <dsp:param name="categoryId" value="${categoryId}"/>
    <dsp:param name="navAction" param="navAction"/>
    <dsp:param name="navCount" param="navCount"/>
  </dsp:include>

  <cast:pageContainer>
    <jsp:attribute name="bottomBanners">true</jsp:attribute>
    <jsp:attribute name="metaInfoInclude">/global/meta.jsp?id=<dsp:valueof param="categoryId"/>&item=category</jsp:attribute>
    <jsp:attribute name="bodyContent">
    
      <dsp:importbean bean="/atg/targeting/TargetingFirst"/>
      <dsp:importbean bean="/atg/commerce/search/catalog/QueryFormHandler"/>
      <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
      <dsp:importbean bean="/com/castorama/search/droplet/SearchEnvironmentStatus" />
      <dsp:importbean bean="/atg/search/repository/FacetSearchTools" />
      <dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet" />

      <dsp:getvalueof var="categoryURI" bean="/OriginatingRequest.requestURI"/>
      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
      <dsp:getvalueof var="categoryId" param="categoryId"/>
  
      <dsp:droplet name="CastCategoryLinkDroplet">
        <dsp:param name="categoryId" value="${categoryId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="url" param="url"/>
          <dsp:getvalueof var="categoryURI" value="${contextPath}${url}"/>
        </dsp:oparam>
      </dsp:droplet>
      
      <!-- slide right panel with shopping list -->
      <dsp:include page="/shoppingList/shoppingListSlider.jsp">
        <dsp:param name="currentPage" value="pivotCategory"/> 
      </dsp:include>
      
      <dsp:droplet name="CategoryLookup">
        <dsp:param name="id" value="${categoryId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="categoryPagingStyle" param="element.style.pagingStyle"/>
          
          <dsp:getvalueof var="bonnesAffaires" param="element.bonnesAffaires" scope="request"/>
          <c:if test="${bonnesAffaires and wrappedStoreId!='999' and (wrappedStoreIsCC || wrappedStoreIsLocal)}">
            <dsp:getvalueof var="baFakeContext" value="${true}" scope="request"/>
          </c:if>
          
          <dsp:include page="breadcrumbs.jsp" flush="true" >
            <dsp:param name="categoryId" value="${categoryId}" />
          </dsp:include>
          
          <dsp:getvalueof var="categoryName" param="element.displayName"/>
          <dsp:getvalueof var="productListingViewVar" param="productListingView"/>
          <c:if test="${empty productListingViewVar }">
              <dsp:getvalueof var="productListingViewVar" param="element.style.listView"/>
          </c:if>
          
          <dsp:getvalueof var="promoTemplate" param="element.promoTemplate" />
          <c:if test="${not empty promoTemplate}">
            <dsp:getvalueof var="layoutType" param="element.promoTemplate.layoutType" />
	            <dsp:include page="includes/catalogPromoTemplates/${layoutType}.jsp" flush="true">
	              <dsp:param name="promoInformation" param="element.promoTemplate.promoInformation" />
	            </dsp:include>
          </c:if>
        </dsp:oparam>
      </dsp:droplet> 

      <c:if test="${empty productListingViewVar }">
        <c:set var="productListingViewVar" value="gallery"/>    
      </c:if>

      <dsp:droplet name="SearchEnvironmentStatus">
        <dsp:param name="searchEnvironmentTypes" value="product"/>
        <dsp:oparam name="unavailable">
           <dsp:include page="../search/includes/searchUnavailable.jsp" flush="true"/>
        </dsp:oparam>
        <dsp:oparam name="initialized">
          <div class="content">  
            <%-- redirect search rule re-factoring 
            <dsp:include page="../search/includes/resubmitSearchRequest.jsp" flush="true">
              <dsp:param name="searchResponse" bean="QueryFormHandler.searchResponse"/>
            </dsp:include>
            --%>
            <!-- DEBUG: before include filters.jsp -->
            <dsp:include page="../search/includes/filters.jsp" flush="true">
              <dsp:param name="trail" param="trail"/>
              <dsp:param name="isSearchForCategory" value="true"/>
              <dsp:param name="container" value="${categoryURI}"/>
              <dsp:param name="categoryUrl" value="${url}"/>
              <dsp:param name="produitsFST" bean="FacetSearchTools"/>
            </dsp:include>
            <!-- DEBUG: after include filters.jsp -->                        
            <div class="prodGalContainer">
            
            <dsp:include page="../search/includes/searchPageTitle.jsp" flush="true">
              <dsp:param name="searchResponse" bean="QueryFormHandler.searchResponse"/>
              <dsp:param name="container" value="${categoryURI}"/>
              <dsp:param name="searchPageTitleMessage" value="search_searchPageTitle.searchResultsCount"/>
            </dsp:include>
	
<c:choose>
     <c:when test="${empty sortByValueFromURL}">
       <dsp:param name="sortByValue" value="${sortBy}"/>
     </c:when>
      <c:otherwise>
        <dsp:param name="sortByValue" value="${sortByValueFromURL}"/>
      </c:otherwise>
  </c:choose>
            <dsp:include page="../search/includes/searchSorter.jsp" flush="true">
              <dsp:param name="productListingView" value="${productListingViewVar }"/>
              <dsp:param name="searchResponse" bean="QueryFormHandler.searchResponse"/>
              <dsp:param name="container" value="${categoryURI}"/>
              <dsp:param name="produitsFST" bean="FacetSearchTools"/>
              <dsp:param name="sortByValue" param="sortByValue"/>
            </dsp:include>
            
            <dsp:include page="/global/featuredProductPageList.jsp" flush="true">
              <dsp:param name="trail" param="trail"/>
            </dsp:include>

            <dsp:include page="../search/includes/searchResults.jsp" flush="true">
              <dsp:param name="results" bean="QueryFormHandler.searchResponse.results"/>
              <dsp:param name="productListingView" value="${productListingViewVar }"/>
              <dsp:param name="container" value="${categoryURI}"/>
              <dsp:param name="navAction" value="push" />
              <dsp:param name="navCount" bean="CatalogNavHistory.navCount" />
            </dsp:include>
          
            <div class="paginator">
              <div class="${categoryPagingStyle}">
                <dsp:include page="../search/includes/searchPaging.jsp" flush="true">
                  <dsp:param name="container" value="${categoryURI}"/>
                  <dsp:param name="pagesAvailable" bean="QueryFormHandler.pagesAvailable"/>
                  <dsp:param name="pageNum" bean="QueryFormHandler.searchResponse.pageNum"/>
                  <dsp:param name="prMagFST" bean="FacetSearchTools"/>
                </dsp:include>
              </div>
            </div> 
          </div>
          
          <dsp:include page="../castCatalog/includes/rightNavigationArea.jsp" flush="true">
            <dsp:param name="categoryId" value="${categoryId}" />
            <dsp:param name="navAction" value="push" />
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet>
      
      <c:if test="${bonnesAffaires}">
          <%-- Omniture params Section begins--%>
        <fmt:message var="namePart1" key="omniture.pageName.catalogue"/>
        <fmt:message var="omnitureChannel" key="omniture.channel.catalogue"/>
                
        <c:set var="omniturePageName" value="${namePart1}${categoryName}" scope="request"/>
            <c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
            <%-- Omniture params Section ends--%>  
      </c:if>
      
    </jsp:attribute>
    <jsp:attribute name="canonicalUrl">
      <dsp:importbean bean="/com/castorama/droplet/CanonicalLinkDroplet"/>
      <dsp:droplet name="CanonicalLinkDroplet">
        <dsp:param name="type" value="pivotCategory"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="canonicalUrl" param="url"/>
        </dsp:oparam>
      </dsp:droplet>
      ${canonicalUrl}
    </jsp:attribute>
  </cast:pageContainer>
  
    </c:otherwise>
  </c:choose>
    
</dsp:page>
