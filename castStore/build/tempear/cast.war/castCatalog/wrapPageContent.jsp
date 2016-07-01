<dsp:page>
  <cast:pageContainer>
    <jsp:attribute name="metaInfoInclude">/global/meta.jsp?id=<dsp:valueof param="promoId"/>&item=promoInformation</jsp:attribute>
    <jsp:attribute name="bodyContent">
      <dsp:importbean bean="/atg/commerce/catalog/PromoInformationLookup"/>
      
      <dsp:getvalueof var="promoId" param="promoId"/>

      <dsp:droplet name="PromoInformationLookup">
        <dsp:param name="id" value="${promoId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="url" param="element.url"/>
        </dsp:oparam>
      </dsp:droplet>
      <div class="content">
        <c:if test="${not empty url}">
          <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
          <c:import charEncoding="utf-8" url="${staticContentPath}${url}"/>
        </c:if>      
      </div>
    </jsp:attribute>
    <jsp:attribute name="canonicalUrl">
		<dsp:droplet name="/com/castorama/droplet/CanonicalLinkDroplet">
	    	<dsp:param name="type" value="promoInformation"/>
	      	<dsp:oparam name="output">
	      		<dsp:getvalueof var="canonicalUrl" param="url"/>
	      		<c:if test="${empty canonicalUrl || (canonicalUrl == '') || (canonicalUrl == 'null')}">
                  <dsp:getvalueof var="promoId" param="promoId"/>
	      		  <dsp:getvalueof var="canonicalUrl" value="/store/pages/${promoId}.html"/>
	      		</c:if>
	      	</dsp:oparam>
	      </dsp:droplet>
		${canonicalUrl}
    </jsp:attribute>  
  </cast:pageContainer>
</dsp:page>