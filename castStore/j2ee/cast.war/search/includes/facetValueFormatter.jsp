<dsp:page>
      
  <dsp:importbean bean="/atg/commerce/search/refinement/RefinementValueDroplet" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="facetedProperty" param="facetedProperty"/>
  <dsp:getvalueof var="value" param="value"/>
  <dsp:getvalueof var="facetId" param="facetId"/>
  <dsp:getvalueof var="facetValue" param="facetValue"/>
 
  <dsp:importbean var="requestLocale" bean="/atg/dynamo/servlet/RequestLocale" />
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
    <c:when test="${not empty facetedProperty && facetedProperty == 'price'}">
      <fmt:message key="search_filters.priceRangesFormatter">
        <fmt:param>
          <fmt:formatNumber type="currency" currencyCode="EUR" value="${facetValue.rangeStart}" maxFractionDigits="0"/>
        </fmt:param>
        <fmt:param>
          <fmt:formatNumber type="currency" currencyCode="EUR" value="${facetValue.rangeEnd}" maxFractionDigits="0"/>
        </fmt:param>
      </fmt:message>
     
    </c:when>
    <c:when test="${not empty facetedProperty && facetedProperty == 'availability' }">
      <fmt:message key="facet.label.availability.${value}"/>
    </c:when>
    <c:when test="${not empty facetedProperty && facetedProperty == 'ccMode' }">
      <fmt:message key="facet.label.ccMode.${value}"/>
    </c:when>
    <c:otherwise>
      <dsp:droplet name="RefinementValueDroplet">
       <dsp:param name="refinementId" value="${facetId }"/>
       <dsp:param name="refinementValue" value="${value }"/>
       <dsp:oparam name="output">
         <dsp:valueof param="displayValue"/>
       </dsp:oparam>
      </dsp:droplet>
    </c:otherwise>
  </c:choose>
 
</dsp:page>