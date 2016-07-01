<dsp:page>
  <dsp:importbean bean="/com/castorama/droplet/UnitPriceCodeLookupDroplet"/>

  <dsp:getvalueof var="sku" param="sku"/>
  <dsp:getvalueof var="price" param="price"/>
  <dsp:getvalueof var="currencyCode" param="currencyCode"/>
  <dsp:getvalueof var="showM2PriceFirst" param="showM2PriceFirst"/>

  <dsp:droplet name="UnitPriceCodeLookupDroplet">
    <dsp:param name="sku" value="${sku}"/>
    <dsp:param name="price" value="${price}"/>
    <dsp:oparam name="output">
      <c:if test="${empty showM2PriceFirst || !showM2PriceFirst}">
        <fmt:message key="castCatalog_productTemplate.soit"/>&nbsp;
      </c:if>
      <dsp:getvalueof var="pricePerUnite" param="pricePerUnite"/>
      <fmt:formatNumber value="${pricePerUnite}" type="currency" currencyCode="${currencyCode}"/>/<dsp:valueof param="libelle" valueishtml="true"/>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>