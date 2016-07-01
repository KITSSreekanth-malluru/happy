<dsp:page>
  <dsp:getvalueof var="facetedProperty" param="facetedProperty"/>
  <dsp:getvalueof var="refinementValue" param="value"/>
  <dsp:getvalueof var="facetId" param="facetId"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <c:choose>
    <c:when test="${facetedProperty == 'documentType' && not empty refinementValue}">
      <fmt:message key="facet.label.documentType.${refinementValue}"/>
    </c:when>
    <c:when test="${facetedProperty == 'documentSubType' && not empty refinementValue}">
      ${fn:replace(refinementValue, '_', ' ')}
    </c:when>
    <c:when test="${facetedProperty == 'rating'}">
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param name="value" value="${refinementValue}"/>
        <dsp:oparam name="0.00-1.01"> 
         <c:set var="starsCount" value="1"/>
        </dsp:oparam>
        <dsp:oparam name="1.01-2.01"> 
         <c:set var="starsCount" value="2"/>
        </dsp:oparam>
        <dsp:oparam name="2.01-3.01"> 
          <c:set var="starsCount" value="3"/>
        </dsp:oparam>
        <dsp:oparam name="3.01-4.01"> 
          <c:set var="starsCount" value="4"/>
        </dsp:oparam>
        <dsp:oparam name="4.01-5.01"> 
          <c:set var="starsCount" value="5"/>
        </dsp:oparam>
      </dsp:droplet>
    
      <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
        <dsp:param name="value" value="${starsCount }"/>
        <dsp:oparam name="false">
          <dsp:droplet name="/atg/dynamo/droplet/For">
            <dsp:param name="howMany" value="${starsCount }"/>
            <dsp:oparam name="output">
              <img src="${contextPath }/images/starYellow.gif"/>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </c:when>
    
    <c:when test="${facetedProperty == 'castDocAncestorCategories.$repositoryId'}">
      <dsp:droplet name="/atg/commerce/catalog/CategoryLookup">  
      <dsp:param name="id" value="${refinementValue}"/>
      <dsp:param name="elementName" value="category"/>
      <dsp:oparam name="output">
      
        <dsp:valueof param="category.displayName"/>
      </dsp:oparam>
      </dsp:droplet>
    </c:when>
    <c:otherwise>
      ${refinementValue }
    </c:otherwise>
   
  </c:choose>
  
</dsp:page>