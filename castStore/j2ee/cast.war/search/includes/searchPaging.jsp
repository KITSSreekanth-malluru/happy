<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/For"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/com/castorama/droplet/CastQueryStringDroplet"/>
  <dsp:getvalueof var="containerVar" value="${requestScope['javax.servlet.forward.request_uri']}"/>
  <c:if test="${empty containerVar}">
    <dsp:getvalueof var="containerVar" param="container"/>
  </c:if>

  <dsp:getvalueof var="pagesAvailableVar"  param="pagesAvailable" />
  
  <dsp:getvalueof var="pageNum"  param="pageNum" />
  
  <c:if test="${pagesAvailableVar != null && pagesAvailableVar > 1}">
    
    <c:if test="${not empty pageNum && pageNum != '' }">
      <c:set var="pageNum"  value="${pageNum + 1}" />
    </c:if>
    <c:if test="${not empty pageNum && pageNum != '' }">
      <dsp:getvalueof var="currentIndexVar"  value="${pageNum }" />
    </c:if>
    <c:if test="${empty pageNum || pageNum == '' }">
      <c:set var="currentIndexVar"  value="1" />
      <dsp:setvalue param="pageNum"  value="1" />
    </c:if>
        
    <dsp:droplet name="CastQueryStringDroplet">
      <dsp:param name="trail" param="prMagFST.facetTrail"/>
      <dsp:param name="sortByValue" param="sortByValue"/>
      <dsp:param name="productListingView" param="productListingView"/>
      <dsp:param name="currentTab" param="currentTab"/>
      <dsp:param name="osearchmode" param="osearchmode"/>
      <dsp:param name="names" value="trail,sortByValue,productListingView,currentTab,osearchmode"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="qString" param="qString"/>
      </dsp:oparam>
    </dsp:droplet>
    
    <div class="precedentDivProds">
    &nbsp;
    <c:if test="${currentIndexVar > 1}"><%-- pagesAvailableVar > 5 - was removed --%>
      <dsp:a href="${containerVar}?${qString}" iclass="precedent">
        <fmt:message key="search_searchPaging.precedent"/>
        <dsp:param name="pageNum" value="${currentIndexVar -1}"/>
      </dsp:a>
    </c:if>
    </div>
    
    <script>
      function pageSelectedProds(selectedValue) {
          if (selectedValue != '${currentIndexVar}') {
              var qString = '${qString}';
        	  if (qString != null && qString != '') {
            	  location.href='${containerVar}?${qString}&pageNum='+ selectedValue;
        	  } else {
        		  location.href='${containerVar}?pageNum='+ selectedValue;
        	  }
          }
      }
    </script>
    <div class="pageSelectDivProds">
    <div class="pageSelectMessageProds"><fmt:message key="search_searchPaging.page"/></div>
    <div class="pageSelectInputProds">
    <select class="styled" id="pageIndexingSelectProds" rel="pageSelectedProds">              
	    <c:set var="showIndexVar" value="1"/>
	    <dsp:droplet name="For">
	    <dsp:param name="howMany" value="${pagesAvailableVar}"/>
	    <dsp:oparam name="output">
	      <dsp:droplet name="Switch">
	        <dsp:param name="value" value="${pageNum }" />
	        <dsp:getvalueof id="pageValue" value="${showIndexVar }" />
            <dsp:oparam name="${pageValue}">
              <option value="${currentIndexVar}" selected>${currentIndexVar}/${pagesAvailableVar}</option>
            </dsp:oparam>
	        <dsp:oparam name="default">
	           <option value="${showIndexVar}">${showIndexVar}/${pagesAvailableVar}</option>
	        </dsp:oparam>
	      </dsp:droplet>
	      
	      <c:set var="showIndexVar" value="${showIndexVar + 1 }"/>
	      </dsp:oparam>
	    </dsp:droplet>
    </select>
    </div>
    </div>
    
    <div class="suivantDivProds">
    <c:if test="${currentIndexVar < pagesAvailableVar}">
      <dsp:a href="${containerVar}?${qString}" iclass="suivant">
          <fmt:message key="search_searchPaging.next"/>
          <dsp:param name="pageNum" value="${currentIndexVar + 1}"/>
  	  </dsp:a>
  	</c:if>
  	&nbsp;
  	</div>
	
  </c:if>
  
</dsp:page>