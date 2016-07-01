<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/KeyValueDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/LinkedSkuTabNamesIteratorDroplet"/>
  <dsp:importbean bean="/com/castorama/CastConfiguration"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="product" param="product"/>
  <dsp:getvalueof var="sku" param="element"/>
  <dsp:getvalueof var="skuParam" param="skuId"/>
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
  <dsp:getvalueof var="skuCodeArticle" param="skuCodeArticle"/>
  <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>
  <dsp:getvalueof var="notCheapestSkuPromoChosen" param="notCheapestSkuPromoChosen"/>

  <div class="productImageColumn">
    <c:choose>
      <c:when test="${isSearchResult}">
        <dsp:getvalueof var="borderColor" param="product.parentCategory.style.borderStyle"/>
      </c:when>
      <c:otherwise>
        <dsp:droplet name="StyleLookupDroplet">
          <dsp:param name="categoryId" value="${categoryId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="borderColor" param="style.borderStyle"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:otherwise>
    </c:choose>
  
   <div class="productImageBorder">
    
    <div class="productImage" onclick="zoomPopupByClickOnProductPicture(this)" currentImgNumber="0">
    <script>
    $('body').keydown(function(event){
      popupKeyEventsHandler(event);
    });
  </script>
   <dsp:include page="prodHighlight.jsp">
        <dsp:param name="product" value="${product}"/>
        <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}"/>
        <dsp:param name="categoryId" value="${categoryId}"/>
        <dsp:param name="view" value="galeryView"/>
      </dsp:include>

      <dsp:getvalueof var="name" param="element.displayName"/>
      <dsp:getvalueof var="largeImage" param="element.largeImage.url"/>
      <dsp:getvalueof var="media_url" param="element.urlVideo"/><%-- sku's url video --%>
      <dsp:getvalueof var="aux_media_url" param="element.urlAuxVideo"/><%-- sku's url aux video --%>
      <dsp:getvalueof var="videoFullSize" param="element.videoFullSize"/><%-- sku's video full size --%>
      <dsp:getvalueof var="flash_url" param="element.flash_produit"/><%-- sku's url 3D --%>
      <dsp:getvalueof var="external_app_url" param="element.urlExternalApp"/><%-- sku's url extrernal application --%>

      <dsp:droplet name="PriceDroplet">
        <dsp:param name="sku" value="${sku}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="listPrice" param="price.listPrice"/>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:getvalueof var="productType" param="product.type"/>
      <dsp:getvalueof var="isMultySKU" param="product.childSKUs"/>
      <c:choose>
        <c:when test="${(fn:length(isMultySKU) > 1 and empty skuParam) or (empty listPrice)}">
          <a href="javascript:void(0)" onclick="showImgPopup(this)">
          <c:choose>
            <c:when test="${not empty largeImage}">
              <img id="lrgImg" src="${largeImage}" alt="${name}" onload="jQuery(this).css('visibility', 'visible')" width="270" height="270" />
            </c:when>
            <c:otherwise>
              <img id="lrgImg" src="/default_images/h_no_img.jpg" alt="${name}" onload="jQuery(this).css('visibility', 'visible')" width="270" height="270" />
            </c:otherwise>
          </c:choose>
          </a>
        </c:when>

        <c:when test="${productType=='casto-grouped-product'}">
          <%@ include file="/shoppingList/includes/groupedProductInfo.jspf"%>
          <a href="javascript:void(0)" onclick="showImgPopup(this)">
          <c:choose>
            <c:when test="${not empty largeImage}">
              <img class="slPrdMarker" id="${skuList}" src="${largeImage}" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}" onload="jQuery(this).css('visibility', 'visible')" width="270" height="270"/>
            </c:when>
            <c:otherwise>
              <img class="slPrdMarker" id="${skuList}" src="/default_images/h_no_img.jpg" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}" onload="jQuery(this).css('visibility', 'visible')" width="270" height="270" />
            </c:otherwise>
          </c:choose>
          </a>
        </c:when>
        <c:otherwise>
        <a href="javascript:void(0)" onclick="showImgPopup(this)">
          <c:choose>
            <c:when test="${not empty largeImage}">
              <img class="prdMarker slPrdMarker" id="${empty skuParam ? sku.repositoryId : skuParam}" skuCodeArticle="${skuCodeArticle }" productId="${product.repositoryId}" src="${largeImage}" alt="${name}" onload="jQuery(this).css('visibility', 'visible')" width="270" height="270" />
            </c:when>
            <c:otherwise>
              <img class="prdMarker slPrdMarker" id="${empty skuParam ? sku.repositoryId : skuParam}" skuCodeArticle="${skuCodeArticle }" productId="${product.repositoryId}" src="/default_images/h_no_img.jpg" alt="${name}" onload="jQuery(this).css('visibility', 'visible')" width="270" height="270" />
            </c:otherwise>
          </c:choose>
          </a>
        </c:otherwise>
      </c:choose>

      <dsp:getvalueof var="auxiliaryMedia" param="element.auxiliaryMedia"/>
      <div class="imgControlsBlock">
        <c:if test="${not empty auxiliaryMedia }">
          <div class="controlButton"><a href="javascript:void(0)" onclick="showImgPopup(this)" class="controlZoom">Zoom</a></div>
        </c:if>
      </div>
    </div>
    </div>
    
    <fmt:message key="castCatalog_label.close" var="fermer"/>
    <fmt:message key="castCatalog_label.print" var="imprimer"/>
    <div class="whitePopupContainer" id="zoomedImage">
      <div class="whitePopupContent popupFormContainer">
        <div class="whitePopupHeader">
            <dsp:a title="${fermer}" iclass="closeBut" onclick="hidePopup(this)" href="javascript:void(0)"><span><!--~--></span>${fermer}</dsp:a>
            <div id="printButtonPopup">
                <dsp:a href="javascript:void(0)" onclick="window.open('${contextPath}/castCatalog/includes/zoomPrint.jsp?borderColor=${borderColor}&amp;imageId='+$('#popupImgLrgActive').attr('src'), '_blank');" iclass="imprimer" title="${imprimer}"><span><!--~--></span>${imprimer}</dsp:a>
            </div>
        </div>
        <div class="clear"><!--~--></div>
        <div class="popupContentContainer">
          <div id="productLargeImage" class="productLrgImg">
            <div class="toLeft" title="<fmt:message key="search_searchPaging.precedent"/>" onclick="showPreviousPicture()"><div><a href="#"></a></div></div>
            <img src="images/blank.gif" id="popupImgLrgActive" class="${borderColor}" onload="jQuery(this).css('visibility', 'visible')" />
            <div id="product_video_from_popup" class=""></div>
