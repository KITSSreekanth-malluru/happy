<dsp:page>

  <dsp:importbean bean="/atg/commerce/search/refinement/CommerceFacetTrailDroplet"/>
  <dsp:importbean bean="/com/castorama/search/droplet/CastFacetedPropertyDroplet"/>
  
  <dsp:getvalueof var="selectedFacet" param="selectedFacet"/>
  <dsp:getvalueof var="trailString" param="trailString"/>
  
  <dsp:getvalueof var="filterColour" param="filterColour"/>
  <dsp:getvalueof var="searchType" param="searchType"/>
  <dsp:getvalueof var="osearchmode" param="osearchmode"/>
  <c:if test="${empty osearchmode and not empty searchType}">
  	<dsp:getvalueof var="osearchmode" value="reg"/>
  </c:if>
  
  <dsp:droplet name="CastFacetedPropertyDroplet">
    <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="facetedPropertyVarOuter" param="facetedProperty"/>
      <c:choose>
        <c:when test="${selectedFacet.class.name  == 'atg.repository.search.refinement.FacetDisjunctionMultiValue' || selectedFacet.class.name == 'atg.repository.search.refinement.RangeFacetDisjunctionMultiValue' }">
        <dsp:getvalueof var="selectedListVar" value="${selectedFacet.values}"/>
          <c:forEach items="${selectedFacet.values}" var="currentMultiFacetValue" varStatus="statusMultiFacet">
            <div class="${filterColour}">
              <dsp:droplet name="CommerceFacetTrailDroplet">
                <dsp:setvalue param="trail" value="${trailString}" />
                <dsp:setvalue param="removeFacet" value="${selectedFacet.facet.id}:${currentMultiFacetValue}" />
                <dsp:oparam name="output">
                  <dsp:getvalueof var="facetTrail" param="facetTrail" />
                                  
                  <dsp:getvalueof var="trailVar" param="facetTrail.trailString" />
                  <dsp:getvalueof var="categoryIdVar" param="categoryId"/>
                  <dsp:getvalueof var="sortByValue" param="sortByValue"/>
                  <dsp:getvalueof var="productListingView" param="productListingView"/>
                  <dsp:getvalueof var="question" param="question"/>
                  <dsp:getvalueof var="searchType" param="searchType"/>
                  <dsp:getvalueof var="currentTab" param="currentTab"/>
                  
                 <dsp:droplet name="CastFacetedPropertyDroplet">
                    <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="facetedPropertyVar" param="facetedProperty"/>
                      <c:choose>
                        <c:when test="${facetedPropertyVar == 'rating'}">
                          <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on"  checked
                                           onClick="location.href='${containerVar }?trail=${castCollection:encode(trailVar)}&categoryId=${categoryIdVar}&sortByValue=${sortByValue }&productListingView=${productListingView }&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab }&osearchmode=${osearchmode}'"/>
                                           
                          
                            <dsp:include page="facetValueFormatter.jsp">
                              <dsp:param name="facetedProperty" param="facetedProperty"/>
                              <dsp:param name="facetValue" value="${selectedFacet}"/>
                              <dsp:param name="value" value="${currentMultiFacetValue}"/>
                              <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                            </dsp:include>
                        </c:when>
                        <c:otherwise>
                          <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on"  checked
                                           onClick="location.href='${containerVar }?trail=${castCollection:encode(trailVar)}&categoryId=${categoryIdVar}&sortByValue=${sortByValue }&productListingView=${productListingView }&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab }&osearchmode=${osearchmode}'"/>
                            <dsp:include page="facetValueFormatter.jsp">
                              <dsp:param name="facetedProperty" param="facetedProperty"/>
                              <dsp:param name="facetValue" value="${selectedFacet}"/>
                              <dsp:param name="value" value="${currentMultiFacetValue}"/>
                              <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                            </dsp:include>
                        </c:otherwise>
                      </c:choose>
                      
                    </dsp:oparam>
                  </dsp:droplet>
                
                </dsp:oparam>
              </dsp:droplet>
            </div>
          </c:forEach>
        </c:when>
        <c:otherwise>
        <div class="${filterColour}">
          <dsp:droplet name="CommerceFacetTrailDroplet">
            <dsp:setvalue param="trail" value="${trailString}" />
            <dsp:setvalue param="removeFacet" value="${selectedFacet}" />
            <dsp:oparam name="output">
              <dsp:getvalueof var="facetTrail" param="facetTrail" />
                              
              <dsp:getvalueof var="trailVar" param="facetTrail.trailString" />
              <dsp:getvalueof var="categoryIdVar" param="categoryId"/>
              <dsp:getvalueof var="sortByValue" param="sortByValue"/>
              <dsp:getvalueof var="productListingView" param="productListingView"/>
              <dsp:getvalueof var="question" param="question"/>
              <dsp:getvalueof var="searchType" param="searchType"/>
              <dsp:getvalueof var="currentTab" param="currentTab"/>
              
               <dsp:droplet name="CastFacetedPropertyDroplet">
                  <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="facetedPropertyVar" param="facetedProperty"/>
                  
                  <c:choose>
                    <c:when test="${facetedPropertyVar == 'rating'}">
                      <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on"  checked
                                       onClick="location.href='${containerVar }?trail=${castCollection:encode(trailVar)}&categoryId=${categoryIdVar}&sortByValue=${sortByValue }&productListingView=${productListingView }&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab }&osearchmode=${osearchmode}'"/>
                                       
                      <dsp:include page="facetValueFormatter.jsp">
                        <dsp:param name="facetedProperty" param="facetedProperty"/>
                        <dsp:param name="facetValue" value="${selectedFacet}"/>
                        <dsp:param name="value" value="${selectedFacet.value}"/>
                        <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                      </dsp:include>
                    </c:when>
                    <c:otherwise>
                      <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on"  checked
                                       onClick="location.href='${containerVar }?trail=${castCollection:encode(trailVar)}&categoryId=${categoryIdVar}&sortByValue=${sortByValue }&productListingView=${productListingView }&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab }&osearchmode=${osearchmode}'"/>
                      <dsp:include page="facetValueFormatter.jsp">
                        <dsp:param name="facetedProperty" param="facetedProperty"/>
                        <dsp:param name="facetValue" value="${selectedFacet}"/>
                        <dsp:param name="value" value="${selectedFacet.value}"/>
                        <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                      </dsp:include>
                    </c:otherwise>
                  </c:choose>
                  
                </dsp:oparam>
              </dsp:droplet>
            
            </dsp:oparam>
          </dsp:droplet>
          </div>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
