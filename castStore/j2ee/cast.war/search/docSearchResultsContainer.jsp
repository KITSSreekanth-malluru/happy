<dsp:page>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="containerVar" param="docContainer" />
  <dsp:getvalueof var="questionVar" param="question" />
  
  <dsp:getvalueof var="docQFH" bean="/com/castorama/document/CastDocQueryFormHandler" />
  
  
  <c:if test="${empty containerVar }">
    <c:set var="containerVar" value="${requestURI }"/>
  </c:if>
  
  <%-- redirect search rule re-factoring 
  dsp:include page="includes/docResubmitSearchRequest.jsp" flush="true">
    <dsp:param name="docQFH" value="${docQFH }"/>
  </dsp:include--%>
  
  <dsp:droplet name="/com/castorama/search/droplet/SearchEnvironmentStatus">
    <dsp:param name="searchEnvironmentTypes" value="document"/>
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
    
    <dsp:include page="includes/searchSuggestionTerm.jsp" flush="true">
      <dsp:param name="suggestion" value="${docQFH.searchResponse.spellingTerms[0].suggestions[0]}"/>
      <dsp:param name="container" value="${containerVar }"/>
    </dsp:include>
  
    
  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
    <dsp:param name="value" value="${docQFH.searchResponse.results}"/>
    <dsp:oparam name="false">
    
    
    <%@ include file="includes/currentTab.jspf" %>
    
    <dsp:include page="includes/searchResultsTabs.jsp" flush="true">
      <dsp:param name="activeTab" value="${activeTabVar }"/>
      <dsp:param name="docSearchResponse" value="${docQFH.searchResponse}"/>
      <dsp:param name="questionVar" param="${questionVar }" />
    </dsp:include>
    
  
    <div class="content">	
  	
  	<dsp:include page="includes/docFilters.jsp" flush="true">
  	  <dsp:param name="docContainer" value="${containerVar }"/>
        <dsp:param name="queryResponse" value="${docQFH.searchResponse}"/>
        <dsp:param name="docFST" bean="/com/castorama/commerce/search/refinement/CastDocFacetSearchTools"/>
  	</dsp:include>
  	
  	<div class="prodGalContainer prodWithLong">
      	<dsp:include page="includes/searchPageTitle.jsp" flush="true">
            <dsp:param name="searchResponse" value="${docQFH.searchResponse}"/>
            <dsp:param name="isSearchResult" value="true"/>
      	  <dsp:param name="searchPageTitleMessage" value="search_docSearchPageTitle.searchResultsCount"/>
          </dsp:include>
    	  <dsp:include page="includes/docSearchResults.jsp" flush="true">
            <dsp:param name="docResults" value="${docQFH.searchResponse.results}"/>
            <dsp:param name="isSearchResult" value="true"/>
          </dsp:include>
          <div class="paginator">
            <div class="bluePage">
              <dsp:include page="includes/docSearchPaging.jsp" flush="true">
          	     <dsp:param name="docContainer" value="${containerVar }"/>
                 <dsp:param name="docPagesAvailable" value="${docQFH.pagesAvailable}"/>
                 <dsp:param name="pageNum" value="${docQFH.searchResponse.pageNum}"/>
                 <dsp:param name="docFST" bean="/com/castorama/commerce/search/refinement/CastDocFacetSearchTools"/>
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