<dsp:page>

  <dsp:importbean bean="/com/castorama/droplet/GUIMenuLookup"/>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  <dsp:importbean bean="/atg/registry/Slots/EcoStyleTopSlot"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/registry/Slots/EcoStyleRightSlot"/>
  <dsp:getvalueof var="menuId" param="menuId" />
  <dsp:getvalueof var="pNum" param="num" />
  <dsp:getvalueof var="numInMenu" param="nim" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <dsp:getvalueof var="isRobot" value="false"/>
  <dsp:droplet name="/com/castorama/droplet/IsRobotDroplet">
    <dsp:oparam name="true">
      <dsp:getvalueof var="isRobot" value="true"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:droplet name="/atg/dynamo/droplet/Cache">
    <dsp:param name="key" value="dropdown_${menuId}_${pNum}"/>
    <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
    <dsp:oparam name="output">
    
      <div class="menuItemLink m_point${numInMenu}${pNum}">
        <dsp:droplet name="GUIMenuLookup"> 
          <dsp:param name="menuId" value="${menuId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="bgImage" param="menu.image.url"/>
            <ul class="menuItemUl">
              
            
                    <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="menu.menuItemsList"/>
                    <dsp:param name="elementName" value="childItem"/>
                      <dsp:oparam name="outputStart">
                        <dsp:getvalueof var="size" param="size" />
                      </dsp:oparam>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="count" param="count" />
                        <dsp:getvalueof var="itemLink" param="childItem.url"/>
                        <li>
                          <dsp:getvalueof var="text" param="childItem.title"/>
                          <dsp:getvalueof var="inNewTab" param="childItem.inNewTab"/>
                          <c:choose>
                            <c:when test="${inNewTab == 'true'}">
                              <a href="#" onclick="javascript: window.open('${itemLink}','_blank');">
                                <span>${text}</span>
                              </a>
                            </c:when>
                            <c:otherwise>
                              <a href="${itemLink}">
                                <span>${text}</span>
                              </a>
                            </c:otherwise>
                          </c:choose>
                        </li  >
                      </dsp:oparam>
                    </dsp:droplet>
                
              
            
              
            </ul>
          </dsp:oparam>
        </dsp:droplet>
      </div>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>