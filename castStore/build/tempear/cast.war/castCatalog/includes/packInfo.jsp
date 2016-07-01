<dsp:page>

  <dsp:getvalueof var="skuParam" param="skuParam"/>
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="bgColor" param="bgColor"/>

  <tr>
    <dsp:include page="packRendererInfo.jsp">
      <dsp:param name="sku" value="${skuParam}"/>
      <dsp:param name="bgColor" value="${bgColor}"/>
	  
    </dsp:include>
   
    <td class="lastCell">
      <dsp:include page="addToCartTabsInfo.jsp">
		  <dsp:param name="skuParam" value="${skuParam}"/>
		  <dsp:param name="pack" value="true"/>
		  <dsp:param name="quantity" param="quantity"/>
      </dsp:include>
        
    </td>
  </tr>
</dsp:page>
