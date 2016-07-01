
<dsp:page>
 <jsp:useBean id="random" class="com.castorama.RandomBean" scope="session"/>

       <c:choose>
       <c:when test="${empty  targetRandom }">
           <c:set var="targetRandom" value="${random.nextInt}" scope="session"/>  
        </c:when>
       </c:choose>
   
          
  <dsp:getvalueof var="oneTargeterTemplate" bean="/com/castorama/CastConfiguration.oneTargeterTemplate" />
  <dsp:getvalueof var="pageContextPlusSlash" value="${pageContext.request.contextPath}/" />
  <dsp:getvalueof var="pageContextHomePage" value="${pageContext.request.contextPath}/home.jsp" />
  <c:choose>
    <c:when test="${oneTargeterTemplate && (fn:endsWith(pageContext.request.requestURL, pageContext.request.contextPath) 
    	|| fn:endsWith(pageContext.request.requestURL, pageContextPlusSlash) 
    	|| fn:endsWith(pageContext.request.requestURL, pageContextHomePage))}">

      <cast:pageContainer>
      
      <jsp:attribute name="metaInfoInclude">/global/meta.jsp?item=category</jsp:attribute>
      <jsp:attribute name="isHome">true</jsp:attribute>
      <jsp:attribute name="bodyContent">
			      
	   
        <dsp:include page="/castCommon/homePageTargeter.jsp" flush="true">
      
        </dsp:include>
      
    	<%-- Omniture params Section begins--%>
        <fmt:message var="omniturePageName" key="omniture.pageName.homePage"/>
	    <fmt:message var="omnitureChannel" key="omniture.channel.homePage"/>    	
	  
	    <c:set var="omniturePageName" value="${omniturePageName}" scope="request"/>
	    <c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
	    <%-- Omniture params Section ends--%>
    	
      </jsp:attribute>
      
      </cast:pageContainer>
      
    </c:when>
    <c:otherwise>
    
      <cast:pageContainer>
      
      <jsp:attribute name="topBanners">true</jsp:attribute>
      <jsp:attribute name="bottomBanners">true</jsp:attribute>
      <jsp:attribute name="metaInfoInclude">/global/meta.jsp?item=category</jsp:attribute>
      <jsp:attribute name="cloudType">page</jsp:attribute>
      <jsp:attribute name="isHome">true</jsp:attribute>
      <jsp:attribute name="bodyContent">
        <dsp:include page="/castCatalog/contentHomePageLayer.jsp" flush="true"/>
    	
        <%-- Omniture params Section begins--%>
        <fmt:message var="omniturePageName" key="omniture.pageName.homePage"/>
	    <fmt:message var="omnitureChannel" key="omniture.channel.homePage"/>    	
	  
	    <c:set var="omniturePageName" value="${omniturePageName}" scope="request"/>
	    <c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
	    <%-- Omniture params Section ends--%>
    	
      </jsp:attribute>
      
      </cast:pageContainer>
    
    </c:otherwise>
  </c:choose>

</dsp:page>
