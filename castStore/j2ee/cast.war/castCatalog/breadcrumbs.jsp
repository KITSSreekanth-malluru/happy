<%--
 Expected params:
 item | (productId | categoryId) 
 itemName - 
 navAction - navigation action
 navCount - navigation count
 className - style class name
 --%>
 <dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet" />
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
 
  <dsp:getvalueof var="navAction" param="navAction" />
  <dsp:getvalueof var="navCount" param="navCount" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="fromLV" param="fromLV"/>
  
  <c:if test="${navAction eq null || navAction eq 'null' || empty navAction}">
    <c:set var="navAction" value="push" />
  </c:if>
  <c:if test="${navCount eq null || navCount eq 'null' || empty navCount}">
    <dsp:getvalueof var="navCount" bean="CatalogNavHistory.navCount" />
    <dsp:getvalueof var="navCount" value="1" />
  </c:if>	
  
  <%--
  <dsp:include page="breadcrumbsCollector.jsp" >
    <dsp:param name="item" param="element"/>
    <dsp:param name="itemName" param="itemName"/>
    <dsp:param name="productId" param="productId"/>
    <dsp:param name="categoryId" param="categoryId"/>
    <dsp:param name="navAction" value="${navAction}"/>
    <dsp:param name="navCount" value="${navCount}"/>
  </dsp:include> 
  --%>
  <dsp:getvalueof var="categoryId" param="categoryId" />
  <dsp:droplet name="StyleLookupDroplet">
    <dsp:param name="categoryId" value="${categoryId}" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="categoryStyle" param="style.breadcrumbsStyle"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:getvalueof var="list" value="${castCollection:column()}"/>
  <dsp:droplet name="ForEach">
    <dsp:param name="array" bean="Profile.catalog.topNavigationCategories"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="catId" param="element.repositoryId"/>
      ${castCollection:add(list, catId)}
    </dsp:oparam>
  </dsp:droplet>

  <dsp:getvalueof var="navArray" bean="CatalogNavHistory.navHistory"/>
  <c:if test="${not empty navArray}">
    <dsp:param name="navArray" value="${navArray}"/>
    <dsp:getvalueof var="topCategoryId" param="navArray[0].repositoryId"/>
    <c:choose>
      <c:when test="${castCollection:contains(list, topCategoryId)}">
        <dsp:getvalueof var="navigationId" value="${topCategoryId}"/>
      </c:when>
      <c:when test="${topCategoryId == 'none'}">
        <dsp:getvalueof var="navigationId" value="${null}"/>
      </c:when>
      <c:otherwise>
        <dsp:getvalueof var="navigationId" param="navArray[0].parentCategory.repositoryId"/>
      </c:otherwise>
    </c:choose>
   
    <dsp:droplet name="ForEach">
      <dsp:param name="array" bean="Profile.catalog.topNavigationCategories"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="universeId" param="element.repositoryId"/>
        <c:if test="${universeId eq navigationId}">
          <dsp:getvalueof var="number" param="count"/>
        </c:if>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
    
  <c:if test="${not empty number}">
    <script>
      $('.menu_point_' + ${number} + ' A').addClass("over");
    </script>
  </c:if>
  
  <dsp:getvalueof var="hideBreadcrumbs" param="hideBreadcrumbs"/>
  <div class="breadcrumbs ${categoryStyle}" <c:if test="${bonnesAffaires || hideBreadcrumbs}">style="display:none"</c:if>>
    <div class="homeBreadIco">
      <a href="${contextPath}/">
        <img title="Accueil" alt="Accueil" src="${contextPath}/images/icoHomeGray.gif"/>
      </a>
    </div>
    
    <c:if test="${not empty fromLV && fromLV == 'true'}">
      <div class="splitter">&gt;</div>
      <div class="active"><fmt:message key="troc.breadcrumb.home" /></div>
      
      <script>
        $('.last_element A').addClass("over");
      </script>
    </c:if>


    <dsp:droplet name="ForEach">
      <dsp:param name="array" bean="CatalogNavHistory.navHistory" />
      <dsp:param name="elementName" value="historyElement" />
      <dsp:oparam name="output">
        <dsp:getvalueof id="size" param="size"/>
        <dsp:getvalueof id="count" param="count"/>
          
          <dsp:getvalueof var="repId" param="historyElement.repositoryId"/>
          <c:if test="${repId == 'none'}">
            <div class="splitter">&gt;</div>
            <c:choose>
              <c:when test="${originatingRequest.servletPath == '/lancez-vous.jsp' and (empty fromLV or fromLV == 'false')}">
                <div class="active"><fmt:message key="troc.breadcrumb.home" /></div>
              </c:when>
              <c:otherwise>
                  <div><a href="${pageContext.request.contextPath}/lancez-vous.jsp"><fmt:message key="troc.breadcrumb.home" /></a></div>
                <dsp:getvalueof var="topic" param="topic.title"/>
                
                <script>
			      $('.last_element A').addClass("over");
			    </script>
              </c:otherwise>
            </c:choose>
          </c:if>
          <dsp:droplet name="Switch">
            <dsp:param name="value" value="${count==size}"/>
            <dsp:oparam name="false">
              <div class="splitter">&gt;</div>
              <div>
                <c:choose>
                  <c:when test="${repId == 'none'}">
                    <a href="<dsp:valueof param="historyElement.url" />">
                      <dsp:valueof param="historyElement.name" />
                    </a>
                  </c:when>
                  <c:otherwise>
                    <dsp:getvalueof var="catDispName" param="historyElement.displayName" scope="request"/>
                    <dsp:getvalueof var="productOmniturePageName" value="${productOmniturePageName}${catDispName}:" scope="request"/>
                    <dsp:include page="categoryLink.jsp" >
                      <dsp:param name="category" param="historyElement" />
                      <dsp:param name="navAction" value="pop" />
                    </dsp:include>
                  </c:otherwise>
                </c:choose>
              </div>
            </dsp:oparam>
            <dsp:oparam name="true">
              <div class="splitter">&gt;</div>
              <h1 class="active">
                <c:choose>
                  <c:when test="${repId == 'none'}">
                    <dsp:valueof param="historyElement.name"/>
                  </c:when>
                  <c:otherwise>
                    <dsp:droplet name="Switch">
                      <dsp:param name="value" param="historyElement.itemDescriptor.itemDescriptorName"/>
                      <dsp:oparam name="castoramaDocument">
                        <dsp:valueof param="historyElement.title"/>
                      </dsp:oparam>
                      <dsp:oparam name="default"> 
                        <c:choose>
                          <c:when test="${repId == 'none'}">
                            <dsp:valueof param="historyElement.name" />
                          </c:when>
                          <c:otherwise>
                            <dsp:valueof param="historyElement.displayName" />
                          </c:otherwise>
                        </c:choose>
                      </dsp:oparam>
                    </dsp:droplet>
                  </c:otherwise>
                </c:choose>
              </h1>
            </dsp:oparam>
          </dsp:droplet>
      </dsp:oparam>
      <dsp:oparam name="empty"/>
    </dsp:droplet>
  </div>

</dsp:page>