<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceItem"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceDroplet"/>
  
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="mobileConfig" bean="/atg/mobile/MobileConfiguration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />

  <jsp:useBean id="now" class="java.util.Date" />

  <dsp:getvalueof var="objectName" param="objectName"/>
  
  
  <dsp:getvalueof var="loadType" param="loadType"/>
  <dsp:getvalueof var="product" param="product"/>
  <c:if test="${empty product}">
    <dsp:droplet name="ProductLookup">
      <dsp:param name="id" param="ppid"/>
      <dsp:param name="elementName" value="product"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="product" param="product"/>
      </dsp:oparam>
    </dsp:droplet>
    <dsp:param name="product" value="${product}"/>
  </c:if>
  
 

  <dsp:droplet name="SKULookup">
    <dsp:param name="id" param="repositoryId"/>
    <dsp:param name="elementName" value="sku"/>
    <dsp:oparam name="output">
     
        <m:jsonObject>
          <dsp:getvalueof var="repositoryId" param="sku.repositoryId"/>
          <json:property name="skuId" value="${repositoryId}"/>
          <json:property name="codeArticle">
            <dsp:valueof param="sku.CodeArticle"/>
          </json:property>
            <json:property name="name">
              <dsp:valueof param="sku.displayName" valueishtml="true"/>
            </json:property>
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
          
          <dsp:getvalueof var="childSKUEndTime" vartype="java.util.Date" param="sku.dateFinVie"/>
          <c:if test="${(not empty now && not empty childSKUEndTime && childSKUEndTime.time > now.time) || empty childSKUEndTime}">
            <dsp:getvalueof var="trAugImageUrl" param="sku.augmentedRealityTransparentImage.url"/>
            <dsp:getvalueof var="carAugImageUrl" param="sku.augmentedRealityCarouselImage.url"/>
            <dsp:getvalueof var="trAugImageRetinaUrl" param="sku.augmentedRealityRetinaImage.url"/>
            
            <%-- KS : check for non-empty images properties: images is important for augmented reality functionality, 
                      if images are empty then sku shouldn't be showed
            --%>
            <c:if test="${not empty param.req && param.req == 'r' && not empty mobileConfig.retinaDynamicImages && mobileConfig.retinaDynamicImages}">
              <c:set var="trAugImageUrl" value="${trAugImageRetinaUrl}"/>
            </c:if>
            <dsp:getvalueof var="skuAgCategoryId" param="agCategoryId"/>
            <json:property name="agCategoryId" value="${skuAgCategoryId}"/>
          </c:if>
          
            <dsp:droplet name="/com/castorama/mobile/droplet/CalcAndVideoDroplet">
              <dsp:param name="codeArticle" param="sku.CodeArticle"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="calcId" param="calculatorId"/>
                <dsp:getvalueof var="videosList" param="videos"/>
              </dsp:oparam>
            </dsp:droplet>
            <json:property name="calculatorId" >${calcId}</json:property>
            <json:array name="videos">
              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" value="${videosList}" />
                <dsp:param name="elementName" value="videoUrl" />
                <dsp:oparam name="output">
                <m:jsonObject>
                  <json:property name="name" ><dsp:valueof param="videoUrl[0]"/></json:property>
                  <json:property name="url" ><dsp:valueof param="videoUrl[1]"/></json:property>
                </m:jsonObject>
                </dsp:oparam>
              </dsp:droplet>
            </json:array>
            
          
          <c:if test="${not empty prImageUrl && (!fn:startsWith(prImageUrl,'http') && !fn:startsWith(prImageUrl,'https'))}">
            <c:set var="prImageUrl" value="${httpLink}${prImageUrl}"/>
          </c:if>
          <c:if test="${not empty smImageUrl && (!fn:startsWith(smImageUrl,'http') && !fn:startsWith(smImageUrl,'https'))}">
            <c:set var="smImageUrl" value="${httpLink}${smImageUrl}"/>
          </c:if>
          
          <json:property name="img" value="${prImageUrl}"/>
          <json:property name="smImg" value="${smImageUrl}"/>
          
          <json:property name="description">
            <dsp:valueof param="sku.LibelleClientLong" valueishtml="true"/>
          </json:property>
          <json:property name="mentionsLegales">
            <dsp:valueof param="sku.MentionsLegalesObligatoires" valueishtml="true"/>
          </json:property>
          <json:property name="available" value=""/>
          <json:property name="plusDuProduit">
            <dsp:valueof param="sku.PlusDuProduit" valueishtml="true"/>
          </json:property>
          <json:property name="garantie">
            <dsp:valueof param="sku.Garantie" valueishtml="true"/>
          </json:property>
          <json:property name="contraintesUtilisation">
            <dsp:valueof param="sku.ContraintesUtilisation" valueishtml="true"/>
          </json:property>
          <json:property name="restrictionsUsage">
            <dsp:valueof param="sku.RestrictionsUsage" valueishtml="true"/>
          </json:property>
          
          <dsp:getvalueof var="poidsUV" param="sku.PoidsUV"/>
          <fmt:formatNumber var="poidsUV" type="number" maxFractionDigits="3" minFractionDigits="1" value="${poidsUV / 1000}"/>
          <json:property name="poidsUV">
            <c:if test="${not empty poidsUV}">
              ${poidsUV} kg
            </c:if>
          </json:property>
      
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
          <dsp:include page="/catalog/productComplementaries.jsp">
            <dsp:param name="sku" param="sku"/>
          </dsp:include>
          
        </m:jsonObject>
      </dsp:oparam>
    </dsp:droplet><%--SkuLookup--%>

</dsp:page>