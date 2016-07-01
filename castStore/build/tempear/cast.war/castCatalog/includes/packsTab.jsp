<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <dsp:getvalueof var="availablePacks" param="availablePacks"/>
  <dsp:getvalueof var="element" param="element"/>
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="bgColor" param="bgColor"/>

  <div class="tabPacks tabContent tabPacksV2">
    <table cellpadding="0" cellspacing="0" class="productsTable productsTabs">
      <dsp:droplet name="ForEach">
        <dsp:param name="array" value="${availablePacks}"/>
        <dsp:param name="elementName" value="product"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="size" param="size"/>
          <dsp:getvalueof var="count" param="count"/>
          <dsp:include page="pack.jsp">
            <dsp:param name="packId" param="product.repositoryId"/>
            <dsp:param name="skuParam" param="product.childSKUs[0]"/>
            <dsp:param name="bgColor" value="${bgColor}"/>
          </dsp:include>
          <c:if test="${size!=count}">
            <tr>
              <td colspan="4" class="tblSplitter">
                <div><!--~--></div>
              </td>
            </tr>
            <tr>
              <td colspan="4" class="productRowEnd tblSplitter noPadding">
                <div><!--~--></div>
              </td>
            </tr>
            <tr>
              <td colspan="4" class="tblSplitter">
                <div><!--~--></div>
              </td>
            </tr>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
    </table>
  </div>
</dsp:page>