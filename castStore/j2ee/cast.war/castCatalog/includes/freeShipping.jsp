<dsp:page>
  <dsp:importbean bean="/com/castorama/droplet/FreeShippingDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <dsp:droplet name="FreeShippingDroplet">
     <dsp:param name="sku" param="sku"/>
     <dsp:param name="store" bean="Profile.currentLocalStore" />
     <dsp:param name="svAvailableMap" value="${svAvailableMap}" />
     <dsp:oparam name="freeShipping">
       <span class="fGratuite">
         <span class="red">
          <fmt:message key="castCatalog_productTemplate.freeShipping"/>
        </span>
      </span>
    </dsp:oparam>
  </dsp:droplet>

</dsp:page>