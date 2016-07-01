<dsp:page>
<dsp:getvalueof var="element" param="element"/>
<dsp:getvalueof var="magasinNom" param="name"/>
<dsp:getvalueof var="isLatoutBreadCrumb" param="isLatoutBreadCrumb"/>

<div class="breadcrumbs bluePage">
    <div class="homeBreadIco">
      	<a href="${pageContext.request.contextPath}/home.jsp">
	        <img title="" alt="" src="${pageContext.request.contextPath}/images/icoHomeGray.gif"/>
	    </a>
	</div>
    <div class="splitter">&gt;</div>
	<div><a href="${pageContext.request.contextPath}/user/clientSpaceHome.jsp"><fmt:message key="client.breadcrumb.my.client" /></a></div>
	<div class="splitter">&gt;</div>
    <c:if test="${not empty isLatoutBreadCrumb && isLatoutBreadCrumb }">
      <div><a href="${pageContext.request.contextPath}/user/atoutAdvanteges.jsp"><fmt:message key="user_atoutAdvanteges.breadCrumb" /></a></div>
      <div class="splitter">&gt;</div>
    </c:if>
    <div class="active"><fmt:message key="${element }"/>
    <c:if test="${not empty magasinNom }">
    	: <c:out value="${magasinNom}"/>
    </c:if> 
    </div>
</div>
</dsp:page>