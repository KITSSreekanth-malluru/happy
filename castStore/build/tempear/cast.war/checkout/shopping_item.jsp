<dsp:page>

  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>

  <dsp:include page="../castCatalog/includes/productViewTemplate.jsp">
    <dsp:param name="productId" param="product.repositoryId" />
     <dsp:param name="navAction" value="jump"/>
     <dsp:param name="noBreadcrumbs" value="true" />
  </dsp:include>

  <dsp:setvalue param="sku" paramvalue="product.childSKUs[0]"/>
  <dsp:droplet name="IsNull">
    <dsp:param name="value" param="sku" />
    <dsp:oparam name="true" />
    <dsp:oparam name="false">
      <dsp:getvalueof var="url" param="url" />
      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
      <dsp:droplet name="CastProductLinkDroplet">
        <dsp:param name="categoryId" value=""/>
        <dsp:param name="productId" param="product.repositoryId"/>
        <dsp:param name="navAction" param="navAction"/>
        <dsp:param name="navCount" param="navCount"/>
        <dsp:param name="isSearchResult" param="isSearchResult"/>
        <dsp:param name="sortByValue" param="sortByValue"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="templateUrl" param="url"/>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:droplet name="CastPriceRangeDroplet">
        <dsp:param name="productId" param="product.repositoryId"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="sku" param="sku"/>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:include page="../castCatalog/includes/addToCartSmall.jsp">
        <dsp:param name="sku" value="${sku}"/>
        <dsp:param name="productId" param="product.repositoryId"/>
        <dsp:param name="childSku" param="product.childSKUs"/>
        <dsp:param name="url" value="${contextPath}${templateUrl}"/>
        <dsp:param name="multiDeliveryMsg" value="false"/>
      </dsp:include>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
