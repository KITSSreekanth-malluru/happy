<dsp:page>
  <%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/main.js"></script> --%>
  
  
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup" />
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  
  <dsp:getvalueof var="cartType" param="cartType"/>
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="divId" param="divId"/>
  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="skuId" param="element.childSKUs[0].repositoryId"/>
      <dsp:getvalueof var="childSku" param="element.childSKUs"/>
      <c:if test="${fn:length(childSku)==1}">
        <c:if test="${not empty skuId}">
          <c:if test="${cartType == 'addToCartArea'}">
            <dsp:include page="addToCartArea.jsp">
              <dsp:param name="sku" param="element.childSKUs[0]"/>
              <dsp:param name="productId" value="${productId}"/>
              <dsp:param name="childSku" param="element.childSKUs"/>
              <dsp:param name="thumbnailImage" value="element.thumbnailImage.url"/>
              <dsp:param name="pageType" value="productDetails"/>
              <dsp:param name="asJS" value="true"/>
              <dsp:param name="divId" value="${divId}"/>
            </dsp:include>
            <br>
          </c:if>
          <c:if test="${cartType == 'addToCartTabs'}">
            <div class="padding">
              <dsp:include page="addToCartTabs.jsp">
                <dsp:param name="productId" param="productId"/>
                <dsp:param name="sku" param="element.childSKUs[0]"/>
                <dsp:param name="childSku" param="element.childSKUs"/>
              </dsp:include>
            </div>
            <br>
          </c:if>
          <c:if test="${cartType == 'addToCartSmall'}">
            <dsp:droplet name="CastProductLinkDroplet">
              <dsp:param name="categoryId" value=""/>
              <dsp:param name="productId" param="productId"/>
              <dsp:param name="navAction" param="navAction"/>
              <dsp:param name="navCount" param="navCount"/>
              <dsp:param name="isSearchResult" param="isSearchResult"/>
              <dsp:param name="sortByValue" param="sortByValue"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="templateUrl" param="url"/>
              </dsp:oparam>
            </dsp:droplet>
            <dsp:include page="addToCartSmall.jsp">
              <dsp:param name="productId" param="productId"/>
              <dsp:param name="sku" param="element.childSKUs[0]"/>
              <dsp:param name="childSku" param="element.childSKUs"/>
              <dsp:param name="url" value="${contextPath}${templateUrl}"/>
              <dsp:param name="multiDeliveryMsg" value="false"/>
            </dsp:include>
            <br>
          </c:if>
          <c:if test="${cartType == 'addToCartList'}">
              <dsp:droplet name="CastProductLinkDroplet">
                <dsp:param name="categoryId" value=""/>
                <dsp:param name="productId" param="productId"/>
                <dsp:param name="navAction" param="navAction"/>
                <dsp:param name="navCount" param="navCount"/>
                <dsp:param name="isSearchResult" param="isSearchResult"/>
                <dsp:param name="sortByValue" param="sortByValue"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="templateUrl" param="url"/>
                </dsp:oparam>
              </dsp:droplet>
               
              <table>    
                <tr>
                  <td>       
                    <dsp:include page="skuPrice.jsp">
                      <dsp:param name="productId" param="productId"/>
                      <dsp:param name="sku" param="element.childSKUs[0]"/>
                    </dsp:include>
                    <dsp:include page="addToCartSmall.jsp">
                      <dsp:param name="sku" param="element.childSKUs[0]"/>
                      <dsp:param name="productId" param="productId"/>
                      <dsp:param name="childSku" param="element.childSKUs"/>
                      <dsp:param name="url" value="${contextPath}${templateUrl}"/>
                      <dsp:param name="multiDeliveryMsg" value="false"/>
                    </dsp:include>
                  </td>
                </tr>
              </table>
          </c:if>
        </c:if>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>