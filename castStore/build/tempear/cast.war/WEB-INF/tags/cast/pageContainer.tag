<%--
  title (optional) - title for page. "Castorama.fr" is used if empty
  metaInfoInclude (optional) - page that will generate meta tags.
  metaKeyword (optional) - content of meta tag keywords(used if param metaInfoInclude is empty).
  metaDescription (optional) - content of meta tag description (used if param metaInfoInclude is empty).
  
  topBanners (optional) - indicates if top banners section should be generated.
  bottomBanners (optional) - indicates if top banners section should be generated.
  cardPriceBanner (optional) - indicates if Castorama card price banner section should be generated.
  
  bodyContent - body content
  cloudType(optional) - type of the cloud (will be passed to bottombannersandcloud.jsp)

  bvInclude(optional) - Bazaarvoice css, js includes: one of "view" or "submit" values.
--%>
<%@ include file="/includes/taglibs.jspf" %>
<%@ tag language="java" %>
<%@ attribute name="title" %>
<%@ attribute name="metaInfoInclude" %>
<%@ attribute name="canonicalUrl" %>
<%@ attribute name="metaKeyword" %>
<%@ attribute name="metaDescription" %>

<%@ attribute name="topBanners" %>
<%@ attribute name="bottomBanners" %>
<%@ attribute name="cardPriceBanner" %>

<%@ attribute name="bodyContent" fragment="true" %>
<%@ attribute name="cloudType" %>
<%@ attribute name="bvInclude" %>
<%@ attribute name="isHome" %>
<%
  response.setHeader("Cache-Control","no-store"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP 1.0
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<dsp:include page="/includes/checkOnCastoramaStaff.jsp"/>
<dsp:include page="/includes/page_start.jsp">
  <dsp:param name="metaInfoInclude" value="${metaInfoInclude}"/>
  <dsp:param name="canonicalUrl" value="${canonicalUrl}"/>
  <dsp:param name="metaKeyword" value="${metaKeyword}"/>
  <dsp:param name="metaDescription" value="${metaDescription}"/>
  <dsp:param name="title" value="${title}"/>
  <dsp:param name="bvInclude" value="${bvInclude}"/>
</dsp:include>

<%-- Header gadgets --%>
<dsp:include page="/castCommon/header.jsp" flush="true"/>

<dsp:include page="/navigation/topmenu.jsp" flush="true">
  <dsp:param name="isHome" value="${isHome}"/>
</dsp:include>

<dsp:getvalueof var="oneTargeterTemplate" bean="/com/castorama/CastConfiguration.oneTargeterTemplate" />
<dsp:getvalueof var="pageContextPlusSlash" value="${pageContext.request.contextPath}/" />
<dsp:getvalueof var="pageContextHomePage" value="${pageContext.request.contextPath}/home.jsp" />
<c:choose>
  <c:when test="${oneTargeterTemplate && (fn:endsWith(pageContext.request.requestURL, pageContext.request.contextPath) 
      || fn:endsWith(pageContext.request.requestURL, pageContextPlusSlash)
      || fn:endsWith(pageContext.request.requestURL, pageContextHomePage))}">
     
     <jsp:invoke fragment="bodyContent"/>
     
  </c:when>
  <c:otherwise>
    
    <c:if test="${not empty topBanners}">
    <dsp:include page="/castCommon/topbanners.jsp" flush="true"/>
  </c:if>

  <jsp:invoke fragment="bodyContent"/>
  <c:if test="${not empty bottomBanners}">
    <dsp:include page="/castCommon/bottombannersandcloud.jsp" flush="true">
      <dsp:param name="cloudType" value="${cloudType}"/>
      <dsp:param name="cardPriceBanner" value="${cardPriceBanner}"/>
    </dsp:include>
  </c:if>
  
  <div class="clear"><!--~--></div>
  <dsp:include page="/castCommon/freeText.jsp" flush="true"/>
  
  </c:otherwise>
</c:choose>

<dsp:include page="/castCommon/subscription.jsp" flush="true"/>
<dsp:include page="/castCommon/reinsuranceSection.jsp" flush="true"/>
<dsp:include page="/castCommon/footer.jsp" flush="true">
  <dsp:param name="omniture-channel"           value="${omnitureChannel}"/>
</dsp:include>

<dsp:include page="/includes/omniture.jsp">
  <dsp:param name="omniture-pageName"          value="${omniturePageName}"/>
  <dsp:param name="omniture-channel"           value="${omnitureChannel}"/>
  <dsp:param name="omniture-eventType"         value="${omnitureEventType}"/>
  <dsp:param name="omniture-products"          value="${omnitureProducts}"/>
  <dsp:param name="omniture-purchaseID"        value="${omniturePurchaseID}"/>
  <dsp:param name="omniture-searchKeyword"     value="${omnitureSearchKeyword}"/>
  <dsp:param name="omniture-searchResults"     value="${omnitureSearchResults}"/>
  <dsp:param name="omniture-pState"            value="${omniturePState}"/>
  <dsp:param name="omniture-newsletterRegistration"  value="${omnitureNewsletterRegistration}"/>
  <dsp:param name="xForwardedFor"              value="${xForwardedFor}"/>
</dsp:include>

<dsp:include page="/includes/tag_commander.jsp">
  <%-- add parameters to pass to page --%>
</dsp:include>
<dsp:getvalueof var="activateOmniture" bean="/com/castorama/CastConfiguration.activateOmniture" />
<c:if test="${activateOmniture}">        
   <dsp:include page="/includes/googleAnalytics.jspf"/>
</c:if>
<dsp:include page="/includes/page_end.jsp"/>
