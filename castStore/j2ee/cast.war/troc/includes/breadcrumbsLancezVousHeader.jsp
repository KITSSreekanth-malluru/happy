<dsp:page>
<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>


<div class="breadcrumbs bluePage">
    <div class="homeBreadIco">
        <a href="${pageContext.request.contextPath}">
          <img title="" alt="" src="${pageContext.request.contextPath}/images/icoHomeGray.gif"/>
      </a>
  </div>

    <div class="splitter">&gt;</div>
    <c:choose>
      <c:when test="${originatingRequest.servletPath == '/lancez-vous.jsp'}">
        <div class="active"><fmt:message key="troc.breadcrumb.home" /></div>
      </c:when>
      <c:otherwise>
          <div><a href="${pageContext.request.contextPath}/lancez-vous.jsp"><fmt:message key="troc.breadcrumb.home" /></a></div>
        <dsp:getvalueof var="topic" param="topic.title"/>
        <c:if test="${not empty topic}">
          <div class="splitter">&gt;</div>
          <div class="active">${topic}</a></div>
        </c:if>
      </c:otherwise>
    </c:choose>
  
  
</div>
</dsp:page>