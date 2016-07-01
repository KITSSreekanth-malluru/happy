<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>

  <dsp:getvalueof var="skuId" param="skuId"/>

  <script type="text/javascript">
    function switch_rows(sel) {
      for (i = 0; i < sel.options.length; i++ ) {
        var cskuId = sel.options[i].value;
        if ( i == sel.selectedIndex ) {
          document.getElementById('tr_' + cskuId).style.display = '';
          var sobj = document.getElementById('sel_' + cskuId);
          for (j = 0; j < sobj.options.length; j++ ) {
            sobj.options[j].selected = (cskuId == sobj.options[j].value); 
          }
        } else {
          document.getElementById('tr_' + cskuId).style.display = 'none';
        }
      }
    }
  </script>
  
  <div class="tabCompl tabContent redContent productsComplimentaryV2">
    <table cellspacing="0" cellpadding="0" class="productsTabs">
      <tbody>
        <dsp:droplet name="SKULookup">
          <dsp:param name="id" value="${skuId}"/>
          <dsp:oparam name="output">

            <dsp:droplet name="/com/castorama/droplet/MultiStockVisAvailabilityDroplet">
			  <dsp:param name="crossSellings" param="element.crossSelling" />
			  <dsp:param name="store" bean="/atg/userprofiling/Profile.currentLocalStore" />
			  <dsp:oparam name="output">
			      <c:choose>
			      <c:when test="${empty svAvailableMap}">
			        <dsp:getvalueof var="svAvailableMap" param="svAvailableMap" scope="request"/>
			      </c:when>
			      <c:otherwise>
			        <dsp:getvalueof var="addToSvAvailableMap" param="svAvailableMap" />
			        ${castCollection:putAll(svAvailableMap, addToSvAvailableMap)}
			      </c:otherwise>
			      </c:choose>
			  </dsp:oparam>
			</dsp:droplet>

            <dsp:droplet name="ForEach">
              <dsp:param name="array" param="element.crossSelling"/>
              <dsp:param name="elementName" value="crossSell"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="index" param="index"/>
                <dsp:getvalueof var="size" param="size"/>
                <dsp:droplet name="ProductLookup">
                  <dsp:param name="id" param="crossSell.product.repositoryId"/>
                  <dsp:param name="elementName" value="product"/>
                  <dsp:oparam name="output">
                    <dsp:droplet name="CastProductLinkDroplet">
                      <dsp:param name="productId" param="product.repositoryId"/>
                      <dsp:param name="categoryId" param="product.parentCategory.repositoryId"/>
                      <dsp:param name="navAction" value="jump"/>
                      <dsp:param name="navCount" param="navCount"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="templateUrl" param="url"/>
                      </dsp:oparam>
                    </dsp:droplet>
                    <dsp:getvalueof var="childSKUs" param="product.childSKUs"/>
                    <dsp:droplet name="CastPriceRangeDroplet">
                      <dsp:param name="productId" param="product.repositoryId"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="cheapest_skuId" param="sku.repositoryId"/>
                      </dsp:oparam>
                    </dsp:droplet>
                    <dsp:droplet name="ForEach">
                      <dsp:param name="array" param="product.childSKUs"/>
                      <dsp:param name="elementName" value="sku"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
                        <dsp:getvalueof var="ImageURL" param="sku.comparatorImage.url"/>
                        <c:choose>
                          <c:when test="${skuId == cheapest_skuId}">
                            <tr id="tr_${skuId}" class="singleProductsComplimentaryV2">
                          </c:when>
                          <c:otherwise>
                            <tr id="tr_${skuId}" style="display:none;" class="singleProductsComplimentaryV2">
                          </c:otherwise>
                        </c:choose>
                        <td class="firstCell">
                         <div class="fixFFV2">
                          <div class="productItemImage">
                            <dsp:a page="${templateUrl}" title="Produit">
                              <c:choose>
                                <c:when test="${not empty ImageURL}">
                                  <dsp:img title="Produit" alt="Produit" src="${ImageURL}" />
                                </c:when>
                                <c:otherwise>
                                  <dsp:img title="Produit" alt="Produit" src="/default_images/e_no_img.jpg" />
                                </c:otherwise>
                              </c:choose>
                              <dsp:param name="skuId" value="${skuId}"/>
                            </dsp:a>
                          </div>
                          <div class="productItemDescription">
                            <dsp:a page="${templateUrl}" title="Produit"> 
                              <h2>
                                <dsp:valueof param="sku.displayName"/>
                              </h2>
                              <dsp:param name="skuId" value="${skuId}"/>
                            </dsp:a>
                            
                            <c:if test="${1 < fn:length(childSKUs)}">
                              <br /><select id="sel_${skuId}" class="styled_" name="searchSorterSelect" onChange="javascript:switch_rows(this);" >
                                <dsp:droplet name="ForEach">
                                  <dsp:param name="array" param="product.childSKUs"/>
                                  <dsp:param name="elementName" value="checkbox_sku"/>
                                  <dsp:oparam name="output">
                                    <dsp:getvalueof var="checkbox_skuId" param="checkbox_sku.repositoryId"/>
                                    <c:choose>
                                      <c:when test="${checkbox_skuId == skuId}">
                                        <option value="${checkbox_skuId}" selected>
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
                           
                            <div class="newLabelComplimentaryV2">
                            </div>
                            <div class="complementaryDescription">
                            	<dsp:droplet name="/com/castorama/droplet/ProductDescPreProcessorDroplet">
		                            <dsp:param name="description" param="sku.LibelleClientLong"/>
		                            <dsp:oparam name="output">
		                              <dsp:valueof param="result" valueishtml="true"/>
		                            </dsp:oparam>
		                        </dsp:droplet>
                            </div>
                            <div class="starRateComplimentaryV2">
                           	 	<dsp:include page="includes/noteArea.jsp"/>
                    			<div class="clear"></div>
                           	 </div>
                            
                           
                          </div>
                         </div> 
                        </td>
                        <td class="lastCell">
                          <dsp:include page="addToCartTabs.jsp">
                            <dsp:param name="productId" param="product.repositoryId"/>
                            <dsp:param name="pageType" value="productPageTabs"/>
                            <dsp:param name="sku" param="sku"/>
                          </dsp:include>
                        </td>
                      </tr>
                    </dsp:oparam>
                  </dsp:droplet>
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
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </tbody>
  </table>
</div>
</dsp:page>