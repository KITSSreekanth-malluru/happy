<dsp:page>
  
  <dsp:getvalueof var="wrapAllowed" param="wrap" />
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />
  <dsp:droplet name="CategoryLookup">  
    <dsp:param name="id" param="categoryId" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="customPageLink" param="element.customPageLink" />
    </dsp:oparam>
  </dsp:droplet>
  
  <c:choose>
    <c:when test="${not empty wrapAllowed and wrapAllowed and not empty customPageLink and customPageLink != ''}">
        
  <cast:pageContainer>
     <jsp:attribute name="metaInfoInclude">/global/meta.jsp?id=<dsp:valueof param="categoryId"/>&item=category</jsp:attribute>
    <jsp:attribute name="bodyContent">
      <div class="content">
        <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
        <c:import charEncoding="utf-8" url="${staticContentPath}${customPageLink}"/>
      </div>
    </jsp:attribute>
    <jsp:attribute name="canonicalUrl">
      <dsp:importbean bean="/com/castorama/droplet/CanonicalLinkDroplet"/>
      <dsp:droplet name="CanonicalLinkDroplet">
        <dsp:param name="type" value="regularCategory"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="canonicalUrl" param="url"/>
        </dsp:oparam>
      </dsp:droplet>
      ${canonicalUrl}
    </jsp:attribute>
  </cast:pageContainer>
          
    </c:when>
    <c:otherwise>
        
  <dsp:include page="breadcrumbsCollector.jsp" >       
    <dsp:param name="categoryId" param="categoryId"/>       
    <dsp:param name="navAction" param="navAction"/>
    <dsp:param name="navCount" param="navCount"/>
  </dsp:include>
  
  <cast:pageContainer>
    <jsp:attribute name="bottomBanners">true</jsp:attribute>
    <jsp:attribute name="metaInfoInclude">/global/meta.jsp?id=<dsp:valueof param="categoryId"/>&item=category</jsp:attribute>
    <jsp:attribute name="bodyContent">
    
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
    <dsp:importbean bean="/atg/dynamo/droplet/For" />
    <dsp:importbean bean="/com/castorama/droplet/IsRobotDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/Cache" />
    <dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />

    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
    
    <!-- slide right panel with shopping list -->
    <dsp:include page="/shoppingList/shoppingListSlider.jsp">
      <dsp:param name="currentPage" value="regularCategory"/> 
    </dsp:include>

    <dsp:droplet name="CategoryLookup">  
      <dsp:param name="id" param="categoryId" />
      <dsp:oparam name="output">
        <dsp:getvalueof var="bonnesAffaires" param="element.bonnesAffaires" scope="request"/>
        <c:if test="${bonnesAffaires and wrappedStoreId!='999' and (wrappedStoreIsCC || wrappedStoreIsLocal)}">
          <dsp:getvalueof var="baFakeContext" value="${true}" scope="request"/>
        </c:if>
          
        <dsp:param name="category" param="element"/>
        <dsp:include page="breadcrumbs.jsp" flush="true">
          <dsp:param name="categoryId" param="categoryId" /> 
        </dsp:include>

        <dsp:getvalueof var="promoTemplate" param="category.promoTemplate" />
        <c:if test="${not empty promoTemplate}">
          <dsp:getvalueof var="layoutType" param="category.promoTemplate.layoutType" />
          <dsp:include page="includes/catalogPromoTemplates/${layoutType}.jsp" flush="true">
            <dsp:param name="promoInformation" param="category.promoTemplate.promoInformation" />
          </dsp:include>	
        </c:if>
        <div class="castoramaArticlesText">
          <dsp:valueof param="category.infoText" />
        </div>
        <div class="castoramaMiddleText">
          <dsp:valueof param="category.commerceText" valueishtml="true"/>
        </div>
        
        <dsp:getvalueof var="isRobot" value="false"/>
        <dsp:droplet name="IsRobotDroplet">
          <dsp:oparam name="true">
            <dsp:getvalueof var="isRobot" value="true"/>
          </dsp:oparam>
        </dsp:droplet>
        <dsp:getvalueof var="categoryId" param="categoryId"/>
        <dsp:droplet name="Cache">
          <dsp:param name="key" value="basic_category_${categoryId}_${isRobot}" />
          <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
          <dsp:oparam name="output">
            <div class="productsListLayer productsListLayerV2">
            <div class="productsListLayerInside">
            <dsp:droplet name="ForEach">
              <dsp:param name="array" param="category.childCategories" />
              <dsp:getvalueof var="count" param="count"/>
              <dsp:getvalueof var="size" param="size"/>
              <dsp:getvalueof var="index" param="index"/>
              <dsp:oparam name="output">
                <dsp:param name="childCategory" param="element"/>
                <dsp:getvalueof var="sortType" param="element.typeSort" scope="request" />
                <c:if test="${index % 2 == 0}">
                  <c:choose>
                    <c:when test="${(index == (size-1)) || (index == (size-2))}">
                      <div class="productsListLayerLine productsListLayerLastLine">
                    </c:when>
                    <c:when test="${index == 0}">
                      <div class="productsListLayerLine productsListLayerFirstLine">
                    </c:when>
                    <c:otherwise>
                      <div class="productsListLayerLine">
                    </c:otherwise>
                  </c:choose>
                </c:if>
                <dsp:droplet name="CastCategoryLinkDroplet">
                          <dsp:param name="categoryId" param="childCategory.repositoryId" />
                          <dsp:param name="navCount" bean="CatalogNavHistory.navCount" />
                          <dsp:param name="navAction" value="push" />
                          <dsp:oparam name="output">
                              <c:choose>
                                  <c:when test="${sortType == '0'}">
                                      <c:set var="sortBy" value="relevance"/>
                                  </c:when>
                                  <c:when test="${sortType == '1'}">
                                      <c:set var="sortBy" value="lowHighPrice"/>
                                  </c:when>
                                  <c:when test="${sortType == '2'}">
                                      <c:set var="sortBy" value="highLowPrice"/>
                                  </c:when>
                                  <c:otherwise>
                                      <c:set var="sortBy" value=""/>
                                  </c:otherwise>
                              </c:choose>
				        <dsp:getvalueof var="url" param="url" />
                        <dsp:getvalueof var="className" param="childCategory.style.linkStyle" />
                        <c:choose>
							<c:when test="${not empty sortBy }">
								<c:set var="productLink" value="${contextPath}${url}&wrap=true&sortByValue=${sortBy}"/>
							</c:when>
							<c:otherwise>
								<c:set var="productLink" value="${contextPath}${url}&wrap=true"/>
							</c:otherwise>
						</c:choose>
                  </dsp:oparam>
                </dsp:droplet>
				<div class="productsListItem">
				<a href="${productLink}" style="text-decoration:none !important;">
				<span class="productListItemWrapper">
                  <div class="leftCol">
                    <div class="productTitle">
                      <h2> 
					<dsp:valueof param="childCategory.displayName" />&nbsp;
					<dsp:getvalueof var="productsNumberContent" param="childCategory.productsNumberContent" />
					<c:choose>
                      <c:when test="${not empty productsNumberContent and productsNumberContent != ''}">
                        <span>${productsNumberContent}</span>
                      </c:when>
                      <c:otherwise>
                        <span> (<dsp:valueof param="childCategory.productsCount" /> produits)</span>
                    </c:otherwise>
					</c:choose>
                  </h2>
                    </div> <!-- end productTitle  -->
                    <div class="productContent">
                      <dsp:getvalueof var="subcategoriesContent" param="childCategory.subcategoriesContent" />
                      <c:choose>
                        <c:when test="${not empty subcategoriesContent and subcategoriesContent != ''}">
                          ${subcategoriesContent}
                        </c:when>
                        <c:otherwise>
                          <dsp:getvalueof var="sb" value="${castCollection:sb()}"/>
                          <dsp:droplet name="ForEach">
                            <dsp:getvalueof var="subCategories" param="childCategory.childCategories" />
                            <c:choose>
                              <c:when test="${not empty subCategories}">
                                <dsp:param name="array" param="childCategory.childCategories" />
                              </c:when>
                              <c:otherwise>
                                <dsp:param name="array" param="childCategory.childProducts" />
                              </c:otherwise>
                            </c:choose>
                            <dsp:getvalueof var="count" param="count" />
                            <dsp:oparam name="output">
                              <%-- DO NOT move "if" on the next line: in this case spaces appear before comma  --%>
                              <dsp:getvalueof var="name" param="element.displayName" />
                              <c:choose>
                                <c:when test="${count!=size}">
                                  ${castCollection:string(sb, name, true)}
                                </c:when>
                                <c:otherwise>
                                  ${castCollection:string(sb, name, false)}
                                </c:otherwise>
                              </c:choose>
                            </dsp:oparam>
                          </dsp:droplet>
                          <dsp:droplet name="/com/castorama/droplet/DocumentDescriptionBraker">
                            <dsp:param name="description" value="${sb}"/>
                            <dsp:param name="length" value="70"/>
                            <dsp:oparam name="output">
                              <dsp:getvalueof var="initialDescription" value="${castCollection:toString(sb)}"/>
                              <dsp:valueof param="updatedDescription" valueishtml="true"/><c:if test="${fn:length(initialDescription) >  70}">...</c:if>
                            </dsp:oparam>
                          </dsp:droplet>
                        </c:otherwise>
                      </c:choose>
                    </div> <!-- end productContent  -->
                  </div> <!-- end leftCol  -->
                  <div class="rightCol">
                    <dsp:droplet name="IsEmpty">
                      <dsp:param name="value" param="childCategory.thumbnailImage.url" />
                      <dsp:oparam name="true">
                        <dsp:getvalueof var="title" param="childCategory.displayName" />
                        <dsp:img src="${contextPath}/images/notAvail_140x140.png" alt="${title}" title="${title}" />
                      </dsp:oparam>
                      <dsp:oparam name="false">
                        <dsp:getvalueof var="img" param="childCategory.thumbnailImage.url" />
                        <dsp:a href="${contextPath}${url}&wrap=true&sortByValue=${sortBy}">
                          <dsp:img src="${img}" alt="${title}" title="${title}" />
                        </dsp:a>
                      </dsp:oparam>
                    </dsp:droplet>
                  </div> <!-- end rightCol  -->
				</span>
				</a>
                </div> <!-- end productsListItem  -->
                <c:if test="${(index % 2 == 1) || (index == (size-1))}">
                  </div>
                </c:if>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>

        </div> <!-- end productsListLayerInside  -->
        </div> <!-- end productsListLayer  -->
      </dsp:oparam>
    </dsp:droplet><!-- end Cache  -->
      <div class="rightNavCatV2">
      <dsp:include page="../castCatalog/includes/rightNavigationArea.jsp" flush="true">
        <dsp:param name="categoryId" param="categoryId" />
        <dsp:param name="navAction" value="push" />
      </dsp:include>
      </div>
    </jsp:attribute>
    
    <jsp:attribute name="canonicalUrl">
      <dsp:importbean bean="/com/castorama/droplet/CanonicalLinkDroplet"/>
      <dsp:droplet name="CanonicalLinkDroplet">
        <dsp:param name="type" value="regularCategory"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="canonicalUrl" param="url"/>
        </dsp:oparam>
      </dsp:droplet>
      ${canonicalUrl}
    </jsp:attribute>
  </cast:pageContainer>        
    </c:otherwise>
  </c:choose>
    
</dsp:page>