<dsp:page>
   <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
    <dsp:param name="id" param="brandId"/>
    <dsp:param name="elementName" value="brand"/>
    <dsp:param name="itemDescriptor" value="marque"/>
    <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog"/>
    <dsp:oparam name="output"> 
      <dsp:getvalueof var="relativeURL" param="brand.url"/>              
      <c:if test="${not empty relativeURL }">
        <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
        <c:import url="${staticContentPath}${relativeURL }" charEncoding="utf-8"/>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>  
</dsp:page>