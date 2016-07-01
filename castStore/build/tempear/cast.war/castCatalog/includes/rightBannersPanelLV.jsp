<dsp:page>

  <dsp:importbean bean="/com/castorama/droplet/GUIMenuLookup"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="disableCastoche" param="disablePC" />


  <c:if test="${empty disableCastoche || disableCastoche != 'true'}">
    <div class="menuPoint">
      <div class="content visible">
        <dsp:droplet name="GUIMenuLookup"> 
          <dsp:param name="menuId" value="300006"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="bgImage" param="menu.image.url"/>
            <img src="${contextPath}/..${bgImage}">
            <dsp:include page="../../navigation/LVSubDropPane.jsp">
              <dsp:param name="menuId" param="menu.repositoryId"/>
              <dsp:param name="num" value="1"/>
              <dsp:param name="nim" value="b1"/>
            </dsp:include>
          </dsp:oparam>
        </dsp:droplet>
      </div>
    </div>
  </c:if>
  <div class="menuPoint">
    <div class="content visible">
      <dsp:droplet name="GUIMenuLookup"> 
        <dsp:param name="menuId" value="300010"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="bgImage" param="menu.image.url"/>
          <img src="${contextPath}/..${bgImage}">
          <dsp:include page="../../navigation/LVSubDropPane.jsp">
            <dsp:param name="menuId" param="menu.repositoryId"/>
            <dsp:param name="num" value="1"/>
            <dsp:param name="nim" value="b2"/>
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet>
    </div>
  </div>
  <div class="menuPoint">
    <div class="content visible">
      <dsp:droplet name="GUIMenuLookup"> 
        <dsp:param name="menuId" value="300013"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="bgImage" param="menu.image.url"/>
          <img src="${contextPath}/..${bgImage}">
          <dsp:include page="../../navigation/LVSubDropPane.jsp">
            <dsp:param name="menuId" param="menu.repositoryId"/>
            <dsp:param name="num" value="1"/>
            <dsp:param name="nim" value="b3"/>
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet>
    </div>
  </div>
</dsp:page>