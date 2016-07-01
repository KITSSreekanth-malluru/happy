<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/KeyValueDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/LinkedSkuTabNamesIteratorDroplet"/>
  <dsp:importbean bean="/com/castorama/CastConfiguration"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="product" param="product"/>
  <dsp:getvalueof var="sku" param="element"/>
  <dsp:getvalueof var="skuParam" param="skuId"/>
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
  <dsp:getvalueof var="skuCodeArticle" param="skuCodeArticle"/>
  <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>
  <dsp:getvalueof var="notCheapestSkuPromoChosen" param="notCheapestSkuPromoChosen"/>

  <div class="productImageColumn">
    <c:choose>
      <c:when test="${isSearchResult}">
        <dsp:getvalueof var="borderColor" param="product.parentCategory.style.borderStyle"/>
      </c:when>
      <c:otherwise>
        <dsp:droplet name="StyleLookupDroplet">
          <dsp:param name="categoryId" value="${categoryId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="borderColor" param="style.borderStyle"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:otherwise>
    </c:choose>
  
   <div class="productImageBorder">
    
    <div class="productImage" >
   <dsp:include page="prodHighlight.jsp">
        <dsp:param name="product" value="${product}"/>
        <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}"/>
        <dsp:param name="categoryId" value="${categoryId}"/>
        <dsp:param name="view" value="galeryView"/>
      </dsp:include>

      <dsp:getvalueof var="name" param="element.displayName"/>
      <dsp:getvalueof var="largeImage" param="element.largeImage.url"/>

      <dsp:droplet name="PriceDroplet">
        <dsp:param name="sku" value="${sku}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="listPrice" param="price.listPrice"/>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:getvalueof var="productType" param="product.type"/>
      <dsp:getvalueof var="isMultySKU" param="product.childSKUs"/>

        <a href="#">
          <c:choose>
            <c:when test="${not empty largeImage}">
              <img class="prdMarker slPrdMarker" src="${largeImage}" alt="${name}" width="270" height="270" />
            </c:when>
            <c:otherwise>
              <img class="prdMarker slPrdMarker" src="/default_images/h_no_img.jpg" alt="${name}" style="visibility:visible;" width="270" height="270" />
            </c:otherwise>
          </c:choose>
          </a>
      <dsp:getvalueof var="auxiliaryMedia" param="element.auxiliaryMedia"/>
    </div>
    </div>
    
    <fmt:message key="castCatalog_label.close" var="fermer"/>
    <fmt:message key="castCatalog_label.print" var="imprimer"/>
    <dsp:getvalueof var="paramToLoad" value=""/>
    <c:if test="${not empty auxiliaryMedia}">
      <fmt:message key="castCatalog_productDetails.view_description" var="autres"/>
      <div class="imageViews">
        <ul>
          <dsp:droplet name="Range">
            <dsp:param name="array" value="${auxiliaryMedia}"/>
            <dsp:param name="howMany" value="6"/>
            <dsp:param name="start" value="1"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="image" param="element.value.url"/>
              <dsp:getvalueof var="index" param="index"/>
              <dsp:getvalueof var="count" param="count"/>
              <c:choose>
                <c:when test="${index==5}">
                  <li class="last imgGalleryItem" imgNumber="${index}">
                </c:when>
                <c:otherwise>
                  <li class="imgGalleryItem" imgNumber="${index}">
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${count == 1}">
                  <dsp:a href="javascript:void(0)" title="${autres}" iclass="lImBorder ${borderColor}">
                    <c:choose>
                      <c:when test="${not empty image}">
                        <img src="${image}" alt="${autres}" title="${autres}" width="43" height="43">
                      </c:when>
                      <c:otherwise>
                        <img src="/default_images/a_no_img.jpg" alt="${autres}" title="${autres}" width="43" height="43">
                      </c:otherwise>
                    </c:choose>
                  </dsp:a>
                </c:when>
                <c:otherwise>
                  <dsp:a href="javascript:void(0)" title="${autres}" iclass="${borderColor}">
                    <c:choose>
                      <c:when test="${not empty image}">
                        <img src="${image}" alt="${autres}" title="${autres}" width="43" height="43"/>
                      </c:when>
                      <c:otherwise>
                        <img src="/default_images/a_no_img.jpg" alt="${autres}" title="${autres}" width="43" height="43"/>
                      </c:otherwise>
                    </c:choose>
                  </dsp:a>
                </c:otherwise>
              </c:choose>
              </li>
            </dsp:oparam>
          </dsp:droplet>
        </ul>
      </div>
    </c:if>
  </div>

</dsp:page>