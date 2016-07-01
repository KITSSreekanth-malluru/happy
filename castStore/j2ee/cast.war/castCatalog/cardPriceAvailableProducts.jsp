<dsp:page>
  <%@ include file="../search/includes/cardPriceResubmitSearchRequest.jspf" %>
  <cast:pageContainer>
    <jsp:attribute name="bottomBanners">true</jsp:attribute>
    <jsp:attribute name="bodyContent">
      
      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
      <dsp:getvalueof var="containerVar" value="${contextPath}/offres-carte-castorama"/>
      
      <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
      <dsp:getvalueof var="cardPriceProduitsQFH" bean="/atg/commerce/search/catalog/CardPriceAvailableQueryFormHandler" />
      
	<!-- slide right panel with shopping list -->
	<dsp:include page="/shoppingList/shoppingListSlider.jsp">
	  <dsp:param name="currentPage" value="product"/> 
	</dsp:include>

	 <c:if test="${empty containerVar}">
	   <c:set var="containerVar" value="${requestURI}"/>
	 </c:if>
  
	  <dsp:droplet name="/com/castorama/search/droplet/SearchEnvironmentStatus">
	    <dsp:param name="searchEnvironmentTypes" value="product"/>
	    <dsp:oparam name="unavailable">
	       <dsp:include page="/search/includes/searchUnavailable.jsp" flush="true">
	       </dsp:include>
	    </dsp:oparam>
	    <dsp:oparam name="initialized">

	     <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
	      <dsp:param name="value" value="${cardPriceProduitsQFH.searchResponse.results}"/>
	      <dsp:oparam name="false">

	        <div class="content">	
	      	
	      	<dsp:include page="/search/includes/filters.jsp" flush="true">
	      	  <dsp:param name="queryResponse" value="${cardPriceProduitsQFH.searchResponse}"/>
	      	  <dsp:param name="container" value="${containerVar}"/>
	          <dsp:param name="produitsFST" bean="/atg/search/repository/FacetSearchTools"/>
	  	    </dsp:include>
	        	
	      	<div class="prodGalContainer prodWithLong">
	      	  
			  <dsp:include page="/search/includes/searchPageTitle.jsp" flush="true">
                <dsp:param name="isCastCardPage" value="true"/>
                <dsp:param name="searchPageTitleMessage" value="search_searchPageTitle.castCardPage"/>
              </dsp:include>
			  
	      	  <dsp:include page="/search/includes/searchSorter.jsp" flush="true">
	      		<dsp:param name="searchResponse" value="${cardPriceProduitsQFH.searchResponse}"/>
	      		<dsp:param name="container" value="${containerVar}"/>
	            <dsp:param name="isSearchResult" value="true"/>
	            <dsp:param name="produitsFST" bean="/atg/search/repository/FacetSearchTools"/>
	      	  </dsp:include>
	      	
	      	  <dsp:include page="/search/includes/searchResults.jsp" flush="true">
	            <dsp:param name="results" value="${cardPriceProduitsQFH.searchResponse.results}"/>
	            <dsp:param name="container" value="${containerVar}"/>
	            <dsp:param name="isSearchResult" value="true"/>
	          </dsp:include>
	          
	          <div class="paginator">
	            <div class="bluePage">
	              <dsp:include page="/search/includes/searchPaging.jsp" flush="true">
	          	 <dsp:param name="container" value="${containerVar}"/>
	               <dsp:param name="pagesAvailable" value="${cardPriceProduitsQFH.pagesAvailable}"/>
	               <dsp:param name="pageNum" value="${cardPriceProduitsQFH.searchResponse.pageNum}"/>                 
	               <dsp:param name="prMagFST" bean="/atg/search/repository/FacetSearchTools"/>
	        	    </dsp:include>
	            </div>
	          </div>
	          
	          </div>
	         </div>
	      </dsp:oparam>
	      <dsp:oparam name="true">
	      </dsp:oparam>
	    </dsp:droplet>
	    </dsp:oparam>
	  </dsp:droplet>
	  
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>
