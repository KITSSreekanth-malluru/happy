<dsp:page>

<dsp:droplet name="/com/castorama/droplet/OrphanProductRedirectDroplet">
  <dsp:param name="productId" param="productId"/>
  <dsp:oparam name="notOrphan">

  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>

  <dsp:getvalueof var="paramSkuId" param="skuId"/>
  <c:if test="${not empty paramSkuId}">
    <dsp:droplet name="SKULookup">
      <dsp:param name="id" value="${paramSkuId}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="sku" param="element"/>

        <dsp:droplet name="/com/castorama/droplet/CastPriceItem">
          <dsp:param name="item" value="${sku}"/>
          <dsp:param name="elementName" value="pricedSku"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="pricedSku" param="pricedSku"/>
            <dsp:getvalueof var="cardPrice" param="pricedSku.priceInfo.cardPrice"/>
            <dsp:getvalueof var="isValidByDateCardPrice" param="pricedSku.priceInfo.IsValidByDateCardPrice"/>
            <c:if test="${(not empty cardPrice) and (cardPrice > 0) and (isValidByDateCardPrice == 'true')}">
              <dsp:getvalueof var="cpb" value="true"/>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>

  <cast:pageContainer>
    <jsp:attribute name="bvInclude">view</jsp:attribute>
    <jsp:attribute name="bottomBanners">true</jsp:attribute>
    <jsp:attribute name="cardPriceBanner">${cpb}</jsp:attribute>
    <jsp:attribute name="metaInfoInclude">/global/meta.jsp?id=<dsp:valueof param="productId"/>&item=product</jsp:attribute>
    <jsp:attribute name="bodyContent">

      <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
      <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
      <dsp:importbean bean="/com/castorama/util/ProductTabs" />
      <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
      <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
      <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
      <dsp:importbean bean="/atg/commerce/catalog/ProductBrowsed"/>
      <dsp:importbean bean="/com/castorama/droplet/CastGetApplicablePromotions"/>
      <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
      <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
      <dsp:importbean bean="/com/castorama/droplet/IsRobotDroplet"/>
      <dsp:importbean bean="/atg/dynamo/droplet/Cache"/>
      <dsp:importbean bean="/com/castorama/droplet/ProductDescPreProcessorDroplet"/>

      <dsp:getvalueof var="productId" param="productId"/>
      <dsp:getvalueof var="categoryId" param="categoryId"/>
      <dsp:getvalueof var="paramSkuId" param="skuId"/>
      <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

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
     
      <c:choose>
        <c:when test="${not empty paramSkuId}">
          <dsp:droplet name="SKULookup">
            <dsp:param name="id" value="${paramSkuId}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="sku" param="element"/>
              <!-- dsp:getvalueof var="pfLeftColumn" value="pfLeftColumn"/-->
              <dsp:getvalueof var="singleSku" value="true"/>
              <dsp:getvalueof var="fixedRelatedProducts" param="element.crossSelling"/>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:droplet name="CastPriceRangeDroplet">
            <dsp:param name="productId" value="${productId}"/>
            <dsp:oparam name="output">              
              <dsp:getvalueof var="cheapestSku" param="sku"/>
              <dsp:getvalueof var="notCheapestSkuPromoExists" param="notCheapestSkuPromo"/>
              <c:if test="${(paramSkuId != cheapestSku.repositoryId) and not empty notCheapestSkuPromoExists 
                  and notCheapestSkuPromoExists}">
                <dsp:getvalueof var="notCheapestSkuPromoChosen" value="true"/>
                <dsp:getvalueof var="notCheapestSkuPromo" value="false"/>
              </c:if>
            </dsp:oparam>
          </dsp:droplet>
        </c:when>
        <c:otherwise>
          <dsp:droplet name="CastPriceRangeDroplet">
            <dsp:param name="productId" value="${productId}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="fixedRelatedProducts" param="sku.crossSelling"/>
              <dsp:getvalueof var="sku" param="sku"/>
              <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>
              <dsp:getvalueof var="singleSku" value="false"/>
            </dsp:oparam>
          </dsp:droplet>  
        </c:otherwise>
      </c:choose>
       
      <dsp:droplet name="ProductLookup">
        <dsp:param name="id" value="${productId}"/>
        <dsp:oparam name="output">
          <dsp:param name="product" param="element"/>
          <dsp:droplet name="ProductBrowsed">
            <dsp:param name="eventobject" param="element"/>
          </dsp:droplet>
          
         
          
          <c:choose>
            <c:when test="${isSearchResult}">
              <dsp:getvalueof var="bgColor" param="product.parentCategory.style.backgroundStyle" />
              <dsp:getvalueof var="linkColor" param="product.parentCategory.style.linkStyle" />
              <dsp:getvalueof var="listColor" param="product.parentCategory.style.listStyle" />
            </c:when>
            <c:otherwise>
              <dsp:droplet name="StyleLookupDroplet">
                <dsp:param name="categoryId" value="${categoryId}" />
                <dsp:oparam name="output">
                  <dsp:getvalueof var="bgColor" param="style.backgroundStyle" />
                  <dsp:getvalueof var="linkColor" param="style.linkStyle" />
                  <dsp:getvalueof var="listColor" param="style.listStyle" /> 
                </dsp:oparam>
              </dsp:droplet> 
            </c:otherwise>
          </c:choose> 
    
      <div class="carouselV2">    
          <dsp:include page="carouselMotherCategory.jsp">
            <dsp:param name="ownerProductId" value="${productId}"/>
            <dsp:param name="ownerCategoryId" value="${categoryId}"/>
          </dsp:include>
        </div>  
      
          <div class="clear"></div>

          <div class="content width800 width750">
            <div class="productBlock singleProductV2" id="productItemWithPrice">
              <c:choose>
                <c:when test="${!isSearchResult}">
              <dsp:include page="breadcrumbsCollector.jsp" >       
                <dsp:param name="productId" param="productId"/>       
                <dsp:param name="navAction" param="navAction"/>
                <dsp:param name="navCount" param="navCount"/>        
              </dsp:include>
              
              <dsp:include page="breadcrumbs.jsp" flush="true" >
                <dsp:param name="productId" param="productId" />
                <dsp:param name="isCollected" value="true" />
                <dsp:param name="navAction" param="navAction" />
                <dsp:param name="navCount" param="navCount" />
                <dsp:param name="categoryId" value="${categoryId}"/>
              </dsp:include>
              <c:set var="footerBreadcrumb" value="productDetails" scope="request"/>
              </c:when>
            <c:otherwise>
                <%@ include file="/castCatalog/includes/returnButton.jsp" %>
            </c:otherwise>
        </c:choose>
            
            
              <dsp:droplet name="PriceDroplet">
                <dsp:param name="sku" value="${sku}"/> 
                <dsp:oparam name="output">
                  <dsp:getvalueof var="listPrice" param="price.listPrice"/> 
                </dsp:oparam> 
              </dsp:droplet>
              
              <c:if test="${empty listPrice}">   
                <dsp:getvalueof var="notCheapestSkuPromo" value="false"/>
                <dsp:getvalueof var="notCheapestSkuPromoChosen" value="false"/>
              </c:if>
            
              <dsp:setvalue param="sku" value="${sku}"/>
              <dsp:getvalueof var="storeId" bean="/atg/userprofiling/Profile.currentLocalStore.id"/>
              <dsp:droplet name="/com/castorama/droplet/ProductDetailsCache">
                <dsp:param name="key" value="multi_sku_${categoryId}_${productId}_${paramSkuId}_1_${isRobot}_${storeId}"/>
                <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
                <dsp:oparam name="output">
                  <dsp:include page="includes/productImageArea.jsp">
                    <dsp:param name="element" value="${sku}"/>
                    <dsp:param name="categoryId" value="${categoryId}"/>
                    <dsp:param name="product" param="product"/>
                    <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}"/>
                    <dsp:param name="notCheapestSkuPromoChosen" value="${notCheapestSkuPromoChosen}" />
                    <dsp:param name="skuCodeArticle" param="sku.CodeArticle"/>
                  </dsp:include>
                </dsp:oparam>
              </dsp:droplet>
            
              <div class="productContent">
                <dsp:droplet name="Cache">
                  <dsp:param name="key" value="multi_sku_${categoryId}_${productId}_${paramSkuId}_2_${isRobot}_${singleSku}_${storeId}"/>
                  <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
                  <dsp:oparam name="output">
                    <dsp:setvalue param="sku" value="${sku}"/>
                    <h1>
                      <c:choose>
                        <c:when test="${singleSku}">
                          <dsp:valueof param="sku.displayName"/>
                        </c:when>
                        <c:otherwise>
                          <dsp:valueof param="product.displayName"/>
                        </c:otherwise>
                      </c:choose>
                    </h1>
                  </dsp:oparam>
                </dsp:droplet>
        
                <div class="productFooter">
                    <div class="inutChoiseV2">
                        <dsp:include page="includes/skuDropDown.jsp" flush="true">
                            <dsp:param name="productId" value="${productId}"/>
                            <dsp:param name="categoryId" value="${categoryId}"/>
                            <dsp:param name="isMultiSku" value="true"/>
                        </dsp:include>
                    </div>
                    <dsp:droplet name="/com/castorama/droplet/ProductDetailsCache">
                    <dsp:param name="key" value="multi_sku_${categoryId}_${productId}_${paramSkuId}_3_${isRobot}_${storeId}"/>
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
                         <a href="javascript:void(0)" onclick="$('#popup_video_img_area_link').click();"><fmt:message key="castCatalog_label.view.video" /></a>
                      </c:if>
                  </c:if>
                  </div>
                           </div>
               </c:if>

                     
                      </div>
                      
                        <div class="pfLeftColumn">
                          <div class="socialRateAndComment">
                
                <!-- 
                <div class="starVote4"></div>
                <div class="countCommentsV2">
                  <a href="#"=#>3 commentaires</a>
                </div>
                <div class="socialFBV2">
                  <a href="#"></a>
                </div>
                <div class="socialGoogleV2">
                  <a href="#"></a>
                </div> -->
                
                <dsp:include page="includes/noteArea.jsp"/>
                        <div class="clear"></div>  
                
                
              </div>
              
                <ul class="nvLinks">  
                          <li class="impri">
                            <fmt:message key="castCatalog_label.print" var="imprimer"/>
                      <dsp:img src="${contextPath}/images/blank.gif" alt="${imprimer}" title="${imprimer}"/>&nbsp;
                      <dsp:getvalueof var="skuId" param="skuId"/>
                      <dsp:a href="${contextPath}/castCatalog/includes/printProductInfo.jsp?productId=${productId}&skuId=${skuId}&categoryId=${categoryId}&isSearchResult=${isSearchResult}&notCheapestSkuPromo=${notCheapestSkuPromo}&notCheapestSkuPromoChosen=${notCheapestSkuPromoChosen}" target="_blank" iclass="imprimer" title="${imprimer}">${imprimer}</dsp:a>
                    </li>
                      </ul>
                            
                             
                            
                            <div class="clear"></div>
                           
                          </div>
                           <div class="productDecription">
                              <c:if test="${singleSku}">
                                <span class="refNum"> 
                                  <fmt:message key="castCatalog_contentProductDetails.ref"/>
                                  <dsp:valueof param="sku.CodeArticle"/>
                                  <dsp:getvalueof var="skuCodeArticle" param="sku.CodeArticle"/>
                                </span>
                              </c:if>  
                          
                              <ul class="${listColor}">
                                <dsp:getvalueof var="showSeeMore" value="false" vartype="boolean"/>
                                <c:choose>
                                  <c:when test="${singleSku}">
                                    <dsp:getvalueof var="description" param="sku.LibelleClientLong"/>
                                  </c:when>
                                  <c:otherwise>
                                    <dsp:getvalueof var="description" param="product.longDescription"/>
                                  </c:otherwise>
                                </c:choose>
                                <dsp:droplet name="ProductDescPreProcessorDroplet">
                                  <dsp:param name="description" value="${description}"/>
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
                  <!-- c:if test="${singleSku}"-->
                   
                  <div class="clear"></div>
                    <dsp:include page="includes/noteArea.jsp"/>
                  <div class="clear"></div>
                  <!-- /c:if-->
                  <c:if test="${not empty listPrice}">
                    <%@ include file="/castCatalog/includes/promotions.jspf" %>
                    <div class="clear"></div>
                  </c:if>
                  
                  <dsp:include page="includes/messageInformation.jsp">
                    <dsp:param name="sku" value="${sku}"/>
                    <dsp:param name="pricedSku" value="${pricedSku}"/>
                  </dsp:include>

                  <%@ include file="/castCatalog/includes/promotionFields.jspf"%>
                  
                  <%@ include file="/castCatalog/includes/productDetailsPromo.jspf" %>
                  
                  <c:if test="${not baFakeContext}">
                    <dsp:include page="/castCatalog/includes/rightNavigationArea.jsp" flush="true">
                      <dsp:param name="categoryId" value="${categoryId}"/>
                      <dsp:param name="navAction" value="push" />
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
              <dsp:param name="singleSku" value="${singleSku}"/>
            </dsp:include>
        
            <div class="clear"></div>
    
            <dsp:include page="includes/seeAlsoArea.jsp">
              <dsp:param name="product" param="product"/>
            </dsp:include>

          </div>

          <div class="rightColumn rightColumnV2 productDetailsRightColumn ">
            
           
               <div class="shoppingCartV2 clearfix">
                      <dsp:include page="includes/addToCartArea.jsp">
                        <dsp:param name="sku" value="${sku}"/>
                        <dsp:param name="productId" value="${productId}"/>
                        <dsp:param name="childSku" value="${childSku}"/>
                        <dsp:param name="skuImage" value="${skuImage}"/>
                        <dsp:param name="pageType" value="productDetails"/>
                        <dsp:param name="multiSku" value="true" />
                        <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromoChosen}" />
                      </dsp:include>
                      <c:if test="${not empty listPrice}">
                        <dsp:include page="includes/castaCardArea.jsp"/>
                      </c:if>
                  </div>

            
            <%--<div class="side_banner_block">    
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
            </div>
            --%>
                
              <dsp:include page="includes/stockAvailabilityArea.jsp">
                <dsp:param name="skuId" value="${sku.repositoryId}"/>
                <dsp:param name="productId" value="${productId}"/>
                <dsp:param name="singleSku" value="${singleSku}"/>
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
          <dsp:include page="includes/sendToAFriendPopUp.jsp"/>
        </dsp:oparam>
      </dsp:droplet>
      
      <%-- Omniture params Section begins--%>
        <c:choose>
            <c:when test="${not empty choosenSkuId}">
                <c:set var="omnitureProducts" value="${choosenSkuId}" scope="request"/>
            </c:when>
            <c:otherwise>
                <c:set var="omnitureProducts" value="${childSkusCodesList}" scope="request"/>
            </c:otherwise>
        </c:choose>
        <%-- Omniture params Section ends--%>
      
      <%-- Tag Commander params Section begins --%>
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