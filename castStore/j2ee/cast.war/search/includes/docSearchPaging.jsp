<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/For"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:getvalueof var="containerVar" param="docContainer"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <dsp:getvalueof var="pagesAvailableVar"  param="docPagesAvailable" />
  <dsp:getvalueof var="pageNum"  param="pageNum" />
 
  <c:if test="${pagesAvailableVar != null && pagesAvailableVar > 1}">
    <c:if test="${not empty pageNum && pageNum != '' }">
      <c:set var="pageNum"  value="${pageNum + 1}" />
    </c:if>

    <c:if test="${not empty pageNum && pageNum != '' }">
      <c:set var="currentIndexVar"  value="${pageNum }" />
    </c:if>
    <c:if test="${empty pageNum || pageNum == '' }">
      <c:set var="currentIndexVar"  value="1" />
      <dsp:setvalue param="pageNum"  value="1" />
    </c:if>
    
    <dsp:getvalueof var="searchType" param="searchType"/>
    <dsp:getvalueof var="question" param="question"/>
    <c:choose>
      <c:when test="${searchType == 1}">
        <dsp:getvalueof var="seachTypePattern" value="4"/>
      </c:when>
      <c:when test="${searchType == 2}">
        <dsp:getvalueof var="seachTypePattern" value=""/>
      </c:when>
      <c:when test="${searchType == 3}">
        <dsp:getvalueof var="seachTypePattern" value="2"/>
      </c:when>
      <c:otherwise>
        <dsp:getvalueof var="seachTypePattern" value="3"/>
      </c:otherwise>
    </c:choose>
    
    <div class="precedentDivIdeas">
    &nbsp;
  	<c:if test="${currentIndexVar > 1}"><%--pagesAvailableVar > 5 --%>
  	  <dsp:a href="${contextPath}/rechercher${seachTypePattern}/${question}" iclass="precedent">
  		<fmt:message key="search_searchPaging.precedent"/>
  		<dsp:param name="docTrail" param="docFST.facetTrail"/>
  		<dsp:param name="docCategoryId" param="docCategoryId"/>
        <dsp:param name="pageNum" value="${currentIndexVar -1}"/>
        <dsp:param name="currentTab" param="currentTab"/>
        <dsp:param name="sortByValue" param="sortByValue"/>
  	  </dsp:a>
  	</c:if>
  	</div>
  	
    <dsp:getvalueof var="docTrailParam" param="docFST.facetTrail"/>
    <dsp:getvalueof var="docCategoryIdParam" param="docCategoryId"/>
    <dsp:getvalueof var="currentTabParam" param="currentTab"/>
    <dsp:getvalueof var="sortByValueParam" param="sortByValue"/>
    <script>
      function pageSelectedIdeas(selectedValue) {
          if (selectedValue != '${currentIndexVar}') {
              location.href='${contextPath}/rechercher${seachTypePattern}/${question}?docTrail=${docTrailParam}&docCategoryId=${docCategoryIdParam}&currentTab=${currentTabParam}&sortByValue=${sortByValueParam}&pageNum='+ selectedValue;
          }
      }
    </script>
    <div class="pageSelectDivIdeas">
    <div class="pageSelectMessageIdeas"><fmt:message key="search_searchPaging.page"/></div>
    <div class="pageSelectInputIdeas">
    <select class="styled" id="pageIndexingSelectIdeas" rel="pageSelectedIdeas">              
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
  	
  	<div class="suivantDivIdeas">
  	<c:if test="${currentIndexVar < pagesAvailableVar}">
  	  <dsp:a href="${contextPath}/rechercher${seachTypePattern}/${question}" iclass="suivant">
  		<fmt:message key="search_searchPaging.next"/>
  		<dsp:param name="docTrail" param="docFST.facetTrail"/>
  		<dsp:param name="docCategoryId" param="docCategoryId"/>
        <dsp:param name="pageNum" value="${currentIndexVar + 1}"/>
        <dsp:param name="currentTab" param="currentTab"/>
        <dsp:param name="sortByValue" param="sortByValue"/>
  	  </dsp:a>
    </c:if>
    &nbsp;
  	</div>
  </c:if>
</dsp:page>