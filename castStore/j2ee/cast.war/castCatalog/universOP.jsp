<dsp:page>
  <cast:pageContainer>
    <jsp:attribute name="bottomBanners">true</jsp:attribute>
    <jsp:attribute name="cloudType">page</jsp:attribute>
    <jsp:attribute name="bodyContent">
      
      <dsp:getvalueof var="pageIndex" param="pageIndex"/>

      <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
      <dsp:importbean bean="/atg/targeting/TargetingFirst"/>
      <dsp:importbean bean="/atg/dynamo/droplet/For"/>

      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
      

      <div class="breadcrumbs orangePage">
        <div class="homeBreadIco">
          <a href="${contextPath}/home.jsp">
            <img src="${contextPath}/images/icoHomeGray.gif" alt="Accueil" title="Accueil"/>
          </a>
        </div>
        <div class="splitter">></div>
        <div class="active">
          <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
		        <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPBreadcrumbsSlot${pageIndex}"/>
	          <dsp:oparam name="output">
							<dsp:droplet name="TargetingFirst">
							  <dsp:param bean="/atg/registry/Slots/UniversOPBreadcrumbsSlot${pageIndex}" name="targeter"/>
							  <dsp:param name="fireViewItemEvent" value="false"/>
							  <dsp:oparam name="output">
							    <dsp:valueof param="element.htmlUrl"/>
							  </dsp:oparam>
							</dsp:droplet> 
	          </dsp:oparam>
	        </dsp:droplet>  
        </div>
      </div>

      <%--<div id="teaser">--%>
      <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
        <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPPageTopSlot${pageIndex}"/>
          <dsp:oparam name="output">
		        <dsp:droplet name="TargetingFirst">
		          <dsp:param bean="/atg/registry/Slots/UniversOPPageTopSlot${pageIndex}" name="targeter"/>
		          <dsp:param name="fireViewItemEvent" value="false"/>
		          <dsp:oparam name="output">
		            <dsp:getvalueof var="promoTemplate" param="element" />
		            <c:if test="${not empty promoTemplate}">
		              <dsp:getvalueof var="layoutType" param="element.layoutType" />
		              <dsp:include page="../castCatalog/includes/catalogPromoTemplates/${layoutType}.jsp" flush="true">
		                <dsp:param name="promoInformation" param="element.promoInformation" />
		              </dsp:include>  
		            </c:if>
		          </dsp:oparam>
		        </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      <%--</div>--%>
 
      <div class="content homePage">
        <div class="leftCol">
          <h2>
            <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
              <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPLeftNameSlot${pageIndex}"/>
              <dsp:oparam name="output">
                <dsp:droplet name="TargetingFirst">
                  <dsp:param bean="/atg/registry/Slots/UniversOPLeftNameSlot${pageIndex}" name="targeter"/>
                  <dsp:param name="fireViewItemEvent" value="false"/>
                  <dsp:oparam name="output">
                    <dsp:valueof param="element.htmlUrl"/>
                  </dsp:oparam>
                </dsp:droplet> 
              </dsp:oparam>
            </dsp:droplet> 
          </h2>
          <div class="borderBlock">
            <div class="productsRow removeBorder">
			      <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
			        <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPFeaturedProductsSlot${pageIndex}"/>
			          <dsp:oparam name="output">
		              <dsp:droplet name="TargetingFirst">
		                <dsp:param bean="/atg/registry/Slots/UniversOPFeaturedProductsSlot${pageIndex}" name="targeter"/>
		                <dsp:param name="fireViewItemEvent" value="false"/>
		                <dsp:param name="howMany" value="16"/>
		                <dsp:oparam name="output">
		                  <dsp:getvalueof var="howMany" param="howMany"/>
		                  <dsp:getvalueof var="count" param="count"/>
		                   <c:choose>
		                      <c:when test="${count mod 4 == 0}">
		                        <div class="productItem piLast">
		                          <dsp:include page="/castCatalog/includes/productTemplate.jsp">
		                            <dsp:param name="productId" param="element.repositoryId"/>
		                            <dsp:param name="categoryId" param="categoryId"/>
		                            <dsp:param name="nozoom" value="false"/>
		                            <dsp:param name="navAction" value="jump"/>
		                            <dsp:param name="navCount" bean="/atg/commerce/catalog/CatalogNavHistory.navCount"/>
		                          </dsp:include>
		                        </div>
		                        <div class="clear"></div>
		                        </div>
		                        <c:if test="${count!=howMany}">
		                          <div class="productsRow">
		                        </c:if>
		                      </c:when>
		                      <c:otherwise>
		                        <div class="productItem">
		                          <dsp:include page="/castCatalog/includes/productTemplate.jsp">
		                            <dsp:param name="productId" param="element.repositoryId"/>
		                            <dsp:param name="categoryId" param="categoryId"/>
		                            <dsp:param name="nozoom" value="false"/>
		                            <dsp:param name="navAction" value="jump"/>
		                            <dsp:param name="navCount" bean="/atg/commerce/catalog/CatalogNavHistory.navCount"/>
		                          </dsp:include>
		                        </div>
		                      </c:otherwise>
		                    </c:choose>
		                  </dsp:oparam>
		                </dsp:droplet>
                  </dsp:oparam>
                </dsp:droplet>
              </div>
            </div>
          <div class="rightCol">
            <h2>
              <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
		            <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPRightNameSlot${pageIndex}"/>
		            <dsp:oparam name="output">
		              <dsp:droplet name="TargetingFirst">
		                <dsp:param bean="/atg/registry/Slots/UniversOPRightNameSlot${pageIndex}" name="targeter"/>
		                <dsp:param name="fireViewItemEvent" value="false"/>
		                <dsp:oparam name="output">
		                  <dsp:valueof param="element.htmlUrl"/>
		                </dsp:oparam>
		              </dsp:droplet> 
		            </dsp:oparam>
		          </dsp:droplet> 
            </h2>
            <div class="banner">
            <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
              <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPPage3Slot${pageIndex}"/>
              <dsp:oparam name="output">
	              <dsp:include page="/castCommon/promoInformationTargeter.jsp">
	                <dsp:param name="homePagePromoBean" bean="/atg/registry/Slots/UniversOPPage3Slot${pageIndex}"/>
	                <dsp:param name="width" value="390"/>
	                <dsp:param name="height" value="188"/>
	                <dsp:param name="flashId" value="UniversOPPage3Slot${pageIndex}"/>
	              </dsp:include>
              </dsp:oparam>
            </dsp:droplet>
            </div>
            <div class="banner">
            <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
              <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPPage4Slot${pageIndex}"/>
              <dsp:oparam name="output">
	              <dsp:include page="/castCommon/promoInformationTargeter.jsp">
	                <dsp:param name="homePagePromoBean" bean="/atg/registry/Slots/UniversOPPage4Slot${pageIndex}"/>
	                <dsp:param name="width" value="390"/>
	                <dsp:param name="height" value="188"/>
	                <dsp:param name="flashId" value="UniversOPPage4Slot${pageIndex}"/>
	              </dsp:include>
              </dsp:oparam>
            </dsp:droplet>
            </div>
            <div class="banner">  
            <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
              <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPPage5Slot${pageIndex}"/>
               <dsp:oparam name="output">
		            <dsp:include page="/castCommon/promoInformationTargeter.jsp">
		              <dsp:param name="homePagePromoBean" bean="/atg/registry/Slots/UniversOPPage5Slot${pageIndex}"/>
		              <dsp:param name="width" value="390"/>
		              <dsp:param name="height" value="188"/>
		              <dsp:param name="flashId" value="UniversOPPage5Slot${pageIndex}"/>
		            </dsp:include>
              </dsp:oparam>
            </dsp:droplet>
            </div>  
            <div class="banner">
            <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
              <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPPage6Slot${pageIndex}"/>
                <dsp:oparam name="output">
		              <dsp:include page="/castCommon/promoInformationTargeter.jsp">
		                <dsp:param name="homePagePromoBean" bean="/atg/registry/Slots/UniversOPPage6Slot${pageIndex}"/>
		                <dsp:param name="width" value="390"/>
		                <dsp:param name="height" value="188"/>
		                <dsp:param name="flashId" value="UniversOPPage6Slot${pageIndex}"/>
		              </dsp:include>
                </dsp:oparam>
              </dsp:droplet>
            </div>  
            <div class="banner">
	            <dsp:droplet name="/com/castorama/droplet/ComponentExistenceDroplet">
	              <dsp:param name="componentName" value="/atg/registry/Slots/UniversOPPage7Slot${pageIndex}"/>
	                <dsp:oparam name="output">
			              <dsp:include page="/castCommon/promoInformationTargeter.jsp">
			                <dsp:param name="homePagePromoBean" bean="/atg/registry/Slots/UniversOPPage7Slot${pageIndex}"/>
			                <dsp:param name="width" value="390"/>
			                <dsp:param name="height" value="188"/>
			                <dsp:param name="flashId" value="UniversOPPage7Slot${pageIndex}"/>
			              </dsp:include>
                </dsp:oparam>
              </dsp:droplet>
            </div>  
          </div>
        </div>

     </jsp:attribute>
  </cast:pageContainer>
</dsp:page>