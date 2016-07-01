<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

  <script type="text/javascript">
    function switch_rows(sel) {
      for (i = 0; i < sel.options.length; i++) {
        var cskuId = sel.options[i].value;
        if (i == sel.selectedIndex) {
          document.getElementById('tr_' + cskuId).style.display = '';
          var sobj = document.getElementById('sel_' + cskuId);
          for (j = 0; j < sobj.options.length; j++) {
            sobj.options[j].selected = (cskuId == sobj.options[j].value);
          }
        } else {
          document.getElementById('tr_' + cskuId).style.display = 'none';
        }
      }
    }

    function switchProduct(select) {
      var skuId = $(select).val();
      var defaultOption = $(select).find(".defaultOption");
      var skuDivName = ".documentHeadProdLine" + skuId;
      var latestCurrentProduct = $(".documentHeadProd").find(".currentProduct");
      $(latestCurrentProduct).removeClass("currentProduct");
      $(skuDivName).addClass("currentProduct");
      $(defaultOption).attr("selected", "selected");
    }
  </script>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:param name="elementName" value="product"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="childSKUs" param="product.childSKUs"/>
      <!-- <dsp:getvalueof var="templateUrl" param="product.template.url"/> -->

      <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
        <dsp:param name="productId" param="productId"/>
        <dsp:param name="navAction" param="navAction"/>
        <dsp:param name="navCount" param="navCount"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="templateUrl" param="url"/>
        </dsp:oparam>
      </dsp:droplet>


      <div class="documentHeadProd">
        <dsp:droplet name="CastPriceRangeDroplet">
          <dsp:param name="productId" param="productId"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="cheapest_skuId" param="sku.repositoryId"/>
          </dsp:oparam>
        </dsp:droplet>
        <dsp:droplet name="ForEach">
          <dsp:param name="array" param="product.childSKUs"/>
          <dsp:param name="elementName" value="sku"/>
          <dsp:getvalueof var="index" param="index" />
            <dsp:oparam name="output">
              <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
              <dsp:getvalueof var="ImageURL" param="sku.carouselImage.url"/>
              <c:choose>
                <c:when test="${index == 0}">
                  <div class="documentHeadProdLine currentProduct documentHeadProdLine${skuId}">
                </c:when>
                <c:otherwise>
                  <div class="documentHeadProdLine documentHeadProdLine${skuId}">
                </c:otherwise>
              </c:choose>
              <div class="productItemImage">
                <dsp:a page="${templateUrl}" title="Produit">
                  <c:choose>
                    <c:when test="${not empty ImageURL}">
                      <dsp:img title="Produit" alt="Produit" src="${ImageURL}" height="75" width="75"/>
                    </c:when>
                    <c:otherwise>
                      <dsp:img title="Produit" alt="Produit" src="/default_images/b_no_img.jpg" height="75" width="75"/>
                    </c:otherwise>
                  </c:choose>
                  <dsp:param name="skuId" value="${skuId}"/>
                </dsp:a>
              </div>
              <div class="productItemDescription">
                <dsp:valueof param="sku.displayName"/>
                <c:if test="${1 < fn:length(childSKUs)}">
                  <br/><select id="sel_${skuId}" name="searchSorterSelect" onchange="javascript:switchProduct(this);">
                    <dsp:droplet name="ForEach">
                      <dsp:param name="array" param="product.childSKUs"/>
                      <dsp:param name="elementName" value="checkbox_sku"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="checkbox_skuId" param="checkbox_sku.repositoryId"/>
                        <c:choose>
                          <c:when test="${checkbox_skuId == skuId}">
                            <option value="${checkbox_skuId}" selected class="defaultOption">
                              <dsp:valueof param="checkbox_sku.displayName"/>
                            </option>
                          </c:when>
                          <c:otherwise>
                            <option value="${checkbox_skuId}">
                              <dsp:valueof param="checkbox_sku.displayName"/>
                           </option>
                          </c:otherwise>
                        </c:choose>
                      </dsp:oparam>
                    </dsp:droplet>
                  </select>
                </c:if>
              </div>
              <dsp:include page="../../castCatalog/includes/addToCartTabs.jsp">
                <dsp:param name="productId" param="productId"/>
                <dsp:param name="pageType" value="documentPage"/>
                <dsp:param name="sku" param="sku"/>
                <dsp:param name="templateUrl" value="${templateUrl}"/>
              </dsp:include>
              </div>
            </dsp:oparam>
        </dsp:droplet>
      </div>
    </dsp:oparam>
  </dsp:droplet>

</dsp:page>