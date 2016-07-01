<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
    <dsp:importbean bean="/atg/commerce/catalog/ProductCatalog" /> 
    <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange" >
        <dsp:param name="itemDescriptor" value="fastLabConfigs" />
        <dsp:param name="repository" bean="ProductCatalog" />
        <dsp:param name="queryRQL" value="ALL" />
        <dsp:param name="howMany" value="1" />
        <dsp:param name="sortProperties" value="" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="fastLabConfig" param="element"/>
        </dsp:oparam>
    </dsp:droplet>
    <dsp:setvalue param="fastLabConfig" value="${fastLabConfig}" />
  <cast:pageContainer>
 
    <jsp:attribute name="bodyContent">	
        <dsp:include page="/castCatalog/includes/errorMessageBlock.jsp">
            <dsp:param name="message" param="fastLabConfig.error404message"/>
        </dsp:include>
        <dsp:include page="/castCatalog/includes/top3categories.jsp">
            <dsp:param name="fastLabConfig" param="fastLabConfig"/>
        </dsp:include>
  </jsp:attribute>
  </cast:pageContainer>
  <!-- Fin content -->
</dsp:page>