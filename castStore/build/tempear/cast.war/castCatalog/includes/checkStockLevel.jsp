<dsp:page>

<dsp:importbean bean="/com/castorama/stockvisualization/StockVisualizationDroplet" />
<dsp:importbean bean="/com/castorama/stockvisualization/StockModelHolder" />
<dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
<dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>
<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
<dsp:importbean bean="/atg/userprofiling/Profile" />

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<dsp:getvalueof var="productId" param="productId" />
<dsp:getvalueof var="featuredProduct" param="featuredProduct" />
<dsp:getvalueof var="categoryId" param="${categoryId}" />
<dsp:getvalueof var="featuredSkuId" param="${featuredSkuId}" />
<dsp:getvalueof var="documentProduct" param="documentProduct" />
<dsp:getvalueof var="templateUrl" param="templateUrl" />
<dsp:getvalueof var="pSkuId" param="skuId" />


<dsp:droplet name="SKULookup">
  <dsp:param name="id" value="${pSkuId}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="codeArticle" param="element.codeArticle"/>
  </dsp:oparam>
</dsp:droplet>

<dsp:getvalueof var="localStore" bean="Profile.currentLocalStore"/>
<dsp:getvalueof var="localStoreId" bean="Profile.currentLocalStore.id"/>
<c:if test="${(not empty localStore) and (localStoreId != '999')}">
  <dsp:droplet name="StockVisualizationDroplet">
    <dsp:param name="queryCondition" value="byDefinedStore" />
    <dsp:param name="prodId" value="${codeArticle}" />
    <dsp:param name="magasinId" value="${localStoreId}" />
  </dsp:droplet>
  <dsp:getvalueof var="userFavoriteStoreStock" bean="StockModelHolder.definedStoreStock" />
</c:if>
<dsp:include page="/svTest/svHiddenFormInputs.jsp" />

<c:choose>
  <c:when test="${featuredProduct}">
    <dsp:droplet name="CastProductLinkDroplet">
      <dsp:param name="productId" value="${productId}"/>
      <dsp:param name="categoryId" param="${categoryId}"/>
      <dsp:param name="navAction" value="jump"/>
      <dsp:param name="navCount" param="navCount"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="templateUrl" param="url"/>
        <c:set var="href" value="${contextPath}${templateUrl}#stockArea" />
      </dsp:oparam>
    </dsp:droplet>
  </c:when>
  <c:when test="${documentProduct}">
    <dsp:getvalueof var="href" value="${contextPath}${templateUrl}#stockArea" />
  </c:when>
  <c:otherwise>
    <c:set var="href" value="javascript:void(0);" />
  </c:otherwise>
</c:choose>

<div id="topSVButtonRefreshableContent">
  <fmt:message var="available" key="stockVisualization.available" />
  <fmt:message var="limQuantity" key="stockVisualization.limitedQuantity" />
  <fmt:message var="notAvailable" key="stockVisualization.notAvailable" />
  
  <c:if test="${not empty userFavoriteStoreStock}">
    <dsp:getvalueof var="stockStoreId" value="${userFavoriteStoreStock.storeId}"/>
    <dsp:setvalue bean="SessionBean.values.stockVisuProdID" value="${userFavoriteStoreStock.prodId}"/>
  </c:if>
  
  <c:choose>
    <c:when test="${(not empty userFavoriteStoreStock) and 
                    ((userFavoriteStoreStock.status == '1') or (userFavoriteStoreStock.status == '2'))}">
      <c:if test="${userFavoriteStoreStock.status == '1'}">
        <c:if test="${not empty stockStoreId}">
          <dsp:setvalue bean="SessionBean.values.stockVisuStatus" value="${available}-${stockStoreId}"/>
        </c:if>
        <dsp:a href="${href}" style="text-decoration: none;" type="button" iclass="buttonStockAvailable"
            id="topSVButtonRefreshableContentLink" >
          <p>
            <fmt:message key="stockVisualization.availableStatusLink" /><br />
            <strong>${userFavoriteStoreStock.magasinName}</strong>
          </p>
          <c:if test="${documentProduct}">
            <dsp:param name="skuId" value="${pSkuId}"/>
          </c:if>
          <c:if test="${featuredProduct}">
            <dsp:param name="skuId" value="${featuredSkuId}"/>
            <dsp:param name="isFeaturedProduct" value="true"/>
          </c:if>
        </dsp:a>
      </c:if>
      <c:if test="${userFavoriteStoreStock.status == '2'}">
      <c:if test="${not empty stockStoreId}">
        <dsp:setvalue bean="SessionBean.values.stockVisuStatus" value="${limQuantity}-${stockStoreId}"/>
      </c:if>
        <dsp:a href="${href}" style="text-decoration: none;" type="button" iclass="buttonStockLimitedAvailability" 
               id="topSVButtonRefreshableContentLink" >
          <p>
            <fmt:message key="stockVisualization.limitedAvailabilityStatusLink" /><br />
            <strong>${userFavoriteStoreStock.magasinName}</strong>
          </p>
          <c:if test="${documentProduct}">
            <dsp:param name="skuId" value="${pSkuId}"/>
          </c:if>
          <c:if test="${featuredProduct}">
            <dsp:param name="skuId" value="${featuredSkuId}"/>
            <dsp:param name="isFeaturedProduct" value="true"/>
          </c:if>
        </dsp:a>
      </c:if>
    </c:when>
    <c:otherwise>
      <c:if test="${not empty stockStoreId}">
        <dsp:setvalue bean="SessionBean.values.stockVisuStatus" value="${notAvailable}-${stockStoreId}"/>
      </c:if>
    
      <dsp:a href="${href}" style="text-decoration: none;" type="button" iclass="buttonCheckStock"
             id="topSVButtonRefreshableContentLink" >
        <c:if test="${documentProduct}">
          <dsp:param name="skuId" value="${pSkuId}"/>
        </c:if>
        <c:if test="${featuredProduct}">
          <dsp:param name="skuId" value="${featuredSkuId}"/>
          <dsp:param name="isFeaturedProduct" value="true"/>
        </c:if>
      </dsp:a>
    </c:otherwise>
  </c:choose>
</div>

</dsp:page>