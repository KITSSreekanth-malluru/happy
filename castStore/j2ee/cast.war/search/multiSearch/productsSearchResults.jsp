<dsp:page>

  <dsp:getvalueof var="activeTabVar" param="activeTab"/>

  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
    <dsp:param name="value" param="produitsQFH.searchResponse.results"/>
    <dsp:oparam name="false">
      <c:choose>
        <c:when test="${not empty activeTabVar && activeTabVar == 'produitsTab'}">
          <div class="searchContent_active" id="produitsTab" >
        </c:when>
        <c:otherwise>
          <div class="searchContent" id="produitsTab" >
        </c:otherwise>
      </c:choose>
    
       <dsp:include page="../includes/filters.jsp" flush="true">
  	     <dsp:param name="produitsFST" param="produitsFST"/>
  	     <dsp:param name="container" param="container"/>
  	   </dsp:include>
  	
  	   <div class="prodGalContainer prodWithLong">
  	     <dsp:include page="../includes/searchPageTitle.jsp" flush="true">
  		  <dsp:param name="searchResponse" param="produitsQFH.searchResponse"/>
  		  <dsp:param name="container" param="container"/>
          <dsp:param name="isSearchResult" value="true"/>
          <dsp:param name="currentTab" param="currentTab"/>
          <dsp:param name="searchPageTitleMessage" value="search_searchPageTitle.searchResultsCount"/>
  	     </dsp:include>
         
  	     <dsp:include page="../includes/searchSorter.jsp" flush="true">
  		  <dsp:param name="searchResponse" param="produitsQFH.searchResponse"/>
  		  <dsp:param name="container" param="container"/>
          <dsp:param name="currentTab" param="currentTab"/>
          <dsp:param name="isSearchResult" value="true"/>
          <dsp:param name="produitsFST" param="produitsFST"/>
  	     </dsp:include>
  	
  	     <dsp:include page="../includes/searchResults.jsp" flush="true">
          <dsp:param name="results" param="produitsQFH.searchResponse.results"/>
          <dsp:param name="container" param="container"/>
          <dsp:param name="isSearchResult" value="true"/>
          <dsp:param name="currentTab" param="currentTab"/>
        </dsp:include>
        <div class="paginator">
          <div class="bluePage">
            <dsp:include page="../includes/searchPaging.jsp" flush="true">
          	 <dsp:param name="container" param="container"/>
              <dsp:param name="pagesAvailable" param="produitsQFH.pagesAvailable"/>
              <dsp:param name="currentTab" param="currentTab"/>
              <dsp:param name="pageNum" param="produitsQFH.searchResponse.pageNum"/>
              <dsp:param name="prMagFST" param="produitsFST"/>
      	    </dsp:include>
          </div>
        </div>
      </div>
    </div>
  </dsp:oparam>
  </dsp:droplet>
  
</dsp:page>