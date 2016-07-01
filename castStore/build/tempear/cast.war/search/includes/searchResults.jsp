<dsp:page>
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
  <dsp:getvalueof var="productListingView" param="productListingView"/>
  <dsp:getvalueof var="osearchmode" param="osearchmode"/>
  <c:choose>
	  <c:when test="${osearchmode == 'reg'}">
	  	<dsp:getvalueof var="osearchmode" value="reg" scope="request"/>
	  </c:when>
	  <c:when test="${osearchmode == 'tagcloud'}">
	  	<dsp:getvalueof var="osearchmode" value="tagcloud" scope="request"/>
	  </c:when>
	  <c:when test="${empty osearchmode || (osearchmode != 'reg' and osearchmode != 'tagcloud')}">
	  	<dsp:getvalueof var="osearchmode" value="" scope="request"/>
	  </c:when>
  </c:choose>
  
  <dsp:droplet name="/com/castorama/droplet/MultiStockVisAvailabilityDroplet">
    <dsp:param name="searchResults" param="results" />
    <dsp:param name="store" bean="/atg/userprofiling/Profile.currentLocalStore" />
    <dsp:oparam name="output">
      <c:choose>
      <c:when test="${empty svAvailableMap}">
        <dsp:getvalueof var="svAvailableMap" param="svAvailableMap" scope="request"/>
      </c:when>
      <c:otherwise>
        <dsp:getvalueof var="addToSvAvailableMap" param="svAvailableMap" />
        ${castCollection:putAll(svAvailableMap, addToSvAvailableMap)}
      </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:getvalueof var="productDeliverySkusIds" value="" scope="request"/>
  
  <c:choose>
    <c:when test="${not empty isSearchResult && isSearchResult}">
      <%--<dsp:getvalueof var="requestURL" bean="/OriginatingRequest.requestURIWithQueryString"/>
      <dsp:getvalueof var="question" param="question"/>
      <dsp:setvalue bean="com/castorama/util/LastViewedPage.lastVisitedUrl" value="${requestURL}"/>
      <dsp:setvalue bean="com/castorama/util/LastViewedPage.searchQuestion" value="${question}"/>
      <dsp:getvalueof var="postParametesNames" bean="/OriginatingRequest.postParameterNames"/>
      <c:if test="${not empty postParameterNames}">
        <c:forEach var="key" items="${postParameterNames}">
            <dsp:getvalueof var="paramVal" bean="/OriginatingRequest.postParameter"/>
            ${castCollection:add(postParameterNames, key, )}
        </c:forEach>
      </c:if>--%>
      <c:choose>
        <c:when test="${productListingView == 'gallery' }">
         <dsp:include page="searchPageGalleryTemplate.jsp">
           <dsp:param name="results" param="results"/>
           <dsp:param name="container" param="container"/>
           <dsp:param name="navAction" value="jump"/>
           <dsp:param name="isSearchResult" param="isSearchResult"/>
         </dsp:include>
        </c:when>
        <c:otherwise>
          <dsp:include page="../../castCatalog/includes/searchPageListTemplate.jsp">
             <dsp:param name="results" param="results"/>
             <dsp:param name="container" param="container"/>
             <dsp:param name="navAction" value="jump"/>
             <dsp:param name="isSearchResult" param="isSearchResult"/>
           </dsp:include>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${productListingView == 'gallery' }">
          <dsp:include page="../../castCatalog/includes/pageGalleryTemplate.jsp">
            <dsp:param name="results" param="results"/>
            <dsp:param name="container" param="container"/>
            <dsp:param name="navAction" value="push"/>
            <dsp:param name="navCount" param="navCount"/>
          </dsp:include>
        </c:when>
        <c:otherwise>
          <dsp:include page="../../castCatalog/includes/pageListTemplate.jsp">
            <dsp:param name="results" param="results"/>
            <dsp:param name="container" param="container"/>
           	<dsp:param name="navAction" value="push"/>
          </dsp:include>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
  
  <script type="text/javascript">
    $(document).ready(function(){
      multiDelivery('${productDeliverySkusIds}');
    });
  </script>
  
</dsp:page>
