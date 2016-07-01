<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="promoInformation" param="promoInformation"/>
  <dsp:getvalueof var="width" param="width"/>
  <dsp:getvalueof var="height" param="height"/>
  <dsp:getvalueof var="flashId" param="flashId"/>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />

  <%@ include file="/castCommon/promoInformationTargeter.jspf" %>
</dsp:page>