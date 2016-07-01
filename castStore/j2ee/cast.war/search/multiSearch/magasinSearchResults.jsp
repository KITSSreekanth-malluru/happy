<dsp:page>

  <dsp:importbean bean="/com/castorama/magasin/MagasinSearchFormHandler"/>
  <dsp:getvalueof var="activeTabVar" param="activeTab"/>
  
 
  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
    <dsp:param name="value" param="magasinQFH.searchResponse.results"/>
    <dsp:oparam name="false">
    
     <c:choose>
      <c:when test="${not empty activeTabVar && activeTabVar == 'magasinTab'}">
        <div class="searchContent_active" id="magasinTab" > 
      </c:when>
      <c:otherwise>
        <div class="searchContent" id="magasinTab" > 
      </c:otherwise>
    </c:choose>
    
    <dsp:include page="../includes/magasinFilters.jsp" flush="true">
      <dsp:param name="magasinContainer" param="container"/>
      <dsp:param name="queryResponse" param="magasinQFH.searchResponse"/>
      <dsp:param name="magasinFST" param="magasinFST"/>
    </dsp:include>
    
    <div class="prodGalContainer prodWithLong">

     <dsp:include page="../includes/searchPageTitle.jsp" flush="true">
        <dsp:param name="searchResponse" param="magasinQFH.searchResponse"/>
        <dsp:param name="container" param="container"/>
        <dsp:param name="isSearchResult" value="true"/>
        <dsp:param name="searchPageTitleMessage" value="search_searchPageTitle.magSearchResultsCount"/>
      </dsp:include>
      
      <dsp:include page="../magasins/magasinSearchResults.jsp" flush="true">
        <dsp:param name="magasinResults" param="magasinQFH.searchResponse.results"/>
        <dsp:param name="container" param="container"/>
        <dsp:param name="isSearchResult" value="true"/>
      </dsp:include>
      
      <div class="paginator">
        <div class="bluePage">
          <dsp:include page="../includes/magasinSearchPaging.jsp" flush="true">
            <dsp:param name="container" param="container"/>
            <dsp:param name="pagesAvailable" param="magasinQFH.pagesAvailable"/>
            <dsp:param name="pageNum" param="magasinQFH.searchResponse.pageNum"/>
            <dsp:param name="prMagFST" param="magasinFST"/>
          </dsp:include>
        </div>
      </div>
    </div>
   </div>
  </dsp:oparam>
  </dsp:droplet>
  
</dsp:page>
