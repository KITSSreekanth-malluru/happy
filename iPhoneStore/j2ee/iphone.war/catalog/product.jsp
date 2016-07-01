<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductBrowsed"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceItem"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceDroplet"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>  
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="mobileConfig" bean="/atg/mobile/MobileConfiguration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
  <jsp:useBean id="now" class="java.util.Date" />

  <dsp:getvalueof var="objectName" param="objectName"/>
  <dsp:getvalueof var="repositoryId" param="repositoryId"/>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" value="${repositoryId}"/>
    <dsp:param name="elementName" value="product"/>
    <dsp:oparam name="output">
      <dsp:droplet name="CastPriceRangeDroplet">
        <dsp:param name="productId" value="${repositoryId}"/>
        <dsp:oparam name="output">
          
          <m:jsonObject>
              <json:property name="productId" value="${repositoryId}"/>
            <json:property name="title">
              <dsp:valueof param="product.displayName" valueishtml="true"/>
            </json:property>
            <json:property name="description">
              <dsp:valueof param="product.longDescription" valueishtml="true"/>
            </json:property>
            <dsp:droplet name="SKULookup">
              <dsp:param name="id" param="selectedSkuId"/>
              <dsp:param name="elementName" value="sku"/>
              <dsp:oparam name="output">
                <c:choose>
                  <c:when test="${not empty mobileConfig.retinaDynamicImages && mobileConfig.retinaDynamicImages}">
                    <dsp:getvalueof var="prImageUrl" param="sku.largeImage.url"/>
                    <dsp:getvalueof var="smImageUrl" param="sku.comparatorImage.url"/>                    
                  </c:when>
                  <c:otherwise>
                    <dsp:getvalueof var="prImageUrl" param="sku.comparatorImage.url"/>
                    <dsp:getvalueof var="smImageUrl" param="sku.carouselImage.url"/>
                  </c:otherwise>
                </c:choose>             
                
                
                <c:if test="${not empty prImageUrl && (!fn:startsWith(prImageUrl,'http') && !fn:startsWith(prImageUrl,'https'))}">
                  <c:set var="prImageUrl" value="${httpLink}${prImageUrl}"/>
                </c:if>
                <c:if test="${not empty smImageUrl && (!fn:startsWith(smImageUrl,'http') && !fn:startsWith(smImageUrl,'https'))}">
                  <c:set var="smImageUrl" value="${httpLink}${smImageUrl}"/>
                </c:if>
                
                <json:property name="img" value="${prImageUrl}"/>                
                <json:property name="smImg" value="${smImageUrl}"/>
                
                <dsp:droplet name="CastPriceItem">
                  <dsp:param name="product" param="product"/>
                  <dsp:param name="item" param="sku"/>
                  <dsp:param name="elementName" value="pricedItem"/>
                  <dsp:oparam name="output">
                    <dsp:include page="/catalog/includes/price.jsp">
                      <dsp:param name="pricedItem" param="pricedItem"/>
                    </dsp:include>
                  </dsp:oparam>
                </dsp:droplet>
               
                <json:property name="available" value=""/>
               
                <json:property name="selectedSkuId">
                  <dsp:valueof param="selectedSkuId"/>
                </json:property>
                <dsp:droplet name="/com/castorama/droplet/BrandLookupDroplet">
                  <dsp:param name="product" param="product"/>    
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="brandMediaURL" param="brand.mediaMarque.url"/>
                    <c:if test="${not empty brandMediaURL && (!fn:startsWith(brandMediaURL,'http') && !fn:startsWith(brandMediaURL,'https'))}">
                      <c:set var="brandMediaURL" value="${httpLink}${brandMediaURL}"/>
                    </c:if>
                    <json:property name="brandImgURL" value="${brandMediaURL}"/>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
              <dsp:oparam name="empty">
              
                <%-- %>dsp:getvalueof var="prImageUrl" param="product.childSkus[0].comparatorImage.url"/--%>
                <dsp:getvalueof var="prImageUrl" param="product.childSkus[0].largeImage.url"/>
                <c:if test="${not empty prImageUrl && (!fn:startsWith(prImageUrl,'http') && !fn:startsWith(prImageUrl,'https'))}">
                  <c:set var="prImageUrl" value="${httpLink}${prImageUrl}"/>
                </c:if>
                <json:property name="img" value="${prImageUrl}"/>
                
                <%-- %>dsp:getvalueof var="smImageUrl" param="product.childSkus[0].carouselImage.url"/--%>
                <dsp:getvalueof var="smImageUrl" param="product.childSkus[0].comparatorImage.url"/>
                <c:if test="${not empty smImageUrl && (!fn:startsWith(smImageUrl,'http') && !fn:startsWith(smImageUrl,'https'))}">
                  <c:set var="smImageUrl" value="${httpLink}${smImageUrl}"/>
                </c:if>
                <json:property name="smImg" value="${smImageUrl}"/>
                <dsp:droplet name="CastPriceItem">
                  <dsp:param name="product" param="product"/>
                  <dsp:param name="item" param="product.childSkus[0]"/>
                  <dsp:param name="elementName" value="pricedItem"/>
                  <dsp:oparam name="output">
                    <dsp:include page="/catalog/includes/price.jsp">
                      <dsp:param name="pricedItem" param="pricedItem"/>
                    </dsp:include>
                  </dsp:oparam>
                </dsp:droplet>
                <json:property name="available" value=""/>
                <json:property name="selectedSkuId">
                </json:property>
                <dsp:droplet name="/com/castorama/droplet/BrandLookupDroplet">
                  <dsp:param name="product" param="product"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="brandMediaURL" param="brand.mediaMarque.url"/>
                    <c:if test="${not empty brandMediaURL && (!fn:startsWith(brandMediaURL,'http') && !fn:startsWith(brandMediaURL,'https'))}">
                      <c:set var="brandMediaURL" value="${httpLink}${brandMediaURL}"/>
                    </c:if>
                    <json:property name="brandImgURL" value="${brandMediaURL}"/>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
            
            <dsp:droplet name="/com/castorama/mobile/droplet/PivotCategoryLookupDroplet">
                <dsp:param name="product" param="product"/>
                <dsp:param name="agRootCategory" bean="Profile.catalog.augmentedRealityCategory"/>
                <dsp:oparam name="output">
                    <dsp:getvalueof var="pivotCategoryId" param="pivotCategory.repositoryId"/>
                    <dsp:getvalueof var="agCategoryId" param="augmentedCategory.repositoryId"/>
                    <dsp:getvalueof var="pivotCategoryName" param="pivotCategory.displayName"/>
                    <json:property name="categoryId" value="${pivotCategoryId}"/>
                    <json:property name="categoryName" value="${pivotCategoryName}"/>
                </dsp:oparam>
            </dsp:droplet>
            
            <json:array name="skus">
              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" param="product.childSKUs"/>
                <dsp:param name="elementName" value="childSKU"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="childSKUEndTime" vartype="java.util.Date" param="childSKU.dateFinVie"/>
                  <c:if test="${(not empty now && not empty childSKUEndTime && childSKUEndTime.time > now.time) || empty childSKUEndTime}">
                    <dsp:include page="/catalog/sku.jsp" flush="true">
                      <dsp:param name="repositoryId" param="childSKU.id"/>
                      <dsp:param name="product" param="product"/>
                      <dsp:param name="agCategoryId" value="${agCategoryId}"/>
                    </dsp:include>
                  </c:if>
                </dsp:oparam>
              </dsp:droplet>
            </json:array>
         </m:jsonObject>
      </dsp:oparam>
    </dsp:droplet><%--CastPriceRangeDroplet--%>        
  </dsp:oparam>
</dsp:droplet><%--ProductLookup--%>

</dsp:page>