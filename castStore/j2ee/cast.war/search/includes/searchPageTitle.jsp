<dsp:page>
  <dsp:importbean bean="/atg/search/repository/FacetSearchTools"/>
  <dsp:importbean bean="/atg/commerce/search/refinement/CommerceFacetTrailDroplet"/>
  
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />
  <dsp:importbean bean="/com/castorama/search/droplet/CastFacetedPropertyDroplet"/>
  
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
  <dsp:getvalueof var="isCastCardPage" param="isCastCardPage"/>
  <dsp:getvalueof var="searchPageTitleMessage" param="searchPageTitleMessage"/>
  <dsp:getvalueof var="searchResponse" param="searchResponse"/>
  
  <div class="pageTitle">
  <h2>
  <c:choose>
    <c:when test="${not empty isCastCardPage && isCastCardPage}">
      <fmt:message key="${searchPageTitleMessage}" />
    </c:when>
    <c:when test="${not empty isSearchResult && isSearchResult}">
      <fmt:message key="${searchPageTitleMessage}">
        <fmt:param value="${searchResponse.groupCount}"/>
      </fmt:message>
    </c:when>
    <c:otherwise>
    <dsp:droplet name="CategoryLookup">
     <dsp:param name="id" param="categoryId" />
     <dsp:param name="elementName" value="category" />
     <dsp:oparam name="output">
       <dsp:getvalueof var="refineConfigObj" param="category.refineConfig" vartype="java.lang.Object" scope="request"/>
     </dsp:oparam>
      </dsp:droplet>
  
      <fmt:message key="search_searchPageTitle.products">
        <fmt:param value="${searchResponse.groupCount}"/>
      </fmt:message>
  
       <dsp:droplet name="CommerceFacetTrailDroplet">
        <dsp:setvalue param="trail" beanvalue="FacetSearchTools.facetTrail" />
        <dsp:param name="refineConfig" value=" ${refineConfigObj }"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="facetTrail" param="facetTrail">&nbsp;</dsp:getvalueof>
          <c:set var="srchFacetLabel" value="SRCH" />
          <c:if test="${fn:length(facetTrail.facetValues) > 1 || (fn:length(facetTrail.facetValues) == 1 && !fn:contains(facetTrail.trailString, 'SRCH'))}">
          <span>dans 
            <c:forEach items="${facetTrail.facetValues}" var="currentFacetValue" varStatus="status">
            
               <c:choose>
                <c:when test="${currentFacetValue.class.name  == 'atg.repository.search.refinement.FacetDisjunctionMultiValue' || currentFacetValue.class.name == 'atg.repository.search.refinement.RangeFacetDisjunctionMultiValue' }">
                  <c:forEach items="${currentFacetValue.values}" var="currentMultiFacetValue" varStatus="statusForMultiFacetValues">
                    <dsp:droplet name="CastFacetedPropertyDroplet">
                      <dsp:param name="facetId" value="${currentFacetValue.facet.id}"/>
                      <dsp:oparam name="output">
                        <c:if test="${currentFacetValue.facet.id != srchFacetLabel}">
                          <dsp:include page="facetValueFormatter.jsp">
                            <dsp:param name="facetedProperty" param="facetedProperty"/>
                            <dsp:param name="value" value="${currentMultiFacetValue}"/>
                            <dsp:param name="facetId" value="${currentFacetValue.facet.id}"/>
                          </dsp:include>
                          <c:if test="${fn:length(currentFacetValue.values) >  statusForMultiFacetValues.count}">
                            &nbsp;<fmt:message key="common.plus"/>&nbsp;
                          </c:if>
                        </c:if>
                      </dsp:oparam>
                    </dsp:droplet>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <dsp:droplet name="CastFacetedPropertyDroplet">
                    <dsp:param name="facetId" value="${currentFacetValue.facet.id}"/>
                    <dsp:oparam name="output">
                      <c:if test="${currentFacetValue.facet.id != srchFacetLabel}">
                        <dsp:include page="facetValueFormatter.jsp">
                          <dsp:param name="facetedProperty" param="facetedProperty"/>
                          <dsp:param name="facetValue" value="${currentFacetValue}"/>
                          <dsp:param name="value" value="${currentFacetValue.value}"/>
                          <dsp:param name="facetId" value="${currentFacetValue.facet.id}"/>
                        </dsp:include>
                      </c:if>
                    </dsp:oparam>
                  </dsp:droplet>
                </c:otherwise>
              </c:choose>
              
              <c:if test="${fn:length(facetTrail.facetValues) >  status.count}">
                &nbsp;<fmt:message key="common.plus"/>&nbsp;
              </c:if>
              
            </c:forEach>
            </span>
          </c:if>
          </dsp:oparam>
        <dsp:oparam name="error">
        </dsp:oparam>
      </dsp:droplet>
  
    </c:otherwise>
  </c:choose>
  </h2>
  </div>
</dsp:page>