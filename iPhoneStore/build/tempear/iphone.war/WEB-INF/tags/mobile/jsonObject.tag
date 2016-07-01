<%@ include file="/includes/taglibs.jspf" %>
<%@ tag language="java" %>
<%@ attribute name="name" %>
<c:choose>
  <c:when test="${!empty name}">
    <json:object name="${name}" prettyPrint="false">
      <jsp:doBody/>
    </json:object>
  </c:when>
  <c:otherwise>
    <json:object prettyPrint="false">
      <jsp:doBody/>
    </json:object>
  </c:otherwise>
</c:choose>
