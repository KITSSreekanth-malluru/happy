<dsp:page>
<div class="breadcrumbs bluePage">
    <div class="homeBreadIco">
        <a href="${pageContext.request.contextPath}/home.jsp">
          <img title="" alt="" src="${pageContext.request.contextPath}/images/icoHomeGray.gif"/>
      </a>
  </div>
  <dsp:getvalueof var="element" param="element"/>
  <dsp:getvalueof var="magasinNom" param="magasinNom"/>
  <dsp:getvalueof var="isLatoutBreadCrumb" param="isLatoutBreadCrumb"/>
  <dsp:getvalueof var="pageType" param="pageType"/>
  <dsp:getvalueof var="subElement" param="subElement"/>
  
  
    
    <div class="splitter">&gt;</div>
  
  <div><a href="${pageContext.request.contextPath}/user/clientSpaceHome.jsp"><fmt:message key="client.breadcrumb.my.client" /></a></div>
  
  <div class="splitter">&gt;</div>
    
    <c:if test="${not empty isLatoutBreadCrumb && isLatoutBreadCrumb }">
      <div><a href="${pageContext.request.contextPath}/user/atoutAdvanteges.jsp"><fmt:message key="user_atoutAdvanteges.breadCrumb" /></a></div>
      <div class="splitter">&gt;</div>
    </c:if>    
    <c:choose>
    <c:when test="${'orders' == pageType}">
        <div class="active"><fmt:message key="client.orders.ref" /></div>
    </c:when>
    <c:when test="${'store.orders' == pageType}">
        <div class="active"><fmt:message key="client.store.orders.ref" /></div>
    </c:when>
    <c:when test="${'order' == pageType}">
        <div><a href="${pageContext.request.contextPath}/user/ordersHistory.jsp"><fmt:message key="client.orders.ref" /></a></div>
        <div class="splitter">&gt;</div>
        <div class="active"><fmt:message key="client.order.ref" />&nbsp;<dsp:valueof param="orderId" /></div>
    </c:when>
    <c:when test="${'contactUs' == pageType}">
        <div><a href="${pageContext.request.contextPath}/contactUs/faq.jsp"><fmt:message key="${subElement}" /></a></div>
        <div class="splitter">&gt;</div>
        <div class="active"><fmt:message key="${element }" /></div>
    </c:when>
    <c:otherwise>
      <div class="active"><fmt:message key="${element }"/>    
          <c:if test="${not empty magasinNom }">
            : <c:out value="${magasinNom}"/>
          </c:if> 
        </div>
    </c:otherwise>
  </c:choose>
</div>
</dsp:page>