<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  <dsp:importbean bean="/atg/registry/Slots/EcoStyleTopSlot"/>
  <dsp:importbean bean="/atg/dynamo/droplet/For"/>
  <dsp:importbean bean="/atg/registry/Slots/EcoStyleRightSlot"/>
  <dsp:getvalueof var="categoryId" param="categoryId" />

  <dsp:getvalueof var="isRobot" value="false"/>
  <dsp:droplet name="/com/castorama/droplet/IsRobotDroplet">
    <dsp:oparam name="true">
      <dsp:getvalueof var="isRobot" value="true"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="/atg/dynamo/droplet/Cache">
    <dsp:param name="key" value="dropdown_${categoryId}_${isRobot}"/>
    <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
    <dsp:oparam name="output">
      <div class="upperMenuPopup">
        <div class="menuContainer">
          <div class="m8">
            <div class="leftCol" id="umpEco">
              <div class="m12_10">
                <dsp:include page="includes/styleTopTargeter.jsp" flush="true">
                  <dsp:param name="styleTopSlot" bean="EcoStyleTopSlot"/>
                </dsp:include>
                <div class="clear"><!--~--></div>
                <dsp:droplet name="CategoryLookup"> 
                  <dsp:param name="id" param="categoryId"/>
                  <dsp:oparam name="output">
                    <dsp:droplet name="For">
                      <dsp:param name="howMany" value="4"/>
                      <dsp:getvalueof var="count" param="count"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="image" param="element.childCategories[param:index].thumbnailImage.url"/>
                        <dsp:getvalueof var="childCategory" param="element.childCategories[param:index]"/>
                        <c:choose>
                          <c:when test="${count == 1}">
                            <div class="subCol">
                            <dsp:include page="includes/ecoStyleTemplate.jsp" flush="true">
                              <dsp:param name="image" value="${image}"/>
                              <dsp:param name="categoryId" param="id"/>
                              <dsp:param name="childCategory" value="${childCategory}"/>
                            </dsp:include>
                            <div class="clear"></div>
                          </c:when>
                          <c:when test="${count mod 2 == 0}">
                            <dsp:include page="includes/ecoStyleTemplate.jsp" flush="true">
                              <dsp:param name="image" value="${image}"/>
                              <dsp:param name="categoryId" param="id"/>
                              <dsp:param name="childCategory" value="${childCategory}"/>
                            </dsp:include>
                            </div>
                          </c:when>
                          <c:when test="${count == 3}">
                            <div class="subCol last">
                            <dsp:include page="includes/ecoStyleTemplate.jsp" flush="true">
                              <dsp:param name="image" value="${image}"/>
                              <dsp:param name="categoryId" param="id"/>
                              <dsp:param name="childCategory" value="${childCategory}"/>
                            </dsp:include>
                            <div class="clear"></div>
                          </c:when>
                        </c:choose>
                      </dsp:oparam>
                    </dsp:droplet>
                  </dsp:oparam>
                </dsp:droplet>
              </div>
            </div>
            <dsp:include page="includes/ecoStyleRightTargeter.jsp" flush="true">
              <dsp:param name="ecoStyleRightSlot" bean="EcoStyleRightSlot"/>
            </dsp:include>
          </div>
        </div>
      </div>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>