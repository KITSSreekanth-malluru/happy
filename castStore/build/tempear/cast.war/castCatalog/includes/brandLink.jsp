<dsp:page>
  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
    <dsp:param name="value" param="product"/>
    <dsp:oparam name="false">
        <dsp:getvalueof var="showBrandLinkPDP" param="product.showBrandLinkPDP"/>
        <dsp:droplet name="/com/castorama/droplet/BrandLookupDroplet">
          <dsp:param name="product" param="product"/>    
          <dsp:oparam name="output">
            <dsp:getvalueof var="brand" param="brand"/>
            <dsp:getvalueof var="brandId" param="brand.repositoryId"/>
            <dsp:getvalueof var="brandName" param="brand.name"/>  
            <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
            <dsp:getvalueof var="className" param="className"/>
            <dsp:getvalueof var="brandMediaURL" param="brand.mediaMarque.url"/>
            <dsp:getvalueof var="showImage" param="showImage" vartype="boolean"/>
            <dsp:getvalueof var="brandURL" param="brand.url"/>
            <dsp:getvalueof var="isProductListingPage" param="isProductListingPage"/>
            <dsp:droplet name="/com/castorama/droplet/SearchLinkDroplet">
              <dsp:param name="contextPath" value="${contextPath}"/>
              <dsp:param name="question" value="${brandName}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="url" param="url"/>
                <dsp:getvalueof var="searchLink" value="${url}?isBrand=true&osearchmode=tagcloud"/>
              </dsp:oparam>
            </dsp:droplet>
            
          </dsp:oparam>
        </dsp:droplet>
    </dsp:oparam>
    <dsp:oparam name="true">
      <dsp:getvalueof var="brand" param="brand"/>
      <dsp:getvalueof var="brandId" param="brand.repositoryId"/>
      <dsp:getvalueof var="brandName" param="brand.name"/>  
      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
      <dsp:getvalueof var="brandMediaURL" param="brand.mediaMarque.url"/>
      <dsp:getvalueof var="showImage" param="showImage" vartype="boolean"/>
      <dsp:getvalueof var="brandURL" param="brand.url"/>
      <dsp:getvalueof var="isProductListingPage" param="isProductListingPage"/>
      <dsp:droplet name="/com/castorama/droplet/SearchLinkDroplet">
    <dsp:param name="contextPath" value="${contextPath}"/>
    <dsp:param name="question" value="${brandName}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="url" param="url"/>
          <dsp:getvalueof var="searchLink" value="${url}?osearchmode=tagcloud"/>
    </dsp:oparam>
    </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>

  <c:if test="${not empty brand}">  
   <c:choose>
     <c:when test="${not empty isProductListingPage && isProductListingPage}">
       <c:if test="${showImage && not empty brandMediaURL}">
         <c:choose>
           <c:when test="${not empty brandURL }">
             <dsp:a href="${contextPath}/brand/brandTemplate.jsp?brandId=${brandId}&osearchmode=tagcloud" title="${brandName}">
               <dsp:img title="${brandName}" alt="${brandName}" src="${brandMediaURL}"/>
             </dsp:a>
           </c:when>
           <c:otherwise>
             <dsp:a href="${searchLink}" title="${brandName}">
               <dsp:img title="${brandName}" alt="${brandName}" src="${brandMediaURL}"/>
             </dsp:a>
           </c:otherwise>
         </c:choose>
       </c:if>
     </c:when>
     <c:otherwise>
       <c:if test="${showImage && not empty brandMediaURL}">
         <c:choose>
           <c:when test="${not empty brandURL }">
             <dsp:a href="${contextPath}/brand/brandTemplate.jsp?brandId=${brandId}&osearchmode=tagcloud" title="${brandName}">
               <dsp:img title="${brandName}" alt="${brandName}" src="${brandMediaURL}"/>
             </dsp:a>
           </c:when>
           <c:otherwise>
             <dsp:a href="${searchLink}" title="${brandName}">
               <dsp:img title="${brandName}" alt="${brandName}" src="${brandMediaURL}"/>
             </dsp:a>
           </c:otherwise>
         </c:choose>
       </c:if>
       <c:if test="${!showImage}">
         <c:choose>
           <c:when test="${not empty brandURL }">
             <dsp:a href="${contextPath}/brand/brandTemplate.jsp?brandId=${brandId}&osearchmode=tagcloud" title="${brandName}">
               <c:out value="${brandName }"/>
             </dsp:a>
           </c:when>
           <c:otherwise>
             <dsp:a href="${searchLink}" title="${brandName}">
               <c:out value="${brandName }"/>
             </dsp:a>
           </c:otherwise>
         </c:choose>
       </c:if>
       <c:if test="${not empty showBrandLinkPDP && showBrandLinkPDP}">
         <span class="lightBg">
           <dsp:a href="${searchLink}" iclass="arrowedLink ${className}">
             <fmt:message key="castCatalog_productDetails.viewMoreBrandProducts"/>&nbsp;<dsp:valueof value="${brandName}"/>
           </dsp:a>
         </span>
       </c:if>
     </c:otherwise>
   </c:choose>
  </c:if>
</dsp:page>