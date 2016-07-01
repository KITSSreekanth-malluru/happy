<dsp:page>

<dsp:droplet name="/com/castorama/droplet/OrphanProductRedirectDroplet">
<dsp:param name="productId" param="productId"/>
<dsp:oparam name="notOrphan">

<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>

<dsp:droplet name="ProductLookup">
  <dsp:param name="id" param="productId"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="sku" param="element.childSKUs[0]"/>
  </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="/com/castorama/droplet/CastPriceItem">
  <dsp:param name="item" value="${sku}"/>
  <dsp:param name="elementName" value="pricedSku"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="cardPrice" param="pricedSku.priceInfo.cardPrice"/>
    <dsp:getvalueof var="pricedSku" param="pricedSku"/>
    <dsp:getvalueof var="isValidByDateCardPrice" param="pricedSku.priceInfo.IsValidByDateCardPrice"/>
    <c:if test="${(not empty cardPrice) and (cardPrice > 0) and (isValidByDateCardPrice == 'true')}">
      <dsp:getvalueof var="cpb" value="true"/>
    </c:if>
  </dsp:oparam>
</dsp:droplet>

<cast:pageContainer>
<jsp:attribute name="bottomBanners">true</jsp:attribute>
<jsp:attribute name="cardPriceBanner">${cpb}</jsp:attribute>
<jsp:attribute name="metaInfoInclude">/global/meta.jsp?id=<dsp:valueof param="productId"/>&item=product</jsp:attribute>
<jsp:attribute name="bvInclude">view</jsp:attribute>
    <jsp:attribute name="bodyContent">
    
      <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
      <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
      <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
      <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
      <dsp:importbean bean="/com/castorama/droplet/CastGetApplicablePromotions"/>
      <dsp:importbean bean="/atg/commerce/catalog/ProductBrowsed"/>
      <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>
      <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
      <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
      <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
      <dsp:importbean bean="/com/castorama/droplet/IsRobotDroplet"/>
      <dsp:importbean bean="/atg/dynamo/droplet/Cache"/>
      <dsp:importbean bean="/com/castorama/droplet/ProductDescPreProcessorDroplet"/>

      <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
      <dsp:getvalueof var="productId" param="productId"/>
      <dsp:getvalueof var="categoryId" param="categoryId"/>
      <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

      <dsp:getvalueof var="bonnesAffaires" param="ba" scope="request"/>
      <c:if test="${bonnesAffaires and wrappedStoreId!='999' and (wrappedStoreIsCC || wrappedStoreIsLocal)}">
        <dsp:getvalueof var="baFakeContext" value="${true}" scope="request"/>
      </c:if>
      
      <dsp:getvalueof var="isRobot" value="false"/>
      <dsp:droplet name="IsRobotDroplet">
        <dsp:oparam name="true">
          <dsp:getvalueof var="isRobot" value="true"/>
        </dsp:oparam>
      </dsp:droplet>
      
      <!-- slide right panel with shopping list -->
      <dsp:include page="/shoppingList/shoppingListSlider.jsp">
        <dsp:param name="currentPage" value="product"/>
      </dsp:include>

      <dsp:droplet name="ProductLookup">
      <dsp:param name="id" param="productId"/>
      <dsp:oparam name="output">
      <dsp:param name="product" param="element"/>
      <dsp:getvalueof var="productImage" param="element.thumbnailImage.url"/>
      <dsp:getvalueof var="sku" param="element.childSKUs[0]"/>
      <dsp:getvalueof var="childSku" param="element.childSKUs"/>
      <dsp:getvalueof var="fixedRelatedProducts" param="element.childSKUs[0].crossSelling"/>
      <dsp:droplet name="ProductBrowsed">
        <dsp:param name="eventobject" param="element"/>
      </dsp:droplet>

      <c:choose>
        <c:when test="${isSearchResult}">
          <dsp:getvalueof var="bgColor" param="product.parentCategory.style.backgroundStyle"/>
          <dsp:getvalueof var="linkColor" param="product.parentCategory.style.linkStyle"/>
          <dsp:getvalueof var="listColor" param="product.parentCategory.style.listStyle"/>
        </c:when>
        <c:otherwise>
          <dsp:droplet name="StyleLookupDroplet">
            <dsp:param name="categoryId" value="${categoryId}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="bgColor" param="style.backgroundStyle"/>
              <dsp:getvalueof var="linkColor" param="style.linkStyle"/>
              <dsp:getvalueof var="listColor" param="style.listStyle"/>
            </dsp:oparam>
          </dsp:droplet>
        </c:otherwise>
      </c:choose>
      <div class="carouselV2">
        <dsp:include page="carouselMotherCategory.jsp">
          <dsp:param name="isSearchResult" value="${isSearchResult}"/>
          <dsp:param name="ownerProductId" param="productId"/>
          <dsp:param name="ownerCategoryId" value="${categoryId}"/>
        </dsp:include>
      </div>

      <div class="clear"></div>

      <div class="content width800 width750">
      <div class="productBlock singleProductV2" id="productItemWithPrice">
        <c:choose>
             <c:when test="${!isSearchResult}">
          <dsp:include page="breadcrumbsCollector.jsp">
            <dsp:param name="productId" param="productId"/>
            <dsp:param name="navAction" param="navAction"/>
            <dsp:param name="navCount" param="navCount"/>
          </dsp:include>

          <dsp:include page="breadcrumbs.jsp" flush="true">
            <dsp:param name="productId" param="productId"/>
            <dsp:param name="isCollected" value="true"/>
            <dsp:param name="navAction" param="navAction"/>
            <dsp:param name="navCount" param="navCount"/>
            <dsp:param name="categoryId" value="${categoryId}"/>
          </dsp:include>
          <c:set var="footerBreadcrumb" value="productDetails" scope="request"/>
        </c:when>
            <c:otherwise>
                <%@ include file="/castCatalog/includes/returnButton.jsp" %>
            </c:otherwise>
        </c:choose>


        <dsp:setvalue param="sku" value="${sku}"/>
        <dsp:getvalueof var="skuCodeArticle" param="sku.CodeArticle" scope="request"/>
        <dsp:getvalueof var="storeId" bean="/atg/userprofiling/Profile.currentLocalStore.id"/>
        <dsp:droplet name="/com/castorama/droplet/ProductDetailsCache">
          <dsp:param name="key" value="single_sku_${categoryId}_${productId}_${sku.repositoryId}_1_${isRobot}_${storeId}"/>
          <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
          <dsp:oparam name="output">
            <dsp:include page="includes/productImageArea.jsp">
              <dsp:param name="element" value="${sku}"/>
              <dsp:param name="categoryId" value="${categoryId}"/>
              <dsp:param name="product" param="product"/>
              <dsp:param name="skuCodeArticle" param="sku.CodeArticle"/>
            </dsp:include>
          </dsp:oparam>
        </dsp:droplet>

        <div class="productContent">

          <dsp:droplet name="/com/castorama/droplet/ProductDetailsCache">
            <dsp:param name="key" value="single_sku_${categoryId}_${productId}_${sku.repositoryId}_2_${isRobot}_${storeId}"/>
            <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
            <dsp:oparam name="output">
              <dsp:setvalue param="sku" value="${sku}"/>
              <h1><dsp:valueof param="sku.displayName"/></h1>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:droplet name="PriceDroplet">
            <dsp:param name="sku" value="${sku}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="listPrice" param="price.listPrice"/>
            </dsp:oparam>
          </dsp:droplet>

          <div class="productFooter">
            <c:if test="${not empty listPrice}">
              <dsp:droplet name="/com/castorama/droplet/ProductPriceCache">
                <dsp:param name="key" value="single_sku_${productId}_${sku.repositoryId}_3_${isRobot}_${storeId}"/>
                <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
                <dsp:oparam name="output">
                  <dsp:setvalue param="sku" value="${sku}"/>
                  <div class="pfRightColumn">
                    <dsp:getvalueof var="flagText" param="product.flagText"/>
                    <dsp:getvalueof var="flagImage" param="product.flagImage.url"/>
                    <c:if test="${not empty flagText and not empty flagImage}">
                      <div class="flagBoxV2">
                        <div class="flagSimpleV2">
                          <img id="flagImg" src="${flagImage}" class="pngImg"/>
                        </div>
                        <dsp:getvalueof var="flagBg" param="product.flagBg"/>
                        <div class="flagSimpleV2Info" <c:if test="${not empty flagBg}">style="background: ${flagBg};"</c:if>>
                          <span>${flagText}</span>
                          <dsp:getvalueof var="auxiliaryMedia" param="sku.auxiliaryMedia"/>
                          <c:if test="${not empty auxiliaryMedia}">
                            <dsp:getvalueof var="aux_media_url" param="sku.urlAuxVideo"/>
                            <c:choose>
                              <c:when test="${not empty aux_media_url}">
                                <dsp:getvalueof var="paramToLoad" value="${contextPath}/castCatalog/includes/video_popup.jsp?skuId=${sku.repositoryId}&productId=${productId}"/>
                              </c:when>
                              <c:otherwise>
                                <dsp:getvalueof var="media_url" param="sku.urlVideo"/>
                                <c:if test="${not empty media_url}">
                                  <dsp:getvalueof var="videoFullSize" param="sku.videoFullSize"/>
                                  <dsp:getvalueof var="paramToLoad" value="${contextPath}/castCatalog/includes/video_popup.jsp?url=${media_url}&skuId=${sku.repositoryId}&productId=${productId}&fullSize=${videoFullSize}"/>
                                </c:if>
                              </c:otherwise>
                            </c:choose>
                            <c:if test="${not empty paramToLoad}">
                              <a href="javascript:void(0)" onclick="$('#popup_video_img_area_link').click();"><fmt:message key="castCatalog_label.view.video"/></a>
                            </c:if>
                          </c:if>
                        </div>
                      </div>
                    </c:if>
                  </div>
                  <div class="pfLeftColumn">
                    <div class="socialRateAndComment">
                      <dsp:include page="includes/noteArea.jsp"/>
                      <div class="clear"></div>
                    </div>
                    <ul class="nvLinks">
                      <li class="impri">
                        <fmt:message key="castCatalog_label.print" var="imprimer"/>
                        <dsp:img src="${contextPath}/images/blank.gif" alt="${imprimer}" title="${imprimer}"/>&nbsp;
                        <dsp:getvalueof var="skuId" param="skuId"/>
                        <dsp:a href="${contextPath}/castCatalog/includes/printProductInfo.jsp?productId=${productId}&skuId=${skuId}&categoryId=${categoryId}&isSearchResult=${isSearchResult}" target="_blank" iclass="imprimer" title="${imprimer}">${imprimer}</dsp:a>
                      </li>
                    </ul>
                    <div class="clear"></div>
                  </div>

                  <div class="productDecription">
                    <div class="clear"></div>
                              <span class="refNum"> 
                                <fmt:message key="castCatalog_contentProductDetails.ref"/>&nbsp;<dsp:valueof param="sku.CodeArticle"/>
                                <dsp:getvalueof var="skuCodeArticle" param="sku.CodeArticle"/>
                              </span>
                    <ul class="${listColor}">
                      <dsp:getvalueof var="showSeeMore" value="false" vartype="boolean"/>
                      <dsp:droplet name="ProductDescPreProcessorDroplet">
                        <dsp:param name="description" param="sku.LibelleClientLong"/>
                        <dsp:oparam name="output">
                          <dsp:valueof param="result" valueishtml="true"/>
                          <dsp:getvalueof var="showSeeMore" param="showSeeMore"/>
                        </dsp:oparam>
                      </dsp:droplet>
                    </ul>
                    <c:if test="${showSeeMore == true}">
                                <span class="lightBg">
                                    <a href="#prodTabs" class="arrowedLink ${linkColor}" onclick="var id=$('.prodDescMarker').parent('div').attr('id');">
                                      <fmt:message key="castCatalog_contentProductDetails.viewMoreLink"/>
                                    </a>
                                </span>
                    </c:if>
                  </div>
                  <div class="productLabel">
                    <dsp:include page="includes/brandLink.jsp">
                      <dsp:param name="product" param="product"/>
                      <dsp:param name="className" value="greyLink"/>
                      <dsp:param name="showImage" value="${true}"/>
                    </dsp:include>
                  </div>

                </dsp:oparam>
              </dsp:droplet>
            </c:if>
            <div class="clear"></div>


            <div class="clear"></div>
            <c:if test="${not empty listPrice}">
              <%@ include file="/castCatalog/includes/promotions.jspf" %>
              <div class="clear"></div>
            </c:if>

            <dsp:include page="includes/messageInformation.jsp">
              <dsp:param name="sku" value="${sku}"/>
              <dsp:param name="pricedItem" value="${pricedSku}"/>
            </dsp:include>

            <%@ include file="/castCatalog/includes/promotionFields.jspf"%>

            <%@ include file="/castCatalog/includes/productDetailsPromo.jspf" %>

            <c:if test="${not baFakeContext}">
              <dsp:include page="/castCatalog/includes/rightNavigationArea.jsp" flush="true">
                <dsp:param name="categoryId" value="${categoryId}"/>
                <dsp:param name="navAction" value="push"/>
              </dsp:include>
            </c:if>
          </div>
        </div>
      </div>
      <div class="clear"></div>
      <dsp:include page="includes/productDetailsTabs.jsp">
        <dsp:param name="listColor" value="${listColor}"/>
        <dsp:param name="bgColor" value="${bgColor}"/>
        <dsp:param name="element" value="${sku}"/>
        <dsp:param name="skuId" value="${sku.repositoryId}"/>
        <dsp:param name="fixedRelatedProducts" value="${fixedRelatedProducts}"/>
      </dsp:include>
      <div class="clear"></div>
      <dsp:include page="includes/seeAlsoArea.jsp">
        <dsp:param name="product" param="product"/>
      </dsp:include>
      </div>
      <div class="rightColumn rightColumnV2 productDetailsRightColumn">
        <div class="shoppingCartV2 clearfix">
          <dsp:include page="includes/addToCartArea.jsp">
            <dsp:param name="sku" value="${sku}"/>
            <dsp:param name="productId" value="${productId}"/>
            <dsp:param name="childSku" value="${childSku}"/>
            <dsp:param name="thumbnailImage" value="${productImage}"/>
            <dsp:param name="pageType" value="productDetails"/>
          </dsp:include>
          <dsp:include page="includes/castaCardArea.jsp">
            <dsp:param name="sku" value="${sku}"/>
          </dsp:include>
        </div>
        <!--<div class="side_banner_block">
            <dsp:droplet name="ProductLookup">
              <dsp:param name="id" param="productId"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="kitchenContentHtml" param="element.kitchenContentHtml" />
                <c:if test="${not empty kitchenContentHtml}">
                  <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
                  <c:import charEncoding="utf-8" url="${staticContentPath}${kitchenContentHtml}"/>
                </c:if>
              </dsp:oparam>
            </dsp:droplet>
            </div>-->
        <dsp:include page="includes/stockAvailabilityArea.jsp">
          <dsp:param name="skuId" value="${sku.repositoryId}"/>
          <dsp:param name="productId" value="${productId}"/>
        </dsp:include>

        <c:if test="${not baFakeContext}">
          <dsp:include page="/comparison/camparateur.jsp">
            <dsp:param name="categoryId" value="${categoryId}"/>
          </dsp:include>
        </c:if>
      </div>
      <div class="clear"><!--~-->
        <script>
          if ($('span.numberMetersV2').length != 0) {
            $(".shoppingCartV2").addClass('quanityToLeft');
          };
        </script>
      </div>
      <dsp:include page="includes/sendToAFriendPopUp.jsp">
        <dsp:param name="productId" param="productId"/>
      </dsp:include>
      </dsp:oparam>
      </dsp:droplet>
      <%-- Omniture params Section begins--%>
      <c:set var="omnitureSkuId" value="${sku.repositoryId}" scope="request"/>
      <c:set var="omnitureProducts" value="${skuCodeArticle}" scope="request"/>
      <%-- Omniture params Section ends--%>

      <%-- Tag Commander params Section begins --%>
      <c:set var="tagCommanderSkuId" value="${sku.repositoryId}" scope="request"/>
    <c:set var="tagCommanderProductId" value="${productId}" scope="request"/>
      <%-- Tag Commander params Section ends --%>

    </jsp:attribute>
    <jsp:attribute name="canonicalUrl">
      <dsp:importbean bean="/com/castorama/droplet/CanonicalLinkDroplet"/>
      <dsp:droplet name="CanonicalLinkDroplet">
        <dsp:param name="type" value="product"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="canonicalUrl" param="url"/>
        </dsp:oparam>
      </dsp:droplet>
      ${canonicalUrl}
    </jsp:attribute>
</cast:pageContainer>

</dsp:oparam>
</dsp:droplet>
</dsp:page>
