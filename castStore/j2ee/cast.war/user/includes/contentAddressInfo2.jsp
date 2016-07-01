<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

  <dsp:getvalueof var="prefix" param="group.prefix" />
  <dsp:getvalueof var="firstName" param="group.firstName" />
  <dsp:getvalueof var="lastName" param="group.lastName" />
  <dsp:getvalueof var="address1" param="group.address1" />
  <dsp:getvalueof var="address2" param="group.address2" />
  <dsp:getvalueof var="address3" param="group.address3" />
  <dsp:getvalueof var="postalCode" param="group.postalCode" />
  <dsp:getvalueof var="city" param="group.city" />
  <dsp:getvalueof var="phoneNumber" param="group.phoneNumber" />
  <dsp:getvalueof var="locality" param="group.locality" />
  <dsp:getvalueof var="phoneNumber2" param="group.phoneNumber2" />
  
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
  <c:out value="${firstName}" />
  &nbsp;<c:out value="${lastName}" /><br />
  <c:out value="${address1}" /><br />
  <c:if test="${not empty address2}">
  	<c:out value="${address2}" /><br />
  </c:if>
  <c:if test="${not empty address3}">
  	<c:out value="${address3}" /><br />
  </c:if>
  <c:if test="${not empty locality}">
  	<c:out value="${locality}" /><br />
  </c:if>
  <c:out value="${postalCode}" />&nbsp;<c:out value="${city}" /><br />


  <c:out value="${phoneNumber}" /><br />
  <c:out value="${phoneNumber2}" />&nbsp;
