<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/droplet/CastSortSkuDropDownDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>

  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="featuredProduct" param="featuredProduct"/>
  <dsp:getvalueof var="productListingView" param="productListingView"/>

  <div class="sorter productTaille">
    <div class="ddWrapper">
	<input type="text" class="selectbox" autocomplete="off" readonly="" tabindex="0" style="width: 190px;" value="">
      <dsp:droplet name="ProductLookup">
        <dsp:param name="id" value="${productId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="choisePhrase" param="element.libelleListe"/>
          <c:if test="${empty choisePhrase || choisePhrase == ''}">
            <fmt:message var="choisePhrase" key="castCatalog_includes_skuDropDown.choose"/>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
    </div>
  </div>
</dsp:page>