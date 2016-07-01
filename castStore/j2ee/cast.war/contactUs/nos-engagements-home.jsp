<dsp:page>
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
        <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
		<c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/content-nos-engagements-home.html"/>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>