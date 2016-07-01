<dsp:page>

  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="homePagePromo" param="homePagePromoBean"/>
  <dsp:getvalueof var="width" param="width"/>
  <dsp:getvalueof var="height" param="height"/>
  <dsp:getvalueof var="url" param="url"/>
  <dsp:getvalueof var="flashId" param="flashId"/>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  
  <dsp:droplet name="TargetingRandom">
    <dsp:param value="${homePagePromo}" name="targeter"/>
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:oparam name="output">
      <dsp:param name="promoInformation" param="element"/>
      <%@ include file="/castCommon/promoInformationTargeter.jspf" %>
    </dsp:oparam>
    <dsp:oparam name="empty">
      <div class="emptyBanner"></div>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>