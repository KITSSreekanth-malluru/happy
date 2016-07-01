<dsp:page>

  <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange" >
    <dsp:param name="itemDescriptor" value="fastLabConfigs" />
    <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog" />
    <dsp:param name="queryRQL" value="ALL" />
    <dsp:param name="howMany" value="1" />
    <dsp:param name="sortProperties" value="" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="fastLabConfigs" param="element" />
    </dsp:oparam>
  </dsp:droplet>
  <dsp:setvalue param="fastLabConfigs" value="${fastLabConfigs}" />
  
  <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
  <dsp:getvalueof var="showServiceClient2012Flap" bean="/com/castorama/CastConfiguration.showServiceClient2012Flap" />
  <c:choose>
  <c:when test="${showServiceClient2012Flap}">
    <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
      <dsp:param name="value" param="fastLabConfigs.reinsuranceSectionFlap" />
      <dsp:oparam name="false"> 
        <dsp:getvalueof var="reinsuranceSectionFlapHtml" param="fastLabConfigs.reinsuranceSectionFlap.htmlUrl" />
        <c:if test="${not empty reinsuranceSectionFlapHtml}">
	      <c:import charEncoding="utf-8" url="${staticContentPath}${reinsuranceSectionFlapHtml}"/>
	    </c:if>
      </dsp:oparam>
    </dsp:droplet>
  </c:when>
  <c:otherwise>
    <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
      <dsp:param name="value" param="fastLabConfigs.reinsuranceSection" />
      <dsp:oparam name="false"> 
        <dsp:getvalueof var="reinsuranceSectionHtml" param="fastLabConfigs.reinsuranceSection.htmlUrl" />
        <c:if test="${not empty reinsuranceSectionHtml}">
	      <c:import charEncoding="utf-8" url="${staticContentPath}${reinsuranceSectionHtml}"/>
	    </c:if>
      </dsp:oparam>
    </dsp:droplet>
  </c:otherwise>
  </c:choose>

</dsp:page>
