<dsp:page>

  <dsp:getvalueof var="skuParam" param="skuParam"/>
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="bgColor" param="bgColor"/>

  <tr>
    <dsp:include page="packRenderer.jsp">
      <dsp:param name="sku" value="${skuParam}"/>
      <dsp:param name="bgColor" value="${bgColor}"/>
    </dsp:include>
   
    <td class="lastCell">
      <dsp:include page="addToCartTabs.jsp">
      <dsp:param name="skuParam" value="${skuParam}"/>
      <dsp:param name="pack" value="true"/>
      </dsp:include>
        
    </td>
  </tr>
</dsp:page>
