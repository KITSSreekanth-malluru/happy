<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/For"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>

  <dsp:getvalueof var="sku" param="sku"/>
  <dsp:getvalueof var="skuLinks" param="sku.bundleLinks"/>
  <dsp:getvalueof var="size" value="${fn:length(skuLinks)}"/>
  <dsp:getvalueof var="bgColor" param="bgColor"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

  <c:choose>
    <c:when test="${size<4}">
      <dsp:getvalueof var="howMany" value="${size}"/>
    </c:when>
    <c:otherwise>
      <dsp:getvalueof var="howMany" value="4"/>
    </c:otherwise>
  </c:choose>

  <td class="firstCell">
    <div class="productItemImage">
      <dsp:droplet name="For">
        <dsp:param name="howMany" value="${howMany}"/>
        <dsp:getvalueof var="count" param="count"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="set" param="sku.bundleLinks[param:index].item.parentProducts.updatedValue"/>
          <dsp:param name="list" value="${castCollection:list(set)}"/>
          <dsp:droplet name="CastProductLinkDroplet">
            <dsp:param name="productId" param="list[0].repositoryId"/>
            <dsp:param name="categoryId" param="list[0].parentCategory.repositoryId"/>
            <dsp:param name="navAction" value="jump"/>
            <dsp:param name="navCount" param="navCount"/>
            <dsp:param name="skuId" param="sku.bundleLinks[param:index].item.repositoryId"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="templateUrl" param="url"/>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:getvalueof var="carouselImage" param="sku.bundleLinks[param:index].item.carouselImage.url"/>
          <dsp:getvalueof var="name" param="sku.bundleLinks[param:index].item.displayName"/>
          <dsp:getvalueof var="quantityImg" param="sku.bundleLinks[param:index].quantity"/>
          <dsp:a href="${contextPath}${templateUrl}">
           
              <div class="productItemImageBanner ${bgColor}">
                <dsp:valueof value="${quantityImg}"/>x
              </div>
           
            <c:choose>
              <c:when test="${not empty carouselImage}">
                <dsp:img src="${carouselImage}" alt="${name}" title="${name}" width="73" height="73"/>
              </c:when>
              <c:otherwise>
                <dsp:img src="/default_images/b_no_img.jpg" alt="${name}" title="${name}" width="73" height="73"/>
              </c:otherwise>
            </c:choose>
            
          </dsp:a>
          <c:if test="${howMany!=count}">
            <span>+</span>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
    </div>
    <div class="productItemDescription">
      <h3>
        <dsp:droplet name="ForEach">
          <dsp:param name="array" param="sku.bundleLinks"/>
          <dsp:getvalueof var="count" param="count"/>
          <dsp:getvalueof var="size" param="size"/>
          <dsp:param name="elementName" value="itemParam"/>
          <dsp:oparam name="output">
            
            <dsp:getvalueof var="quantity" param="itemParam.quantity"/>
            
             <span class="blueLinkPacks">
              <dsp:valueof value="${quantity}"/> x
             </span>
            
            
            <dsp:valueof param="itemParam.item.displayName"/>
            <c:if test="${size!=count}">
            <span class="blueLinkPacks">
              +
            </span>  
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </h3>
    </div>
  </td>
</dsp:page>