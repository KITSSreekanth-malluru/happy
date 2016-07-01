<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/com/castorama/droplet/CastComparisonDroplet"/>
<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<dsp:getvalueof var="categoryId" param="categoryId" />

<dsp:droplet name="CategoryLookup">
  <dsp:param name="id" param="categoryId" />
  <dsp:oparam name="output">
    <dsp:getvalueof var="categoryStyle" param="element.style.rightMenuStyle"/>
  </dsp:oparam>
</dsp:droplet>

    <style>
        .comparatorDialog .ui-widget-header{
            display: none;
        }
        .ui-effects-transfer { border: 1px solid gray; }
    </style>
    
    <div id="compareDialog" style="display:none;">
                
    </div>
    <div class="rd_camparateur width190pxComp">
      <fmt:message key="castCatalog_camparateur.comparateur" var="comparatorTitle"/>
      <fmt:message key="castCatalog_rd_camparateur.comparateur" var="comparatorLabel"/>
      
      <div id="rd_campCollapsed" class="rd_buttonCompareWr droppable2" >
        <a class="rd_buttonCompareAddGray droppable2" title="${comparatorTitle}" href="javascript:void(0)" onclick="toggleCamparateur(); addProductLink(productParams);">${comparatorLabel}</a>
      </div>
      
      <div class="rd_camparateurContent" id="campExpanded">
        <div class="rd_buttonCompareBlueWr">
          <a href="javascript:void(0)" onclick="addProductLink(productParams);" class="rd_buttonCompareAddWhite">${comparatorLabel}</a>
        </div>
        
        <div class="rd_campHeader"><!-- --></div>
        <div class="rd_campProductWr">
            <dsp:droplet name="CastComparisonDroplet">
                <dsp:oparam name="output">
                    <dsp:getvalueof var="products" param="compareProducts"/>
                    <dsp:getvalueof var="discounts" param="discounts"/>
                    <dsp:getvalueof var="size" value="${fn:length(products)}"/>
                    <input type="hidden" id="comparedProducsAmount" value="0"/>
                    <c:choose>
                        <c:when test="${not empty products}">
                            <c:forEach var="result" items="${products}" varStatus='status'>
                                <c:choose>
                                    <c:when test="${not empty result.value}">
                                        <dsp:droplet name="ForEach">
                                            <dsp:param name="array" value="${result.value}" />
                                            <dsp:param name="elementName" value="sku"/>
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="skuName" param="sku.displayName"/>
                                                <dsp:getvalueof var="skuId" param="sku.id"/>
                                                <dsp:getvalueof var="skuCodeArticle" param="sku.CodeArticle"/>
                                                
                                                <dsp:getvalueof var="thumbnailImage" param="sku.thumbnailImage.url"/>
                                                <div class="rendered rd_campProduct droppable cmpMarker" id="${result.key}">
                                                  <c:choose>
                                                    <c:when test="${not empty  thumbnailImage}">
                                                      <img src="${thumbnailImage}" title="${skuName}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                      <img src="/default_images/c_no_img.jpg" title="${skuName}"/>
                                                    </c:otherwise>
                                                  </c:choose>
                                                    <c:set var="thisDiscount" value="${discounts[skuId]}"/>
                                                    <c:if test="${not empty thisDiscount}">
                                                      <div class="rd_boxPriceStockTooltip">${thisDiscount}%</div>
                                                    </c:if>
                                                    <fmt:message key="castCatalog_camparateur.remove" var="removeLabel"/>
                                                    <a id="comp_${skuId}" class="campRemove" href="javascript:void(0);" title="${removeLabel}">${removeLabel}</a>
                                                </div>
                                                <script>
                                                $(document).ready(function(){
                                                    increaseProductsAmount();
                                                    arrayOfProducts[arrayOfProducts.length] = '${skuId}';
                                                    prodList[prodList.length]='${skuCodeArticle}';
                                                });
                                                </script>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="rd_campProduct droppable cmpMarker" id="${result.key}"></div>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="rd_campProduct droppable cmpMarker" id="compared0"></div>
                            <div class="rd_campProduct droppable cmpMarker" id="compared1"></div>
                            <div class="rd_campProduct droppable cmpMarker" id="compared2"></div>
                            <div class="rd_campProduct droppable cmpMarker" id="compared3"></div>
                        </c:otherwise>
                    </c:choose>
                </dsp:oparam>
            </dsp:droplet>
      </div>
            <script>
                var continueUrl = '${castCollection:encode(requestURIwithQueryString)}';
            </script>
            <div class="rd_campProducManipulationButton">
                <fmt:message key="castCatalog_camparateur.compare.products" var="compareProductsLabel"/>
                <fmt:message key="castCatalog_camparateur.clear.comparator" var="clearComparatorLabel"/>
                <a href="javascript:void(0)" class="rd_buttonComparer" id="btnCompareProducts" title="${compareProductsLabel}"><!-- --></a>
                <a href="javascript:void(0)" class="rd_buttonVider" id="btnClearComparator" title="${clearComparatorLabel}" >${clearComparatorLabel}</a>
            </div>
        </div>
    </div>
    
    <div class="whitePopupContainer compPopupV2" id="comparateurPopup">
        
    </div>
</dsp:page>