<dsp:page>
  <dsp:importbean bean="/atg/registry/Slots/HomePageSlot"/>
  <dsp:importbean bean="/atg/targeting/TargetingRange"/>


  <dsp:droplet name="TargetingRange">
 
    <dsp:param bean="HomePageSlot" name="targeter"/>
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:param name="start" value="${targetRandom }"/>
    <dsp:param name="howMany" value="1"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="promoTemplate" param="element" />
      <c:if test="${not empty promoTemplate}">
        <dsp:getvalueof var="layoutType" param="element.layoutType" />
        <dsp:include page="../castCatalog/includes/catalogPromoTemplates/${layoutType}.jsp" flush="true">
          <dsp:param name="promoInformation" param="element.promoInformation" />
        </dsp:include>  
      </c:if>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>