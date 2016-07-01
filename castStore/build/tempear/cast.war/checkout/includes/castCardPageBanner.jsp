<dsp:page>
  <dsp:importbean bean="/atg/registry/Slots/CastCardPageSlot"/>
  <dsp:importbean bean="/atg/targeting/TargetingFirst"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:droplet name="TargetingFirst">
    <dsp:param bean="CastCardPageSlot" name="targeter"/>
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="promoTemplate" param="element" />

      <c:if test="${not empty promoTemplate}">
        <dsp:getvalueof var="layoutType" param="element.layoutType" />
        <dsp:include page="../../castCatalog/includes/catalogPromoTemplates/${layoutType}.jsp" flush="true">
          <dsp:param name="promoInformation" param="element.promoInformation" />
        </dsp:include>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>