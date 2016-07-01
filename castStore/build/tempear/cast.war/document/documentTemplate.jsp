<dsp:page>
  <dsp:include page="/castCatalog/breadcrumbsCollector.jsp" >
    <dsp:param name="documentId" param="documentId"/>
    <dsp:param name="navAction" param="navAction"/>
    <dsp:param name="navCount" param="navCount"/>
  </dsp:include>

  <cast:pageContainer>
    <jsp:attribute name="metaInfoInclude">/global/meta.jsp?id=<dsp:valueof param="documentId"/>&item=castoramaDocument</jsp:attribute>
    <jsp:attribute name="bottomBanners">true</jsp:attribute>
    <jsp:attribute name="cloudType">advice</jsp:attribute>
    <jsp:attribute name="bvInclude">view</jsp:attribute>
    <jsp:attribute name="bodyContent">
      <dsp:include page="/document/includes/documentTemplate.jsp" flush="true"/>
    </jsp:attribute>
    <jsp:attribute name="canonicalUrl">
      <dsp:droplet name="/com/castorama/droplet/CanonicalLinkDroplet">
          <dsp:param name="type" value="castoramaDocument"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="canonicalUrl" param="url"/>
            </dsp:oparam>
    </dsp:droplet>
    ${canonicalUrl}
  </jsp:attribute>
  </cast:pageContainer>
</dsp:page>