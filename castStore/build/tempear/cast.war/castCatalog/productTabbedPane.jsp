<dsp:page>
  <dsp:importbean bean="/com/castorama/util/ProductTabs" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <dsp:setvalue bean="ProductTabs.tabs.packsTab.title" value="Packs" />
  <dsp:setvalue bean="ProductTabs.tabs.packsTab.page" value="includes/packsTab.jsp" />

  <fmt:message var="tab2" key="facet.label.Rating" />
  <dsp:setvalue bean="ProductTabs.tabs.complementaryTab.title" value="${tab2}" />
  <dsp:setvalue bean="ProductTabs.tabs.complementaryTab.page" value="includes/complementaryTab.jsp" />

  <dsp:getvalueof var="tabs" bean="ProductTabs.tabs" />
  <dsp:getvalueof var="tabname" value="Comments" />
  <dsp:getvalueof var="page" value="includes/complementaryTab.jsp" />
  ${castCollection:put(tabs,  tabname, page)}

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <%@ include file="includes/productTabbedPane.jspf" %>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>