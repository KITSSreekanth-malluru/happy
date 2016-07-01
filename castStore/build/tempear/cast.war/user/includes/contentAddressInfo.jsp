<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

  <dsp:getvalueof var="prefix" param="address.prefix" />
  <dsp:getvalueof var="firstName" param="address.firstName" />
  <dsp:getvalueof var="lastName" param="address.lastName" />
  <dsp:getvalueof var="address1" param="address.address1" />
  <dsp:getvalueof var="address2" param="address.address2" />
  <dsp:getvalueof var="address3" param="address.address3" />
  <dsp:getvalueof var="locality" param="address.locality" />
  <dsp:getvalueof var="postalCode" param="address.postalCode" />
  <dsp:getvalueof var="city" param="address.city" />
  <dsp:getvalueof var="phoneNumber" param="address.phoneNumber" />
  <dsp:getvalueof var="phoneNumber2" param="address.phoneNumber2" />
  
  <c:choose>
    <c:when test="${'miss' == prefix}">
      <fmt:message key="msg.address.prefix.miss" />&nbsp;
    </c:when>
    <c:when test="${'mrs' == prefix}">
      <fmt:message key="msg.address.prefix.mrs" />&nbsp;
    </c:when>
    <c:when test="${'mr' == prefix}">
      <fmt:message key="msg.address.prefix.mr" />&nbsp;
    </c:when>
  </c:choose>
  <c:if test="${not empty firstName || not empty lastName}">
   <c:out value="${firstName}" />&nbsp;<c:out value="${lastName}" /><br />
  </c:if>
  <c:if test="${not empty address1}">
    <c:out value="${address1}" /><br />
  </c:if>
  <c:if test="${not empty address2}">
  	<c:out value="${address2}" /><br />
  </c:if>
  <c:if test="${not empty address3}">
  	<c:out value="${address3}" /><br />
  </c:if>
  <c:if test="${not empty locality}">
  	<c:out value="${locality}" /><br />
  </c:if>
  <c:if test="${not empty postalCode || not empty city}">
    <c:out value="${postalCode}" />&nbsp;<c:out value="${city}" /><br />
  </c:if>

  <c:if test="${not empty phoneNumber}">
    <c:out value="${phoneNumber}" /><br />
  </c:if>
  <c:if test="${not empty phoneNumber2}">
    <c:out value="${phoneNumber2}" />
  </c:if>
