<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

  <div class="productsRowTable">
    <%-- Following three lines of code are added to support the Omniture functionality.
         "productsList" variable is modified during iteration in productViewTemplate.jsp file. --%>
    <c:set var="productsList" value=""  scope="request"/>
    <dsp:getvalueof param="results" var="results"/>
    <c:set var="size" value="${fn:length(results)}"/>
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="results"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="size" param="size"/>
        <dsp:getvalueof var="index" param="index"/>
        <dsp:getvalueof var="count" param="count"/>
        <c:choose>
          <c:when test="${index == 0 || index mod 8 == 0}">
            <div class="productsRow hasHighlight">
          </c:when>
          <c:otherwise>
            <c:if test="${index mod 4 == 0}">
              <div class="productsRow hasHighlight">
            </c:if>
          </c:otherwise>
        </c:choose>
        <c:choose>
          <c:when test="${(index + 1) mod 4 == 0 }">
            <div class="productItem piLast" style="z-index: 5;">
          </c:when>
          <c:otherwise>
            <div class="productItem" style="z-index: 5;">
          </c:otherwise>
        </c:choose>
        <div class="productMainInfo">
        <dsp:include page="productViewTemplate.jsp">
          <dsp:param name="productId" param="element.document.properties.$repositoryId"/>
          <dsp:param name="navAction" param="navAction"/>
          <dsp:param name="navCount" param="navCount"/>
          <dsp:param name="categoryId" param="categoryId"/>
          <dsp:param name="draggable" value="true"/>
        </dsp:include>

        <dsp:droplet name="ProductLookup">
          <dsp:param name="id" param="element.document.properties.$repositoryId"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="productId" param="element.repositoryId"/>
            <dsp:droplet name="CastPriceRangeDroplet">
              <dsp:param name="productId" value="${productId}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="sku" param="sku"/>
              </dsp:oparam>
            </dsp:droplet>
            <dsp:getvalueof var="childSku" param="element.childSKUs"/>
            <dsp:droplet name="CastProductLinkDroplet">
              <dsp:param name="categoryId" value=""/>
              <dsp:param name="productId" value="${productId}"/>
              <dsp:param name="navAction" param="navAction"/>
              <dsp:param name="navCount" param="navCount"/>
              <dsp:param name="isSearchResult" param="isSearchResult"/>
              <dsp:param name="sortByValue" param="sortByValue"/>
              <dsp:param name="ba" value="${baFakeContext}"/>
              <dsp:param name="hideBreadcrumbs" value="${bonnesAffaires}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="templateUrl" param="url"/>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
        </div>

        <dsp:include page="addToCartSmall.jsp">
          <dsp:param name="sku" value="${sku}"/>
          <dsp:param name="productId" value="${productId}"/>
          <dsp:param name="childSku" value="${childSku}"/>
          <dsp:param name="url" value="${contextPath}${templateUrl}"/>
        </dsp:include>
        </div>
        <c:if test="${(((index + 1) == size) && (size mod 4 != 0)) || ((index + 1) mod 4 == 0)}">
          <div class="clear"></div>
          </div>
        </c:if>
        <%-- Following line of code is added to support the Omniture functionality. --%>
        <c:if test="${size != count}"><c:set var="productsList" value="${productsList}," scope="request"/></c:if>
      </dsp:oparam>
    </dsp:droplet>
  </div>

    <%-- Omniture params Section begins--%>
  <fmt:message var="omnitureChannel" key="omniture.channel.catalogue"/>
  <c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
  <c:if test="${size > 0}">
    <c:set var="omnitureProducts" value="${productsList}" scope="request"/>
  </c:if>
  <fmt:message var="omniturePState" key="omniture.state.product.list"/>
  <c:set var="omniturePState" value="${omniturePState}" scope="request"/>
  <%-- Omniture params Section ends--%>
  
</dsp:page>