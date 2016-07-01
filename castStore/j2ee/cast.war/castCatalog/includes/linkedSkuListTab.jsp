<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/droplet/LinkedSkuTabNamesIteratorDroplet"/>
  <dsp:importbean bean="/com/castorama/util/ProductTabs"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="listColor" param="listColor"/>
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>

  <a name="prodTabs"></a>

    <dsp:droplet name="/com/castorama/droplet/StyleLookupDroplet">
      <dsp:param name="categoryId" param="categoryId"/>
       <dsp:oparam name="output">
        <dsp:getvalueof var="tabStyle" param="style.tabStyle"/>
       </dsp:oparam>
    </dsp:droplet>

    <dsp:droplet name="LinkedSkuTabNamesIteratorDroplet">
      <dsp:param name="bundleLinks" param="product.childSKUs[0].bundleLinks"/>
      <dsp:param name="assemblyOptions" param="product.assemblyOptions"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="groupedProdMap" param="groupedProdMap"/>
        
        <fmt:message key="castCatalog_tabNames.technic" var="technic"/>
        <dsp:getvalueof var="technicDescription" param="product.childSKUs[0].LibelleClientLong"/>
        <c:if test="${not empty technicDescription}">
          <dsp:getvalueof var="pageName" value="technicTab.jsp"/>
          <dsp:setvalue bean="ProductTabs.tabs.technicTab.page" value="${pageName}" />
          <dsp:setvalue bean="ProductTabs.tabs.technicTab.title" value="${technic}" />
        </c:if>

        <fmt:message key="castCatalog_tabNames.complementary" var="complementary"/>
        <dsp:getvalueof var="fixedRelatedProducts" param="product.childSKUs[0].crossSelling"/>
        <c:if test="${not empty fixedRelatedProducts}">
          <dsp:getvalueof var="pageName" value="complementaryTab.jsp"/>
          <dsp:setvalue bean="ProductTabs.tabs.complementaryTab.title" value="${complementary}" />
          <dsp:setvalue bean="ProductTabs.tabs.complementaryTab.page" value="${pageName}" />
        </c:if>

        <dsp:droplet name="ForEach">
          <dsp:param name="array" param="tabName"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="tabName" param="element"/>
            <dsp:droplet name="Switch">
              <dsp:param name="value" value="${tabName}"/>
              <dsp:oparam name="default">
                <dsp:getvalueof var="pageName" value="defaultTab.jsp"/>
                <dsp:setvalue bean="ProductTabs.tabs.${tabName}.page" value="${pageName}" />
                <dsp:setvalue bean="ProductTabs.tabs.${tabName}.title" value="${tabName}" />
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>

        <fmt:message var="review" key="castCatalog_tabNames.review" />
        <dsp:setvalue bean="ProductTabs.tabs.ratingCommentsTab.page" value="ratingCommentsTab.jsp" />
        <dsp:setvalue bean="ProductTabs.tabs.ratingCommentsTab.title" value="${review}" />
        <dsp:setvalue bean="ProductTabs.tabs.ratingCommentsTab.visible" value="false" />

        <dsp:setvalue param="groupedProdMap" value="${groupedProdMap}"/>
        <dsp:getvalueof var="skuId" param="product.childSKUs[0].repositoryId"/>
        <dsp:setvalue param="skuId" value="${skuId}"/>
        <dsp:setvalue param="listColor" value="${listColor}"/>
        <%@ include file="productTabbedPane.jspf" %>
        <div class="clear"></div>
      </dsp:oparam>
    </dsp:droplet>

  <div class="clear"></div>
</dsp:page>