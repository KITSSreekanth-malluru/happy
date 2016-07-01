<dsp:page>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="itemDescriptorVar" param="itemDescriptor" />
  <dsp:getvalueof var="repItemIdVar" param="repItemId" />
  
  <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
    <dsp:param name="id" param="repItemId"/>
    <dsp:param name="elementName" value="repItem"/>
    <dsp:param name="itemDescriptor" param="itemDescriptor"/>
    <dsp:param name="repository" param="repository"/>
    <dsp:oparam name="output"> 
    
    <c:choose>
      <c:when test="${itemDescriptorVar == 'magasin' }">
        <c:set var="url" value="/magasin/magasin-fiche.jsp?magasinId=${repItemIdVar}&parent=search"/>
        <dsp:setvalue param="imageURL" paramvalue="repItem.url_photo"/>
        <dsp:setvalue param="title" paramvalue="repItem.nom"/>
        <dsp:getvalueof var="storeUrl" param="repItem.storeUrl" />
      </c:when>
      <c:when test="${itemDescriptorVar == 'castoramaDocument' }">
        <dsp:droplet name="/com/castorama/droplet/CastDocumentLinkDroplet">
          <dsp:param name="documentId" param="repItem.repositoryId"/>
          <dsp:param name="navAction" value="jump"/>
          <dsp:param name="isSearchResult" param="isSearchResult"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="url" param="url"/>
          </dsp:oparam>
        </dsp:droplet>
        <c:if test="${fn:startsWith(url, 'http') == false}">
            <dsp:getvalueof var="url" value="${contextPath }${url}"/>
        </c:if>
        <dsp:setvalue param="imageURL" paramvalue="repItem.image.url"/>
        <dsp:setvalue param="title" paramvalue="repItem.title"/>
        <dsp:getvalueof var="additionalClass" value="doc"/>
      </c:when>
    </c:choose>
    
      <td>
        
        <c:choose>
          <c:when test="${not empty storeUrl && storeUrl != ''}">
            <div class="productItemImage ${additionalClass}">
              <a href="${storeUrl}" target="_blank" >
                <img title="<dsp:valueof param="title"/>" alt="<dsp:valueof param="title"/>" src="<dsp:valueof param="imageURL"/>"/>
              </a>
            </div>
          </c:when>
          <c:otherwise>
            <div class="productItemImage ${additionalClass}">
              <a href="${url}">
                <img title="<dsp:valueof param="title"/>" alt="<dsp:valueof param="title"/>" src="<dsp:valueof param="imageURL"/>"/>
              </a>
            </div>
          </c:otherwise>
        </c:choose>
        
      </td>
      <td>
        <div class="productItemDescription">
         <c:choose>
           <c:when test="${itemDescriptorVar == 'magasin' }">
            
            <c:choose>
            <c:when test="${not empty storeUrl && storeUrl != ''}">
              <a href="${storeUrl}" target="_blank" style="color:#000000;text-decoration:none;">
                <h3>
                  <dsp:valueof param="title"/>
                </h3>
                <dsp:valueof param="repItem.description" valueishtml="true"/>
              </a>
            </c:when>
            <c:otherwise>
            <a href="${url}" style="color:#000000;text-decoration:none;">
              <h3>
                <dsp:valueof param="title"/>
              </h3>
              <dsp:valueof param="repItem.description" valueishtml="true"/>
            </a>
            </c:otherwise>
          </c:choose>
            
          </c:when>
          <c:when test="${itemDescriptorVar == 'castoramaDocument' }">
            <h3>
              <dsp:valueof param="title"/>
            </h3>
            <a href="${url}" style="color:#000000;text-decoration:none;">
              <dsp:getvalueof var="descriptionVar" param="repItem.description"/>
              <dsp:droplet name="/com/castorama/droplet/DocumentDescriptionBraker">
                <dsp:param name="description" param="repItem.description"/>
                <dsp:param name="length" value="518"/>
                <dsp:oparam name="output">
                   <dsp:getvalueof var="initialDescription" param="repItem.description"/>
                   <dsp:valueof param="updatedDescription" valueishtml="true"/><c:if test="${fn:length(initialDescription) >  518}">...</c:if>
                </dsp:oparam>
              </dsp:droplet>
            </a>
          </c:when>
        </c:choose>
        </div>
      </td>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
