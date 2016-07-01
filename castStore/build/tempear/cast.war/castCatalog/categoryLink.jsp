<%--
 Expected params:
 category - category
 navAction - navigation action
 className - style class name
 showProductCount - show/hide product count flag 
 --%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>
  <dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet"/>

  <!-- !!!<dsp:getvalueof var="categoryTemplateURL" param="category.template.url"/> -->
  <dsp:getvalueof var="className" param="className"/>
  <dsp:getvalueof var="showProductCount" param="showProductCount"/>
  <dsp:getvalueof var="navAction" param="navAction"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  
  <dsp:droplet name="CastCategoryLinkDroplet">
    <dsp:param name="categoryId" param="category.repositoryId"/>  
    <dsp:param name="navCount" bean="/Constants.null"/>    
    <dsp:param name="navAction" param="navAction"/> 
    <dsp:oparam name="output">
      <dsp:getvalueof var="url" param="url"/>
      <dsp:getvalueof var="categoryTemplateURL" value="${url}"/>
    </dsp:oparam>
  </dsp:droplet>

  <fmt:message key="castCatalog_label.products" var="productsLabel"/>
  <c:choose>
    <c:when test="${null == className}">
      <dsp:a href="${pageContext.request.contextPath}${categoryTemplateURL }&wrap=true">
        <dsp:valueof param="category.displayName" />
        <c:if test="${'true' == showProductCount}">
          <span>(<dsp:valueof param="category.productsCount" /> ${productsLabel})</span>
        </c:if>
      </dsp:a>
    </c:when>
    <c:otherwise>
      <dsp:a iclass="${className}" href="${pageContext.request.contextPath}${categoryTemplateURL }&wrap=true">
        <dsp:valueof param="category.displayName" />
        <c:if test="${'true' == showProductCount}">
          <span>(<dsp:valueof param="category.productsCount" /> ${productsLabel})</span>
        </c:if>
      </dsp:a>
    </c:otherwise>
  </c:choose>

</dsp:page>
