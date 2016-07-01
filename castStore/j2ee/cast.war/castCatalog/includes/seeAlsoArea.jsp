<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/droplet/SeeAlsoDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/IsRobotDroplet"/>

  <dsp:getvalueof var="isRobot" value="false"/>
  
  <dsp:droplet name="IsRobotDroplet">
    <dsp:oparam name="true">
      <dsp:getvalueof var="isRobot" value="true"/>
    </dsp:oparam>
  </dsp:droplet>

  <div class="voirAussiBlock">
    <ul>
      <dsp:getvalueof var="productId" param="product.repositoryId"/>
      <dsp:getvalueof var="storeId" bean="/atg/userprofiling/Profile.currentLocalStore.id"/>
      <dsp:droplet name="/atg/dynamo/droplet/Cache">
        <dsp:param name="key" value="seeAlso_${productId}_${isRobot}_${storeId}"/>
        <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
        <dsp:oparam name="output">
          <dsp:droplet name="SeeAlsoDroplet">
            <dsp:param name="item" param="product"/>
            <dsp:oparam name="output">
              <h2><fmt:message key="castCatalog_productDetails.seeAlso"/></h2>
              <dsp:getvalueof var="categoriesList" param="results"/>
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="results"/>
                <dsp:oparam name="output">
                  <li>
                    <dsp:droplet name="ForEach">
                      <dsp:param name="array" param="element"/>
                      <dsp:param name="elementName" value="category"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="count" param="count"/>
                        <c:set var="first" value="${count == 1}"/>
                        <c:choose>
                          <c:when test="${first}">
                            <b>&gt;&nbsp;</b><dsp:valueof param="category.displayName"/>
                          </c:when>
                          <c:otherwise>
                            <dsp:include page="../categoryLink.jsp">
                              <dsp:param name="category" param="category"/>
                              <dsp:param name="navCount" bean="/Constants.null"/>
                              <dsp:param name="navAction" value="jump" /><b>&nbsp;&gt;</b>
                            </dsp:include>
                          </c:otherwise>
                        </c:choose>
                      </dsp:oparam>
                    </dsp:droplet>
                  </li>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </ul>
  </div>
</dsp:page>