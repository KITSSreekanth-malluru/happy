<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

  <dsp:getvalueof var="itemType" param="itemType"/>
  <dsp:getvalueof var="commerceItem" param="commerceItem"/>
  
  <dsp:getvalueof var="hasProductsWithDiscount" param="hasProductsWithDiscount"/>
  <dsp:getvalueof var="currencyCode" param="currencyCode"/>
  
  <dsp:getvalueof var="sku"         param="sku"/>
  <dsp:getvalueof var="skuId"       param="sku.repositoryId"/>
  <dsp:getvalueof var="refNumber"   param="sku.CodeArticle"/>
  <dsp:getvalueof var="ImageURL"    param="sku.miniatureImage.url"/>
  <dsp:getvalueof var="productName" param="sku.displayName"/>

  <dsp:getvalueof var="childSku"    param="product.childSKUs"/>
  <dsp:getvalueof var="template"    param="product.template.url"/>
  <dsp:getvalueof var="set"         param="sku.parentProducts.updatedValue" />

  <dsp:param name="list" value="${castCollection:list(set)}" />
  <dsp:getvalueof var="templateUrl" param="list[0].template.url" />
 
  <dsp:getvalueof var="productId"  param="product.repositoryId"/>

  <dsp:getvalueof var="skuLinks" param="sku.bundleLinks"/>
  <dsp:getvalueof var="size" value="${fn:length(skuLinks)}"/>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  
  <c:choose>
    <c:when test="${itemType eq 'shopping'}">
      <c:set var="imageClass" value="productItemImage"/>
      <c:set var="descriptionClass" value="productItemDescription"/>
      <c:set var="imageSize" value="43"/>
      <c:set var="imageDefault" value="/default_images/a_no_img.jpg"/>
    </c:when>
    <c:when test="${itemType eq 'preshopping'}">
      <c:set var="imageClass" value="rightProductImage"/>
      <c:set var="descriptionClass" value="rightProductDescr"/>
      <dsp:getvalueof var="ImageURL" param="sku.carouselImage.url"/>
      <c:set var="imageSize" value="75"/>
      <c:set var="imageDefault" value="/default_images/b_no_img.jpg"/>
    </c:when>
    <c:when test="${itemType eq 'delivery'}">
      <c:set var="imageClass" value="rightProductImage cartInfoImg"/>
      <c:set var="descriptionClass" value="rightProductDescr"/>
      <c:set var="imageSize" value="43"/>
      <c:set var="imageDefault" value="/default_images/a_no_img.jpg"/>
    </c:when>
  </c:choose>

  <c:choose>
    <c:when test="${itemType == 'delivery'}">
      <c:if test="${ImageURL == null}">
        <dsp:getvalueof var="ImageURL" param="sku.bundleLinks[0].item.miniatureImage.url"/>
      </c:if>
      <div class="${imageClass}">
        <c:choose>
          <c:when test="${not empty ImageURL}">
            <img src="${ImageURL}" alt="${productName}" title="${productName}" height="${imageSize}" width="${imageSize}" />
          </c:when>
          <c:otherwise>
            <img src="${imageDefault}" alt="${productName}" title="${productName}" height="${imageSize}" width="${imageSize}" />
          </c:otherwise>
        </c:choose>
      </div>
      <div class="${descriptionClass}">
        <h3>
          <c:choose>
            <c:when test="${not empty size && size > 0}">
              <%@ include file="productName.jspf" %>
            </c:when>
            <c:otherwise>
              ${productName}
            </c:otherwise>
          </c:choose>
        </h3>
        <div class="refNum"><fmt:message key="msg.cart.ref" />&nbsp; <c:out value="${refNumber}" /></div>
        <div class="qty">
          <fmt:message key="msg.cart.quantity" />&nbsp; <dsp:valueof param="commerceItem.quantity"/>
        </div>
        <dsp:include page="skuPrice.jsp">
          <dsp:param name="thisProductId" value="${productId}"/>
          <dsp:param name="thisSku" param="sku"/>
          <dsp:param name="commerceItem" param="commerceItem"/>
          <dsp:param name="hasProductsWithDiscount" value="${hasProductsWithDiscount}"/>
          <dsp:param name="showCastCardPrice" param="showCastCardPrice"/>
          <dsp:param name="enabledCastCart" param="enabledCastCart"/>
        </dsp:include>
        <dsp:a page="/checkout/cart.jsp" iclass="buttonModifier" title="Modifier">Modifier</dsp:a>
      </div>
    </c:when>
    <c:otherwise>
      <dsp:getvalueof var="qtyWeb" param="commerceItem.quantity"/>
      <dsp:getvalueof var="qtyLocal" param="commerceItemLocal.quantity"/>
      <c:choose>
        <c:when test="${qtyWeb >= qtyLocal}">
          <dsp:getvalueof var="qty" value="${qtyWeb}"/>
        </c:when>
        <c:when test="${qtyWeb <= qtyLocal}">
          <dsp:getvalueof var="qty" value="${qtyLocal}"/>
          <dsp:getvalueof var="commerceItem" param="commerceItemLocal"/>
        </c:when>
        <c:when test="${not empty qtyWeb && empty qtyLocal}">
          <dsp:getvalueof var="qty" value="${qtyWeb}"/>
        </c:when>
        <c:when test="${empty qtyWeb && not empty qtyLocal}">
          <dsp:getvalueof var="qty" value="${qtyLocal}"/>
          <dsp:getvalueof var="commerceItem" param="commerceItemLocal"/>
        </c:when>
        <c:otherwise>
          <dsp:getvalueof var="qty" value="${qtyWeb}" scope="page"/>
        </c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${not empty size && size > 0}">
          <!-- Show pack's details --> 
          
          <c:if test="${ImageURL == null}">
            <c:choose>
              <c:when test="${itemType eq 'preshopping'}">
              <dsp:getvalueof var="ImageURL" param="sku.bundleLinks[0].item.carouselImage.url"/>
              </c:when>
              <c:otherwise>
              <dsp:getvalueof var="ImageURL" param="sku.bundleLinks[0].item.miniatureImage.url"/>
              </c:otherwise>
            </c:choose>
          </c:if>
          
          <div class="${imageClass}">
            <c:choose>
              <c:when test="${not empty ImageURL}">
                <img src="${ImageURL}" alt="${productName}" title="${productName}" height="${imageSize}" width="${imageSize}" />
              </c:when>
              <c:otherwise>
                <img src="${imageDefault}" alt="${productName}" title="${productName}" height="${imageSize}" width="${imageSize}" />
              </c:otherwise>
            </c:choose>
          </div>
          <div class="${descriptionClass}">
            <h3>
              <%@ include file="productName.jspf" %>
            </h3>
            <div class="refNum"><fmt:message key="msg.cart.ref" />&nbsp; <c:out value="${refNumber}" /></div>
            <c:if test="${itemType != 'shopping'}">
              <div class="qty">
                <fmt:message key="msg.cart.quantity" />&nbsp; <dsp:valueof value="${qty}"/>
              </div>
              <dsp:include page="skuPrice.jsp">
                <dsp:param name="thisProductId" value="${productId}"/>
                <dsp:param name="thisSku" param="sku"/>
                <dsp:param name="commerceItem" value="${commerceItem}"/>
                <dsp:param name="hasProductsWithDiscount" value="${hasProductsWithDiscount}"/>
                <dsp:param name="showCastCardPrice" param="showCastCardPrice"/>
                <dsp:param name="enabledCastCart" param="enabledCastCart"/>
              </dsp:include>  
            </c:if>
          </div>
        </c:when>
        <c:otherwise>
          <!-- Show single product's details -->
          <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
            <dsp:param name="productId" value="${productId}"/>
            <dsp:param name="navAction" value="jump"/>
            <dsp:param name="skuId" value="${skuId}"/>
            <dsp:oparam name="output">
               <dsp:getvalueof var="productLink" param="url"/>
            </dsp:oparam>
          </dsp:droplet>

          <div class="${imageClass}">
            <c:choose>
              <c:when test="${template != null}">
                <dsp:a href="${contextPath}${productLink}">
                  <c:choose>
                    <c:when test="${not empty ImageURL}">
                      <img src="${ImageURL}" alt="${productName}" title="${productName}" />
                    </c:when>
                    <c:otherwise>
                      <img src="${imageDefault}" alt="${productName}" title="${productName}" />
                    </c:otherwise>
                  </c:choose>
                </dsp:a>
              </c:when>
              <c:otherwise>
                <c:choose>
                  <c:when test="${not empty ImageURL}">
                    <img src="${ImageURL}" alt="${productName}" title="${productName}" />
                  </c:when>
                  <c:otherwise>
                    <img src="${imageDefault}" alt="${productName}" title="${productName}" />
                  </c:otherwise>
                </c:choose>
              </c:otherwise>
            </c:choose>
          </div>
          <div class="${descriptionClass}">
            <c:choose>
              <c:when test="${template != null}">
                <h3>
                  <dsp:a href="${contextPath}${productLink}">${productName}</dsp:a>
                </h3>
              </c:when>
              <c:otherwise>
                <h3>${productName}</h3>
              </c:otherwise>
            </c:choose>
            <div class="refNum"><fmt:message key="msg.cart.ref" />&nbsp; <c:out value="${refNumber}" /></div>
            <c:if test="${itemType != 'shopping'}">
              <div class="qty">
                <fmt:message key="msg.cart.quantity" />&nbsp; <dsp:valueof value="${qty}"/>
              </div>
              <dsp:include page="skuPrice.jsp">
                <dsp:param name="thisProductId" value="${productId}"/>
                <dsp:param name="thisSku" param="sku"/>
                <dsp:param name="commerceItem" value="${commerceItem}"/>
                <dsp:param name="hasProductsWithDiscount" value="${hasProductsWithDiscount}"/>
                <dsp:param name="showCastCardPrice" param="showCastCardPrice"/>
                <dsp:param name="enabledCastCart" param="enabledCastCart"/>
              </dsp:include>
            </c:if>
          </div>
        </c:otherwise>
      </c:choose>  
    </c:otherwise>
  </c:choose>
</dsp:page>
