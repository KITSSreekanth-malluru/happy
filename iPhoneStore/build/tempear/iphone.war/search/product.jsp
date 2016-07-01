<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/atg/commerce/catalog/ProductBrowsed" />
  <dsp:importbean bean="/atg/dynamo/droplet/Compare" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/com/castorama/droplet/CastPriceItem" />
  <dsp:importbean bean="/com/castorama/droplet/CastPriceDroplet" />
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet" />
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup" />
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="mobileConfig" bean="/atg/mobile/MobileConfiguration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
  <jsp:useBean id="now" class="java.util.Date" />
  <dsp:getvalueof var="repositoryId" param="repositoryId" />
  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" value="${repositoryId}" />
    <dsp:param name="elementName" value="product" />
    <dsp:oparam name="output">
      <m:jsonObject>
        <json:property name="productId" value="${repositoryId}" />
        <json:property name="label">
          <dsp:valueof param="product.displayName" valueishtml="true" />
        </json:property>
        <json:property name="img">
          ${httpLink}<dsp:valueof param="product.smallImage.URL" />
        </json:property>
      </m:jsonObject>
      <%--CastPriceRangeDroplet--%>
    </dsp:oparam>
  </dsp:droplet>
  <%--ProductLookup--%>
</dsp:page>