<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler"/>
  <dsp:importbean bean="/com/castorama/profile/CastLoginFormHandler"/>
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="closeImageVar" param="closeImage"/>
  
	<dsp:droplet name="/atg/dynamo/droplet/Compare">
	<dsp:param bean="/atg/userprofiling/Profile.securityStatus" name="obj1"/>
	<dsp:param bean="/atg/userprofiling/PropertyManager.securityStatusCookie" name="obj2"/>
	<dsp:oparam name="lessthan">
	  <%-- if user logged out --%>
	  <a href="${pageContext.request.contextPath}/user/login.jsp">
	  <img src="${closeImageVar}" alt="Mon espace client"/>
	  <fmt:message key="header.connect" />
	  </a>
	</dsp:oparam>
	<dsp:oparam name="default">
    <%-- if user logged in --%>
    <dsp:form action="logout-link.jsp" method="post" iclass="formless" id="logoutForm">
      <a href="javascript:document.getElementById('logout').click()">
        <img src="${closeImageVar}" alt="Mon espace client"/>
        <fmt:message key="header.log.out" /> 
      </a>
      <dsp:input bean="CastProfileFormHandler.logout" type="submit" value="Valider" id="logout" style="display:none"/>
      <dsp:input bean="CastProfileFormHandler.logoutSuccessURL" type="hidden" value="${pageContext.request.contextPath}/user/login.jsp"/>
    </dsp:form>
	</dsp:oparam>	
	</dsp:droplet>
</dsp:page>