<dsp:page>
  <dsp:importbean bean="/atg/registry/Slots/HomePageTopSlot"/>
  <dsp:importbean bean="/atg/targeting/TargetingFirst"/>
    
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  
  <%--<div id="teaser">--%>
    <dsp:droplet name="TargetingFirst">
      <dsp:param bean="HomePageTopSlot" name="targeter"/>
      <dsp:param name="fireViewItemEvent" value="false"/>
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
  <%--</div>--%>
</dsp:page>