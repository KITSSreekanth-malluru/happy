<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/com/castorama/droplet/CastLookupDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductCatalog"/>
  <dsp:importbean bean="/com/castorama/search/droplet/CastCategoryDocumentsDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet" />

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="documentId" param="documentId"/>
  <dsp:getvalueof var="noBreadcrumbs" param="noBreadcrumbs"/>
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>

  <c:choose>
    <c:when test="${noBreadcrumbs || isSearchResult}">
      <dsp:droplet name="ProductLookup">
      <dsp:param name="id" param="productId"/>
      <dsp:param name="elementName" value="prodItem"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="categoryStyle" param="prodItem.parentCategory.style.rightMenuStyle"/>
      </dsp:oparam>
    </dsp:droplet>
    </c:when>
    <c:otherwise>
      <dsp:droplet name="StyleLookupDroplet">
        <dsp:param name="categoryId" value="${categoryId}" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="categoryStyle" param="style.rightMenuStyle"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:otherwise>
  </c:choose>

  <c:choose>
    <c:when test="${not empty documentId}">
      <dsp:droplet name="CastLookupDroplet">
        <dsp:param name="id" param="documentId"/>
        <dsp:param name="elementName" value="document"/>
        <dsp:param name="itemDescriptor" value="castoramaDocument"/>
        <dsp:param name="repository" bean="ProductCatalog"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="document" param="document"/>
          <dsp:getvalueof var="documentType" param="document.documentType"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${not empty productId}">
          <dsp:droplet name="ProductLookup">
            <dsp:param name="id" param="productId"/>
            <dsp:param name="elementName" value="productItem"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="product" param="productItem"/>
              <dsp:getvalueof var="category" param="productItem.parentCategory"/>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:droplet name="CategoryLookup">
            <dsp:param name="id" param="categoryId"/>
            <dsp:param name="elementName" value="pivotCategory"/>
            <dsp:oparam name="output">
               <dsp:getvalueof var="pivotCategory" param="pivotCategory"/>
            </dsp:oparam>
          </dsp:droplet>
        </c:when>
        <c:otherwise>
          <dsp:droplet name="CategoryLookup">
            <dsp:param name="id" param="categoryId"/>
            <dsp:param name="elementName" value="categoryItem"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="isPivot" param="categoryItem.pivot"/>
              <c:choose>
                <c:when test="${isPivot}">
                  <dsp:getvalueof var="pivotCategory" param="categoryItem"/>
                </c:when>
                <c:otherwise>
                  <dsp:getvalueof var="category" param="categoryItem"/>
                </c:otherwise>
              </c:choose>
            </dsp:oparam>
          </dsp:droplet>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
  <c:choose>
    <c:when test="${not empty product}">
      <%@ include file="centerDocNavigationAreaUI.jspf" %>
    </c:when>
    <c:otherwise>
      <%@ include file="rightNavigationAreaUI.jspf" %>
    </c:otherwise>
  </c:choose>
  


</dsp:page>