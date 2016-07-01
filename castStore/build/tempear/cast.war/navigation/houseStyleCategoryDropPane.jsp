<dsp:page>

  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
    <dsp:importbean bean="/atg/registry/Slots/HouseStyleTopSlot"/>
    <dsp:importbean bean="/atg/registry/Slots/HouseStyleRightSlot"/>
    
    <dsp:importbean bean="/atg/dynamo/droplet/For"/>
    <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
    
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
    <dsp:getvalueof var="categoryId" param="categoryId" />
    
    <dsp:droplet name="/atg/dynamo/droplet/Cache">
    <dsp:param name="key" value="dropdown_${categoryId}"/>
         <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
        <dsp:oparam name="output">
          <div class="upperMenuPopup">
        <div class="menuContainer">
          <div class="m8">  
            <div class="leftCol">
              <div class="m12_10">
                <dsp:include page="includes/styleTopTargeter.jsp" flush="true">
                  <dsp:param name="styleTopSlot" bean="HouseStyleTopSlot"/>
                </dsp:include>
                 <div class="clear"><!--~--></div>
                <dsp:droplet name="CategoryLookup">  
                  <dsp:param name="id" value="${categoryId}"/>
                  <dsp:oparam name="output">
                    <dsp:droplet name="For">
                      <dsp:param name="howMany" value="4"/>
                      <dsp:getvalueof var="count" param="count"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="image" param="element.childCategories[param:index].thumbnailImage.url"/>
                        <dsp:getvalueof var="template" param="element.childCategories[param:index].template.url"/>
                        <dsp:getvalueof var="categoryId" param="element.childCategories[param:index].repositoryId"/>
                        <dsp:droplet name="/com/castorama/droplet/CastCategoryLinkDroplet">
                          <dsp:param name="categoryId" param="element.childCategories[param:index].repositoryId"/>
                          <dsp:param name="navAction" value="jump"/>
                                                    <dsp:param name="navCount" bean="/atg/commerce/catalog/CatalogNavHistory.navCount"/>
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="template" param="url"/>
                          </dsp:oparam>
                        </dsp:droplet>
                        <c:choose>
                          <c:when test="${count == 1 || count == 3}">
                            <div class="subCol">
                              <dsp:a href="${contextPath}${template}" iclass="fourmaisonsLink">
                                <dsp:img src="${image}" width="220" height="170" alt=""/>
                              </dsp:a>
                            </div>
                          </c:when>
                          <c:otherwise>
                            <div class="subCol last">
                              <dsp:a href="${contextPath}${template}" iclass="fourmaisonsLink">
                                <dsp:img src="${image}" width="220" height="170" alt=""/>
                              </dsp:a>
                            </div>  
                          </c:otherwise>
                        </c:choose>
                      </dsp:oparam>
                    </dsp:droplet>
                  </dsp:oparam>
                </dsp:droplet>              
              </div>  
            </div>
            <dsp:include page="includes/houseStyleRightTargeter.jsp" flush="true">
              <dsp:param name="houseStyleRightSlot" bean="HouseStyleRightSlot"/>
            </dsp:include>
          </div>  
        </div>  
      </div>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>