<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/For"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>  
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="mobileConfig" bean="/atg/mobile/MobileConfiguration" />
  
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
  
  
  <jsp:useBean id="now" class="java.util.Date" />
  
  
     
  <dsp:droplet name="IsEmpty">
    <dsp:param name="value" param="sku.crossSelling"/>
    <dsp:oparam name="false">
      <dsp:getvalueof var="crossSellingSkus" param="sku.crossSelling"/>
      <c:choose>
        <c:when test="${not empty crossSellingSkus && fn:length(crossSellingSkus) >= 8}">
          <c:set var="howManyVar" value="8"/>
        </c:when>
        <c:otherwise>
          <c:set var="howManyVar" value="${fn:length(crossSellingSkus)}"/>
        </c:otherwise>
      </c:choose>
  
      <json:array name="prCompl">
        <dsp:droplet name="For">
          <dsp:param name="howMany" value="${howManyVar}"/>
          <dsp:oparam name="output">
            <dsp:droplet name="ProductLookup">
              <dsp:param name="id" param="sku.crossSelling[param:index].product.repositoryId"/>
              <dsp:param name="elementName" value="product"/>
              <dsp:oparam name="output">
                <dsp:droplet name="IsEmpty">
                  <dsp:param name="value" param="product.childSKUs"/>
                  <dsp:oparam name="false">
                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                      <dsp:param name="array" param="product.childSKUs"/>
                      <dsp:param name="elementName" value="prdChildSKU"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="prdChildSKUEndTime" vartype="java.util.Date" param="prdChildSKU.dateFinVie"/>
                        <c:if test="${(not empty now && not empty prdChildSKUEndTime && prdChildSKUEndTime.time > now.time) || empty prdChildSKUEndTime}">
                          <c:set var="isAvailableSKUExists" value="${true}"/>
                        </c:if>
                      </dsp:oparam>
                    </dsp:droplet>
                    <c:if test="${not empty isAvailableSKUExists && isAvailableSKUExists}">
                      <m:jsonObject>
                        <json:property name="productId">
                          <dsp:valueof param="product.id"/>
                        </json:property>
                        <json:property name="title">
                          <dsp:valueof param="product.displayName"/>
                        </json:property>
                        <c:choose>
                          <c:when test="${not empty mobileConfig.retinaDynamicImages && mobileConfig.retinaDynamicImages}">
                            <dsp:getvalueof var="prImageUrl" param="product.childSKUs[0].comparatorImage.url"/>
                          </c:when>
                          <c:otherwise>
                            <dsp:getvalueof var="prImageUrl" param="product.childSKUs[0].carouselImage.url"/>
                          </c:otherwise>
                        </c:choose>
                        
                        <c:if test="${not empty prImageUrl && (!fn:startsWith(prImageUrl,'http') && !fn:startsWith(prImageUrl,'https'))}">
                          <c:set var="prImageUrl" value="${httpLink}${prImageUrl}"/>
                        </c:if>
                        <json:property name="imageURL" value="${prImageUrl}"/>
                        
                      </m:jsonObject>
                    </c:if>
                  </dsp:oparam>
                </dsp:droplet><%-- End of IsEmpty--%>  
              </dsp:oparam>
            </dsp:droplet><%-- End of ProductLookup--%>      
          </dsp:oparam>
        </dsp:droplet><%-- End of ForEach--%>
      </json:array>
    </dsp:oparam>    
  </dsp:droplet><%-- End of IsEmpty--%>
</dsp:page>
