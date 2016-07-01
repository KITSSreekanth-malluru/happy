<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
  
  <dsp:getvalueof var="listColor" param="listColor"/>
  <dsp:getvalueof var="isPrintVersion" param="isPrintVersion"/>

  <div class="tabTechnique tabContent redContent prodDescMarker">
    <dsp:droplet name="SKULookup">
      <dsp:param name="id" param="skuId"/>
      <dsp:oparam name="output">
          <ul class="${listColor}">
            <dsp:valueof param="element.LibelleClientLong" valueishtml="true"/>
          </ul>  
        <dsp:getvalueof var="plusDuProduit" param="element.PlusDuProduit"/>
        <c:if test="${not empty plusDuProduit}">
          <p><strong><fmt:message key="castCatalog_technicTab.plus"/></strong> <dsp:valueof value="${plusDuProduit}" valueishtml="true"/></p> 
        </c:if>    
        <dsp:getvalueof var="contraintesUtilisation" param="element.ContraintesUtilisation"/>
        <c:if test="${not empty contraintesUtilisation}">
          <p><strong><fmt:message key="castCatalog_technicTab.conseils"/></strong> <dsp:valueof value="${contraintesUtilisation}" valueishtml="true"/></p> 
        </c:if>
        <dsp:getvalueof var="normesText" param="element.normesText"/>
        <c:if test="${not empty normesText}">
          <p><strong><fmt:message key="castCatalog_technicTab.normes"/></strong> <dsp:valueof value="${normesText}" valueishtml="true"/></p> 
        </c:if>
        <dsp:getvalueof var="libelleEspaceNouveaute" param="element.LibelleEspaceNouveaute"/>
        <c:if test="${not empty libelleEspaceNouveaute}">
          <p><dsp:valueof value="${libelleEspaceNouveaute}" valueishtml="true"/></p>
        </c:if>
        <dsp:getvalueof var="garantie" param="element.Garantie" />
        <c:if test="${not empty garantie}">
          <p><strong><fmt:message key="castCatalog_technicTab.garantie"/></strong> <dsp:valueof value="${garantie}" valueishtml="true"/></p> 
        </c:if>    
        <dsp:getvalueof var="restrictionsUsage" param="element.RestrictionsUsage"/>
        <c:if test="${not empty restrictionsUsage}">
          <p><strong><fmt:message key="castCatalog_technicTab.restrictions"/></strong> <dsp:valueof value="${restrictionsUsage}" valueishtml="true"/></p>  
        </c:if>
        <dsp:getvalueof var="mentionsLegalesObligatoires" param="element.MentionsLegalesObligatoires"/>
        <c:if test="${not empty mentionsLegalesObligatoires}">
          <p><strong><fmt:message key="castCatalog_technicTab.mentions"/></strong> <dsp:valueof value="${mentionsLegalesObligatoires}" valueishtml="true"/></p> 
        </c:if>
        <dsp:getvalueof var="poidsUV" param="element.PoidsUV"/>
        <c:if test="${not empty poidsUV}">
          <p><strong><fmt:message key="castCatalog_technicTab.poids"/></strong> <dsp:valueof value="${poidsUV / 1000}"/>&nbsp;<fmt:message key="castCatalog_technicTab.kg"/></p>
        </c:if>
        
        <c:if test="${empty isPrintVersion or not isPrintVersion}">
        <dsp:getvalueof var="map" param="element.instructions" vartype="java.util.Map"/>
        <c:if test="${not empty map}">
          <div class="linkedDocsV2">
            <p><fmt:message key="castCatalog_technicTab.documents"/></p>

            <dsp:droplet name="Range">
              <dsp:param name="array" value="${map}"/>
              <dsp:param name="howMany" value="6"/>
              <dsp:param name="start" value="1"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="key" param="element.key"/>
                <dsp:getvalueof var="mediaUrl" param="element.value.url"/>
                <c:choose>
                  <c:when test="${castCollection:endsWith(mediaUrl)}">
                    <dsp:getvalueof var="paramToLoad" value="${contextPath}/castCatalog/includes/video_popup.jsp?url=${mediaUrl}&skuId=${sku.repositoryId}&productId=${product.repositoryId}"/>
                    <a href="javascript:void(0)" onclick="showVideoPopup('product_video', '${paramToLoad}');" class="singleDocV2"><span>${key}</span></a>
                  </c:when>
                  <c:otherwise>
                    <a href="javascript:void(0)" onclick="javascript:window.open('${mediaUrl}', '_blank');" class="singleDocV2"><span>${key}</span></a>
                  </c:otherwise>
                </c:choose>
              </dsp:oparam>
            </dsp:droplet>
          </div>
        </c:if>
        </c:if>

        <table cellspacing="0" cellpadding="0" class="techniqueTable">
          <tbody>
            <dsp:droplet name="Range">
              <dsp:param name="array" param="element.attributes"/>
              <dsp:param name="howMany" value="40"/>
              <dsp:param name="start" value="1"/>
              <dsp:param name="elementName" value="attribute" />
                <dsp:oparam name="output">
                  <dsp:getvalueof var="index" param="index" />
                  <dsp:getvalueof var="attr_unit" param="attribute.attr_Unit" />
                  <dsp:getvalueof var="attr_type" param="attribute.attr_Type" />
                  <c:choose>
                    <c:when test="${ 1 == attr_type }" >
                      <dsp:getvalueof var="attr_value" param="attribute.attr_ValueInt" />
                    </c:when>
                    <c:when test="${ 2 == attr_type }" >
                      <dsp:getvalueof var="attr_value" param="attribute.attr_ValueString" />
                    </c:when>
                    <c:when test="${ 3 == attr_type }" >
                      <dsp:getvalueof var="attr_value" param="attribute.attr_ValueFloat" />
                    </c:when>
                    <c:when test="${ 4 == attr_type }" >
                      <dsp:getvalueof var="attr_value" param="attribute.attr_ValueDate" />
                    </c:when>
                    <c:when test="${ 5 == attr_type }" >
                      <dsp:getvalueof var="attr_value" param="attribute.attr_ValueBoolean" />
                      <c:choose>
                        <c:when test="${ true == attr_value }" >
                          <fmt:message var="attr_value" key="true"/>
                        </c:when>
                        <c:otherwise>
                          <fmt:message var="attr_value" key="false"/>
                        </c:otherwise>
                      </c:choose>
                    </c:when>
                  </c:choose>

                  <c:choose>
                    <c:when test="${ 0 == (index % 2) }">
                      <tr class="odd">
                    </c:when>
                    <c:otherwise>
                      <tr>
                    </c:otherwise>
                  </c:choose>
                  <th>
                    <span><dsp:valueof param="attribute.attr_displayName" /></span>
                  </th>
                  <td>
                    <strong>
                      ${attr_value}
                      <c:if test="${ null != attr_unit }">
                        &nbsp;${attr_unit}
                      </c:if>
                    </strong>
                  </td>
                </tr>
              </dsp:oparam>
            </dsp:droplet>
          </tbody>
        </table>
      </dsp:oparam>
    </dsp:droplet>
  </div>
</dsp:page>
