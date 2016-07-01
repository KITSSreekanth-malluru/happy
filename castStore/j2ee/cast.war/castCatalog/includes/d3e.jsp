<dsp:page>

  <dsp:getvalueof var="soumisaD3E" param="sku.SoumisaD3E"/>
  <dsp:getvalueof var="currencyCode" param="currencyCode"/>

  <c:if test="${soumisaD3E}">
    <div class="additionalPrice">
      <dsp:getvalueof var="tax" param="sku.ecoTaxeEnEuro"/>
      <fmt:message key="castCatalog_productTemplate.d3e">
        <fmt:param>
          <fmt:formatNumber value="${tax}" type="currency" currencyCode="${currencyCode}"/>
        </fmt:param>
      </fmt:message>
    </div>  
  </c:if>
</dsp:page>