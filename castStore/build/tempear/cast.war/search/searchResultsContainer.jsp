<dsp:page>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="containerVar" param="container" />
  
  <dsp:getvalueof var="produitsQFH" bean="/atg/commerce/search/catalog/QueryFormHandler" />
  
 
  <dsp:getvalueof var="questionVar" param="question" />
  
  <!-- slide right panel with shopping list -->
  <dsp:include page="/shoppingList/shoppingListSlider.jsp">
    <dsp:param name="currentPage" value="product"/> 
  </dsp:include>

  <c:if test="${empty containerVar }">
    <c:set var="containerVar" value="${requestURI }"/>
  </c:if>
  
  
  <dsp:droplet name="/com/castorama/search/droplet/SearchEnvironmentStatus">
    <dsp:param name="searchEnvironmentTypes" value="product"/>
    <dsp:oparam name="unavailable">
       <dsp:include page="includes/searchUnavailable.jsp" flush="true">
       </dsp:include>
    </dsp:oparam>
    <dsp:oparam name="initialized">
    
    <div class="pageTitle blueTitle">
      <h1>
      
        <c:if test="${empty questionVar && (questionVar != 'null' || questionVar != null)}">
          <c:set var="questionVar" value=""/>
         </c:if>
         <fmt:message key="search_searchResultsContainer.searchFor">
         </fmt:message>
          <dsp:valueof value="${fn:trim(questionVar) }" valueishtml="false"/>
         <fmt:message key="search_searchResultsContainer.bracket">
         </fmt:message>
      </h1>

    </div>

    <dsp:include page="/castCommon/topSearchBanner.jsp" flush="true"/>

    <%--  redirect search rule re-factoring 
    dsp:include page="includes/resubmitSearchRequest.jsp" flush="true">
      <dsp:param name="searchResponse" value="${produitsQFH.searchResponse}"/>
    </dsp:include--%>
    
    <dsp:include page="includes/searchSuggestionTerm.jsp" flush="true">
      <dsp:param name="suggestion" value="${produitsQFH.searchResponse.spellingTerms[0].suggestions[0]}"/>
      <dsp:param name="container" value="${containerVar }"/>
    </dsp:include>
   
     <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
      <dsp:param name="value" value="${produitsQFH.searchResponse.results}"/>
      <dsp:oparam name="false">
     
        <%@ include file="includes/currentTab.jspf" %>
  
    
        <dsp:include page="includes/searchResultsTabs.jsp" flush="true">
          <dsp:param name="activeTab" value="${activeTabVar }"/>
          <dsp:param name="searchResponse" value="${produitsQFH.searchResponse}"/>
          <dsp:param name="questionVar" value="${questionVar }" />
        </dsp:include>
      
        <div class="content">
      	
      	<dsp:include page="includes/filters.jsp" flush="true">
      	  <dsp:param name="queryResponse" value="${produitsQFH.searchResponse}"/>
      	  <dsp:param name="container" value="${containerVar }"/>
            <dsp:param name="produitsFST" bean="/atg/search/repository/FacetSearchTools"/>
  	    </dsp:include>
        	
      	<div class="prodGalContainer prodWithLong">
      	  <dsp:include page="includes/searchPageTitle.jsp" flush="true">
      		<dsp:param name="searchResponse" value="${produitsQFH.searchResponse}"/>
      		<dsp:param name="container" value="${containerVar }"/>
            <dsp:param name="isSearchResult" value="true"/>
            <dsp:param name="searchPageTitleMessage" value="search_searchPageTitle.searchResultsCount"/>
      	  </dsp:include>
      	  <dsp:include page="includes/searchSorter.jsp" flush="true">
      		<dsp:param name="searchResponse" value="${produitsQFH.searchResponse}"/>
      		<dsp:param name="container" value="${containerVar }"/>
            <dsp:param name="isSearchResult" value="true"/>
            <dsp:param name="produitsFST" bean="/atg/search/repository/FacetSearchTools"/>
      	  </dsp:include>
      	
      	  <dsp:include page="includes/searchResults.jsp" flush="true">
            <dsp:param name="results" value="${produitsQFH.searchResponse.results}"/>
            <dsp:param name="container" value="${containerVar }"/>
            <dsp:param name="isSearchResult" value="true"/>
          </dsp:include>
          
          <div class="paginator">
            <div class="bluePage">
              <dsp:include page="includes/searchPaging.jsp" flush="true">
          	 <dsp:param name="container" value="${containerVar }"/>
               <dsp:param name="pagesAvailable" value="${produitsQFH.pagesAvailable}"/>
               <dsp:param name="pageNum" value="${produitsQFH.searchResponse.pageNum}"/>                 
               <dsp:param name="prMagFST" bean="/atg/search/repository/FacetSearchTools"/>
        	    </dsp:include>
            </div>
          </div>
          
          </div>
         </div>
      </dsp:oparam>
      <dsp:oparam name="true">
        <dsp:include page="includes/emptySearchResult.jsp" flush="true">
            <dsp:param name="question" value="${questionVar}"/>
        </dsp:include>
      </dsp:oparam>
    </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
    
</dsp:page>
