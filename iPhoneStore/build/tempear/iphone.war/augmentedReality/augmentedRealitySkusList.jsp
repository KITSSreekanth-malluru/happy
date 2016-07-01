<%@page contentType="application/json"%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="mobileConfig" bean="/atg/mobile/MobileConfiguration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
  <jsp:useBean id="now" class="java.util.Date" />
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <json:object>
    
        <dsp:droplet name="/atg/commerce/catalog/CategoryLookup">  
          <dsp:param name="id" param="categoryId"/>
          <dsp:param name="elementName" value="category"/>
          <dsp:oparam name="output">
            <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
              <dsp:param name="value" param="category.fixedChildProducts"/>
              <dsp:oparam name="true"> 
                <json:property name="result" value="1"/>
                <json:property name="errorMessage"><fmt:message key="er_301"/></json:property>
                <json:array name="skus"/>
              </dsp:oparam>
              <dsp:oparam name="false"> 
                <json:array name="skus">
                  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                    <dsp:param name="array" param="category.fixedChildProducts"/>
                    <dsp:param name="elementName" value="product"/>
                    <dsp:oparam name="output">
                      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                        <dsp:param name="array" param="product.childSKUs"/>
                        <dsp:param name="elementName" value="childSKU"/>
                        <dsp:oparam name="output">
                        
                          <dsp:getvalueof var="childSKUEndTime" vartype="java.util.Date" param="childSKU.dateFinVie"/>
                          <c:if test="${(not empty now && not empty childSKUEndTime && childSKUEndTime.time > now.time) || empty childSKUEndTime}">
                            <dsp:getvalueof var="trAugImageUrl" param="childSKU.augmentedRealityTransparentImage.url"/>
                            <dsp:getvalueof var="carAugImageUrl" param="childSKU.augmentedRealityCarouselImage.url"/>
                            <dsp:getvalueof var="trAugImageRetinaUrl" param="childSKU.augmentedRealityRetinaImage.url"/>
                            
                            <%-- KS : check for non-empty images properties: images is important for augmented reality functionality, 
                                      if images are empty then sku shouldn't be showed
                            --%>
                            <c:if test="${not empty param.req && param.req == 'r' && not empty mobileConfig.retinaDynamicImages && mobileConfig.retinaDynamicImages}">
                              <c:set var="trAugImageUrl" value="${trAugImageRetinaUrl}"/>
                            </c:if>
                            <c:if test="${not empty trAugImageUrl && fn:length(trAugImageUrl) > 0 && not empty carAugImageUrl && fn:length(carAugImageUrl) > 0}">
                              <c:if test="${not empty trAugImageUrl && (!fn:startsWith(trAugImageUrl,'http') && !fn:startsWith(trAugImageUrl,'https'))}">
                                <c:set var="trAugImageUrl" value="${httpLink}${trAugImageUrl}"/>
                              </c:if>
                              <c:if test="${not empty carAugImageUrl && (!fn:startsWith(carAugImageUrl,'http') && !fn:startsWith(carAugImageUrl,'https'))}">
                                <c:set var="carAugImageUrl" value="${httpLink}${carAugImageUrl}"/>
                              </c:if>
                              <c:set var="isSKUExists" value="${true}"/>
                              <json:object>
                                <json:property name="productId"><dsp:valueof param="product.repositoryId"/></json:property>
                                <json:property name="refId"><dsp:valueof param="childSKU.codeArticle"/></json:property>
                                <json:property name="title"><dsp:valueof param="childSKU.displayName"/></json:property>
                                <json:property name="transparentImageURL" value="${trAugImageUrl}"/>
                                <json:property name="carouselImageURL" value="${carAugImageUrl}"/>
                                <json:array name="images">
                                  <json:object>
                                    <json:property name="trURL" value="${trAugImageUrl}"/>
                                    <json:property name="carURL" value="${carAugImageUrl}"/>
                                  </json:object>
                                  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                    <dsp:param name="array" param="childSKU.auxAugmRealityImages"/>
                                    <dsp:param name="elementName" value="auxAugmRealityImage"/>
                                    <dsp:oparam name="output"> 
                                      <dsp:getvalueof var="auxTrIm" param="auxAugmRealityImage.augmentedRealityRetinaImage.url"/>
                                      <dsp:getvalueof var="auxCarIm" param="auxAugmRealityImage.augmentedRealityCarouselImage.url"/>
                                      
                                      <c:if test="${not empty auxTrIm && (!fn:startsWith(auxTrIm,'http') && !fn:startsWith(auxTrIm,'https'))}">
                                        <c:set var="auxTrIm" value="${httpLink}${auxTrIm}"/>
                                      </c:if>
                                      <c:if test="${not empty auxCarIm && (!fn:startsWith(auxCarIm,'http') && !fn:startsWith(auxCarIm,'https'))}">
                                        <c:set var="auxCarIm" value="${httpLink}${auxCarIm}"/>
                                      </c:if>
                                      <json:object>
                                        <json:property name="trURL" value="${auxTrIm}"/>
                                        <json:property name="carURL" value="${auxCarIm}"/>
                                      </json:object>
                                    </dsp:oparam>
                                  </dsp:droplet>
                                </json:array>
                              </json:object>
                            </c:if>
                          </c:if>
                      </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>       
                </json:array>
                <c:choose>
                  <c:when test="${not empty isSKUExists && isSKUExists}">
                    <json:property name="result" value="0"/>
                    <json:property name="errorMessage" value=""/>
                  </c:when>
                  <c:otherwise>
                    <json:property name="result" value="1"/>
                    <json:property name="errorMessage"><fmt:message key="er_301"/></json:property>
                  </c:otherwise>
                </c:choose>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
          <dsp:oparam name="empty">
            <json:property name="result" value="1"/>
            <json:property name="errorMessage"><fmt:message key="er_117"/></json:property>
            <json:array name="skus"/>
          </dsp:oparam>
        </dsp:droplet>
      </json:object>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>