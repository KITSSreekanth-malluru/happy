<%@page contentType="application/json"%>
<dsp:page>
  <dsp:getvalueof var="storeId" param="storeId"/>
  <dsp:getvalueof var="latitude" param="latitude"/>
  <dsp:getvalueof var="longitude" param="longitude"/>
  <c:choose>
    <c:when test="${not empty storeId}">
      <dsp:include page="/store/store.jsp" flush="true">
        <dsp:param name="storeId" param="storeId"/>
	    <dsp:param name="latitude" param="latitude"/>
        <dsp:param name="longitude" param="longitude"/>
      </dsp:include>
    </c:when>
    <c:when test="${empty storeId && not empty latitude && not empty longitude}">
      <dsp:include page="/store/storeByGeoLocation.jsp" flush="true">
        <dsp:param name="latitude" param="latitude"/>
        <dsp:param name="longitude" param="longitude"/>
        <dsp:param name="numberOfStoresInList" param="n"/>
      </dsp:include>
    </c:when>
    <c:otherwise>
      <m:jsonObject name="store">
         <json:property name="errorMessage">
           <fmt:message key="er_407"/>
         </json:property>
         <json:property name="errorCode" value="${1}"/>
      </m:jsonObject>
    </c:otherwise>
  </c:choose>
</dsp:page>