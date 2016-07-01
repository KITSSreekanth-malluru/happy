<dsp:page>
  <dsp:setvalue param="promoInformation" paramvalue="promoInformation"/>
  <div class="advertiseBlocks">
    <div class="banner-590x118">
      <dsp:include page="../layoutTypeInformation.jsp">
        <dsp:param name="promoInformation" param="promoInformation.1"/>
        <dsp:param name="flashId" value="promoInformation1"/>
        <dsp:param name="width" value="590"/>
        <dsp:param name="height" value="118"/>
      </dsp:include>
    </div>
    <div class="banner-390x118">
      <dsp:include page="../layoutTypeInformation.jsp">
        <dsp:param name="promoInformation" param="promoInformation.2"/>
        <dsp:param name="flashId" value="promoInformation2"/>
        <dsp:param name="width" value="390"/>
        <dsp:param name="height" value="118"/>
      </dsp:include>
    </div>
  </div>
</dsp:page>