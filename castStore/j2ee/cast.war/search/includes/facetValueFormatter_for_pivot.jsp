<dsp:page>
      
  <dsp:importbean bean="/atg/commerce/search/refinement/RefinementValueDroplet" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="facetedProperty" param="facetedProperty"/>
  <dsp:getvalueof var="value" param="value"/>
  <dsp:getvalueof var="facetId" param="facetId"/>
  <dsp:getvalueof var="trail" param="trail"/>
  <dsp:getvalueof var="trailVar" param="trailVar"/>
  <dsp:getvalueof var="facetValue" param="facetValue"/>
  <dsp:getvalueof var="labelType" param="labelType"/>
  <dsp:getvalueof var="categoryUrl" param="categoryUrl"/>
  <dsp:getvalueof var="qString" param="qString"/>
  <dsp:getvalueof var="checked" param="checked"/>
  <dsp:getvalueof var="includeInput" param="includeInput"/>
 
 
  <dsp:importbean var="requestLocale" bean="/atg/dynamo/servlet/RequestLocale" />
  
  
  <dsp:getvalueof var="bFlag" value="${not empty includeInput && includeInput == 'true'}"/>
  <c:choose>
    <c:when test="${not empty trailVar}">
      <dsp:getvalueof var="linkUrl" value="${contextPath}/${fn:replace(castCollection:encode(trailVar), '%3A', '/')}${categoryUrl}?${qString}"/>
    </c:when>
    <c:otherwise>
      <dsp:getvalueof var="linkUrl" value="${contextPath}${categoryUrl}?${qString}"/>
    </c:otherwise>
  </c:choose>
  <c:if test="${bFlag}">
    <input id="${facetValue}" type="checkbox" defaultvalue="on" ${checked} 
           onClick="location.href='${linkUrl}'"/>
  </c:if>
  
  
  <c:choose>
    <c:when test="${not empty facetedProperty && facetedProperty == 'rating'}">
      <c:set var="starsCount" value="0"/>
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param name="value" value="${value}"/>
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
      <c:choose>
        <c:when test="${not empty labelType && labelType == 'label'}">
          <label for="${facetValue}">${displayValue}</label>
        </c:when>
        <c:when test="${not empty labelType && labelType == 'removeLink'}">
          <dsp:a href="${linkUrl}" iclass="stars">
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
          &nbsp;<span></span></dsp:a>
        </c:when>
        <c:otherwise>
          <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
            <dsp:param name="value" value="${starsCount }"/>
            <dsp:oparam name="false"> 
              <label for="${facetValue}">
                <dsp:droplet name="/atg/dynamo/droplet/For">
                  <dsp:param name="howMany" value="${starsCount }"/>
                  <dsp:oparam name="output"> 
                    <img src="${contextPath }/images/starYellow.gif"/>
                  </dsp:oparam>
                </dsp:droplet>
              <label for="${facetValue}">
            </dsp:oparam>
          </dsp:droplet>
        </c:otherwise>
      </c:choose>
      
     
      
    </c:when>
    <c:when test="${not empty facetedProperty && facetedProperty == 'price'}">
      <dsp:getvalueof var="priceFacetValue" value="${value }"/>
      <c:set var="priceRanges" value="${fn:split(priceFacetValue, '-')}" />
      <c:choose>
        <c:when test="${not empty labelType && labelType == 'removeLink'}">
          <a href="${linkUrl}" >
            <fmt:message key="search_filters.priceRangesFormatter">
              <fmt:param>
                <fmt:formatNumber type="currency" currencyCode="EUR" value="${priceRanges[0] }" maxFractionDigits="0"/>
              </fmt:param>
              <fmt:param>
                <fmt:formatNumber type="currency" currencyCode="EUR" value="${priceRanges[1] }" maxFractionDigits="0"/>
              </fmt:param>
             </fmt:message>
           &nbsp;<span></span></a>
        </c:when>
        <c:otherwise>
          <label for="${facetValue}">
            <fmt:message key="search_filters.priceRangesFormatter">
              <fmt:param>
                <fmt:formatNumber type="currency" currencyCode="EUR" value="${priceRanges[0] }" maxFractionDigits="0"/>
              </fmt:param>
              <fmt:param>
                <fmt:formatNumber type="currency" currencyCode="EUR" value="${priceRanges[1] }" maxFractionDigits="0"/>
              </fmt:param>
            </fmt:message>
          </label>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:when test="${not empty facetedProperty && facetedProperty == 'availability' }">
      <c:choose>
        <c:when test="${not empty labelType && labelType == 'removeLink'}">
          <a href="${linkUrl}" >
            <fmt:message key="facet.label.availability.${value}"/>&nbsp;<span></span>
          </a>
        </c:when>
        <c:otherwise>
          <label for="${facetValue}">
            <fmt:message key="facet.label.availability.${value}"/>
          </label>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:when test="${not empty facetedProperty && facetedProperty == 'ccMode' }">
      <c:choose>
        <c:when test="${not empty labelType && labelType == 'removeLink'}">
          <a href="${linkUrl}" >
            <fmt:message key="facet.label.ccMode.${value}"/>&nbsp;<span></span>
          </a>
        </c:when>
        <c:otherwise>
          <label for="${facetValue}">
            <fmt:message key="facet.label.ccMode.${value}"/>
          </label>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      
      <dsp:droplet name="RefinementValueDroplet">
        <dsp:param name="refinementId" value="${facetId }"/>
        <dsp:param name="refinementValue" value="${value }"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="displayValue" param="displayValue"/>
          
          <c:choose>
            <c:when test="${not empty labelType && labelType == 'label'}">
              <label for="${facetValue}">${displayValue}</label>
            </c:when>
            <c:when test="${not empty labelType && labelType == 'removeLink'}">
              <a href="${linkUrl}" >${displayValue}&nbsp;<span></span></a>
            </c:when>
            <c:otherwise>
              <a href="${linkUrl}" class="darkLink">${displayValue}</a>
            </c:otherwise>
          </c:choose>
        </dsp:oparam>
      </dsp:droplet>
    </c:otherwise>
  </c:choose>
 
</dsp:page>