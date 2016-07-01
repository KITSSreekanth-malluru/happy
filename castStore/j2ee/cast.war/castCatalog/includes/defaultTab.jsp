<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>

  <dsp:getvalueof var="tabName" param="tabName"/>
  <dsp:getvalueof var="skuList" param="groupedProdMap.${tabName}"/>

  <dsp:droplet name="/com/castorama/droplet/MultiStockVisAvailabilityDroplet">
    <dsp:param name="skus" value="${skuList}"/>
    <dsp:param name="store" bean="/atg/userprofiling/Profile.currentLocalStore"/>
    <dsp:oparam name="output">
      <c:choose>
        <c:when test="${empty svAvailableMap}">
          <dsp:getvalueof var="svAvailableMap" param="svAvailableMap" scope="request"/>
        </c:when>
        <c:otherwise>
          <dsp:getvalueof var="addToSvAvailableMap" param="svAvailableMap"/>
          ${castCollection:putAll(svAvailableMap, addToSvAvailableMap)}
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>

  <div class="tabCompl tabContent redContent defTabV2">
    <table cellspacing="0" cellpadding="0" class="productsTabs">
      <tbody>
      <dsp:droplet name="ForEach">
        <dsp:param name="array" value="${skuList}"/>
        <dsp:param name="elementName" value="sku"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="index" param="index"/>
          <dsp:getvalueof var="size" param="size"/>
          <dsp:getvalueof var="set" param="sku.parentProducts.updatedValue"/>
          <dsp:param name="list" value="${castCollection:list(set)}"/>
          <dsp:droplet name="CastProductLinkDroplet">
            <dsp:param name="productId" param="list[0].repositoryId"/>
            <dsp:param name="categoryId" param="list[0].parentCategory.repositoryId"/>
            <dsp:param name="navAction" value="jump"/>
            <dsp:param name="navCount" param="navCount"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="templateUrl" param="url"/>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
          <dsp:getvalueof var="ImageURL" param="sku.comparatorImage.url"/>
          <dsp:getvalueof var="name" param="sku.displayName"/>

          <tr>
            <td class="firstCell">
              <div class="fixFFV2">
                <div class="productItemImage">
                  <dsp:a page="${templateUrl}" title="${name}">
                    <c:choose>
                      <c:when test="${not empty ImageURL}">
                        <dsp:img src="${ImageURL}"/>
                      </c:when>
                      <c:otherwise>
                        <dsp:img src="/default_images/e_no_img.jpg"/>
                      </c:otherwise>
                    </c:choose>
                  </dsp:a>
                </div>
                <div class="productItemDescription">
                  <dsp:a page="${templateUrl}" title="${name}">
                    <h2>
                      <dsp:valueof param="sku.displayName"/>
                    </h2>
                  </dsp:a>

                  <div class="newLabelComplimentaryV2">
                  </div>
                  <p>
                    <dsp:droplet name="/com/castorama/droplet/ProductDescPreProcessorDroplet">
                      <dsp:param name="description" param="sku.LibelleClientLong"/>
                      <dsp:oparam name="output">
                        <dsp:valueof param="result" valueishtml="true"/>
                      </dsp:oparam>
                    </dsp:droplet>
                  </p>

                  <div class="starRateComplimentaryV2">
                    <dsp:include page="includes/noteArea.jsp"/>
                    <div class="clear"></div>
                  </div>
                </div>
              </div>
            </td>

            <td class="lastCell">
              <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
              <dsp:getvalueof var="set" param="sku.parentProducts.updatedValue"/>
              <dsp:param name="list" value="${castCollection:list(set)}"/>
              <dsp:getvalueof var="productId" param="list[0].repositoryId"/>

              <dsp:droplet name="ProductLookup">
                <dsp:param name="id" value="${productId}"/>
                <dsp:param name="elementName" value="product"/>
                <dsp:oparam name="output">
                  <dsp:include page="addToCartTabs.jsp">
                    <dsp:param name="productId" value="${productId}"/>
                    <dsp:param name="pageType" value="productPageTabs"/>
                    <dsp:param name="sku" param="sku"/>
                    <dsp:param name="childSku" param="product.childSKUs"/>
                  </dsp:include>
                </dsp:oparam>
              </dsp:droplet>
            </td>

          </tr>

          <c:if test="${ (index + 1) < size }">
            <tr>
              <td class="tblSplitter" colspan="4">
                <div></div>
              </td>
            </tr>
            <tr>
              <td class="productRowEnd tblSplitter noPadding" colspan="4">
                <div></div>
              </td>
            </tr>
            <tr>
              <td class="tblSplitter" colspan="4">
                <div></div>
              </td>
            </tr>
          </c:if>

        </dsp:oparam>
      </dsp:droplet>
      </tbody>
    </table>
  </div>
</dsp:page>