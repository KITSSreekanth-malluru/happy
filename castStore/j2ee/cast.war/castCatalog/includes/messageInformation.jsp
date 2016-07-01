<dsp:page>
  <dsp:getvalueof var="isOnSale" param="pricedItem.priceInfo.onSale"/>
  <dsp:getvalueof var="msgInfo" param="sku.messageInformation"/>
  <c:if test="${isOnSale && not empty msgInfo}">
    <dsp:valueof param="sku.messageInformation" valueishtml="true"/>
  </c:if>
</dsp:page>