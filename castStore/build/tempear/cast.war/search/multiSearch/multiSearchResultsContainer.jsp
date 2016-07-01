<dsp:page>
  <dsp:importbean bean="/atg/commerce/search/catalog/QueryFormHandler"/>
  <dsp:importbean bean="/com/castorama/document/CastDocQueryFormHandler"/>
  <dsp:importbean bean="/com/castorama/magasin/MagasinSearchFormHandler"/>
  
  <dsp:importbean bean="/com/castorama/commerce/search/refinement/CastFacetSearchTools"/>
  <dsp:importbean bean="/com/castorama/commerce/search/refinement/CastFacetTrailDroplet"/>
  
  <dsp:importbean bean="/com/castorama/search/MultiFacetedSearchSessionBean"/>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="questionVar" param="question" />

  <dsp:getvalueof var="containerVar" param="container"/>
  
  <%-- redirect search rule re-factoring 
  dsp:include page="multiResubmitSearchRequest.jsp" flush="true">
  </dsp:include --%>
  
  <dsp:getvalueof var="produitsQFH" bean="MultiFacetedSearchSessionBean.values.produitsQFH"/>
  <dsp:getvalueof var="produitsFST" bean="MultiFacetedSearchSessionBean.values.produitsFST"/>
  
  <dsp:getvalueof var="docQFH" bean="MultiFacetedSearchSessionBean.values.docQFH"/>
  <dsp:getvalueof var="docFST" bean="MultiFacetedSearchSessionBean.values.docFST"/>
  
  <dsp:getvalueof var="magasinQFH" bean="MultiFacetedSearchSessionBean.values.magasinQFH"/>
  <dsp:getvalueof var="magasinFST" bean="MultiFacetedSearchSessionBean.values.magasinFST"/>
  
  <!-- slide right panel with shopping list -->
  <dsp:include page="/shoppingList/shoppingListSlider.jsp">
    <dsp:param name="currentPage" value="product"/> 
  </dsp:include>


  <dsp:droplet name="/com/castorama/search/droplet/SearchEnvironmentStatus">
    <dsp:param name="searchEnvironmentTypes" value="product:document:magasin"/>
    <dsp:oparam name="unavailable">
       <dsp:include page="/search/includes/searchUnavailable.jsp" flush="true">
       </dsp:include>
    </dsp:oparam>
    <dsp:oparam name="initialized">
  
  <dsp:include page="/castCommon/topSearchBanner.jsp" flush="true"/>


  
  <c:choose>
    <c:when test="${(empty  produitsQFH || fn:length(produitsQFH.searchResponse.results) == 0)&& (empty docQFH || fn:length(docQFH.searchResponse.results) == 0)&&( empty magasinQFH || fn:length(magasinQFH.searchResponse.results) == 0)}">
      <dsp:include page="../includes/emptySearchResult.jsp" flush="true">
      </dsp:include>
        <dsp:include page="../includes/searchSuggestionTerm.jsp" flush="true">
            <c:choose>
                <c:when test="${not empty  produitsQFH.searchResponse.spellingTerms[0].suggestions[0]}">
                    <dsp:param name="suggestion" value="${produitsQFH.searchResponse.spellingTerms[0].suggestions[0]}"/>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${not empty  magasinQFH.searchResponse.spellingTerms[0].suggestions[0]}">
                            <dsp:param name="suggestion" value="${magasinQFH.searchResponse.spellingTerms[0].suggestions[0]}"/>
                        </c:when>
                        <c:otherwise>
                            <dsp:param name="suggestion" value="${docQFH.searchResponse.spellingTerms[0].suggestions[0]}"/>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
            <dsp:param name="container" value="${containerVar }"/>
        </dsp:include>
    </c:when>
    <c:otherwise>
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
        <dsp:include page="../includes/searchSuggestionTerm.jsp" flush="true">
            <c:choose>
                <c:when test="${not empty  produitsQFH.searchResponse.spellingTerms[0].suggestions[0]}">
                    <dsp:param name="suggestion" value="${produitsQFH.searchResponse.spellingTerms[0].suggestions[0]}"/>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${not empty  magasinQFH.searchResponse.spellingTerms[0].suggestions[0]}">
                            <dsp:param name="suggestion" value="${magasinQFH.searchResponse.spellingTerms[0].suggestions[0]}"/>
                        </c:when>
                        <c:otherwise>
                            <dsp:param name="suggestion" value="${docQFH.searchResponse.spellingTerms[0].suggestions[0]}"/>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
            <dsp:param name="container" value="${containerVar }"/>
        </dsp:include>
      <%@ include file="../includes/currentTab.jspf" %> 
    
      <dsp:include page="../includes/searchResultsTabs.jsp" flush="true">
        <dsp:param name="activeTab" value="${activeTabVar }"/>
        <dsp:param name="searchResponse" value="${produitsQFH.searchResponse}"/>
        <dsp:param name="docSearchResponse" value="${docQFH.searchResponse}"/>
        <dsp:param name="magasinSearchResponse" value="${magasinQFH.searchResponse}"/>
        <dsp:param name="questionVar" value="${questionVar }" />
      </dsp:include>
    
      <dsp:include page="productsSearchResults.jsp" flush="true">
       <dsp:param name="container" value="${containerVar }" />
       <dsp:param name="currentTab" value="produitsTab"/>
       <dsp:param name="activeTab" value="${activeTabVar }"/>
       <dsp:param name="produitsQFH" value="${produitsQFH }"/>
       <dsp:param name="produitsFST" value="${produitsFST }"/>
      </dsp:include>
      
      <dsp:include page="docSearchResults.jsp" flush="true">
        <dsp:param name="container" value="${containerVar }" />
        <dsp:param name="currentTab" value="ideasTab"/>
        <dsp:param name="activeTab" value="${activeTabVar }"/>
        <dsp:param name="docQFH" value="${docQFH }"/>
        <dsp:param name="docFST" value="${docFST }"/>
        <dsp:param name="isSearchResult" value="true"/>
      </dsp:include>
      
      <dsp:include page="magasinSearchResults.jsp" flush="true">
        <dsp:param name="container" value="${containerVar }" />
        <dsp:param name="currentTab" value="magasinTab"/>
        <dsp:param name="activeTab" value="${activeTabVar }"/>
        <dsp:param name="magasinQFH" value="${magasinQFH }"/>
        <dsp:param name="magasinFST" value="${magasinFST }"/>
      </dsp:include>
	 </c:otherwise>
  </c:choose>
  </dsp:oparam>
 </dsp:droplet>
 
</dsp:page>