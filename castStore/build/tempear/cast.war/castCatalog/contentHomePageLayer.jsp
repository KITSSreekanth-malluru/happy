<dsp:page>

  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  <dsp:importbean bean="/atg/targeting/TargetingFirst"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePageFeaturedProductsSlot"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePageFeaturedSectionSlot"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePagePromo1Slot"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePagePromo2Slot"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePagePromo3Slot"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePagePromo4Slot"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePagePromo5Slot"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePagePromo6Slot"/>
  <dsp:importbean bean="/atg/dynamo/droplet/For"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  
  <!-- slide right panel with shopping list -->
  <dsp:include page="/shoppingList/shoppingListSlider.jsp">
    <dsp:param name="currentPage" value="product"/> 
  </dsp:include>

  <div class="content homePage">
    <div class="leftCol">
      <div class="borderBlock">
        <dsp:droplet name="TargetingFirst">
          <dsp:param name="howMany" value="1"/>
          <dsp:param bean="HomePageFeaturedSectionSlot" name="targeter"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:oparam name="output">
            <dsp:droplet name="ProductLookup">
              <dsp:param name="id" param="element.product.repositoryId"/>
              <dsp:param name="elementName" value="product"/>
              <dsp:oparam name="output">
                <dsp:droplet name="CastPriceRangeDroplet">
                  <dsp:param name="productId" param="product.repositoryId"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>
                    <div class="randHeadline">
                      <div class="productHeaderB1">
                        <dsp:valueof param="element.description" valueishtml="true"/>
                      </div>
                      <div class="productHeaderB2">
                        <dsp:getvalueof var="name" param="product.displayName"/>
                        <dsp:getvalueof var="comparatorImage" param="sku.comparatorImage.url"/>
                        <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
                          <dsp:param name="productId" param="product.repositoryId"/>
                          <dsp:param name="navAction" value="jump"/>
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="template" param="url"/>
                          </dsp:oparam>
                        </dsp:droplet>
                        
                        <dsp:include page="includes/prodHighlight.jsp">
                          <dsp:param name="product" param="product"/>
                          <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}"/>
                          <dsp:param name="categoryId" param="categoryId"/>
                          <dsp:param name="view" value="galeryView"/>
                          <dsp:param name="noBreadcrumbs" value="true" />
                        </dsp:include>
                        <dsp:a href="${contextPath}${template}" title="${name}">
                          <c:choose>
                            <c:when test="${not empty comparatorImage}">
                              <dsp:img src="${comparatorImage}" width="140" height="140" alt="${name}" title="${name}" /> 
                            </c:when>
                            <c:otherwise>
                              <dsp:img src="/default_images/e_no_img.jpg" width="140" height="140" alt="${name}"  title="${name}" />
                            </c:otherwise>
                          </c:choose>
                          <dsp:param name="productId" param="product.repositoryId"/>
                        </dsp:a>
                      </div>
    
                      <div class="productHeaderB3">
                        <div class="productItem">
                          <dsp:a href="${contextPath}${template}" title="${name}">
                            <dsp:valueof value="${name}"/>
                          </dsp:a>
                          <dsp:include page="includes/skuPrice.jsp">
                            <dsp:param name="pageType" value="homePage"/>
                            <dsp:param name="productId" param="product.repositoryId"/>
                            <dsp:param name="sku" param="sku"/>
                          </dsp:include>
                        </div>
                      </div>
                    </div>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>        
          </dsp:oparam>
          <dsp:oparam name="empty">
           <dsp:getvalueof var="isEmpty" value="true"/>
          </dsp:oparam>
        </dsp:droplet>

        <c:choose>
          <c:when test="${isEmpty}">
            <div class="productsRow removeBorder hasHighlight">
          </c:when>
          <c:otherwise>
            <div class="productsRow hasHighlight">
          </c:otherwise>
        </c:choose>
        <dsp:droplet name="TargetingFirst">
          <dsp:param bean="HomePageFeaturedProductsSlot" name="targeter"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:param name="howMany" value="16"/>
          <dsp:oparam name="empty">
          </dsp:oparam>
          <dsp:oparam name="output">
            <dsp:getvalueof var="howMany" param="howMany"/>
            <dsp:getvalueof var="count" param="count"/>
            <c:choose>
              <c:when test="${count mod 4 == 0}">
                <div class="productItem piLast">
                  <dsp:include page="/castCatalog/includes/productTemplate.jsp">
                    <dsp:param name="productId" param="element.repositoryId"/>
                    <dsp:param name="categoryId" param="categoryId"/>
                    <dsp:param name="navAction" value="jump"/> 
                    <dsp:param name="navCount" bean="/atg/commerce/catalog/CatalogNavHistory.navCount"/> 
                  </dsp:include>
                </div>
                <div class="clear"></div>
                </div>
                <c:if test="${count!=howMany}">
                  <div class="productsRow hasHighlight">
                </c:if>
              </c:when>
              <c:otherwise>
                <div class="productItem">
                  <dsp:include page="/castCatalog/includes/productTemplate.jsp">
                    <dsp:param name="productId" param="element.repositoryId"/>
                    <dsp:param name="categoryId" param="categoryId"/>
                    <dsp:param name="navAction" value="jump"/>  
                    <dsp:param name="navCount" bean="/atg/commerce/catalog/CatalogNavHistory.navCount"/>  
                  </dsp:include>
                </div>
              </c:otherwise>
            </c:choose>
          </dsp:oparam>
        </dsp:droplet>
      </div>
    </div>
    <div class="rightCol">
      <div class="banner">
        <dsp:include page="/castCommon/promoInformationTargeter.jsp">
          <dsp:param name="homePagePromoBean" bean="HomePagePromo1Slot"/>
          <dsp:param name="width" value="390"/>
          <dsp:param name="height" value="188"/>
          <dsp:param name="flashId" value="HomePagePromo1Slot"/>
        </dsp:include>
      </div>
      <div class="banner">
        <dsp:include page="/castCommon/promoInformationTargeter.jsp">
          <dsp:param name="homePagePromoBean" bean="HomePagePromo2Slot"/>
          <dsp:param name="width" value="390"/>
          <dsp:param name="height" value="188"/>
          <dsp:param name="flashId" value="HomePagePromo2Slot"/>
        </dsp:include>
      </div>  
      <div class="banner">
        <dsp:include page="/castCommon/promoInformationTargeter.jsp">
          <dsp:param name="homePagePromoBean" bean="HomePagePromo3Slot"/>
          <dsp:param name="width" value="390"/>
          <dsp:param name="height" value="188"/>
          <dsp:param name="flashId" value="HomePagePromo3Slot"/>
        </dsp:include>
      </div>
      <div class="banner">  
        <dsp:include page="/castCommon/promoInformationTargeter.jsp">
          <dsp:param name="homePagePromoBean" bean="HomePagePromo4Slot"/>
          <dsp:param name="width" value="390"/>
          <dsp:param name="height" value="188"/>
          <dsp:param name="flashId" value="HomePagePromo4Slot"/>
        </dsp:include>
      </div>
      <div class="banner">  
        <dsp:include page="/castCommon/promoInformationTargeter.jsp">
          <dsp:param name="homePagePromoBean" bean="HomePagePromo5Slot"/>
          <dsp:param name="width" value="390"/>
          <dsp:param name="height" value="188"/>
          <dsp:param name="flashId" value="HomePagePromo5Slot"/>
        </dsp:include>
      </div>
      <div class="banner">  
        <dsp:include page="/castCommon/promoInformationTargeter.jsp">
          <dsp:param name="homePagePromoBean" bean="HomePagePromo6Slot"/>
          <dsp:param name="width" value="390"/>
          <dsp:param name="height" value="188"/>
          <dsp:param name="flashId" value="HomePagePromo6Slot"/>
        </dsp:include>
      </div>  
    </div>
  </div>
</dsp:page>
