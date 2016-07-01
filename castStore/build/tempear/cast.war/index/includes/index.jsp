<dsp:page>
<dsp:getvalueof var="indexedMap" param="indexedMap"/>
<dsp:getvalueof var="indexedItem" param="indexedItem"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
    <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
<div class="paginator">
  <div class="bluePage">
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
       <dsp:param name="array" value="${indexedMap}" />
       <dsp:param name="sortProperties" value="+_key"/>
       <dsp:oparam name="output">
         <dsp:getvalueof var="alpha" param="key"/>
         <dsp:getvalueof var="elements" param="element"/>
         
         <c:choose>
           <c:when test="${not empty elements }">
             <a href="#p${alpha}">${alpha}</a>
           </c:when>
           <c:otherwise>
             <a href="#p${alpha}" class="disabled">${alpha}</a>
           </c:otherwise>
         </c:choose>
       </dsp:oparam>
    </dsp:droplet>            
  </div>
</div>

<dsp:droplet name="/atg/dynamo/droplet/Cache">
  <dsp:param name="key" value="azIndex_${indexedItem}"/>
  <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
  <dsp:oparam name="output">
    <div class="azTable">
      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" value="${indexedMap}" />
        <dsp:param name="sortProperties" value="+_key"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="alpha" param="key"/>
          <dsp:getvalueof var="elements" param="element"/>
          <h2 id="p${alpha}">${alpha} <a href="#"><fmt:message key="index.to.top"/> </a></h2>
          <div class="azList">
    	      <dsp:test var="objectSize" value="${elements}" />
    	      <fmt:formatNumber type="number" maxFractionDigits="0"  value="${objectSize.size / 4 + 1}" var="lenght"/>
    	      <c:if test="${objectSize.size < 20 }">
    	        <c:set var="lenght" value="5"/>
    	      </c:if>
                      
    	      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
    	       <dsp:param name="array" value="${elements }" />
    	       <dsp:oparam name="output">
              <dsp:getvalueof var="count" param="count"/>
              <c:if test="${count mod lenght == 1 || lenght == 1}">
                <ul>
              </c:if>
              
              <li>
              
              <c:if test="${indexedItem=='marque' }"> 
    	          <dsp:include page="/castCatalog/includes/brandLink.jsp">
    	            <dsp:param name="isProductListingPage" value="false"/>
    	            <dsp:param name="brand" param="element"/>            
    	            <dsp:param name="showImage" value="${false}"/>
    	          </dsp:include>
              </c:if>
              
              <c:if test="${indexedItem =='category' }">
                <dsp:getvalueof var="category" param="element.displayName"/>
                
                <dsp:droplet name="/com/castorama/droplet/CastCategoryLinkDroplet">
                  <dsp:param name="categoryId" param="element.repositoryId" />
                  <dsp:param name="navCount" bean="CatalogNavHistory.navCount" />
                  <dsp:param name="navAction" value="jump" />
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="url" param="url" />
                    <dsp:a title="" href="${pageContext.request.contextPath}${url}">
                      ${category }
                    </dsp:a>
                   </dsp:oparam>
                 </dsp:droplet>
              </c:if>
               
              </li>
                              
              <c:if test="${count mod lenght == 0 || lenght == 1 || count == objectSize.size}">                         
                </ul>
              </c:if>
             </dsp:oparam>
            </dsp:droplet>
                     
           </div>
          </dsp:oparam>
         </dsp:droplet>
       </div>
     </div>
  </dsp:oparam>
</dsp:droplet>
</dsp:page>