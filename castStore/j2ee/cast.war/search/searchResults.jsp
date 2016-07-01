<dsp:page>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  
  <c:choose>
    <c:when test="${not empty param.searchType && param.searchType == '2'}">
       <%@ include file="includes/resubmitSearchRequest.jspf" %>
    </c:when>
    <c:when test="${not empty param.searchType && param.searchType == '3'}">
      <%@ include file="includes/docResubmitSearchRequest.jspf" %>
    </c:when>
    <c:when test="${not empty param.searchType && param.searchType == '4'}">
      <%@ include file="magasins/magasinResubmitSearchRequest.jspf" %>
    </c:when>
    <c:otherwise>
      <%@ include file="multiSearch/multiResubmitSearchRequest.jspf" %>
    </c:otherwise>        
  </c:choose>
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
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
      <dsp:getvalueof var="containerVar" value="${contextPath}/rechercher${seachTypePattern}/${question}"/>
      <c:choose>
        <c:when test="${not empty param.searchType && param.searchType == '2'}">
          <dsp:include page="/search/searchResultsContainer.jsp" flush="true">
            <dsp:param name="container" value="${containerVar}"/>
          </dsp:include>
        </c:when>
        <c:when test="${not empty param.searchType && param.searchType == '3'}">
          <dsp:include page="/search/docSearchResultsContainer.jsp" flush="true">
            <dsp:param name="container" value="${containerVar}"/>
          </dsp:include>
        </c:when>
        <c:when test="${not empty param.searchType && param.searchType == '4'}">
          <dsp:include page="/search/magasinSearchResultsContainer.jsp" flush="true">
            <dsp:param name="container" value="${containerVar}"/>
          </dsp:include>
        </c:when>
        <c:otherwise>
          <dsp:include page="/search/multiSearch/multiSearchResultsContainer.jsp" flush="true">
            <dsp:param name="container" value="${containerVar}"/>
          </dsp:include>
        </c:otherwise>        
      </c:choose>
      
      <%-- Omniture params Section begins--%>
      <fmt:message var="namePart1" key="omniture.pageName.catalogue"/>
	  <fmt:message var="omnitureChannel" key="omniture.channel.catalogue"/>
	  
      <c:choose>
        <c:when test="${not empty param.isBrand && param.isBrand == 'true'}">
        	<fmt:message var="namePart2" key="facet.label.MarqueCommerciale"/>
          	<c:set var="omniturePageName" value="${namePart1}${namePart2}:${param.question}" scope="request"/>
        </c:when>
        <c:otherwise>
          	<fmt:message var="namePart2" key="omniture.searchResults"/>
          	<c:set var="omniturePageName" value="${namePart1}${namePart2}" scope="request"/>
        </c:otherwise>        
      </c:choose>
      
      <c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
      
      <dsp:getvalueof var="groupCount" bean="/atg/commerce/search/catalog/QueryFormHandler.searchResponse.groupCount" />
	     <dsp:getvalueof var="questionVar" param="question" />
	     
	     <dsp:getvalueof var="pageNum" param="pageNum" />
	     <c:if test="${empty pageNum or (not empty pageNum and pageNum==1) }">
	        <c:set var="omnitureSearchKeyword" value="${questionVar}" scope="request"/>
          <c:set var="omnitureSearchResults" value="${groupCount}" scope="request"/>
	     </c:if>
     
      
	  <%-- Omniture params Section ends--%>
		
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>