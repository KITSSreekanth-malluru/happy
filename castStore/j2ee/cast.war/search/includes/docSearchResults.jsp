<dsp:page>
  <dsp:include page="searchPageListTemplate.jsp">
    <dsp:param name="itemDescriptor" value="castoramaDocument"/>
    <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog"/>
    <dsp:param name="results" param="docResults"/>
    <dsp:param name="isSearchResult" param="isSearchResult"/>
  </dsp:include>
</dsp:page>