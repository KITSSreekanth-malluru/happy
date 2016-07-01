<dsp:page>

  <dsp:importbean bean="/com/castorama/droplet/GUIMenuLookup"/>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  <dsp:importbean bean="/atg/registry/Slots/EcoStyleTopSlot"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/registry/Slots/EcoStyleRightSlot"/>
  <dsp:getvalueof var="menuId" param="menuId" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

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
        <dsp:droplet name="GUIMenuLookup"> 
          <dsp:param name="menuId" value="${menuId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="bgImage" param="menu.image.url"/>
            <div class="menuContainer" style="background: url(../..${bgImage}) 100% 0 repeat-x;">
              <div class="m8">
            
                    <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="menu.menuItemsList"/>
                    <dsp:param name="elementName" value="childItem"/>
                      <dsp:oparam name="outputStart">
                        <dsp:getvalueof var="size" param="size" />
                      </dsp:oparam>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="count" param="count" />
                          <c:choose>
                            <c:when test="${size != count}">
                              <dsp:getvalueof var="styleClass" value="imageLink menuItemUnderline" />
                            </c:when>
                            <c:otherwise>
                              <dsp:getvalueof var="styleClass" value="imageLink" />
                            </c:otherwise>
                          </c:choose>
                        <div class="${styleClass}">
                          <dsp:getvalueof var="imageUrl" param="childItem.image.url"/>
                          <img src="${contextPath}/..${imageUrl}">
                          <dsp:getvalueof var="templateURL" param="childItem.template.url">
                            <dsp:include page="${templateURL}">
                              <dsp:param name="menuId" param="childItem.menu.repositoryId"/>
                              <dsp:param name="num" param="count"/>
                              <dsp:param name="nim" param="nim"/>
                            </dsp:include>
                          </dsp:getvalueof>
                        </div>
                      </dsp:oparam>
                    </dsp:droplet>
                
              
            
              </div>
            </div>
          </dsp:oparam>
        </dsp:droplet>
      </div>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>