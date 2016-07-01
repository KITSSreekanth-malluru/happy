<dsp:page>

  <dsp:getvalueof var="activeTabVar" param="activeTab"/>
  
  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
    <dsp:param name="value" param="docQFH.searchResponse.results"/>
    <dsp:oparam name="false">
    
       <c:choose>
        <c:when test="${not empty activeTabVar && activeTabVar == 'ideasTab'}">
          <div class="searchContent_active" id="ideasTab" > 
        </c:when>
        <c:otherwise>
          <div class="searchContent" id="ideasTab" > 
        </c:otherwise>
      </c:choose>

      
        <dsp:include page="../includes/docFilters.jsp" flush="true">
          <dsp:param name="docFST" param="docFST"/>
          <dsp:param name="docContainer" param="container"/>
        </dsp:include>
      
        <div class="prodGalContainer prodWithLong">
          <dsp:include page="../includes/searchPageTitle.jsp" flush="true">
            <dsp:param name="searchResponse" param="docQFH.searchResponse"/>
            <dsp:param name="currentTab" param="currentTab"/>
            <dsp:param name="isSearchResult" value="true"/>
            <dsp:param name="searchPageTitleMessage" value="search_docSearchPageTitle.searchResultsCount"/>
          </dsp:include>
      
        
            <dsp:include page="../includes/docSearchResults.jsp" flush="true">
             <dsp:param name="docResults" param="docQFH.searchResponse.results"/>
             <dsp:param name="currentTab" param="currentTab"/>
             <dsp:param name="isSearchResult" param="isSearchResult"/>
            </dsp:include>
            
            <div class="paginator">
              <div class="bluePage">
                <dsp:include page="../includes/docSearchPaging.jsp" flush="true">
                  <dsp:param name="docContainer" param="container"/>
                  <dsp:param name="docPagesAvailable" param="docQFH.pagesAvailable"/>
                  <dsp:param name="pageNum" param="docQFH.searchResponse.pageNum"/>
                  <dsp:param name="docFST" param="docFST"/>
                  <dsp:param name="currentTab" param="currentTab"/>
                </dsp:include>
              </div>
            </div>
          </div>
        </div>
      
    </dsp:oparam>
  </dsp:droplet>
  
</dsp:page>