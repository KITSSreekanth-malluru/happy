<dsp:page>

  <dsp:importbean bean="/com/castorama/droplet/GUIMenuLookup"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="disableCastoche" param="disablePC" />

  <div class="groupMenuPointV2 forumsV2">
    <div class="groupMenuPointHeaderV2">
      <div class="nameMenuPointHeaderV2"></div>
    </div>
    <div class="menuPointV2 noBg">
      <img src="/images/temp/4.png"></img>
      <h2><fmt:message key="fl.partagezEchangez"/></h2>
      <span><fmt:message key="fl.desConseils"/></span>
      <dsp:droplet name="GUIMenuLookup"> 
        <dsp:param name="menuId" value="300013"/>
        <dsp:oparam name="output">
          <dsp:include page="../../navigation/LVSubDropPane.jsp">
            <dsp:param name="menuId" param="menu.repositoryId"/>
            <dsp:param name="num" value="1"/>
            <dsp:param name="nim" value="b3"/>
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet>
    </div>
    <div class="menuPointV2">
      <img src="/images/temp/5.jpg"></img>
      <h2><fmt:message key="fl.entradeGraduite"/></h2>
      <span><fmt:message key="fl.partagezDes"/></span>
      <dsp:droplet name="GUIMenuLookup"> 
        <dsp:param name="menuId" value="300010"/>
        <dsp:oparam name="output">
          <dsp:include page="../../navigation/LVSubDropPane.jsp">
            <dsp:param name="menuId" param="menu.repositoryId"/>
            <dsp:param name="num" value="1"/>
            <dsp:param name="nim" value="b2"/>
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet>
    </div>
  </div>
</dsp:page>