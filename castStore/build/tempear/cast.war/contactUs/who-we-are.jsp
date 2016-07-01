<dsp:page>
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
        <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
		<c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/qui-sommes-nous-accueil.html"/>
		
		<%-- Omniture params Section begins--%>
	   	<fmt:message var="omniturePageName" key="omniture.pageName.miscellaneous"/>
		<fmt:message var="omnitureChannel" key="omniture.channel.miscellaneous"/>
		    	
		<c:set var="omniturePageName" value="${omniturePageName}" scope="request"/>
		<c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
		<%-- Omniture params Section ends--%>
		
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>