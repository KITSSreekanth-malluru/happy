<dsp:page>
 
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="containerVar" param="magasinContainer" />
  <dsp:getvalueof var="questionVar" param="question" />
  <dsp:getvalueof var="magasinQFH" bean="/com/castorama/magasin/MagasinSearchFormHandler" />
  
  
  <c:if test="${empty containerVar }">
    <c:set var="containerVar" value="${requestURI }"/>
  </c:if>
  
  <%-- redirect search rule re-factoring 
  dsp:include page="magasins/magasinResubmitSearchRequest.jsp" flush="true">
  </dsp:include
  --%>
  
  <dsp:droplet name="/com/castorama/search/droplet/SearchEnvironmentStatus">
    <dsp:param name="searchEnvironmentTypes" value="magasin"/>
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
            <dsp:valueof value="${fn:trim(questionVar) }" valueishtml="true"/>
           <fmt:message key="search_searchResultsContainer.bracket">
           </fmt:message>
        </h1>
      </div>
      
      <dsp:include page="includes/searchSuggestionTerm.jsp" flush="true">
        <dsp:param name="suggestion" value="${magasinQFH.searchResponse.spellingTerms[0].suggestions[0]}"/>
        <dsp:param name="container" value="${containerVar }"/>
      </dsp:include>
 
 
    <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
      <dsp:param name="value" value="${magasinQFH.searchResponse.results}"/>
      <dsp:oparam name="false">
      <%@ include file="includes/currentTab.jspf" %>
      
      <dsp:include page="includes/searchResultsTabs.jsp" flush="true">
        <dsp:param name="activeTab" value="${activeTabVar }"/>
        <dsp:param name="magasinSearchResponse" value="${magasinQFH.searchResponse}"/>
        <dsp:param name="questionVar" value="${questionVar }" />
      </dsp:include>
      
    
      <div class="content"> 
      
      <dsp:include page="includes/magasinFilters.jsp" flush="true">
        <dsp:param name="magasinContainer" value="${containerVar }"/>
        <dsp:param name="queryResponse" value="${magasinQFH.searchResponse}"/>
        <dsp:param name="magasinFST" bean="/com/castorama/magasin/MagasinFacetSearchTools"/>
      </dsp:include>
      
      <div class="prodGalContainer prodWithLong">
        <dsp:include page="includes/searchPageTitle.jsp" flush="true">
          <dsp:param name="searchResponse" value="${magasinQFH.searchResponse}"/>
          <dsp:param name="isSearchResult" value="true"/>
          <dsp:param name="searchPageTitleMessage" value="search_searchPageTitle.magSearchResultsCount"/>
        </dsp:include>
    
        <dsp:include page="magasins/magasinSearchResults.jsp" flush="true">
          <dsp:param name="magasinResults" value="${magasinQFH.searchResponse.results}"/>
        </dsp:include>
        
        <div class="paginator">
          <div class="bluePage">
            <dsp:include page="includes/magasinSearchPaging.jsp" flush="true">
              <dsp:param name="container" value="${containerVar }"/>
              <dsp:param name="pagesAvailable" value="${magasinQFH.pagesAvailable}"/>
              <dsp:param name="pageNum" value="${magasinQFH.searchResponse.pageNum}"/>
              <dsp:param name="prMagFST" bean="/com/castorama/magasin/MagasinFacetSearchTools"/>
            </dsp:include>        
          </div>
        </div>
      </div>
      </div>
      </dsp:oparam>
        <dsp:oparam name="true">
          <dsp:include page="includes/emptySearchResult.jsp" flush="true">
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet>
      
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>