<%--            <div id="videoPPP"></div> --%>
            <div class="toRight" title="<fmt:message key="search_searchPaging.next"/>" onclick="showNextPicture()"><div><a href="#"></a></div></div>  
          </div>
<%--          <div id="product_video_from_popup" class="productLrgImg" style="margin-bottom:10px;"></div> --%><%-- my--%>
          <div id="controlButtonPopup">
            <%-- end video button--%>
            <%-- 3d button from popup--%>
            <c:if test="${not empty flash_url }">
              <dsp:getvalueof var="paramToLoad" value="${contextPath}/castCatalog/includes/3d_popup.jsp?skuId=${sku.repositoryId}&productId=${product.repositoryId}"/>
              <div class="controlButton"><a href="javascript:void(0)" onclick="show3DFromPopup('product_video_from_popup', '${paramToLoad}');" class="control3d">3D</a></div>
            </c:if>
            <%-- end 3d button--%>
          </div>
        </div>
      </div>
    </div>

    <dsp:getvalueof var="paramToLoad" value=""/>
    <c:if test="${not empty auxiliaryMedia}">
      <fmt:message key="castCatalog_productDetails.view_description" var="autres"/>
      <div class="imageViews">
        <h3>${autres}</h3>
        <ul>
          <c:choose>
            <c:when test="${not empty aux_media_url}">
              <dsp:getvalueof var="paramToLoad" value="${contextPath}/castCatalog/includes/video_popup.jsp?skuId=${sku.repositoryId}&productId=${product.repositoryId}"/>
            </c:when>
            <c:otherwise>
              <c:if test="${not empty media_url}">
              <dsp:getvalueof var="paramToLoad" value="${contextPath}/castCatalog/includes/video_popup.jsp?url=${media_url}&skuId=${sku.repositoryId}&productId=${product.repositoryId}&fullSize=${videoFullSize}"/>
            </c:if>
            </c:otherwise>
          </c:choose>
          <c:if test="${not empty paramToLoad}">
            <li class="videoCastocheV2">
              <a href="javascript:void(0)" param="${paramToLoad}" onclick="showImgPopup2(this, '${paramToLoad}');" class="controlVideo" id="popup_video_img_area_link"></a>
            </li>
          </c:if>
          <c:if test="${not empty flash_url }">
            <dsp:getvalueof var="paramToLoad" value="${contextPath}/castCatalog/includes/3d_popup.jsp?skuId=${sku.repositoryId}&productId=${product.repositoryId}"/>
            <li class="rotateCastocheV2">
              <a href="javascript:void(0)" id="popup_rotate" param="${paramToLoad}" onclick="showImgPopup2(this, '${paramToLoad}');" class="control3d"></a>
            </li>
          </c:if>
          <c:if test="${not empty external_app_url}">
            <li class="d3CastocheV2">
              <a title="Visualiser ce produit dans le configurateur" href="${external_app_url}" class="controlLink" target="_blank"></a>
            </li>
          </c:if>
          <dsp:droplet name="Range">
            <dsp:param name="array" value="${auxiliaryMedia}"/>
            <dsp:param name="howMany" value="6"/>
            <dsp:param name="start" value="1"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="image" param="element.value.url"/>
              <dsp:getvalueof var="index" param="index"/>
              <dsp:getvalueof var="count" param="count"/>
              <c:choose>
                <c:when test="${index==5}">
                  <li class="last imgGalleryItem" imgNumber="${index}">
                </c:when>
                <c:otherwise>
                  <li class="imgGalleryItem" imgNumber="${index}">
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${count == 1}">
                  <dsp:a href="javascript:void(0)" title="${autres}" onclick="showImgPopup2(this)" iclass="lImBorder ${borderColor}">
                    <c:choose>
                      <c:when test="${not empty image}">
                        <dsp:img src="${image}" alt="${autres}" title="${autres}" width="43" height="43"/>
                      </c:when>
                      <c:otherwise>
                        <dsp:img src="/default_images/a_no_img.jpg" alt="${autres}" title="${autres}" width="43" height="43"/>
                      </c:otherwise>
                    </c:choose>
                  </dsp:a>
                </c:when>
                <c:otherwise>
                  <dsp:a href="javascript:void(0)" title="${autres}" onclick="showImgPopup2(this)" iclass="${borderColor}">
                    <c:choose>
                      <c:when test="${not empty image}">
                        <dsp:img src="${image}" alt="${autres}" title="${autres}" width="43" height="43"/>
                      </c:when>
                      <c:otherwise>
                        <dsp:img src="/default_images/a_no_img.jpg" alt="${autres}" title="${autres}" width="43" height="43"/>
                      </c:otherwise>
                    </c:choose>
                  </dsp:a>
                </c:otherwise>
              </c:choose>
              </li>
            </dsp:oparam>
          </dsp:droplet>
        </ul>
      </div>
    </c:if>
    <div class="allStandarts">
    <dsp:getvalueof var="standards" param="element.urlPicto"/>
    <c:if test="${not empty standards}">
      <div class="standards">
          <dsp:droplet name="ForEach">
            <dsp:param name="array" value="${standards}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="url" param="element"/>
              <img src="${url}"/>
            </dsp:oparam>
          </dsp:droplet>
        </div>
    </c:if>
    
    <dsp:getvalueof var="image_url" param="element.airEmissionLevelImage.url"/>
    <c:if test="${not empty image_url}">
      <div class="standards">
        <dsp:getvalueof var="link_url" bean="CastConfiguration.airEmissionUrl"/>
        <a href="${link_url}">
          <fmt:message key="emissionLevel.alt.text" var="emalt"/>
          <img src="${image_url}" width="123" height="66" alt="${emalt}" title="${emalt}"/>
        </a>
      </div>
    </c:if>
    </div>
    <ul class="nvLinks">
      <dsp:getvalueof var="map" param="element.instructions" vartype="java.util.Map"/>
      <c:if test="${not empty map}">
        <dsp:droplet name="Range">
          <dsp:param name="array" value="${map}"/>
          <dsp:param name="howMany" value="6"/>
          <dsp:param name="start" value="1"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="key" param="element.key"/>
            <li class="tele">
              <img src="${contextPath}/images/blank.gif" alt="" title=""/>
              <dsp:getvalueof var="mediaUrl" param="element.value.url"/>
              <c:choose>
                <c:when test="${castCollection:endsWith(mediaUrl)}">
                  <dsp:getvalueof var="paramToLoad" value="${contextPath}/castCatalog/includes/video_popup.jsp?url=${mediaUrl}&skuId=${sku.repositoryId}&productId=${product.repositoryId}"/>
                  <a href="javascript:void(0)" onclick="showVideoPopup('product_video', '${paramToLoad}');">${key}</a>
                </c:when>
                <c:otherwise>
                  <a href="javascript:void(0)" onclick="javascript:window.open('${mediaUrl}', '_blank');">${key}</a>
                </c:otherwise>
              </c:choose>
            </li>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
      <li class="impri">
        <dsp:img src="${contextPath}/images/blank.gif" alt="${imprimer}" title="${imprimer}"/>&nbsp;
        <dsp:getvalueof var="skuId" param="skuId"/>
        <dsp:a href="printProductInfo.jsp?productId=${product.repositoryId}&skuId=${skuId}&categoryId=${categoryId}&isSearchResult=${isSearchResult}&notCheapestSkuPromo=${notCheapestSkuPromo}&notCheapestSkuPromoChosen=${notCheapestSkuPromoChosen}" target="_blank" iclass="imprimer" title="${imprimer}">${imprimer}</dsp:a>
      </li>
      <li class="envoy">
        <fmt:message key="castCatalog_label.envoer" var="envoer"/>
        <dsp:img src="${contextPath}/images/blank.gif" alt="${envoer}" title="${envoer}"/>&nbsp;
        <a href="javascript:void(0)" onclick="clearForm();showPopup('emailAFriend')">${envoer}</a>
      </li>
    </ul>
  </div>
  <div id="product_video" class="whitePopupContainer"></div>
  <div id="product_3D" class="whitePopupContainer"></div>

</dsp:page>