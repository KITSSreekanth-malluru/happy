<dsp:page>
    
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/registry/Slots/TopBarPromoSlot"/>
    
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  
  <div id="navPane">
    <div class="mainNavPanel">
      <div class="homeLinkP">
        <a href="${contextPath }" title="Accueil">&nbsp;</a>
      </div>
      <ul class="mainMenuUL" id="mainMenuUL">
        <dsp:droplet name="ForEach">
          <dsp:param name="array" bean="Profile.catalog.topNavigationCategories"/>
          <dsp:oparam name="output">
            <dsp:param name="id" param="element.repositoryId"/>
            <dsp:getvalueof var="index" param="index"/>
            <dsp:getvalueof var="count" param="size"/>
            <dsp:getvalueof var="displayName" param="element.displayName"/>
            <c:choose>
              <c:when test="${index <(count-1)}">
                <dsp:getvalueof id="dropDownTemplate" param="element.dropDownTemplate.url" idtype="java.lang.String"/>
                <li class="menu_point_<dsp:valueof param="count"/> mainMenuPoint">
                  <c:choose>
                    <c:when test="${not empty dropDownTemplate}">
                      <a href="#">
                                <img class="menuBgImg" src="${pageContext.request.contextPath}/images/blank.gif" alt="${displayName}" title="${displayName}" />                                
                      </a>
                      <dsp:getvalueof id="templateURL" param="element.dropDownTemplate.url" idtype="java.lang.String">
                        <dsp:include page="${templateURL}">
                          <dsp:param name="categoryId" param="element.repositoryId"/>
                        </dsp:include>
                      </dsp:getvalueof>
                    </c:when>
                    <c:otherwise>
                      <dsp:droplet name="/com/castorama/droplet/CastCategoryLinkDroplet">
                        <dsp:param name="categoryId" param="element.repositoryId"/>
                        <dsp:param name="navCount" bean="/Constants.null"/>
                        <dsp:param name="navAction" value="jump"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="template" param="url"/>
                        </dsp:oparam>
                      </dsp:droplet>
                      <a href="${pageContext.request.contextPath}${template}">
                        <img class="menuBgImg" src="${pageContext.request.contextPath}/images/blank.gif" alt="${displayName}" title="${displayName}" />
                      </a>
                    </c:otherwise>
                  </c:choose>
                </li>
              </c:when>
              <c:otherwise>
                <li class="last_element mainMenuPoint">
                  <a href="${pageContext.request.contextPath}/lancez-vous.jsp">
                        <img class="menuBgImg" src="${pageContext.request.contextPath}/images/blank.gif" alt="${displayName}" title="${displayName}" />
                  </a>
                </li>
              </c:otherwise>
            </c:choose>
          </dsp:oparam>
        </dsp:droplet>
          </ul>
      <div class="clear"><!--~--></div>
    </div>
	<div class="searchPanel">
        <%@include file="../../search/searchForm.jspf"%>
        <div class="block3">
		  <dsp:include page="/castCommon/promoInformationTargeter.jsp">
			<dsp:param name="homePagePromoBean" bean="TopBarPromoSlot"/>
			<%--<dsp:param name="width" value="382"/>
			<dsp:param name="height" value="48"/>--%>
			<dsp:param name="flashId" value="TopBarPromoSlot"/>
		  </dsp:include>
		</div>
	</div>
  </div>

  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="promoInformation" bean="Profile.wrappedCurrentLocalStore.ccTopBannerPromoInfo"/>
  <dsp:getvalueof var="isHome" param="isHome"/>
  <c:if test="${not empty promoInformation 
    && wrappedStoreId != '999'
    && (isHome
        || fn:contains(requestURI, 'searchResults.jsp')
        || fn:contains(requestURI, '/castCatalog/'))
    && !fn:contains(requestURI, '/castCatalog/submitReview.jsp')
    && !fn:contains(requestURI, '/castCatalog/wrapPageContent.jsp')}">
    <dsp:include page="/castCatalog/includes/catalogPromoTemplates/Click & Collect top banner.jsp" flush="true">
      <dsp:param name="promoInformation" value="${promoInformation}"/>
    </dsp:include>
  </c:if>
</dsp:page>
