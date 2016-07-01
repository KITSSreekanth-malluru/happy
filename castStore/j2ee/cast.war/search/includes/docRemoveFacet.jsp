<dsp:page>
  <dsp:importbean bean="/com/castorama/commerce/search/refinement/CastFacetTrailDroplet"/>
  <dsp:importbean bean="/com/castorama/search/droplet/CastDocFacetedPropertyDroplet"/>
  
  <dsp:getvalueof var="selectedFacet" param="selectedFacet"/>
  <dsp:getvalueof var="trailString" param="trailString"/>
  <dsp:getvalueof var="filterColour" param="filterColour"/>
  <dsp:getvalueof var="searchType" param="searchType"/>
  <dsp:getvalueof var="osearchmode" param="osearchmode"/>
  <c:if test="${empty osearchmode and not empty searchType}">
  	<dsp:getvalueof var="osearchmode" value="reg"/>
  </c:if>

  <c:choose>
    <c:when test="${not empty selectedFacet && (selectedFacet.class.name  == 'atg.repository.search.refinement.FacetDisjunctionMultiValue' || selectedFacet.class.name == 'atg.repository.search.refinement.RangeFacetDisjunctionMultiValue') }">
     <c:forEach items="${selectedFacet.values}" var="currentMultiFacetValue" varStatus="statusMultiFacet">
      <dsp:droplet name="CastDocFacetedPropertyDroplet">
        <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
        <dsp:oparam name="output">
        <div class="${filterColour}">
          <dsp:droplet name="CastFacetTrailDroplet">
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
        
               <dsp:droplet name="CastDocFacetedPropertyDroplet">
                <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="facetedPropertyVar" param="facetedProperty"/>
                  <c:choose>
                    <c:when test="${facetedPropertyVar == 'rating'}">
                      <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on"  checked
                                       onClick="location.href='${containerVar }?docTrail=${castCollection:encode(trailVar)}&categoryId=${categoryIdVar}&sortByValue=${sortByValue }&productListingView=${productListingView }&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab }&osearchmode=${osearchmode}'"/>
                      
                      <dsp:include page="docFacetValueFormatter.jsp">
                        <dsp:param name="facetedProperty" param="facetedProperty"/>
                        <dsp:param name="value" value="${currentMultiFacetValue}"/>
                        <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>                    
                      </dsp:include>
                    </c:when>
                    <c:otherwise>
                      <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on"  checked
                                       onClick="location.href='${containerVar }?docTrail=${castCollection:encode(trailVar)}&categoryId=${categoryIdVar}&sortByValue=${sortByValue }&productListingView=${productListingView }&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab }&osearchmode=${osearchmode}'"/>
                      <dsp:include page="docFacetValueFormatter.jsp">
                        <dsp:param name="facetedProperty" param="facetedProperty"/>
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
        </dsp:oparam>
      </dsp:droplet>
      </c:forEach>
    </c:when>
    <c:otherwise>
      
      <dsp:droplet name="CastDocFacetedPropertyDroplet">
        <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
        <dsp:oparam name="output">
          <div class="${filterColour}">    
            <dsp:droplet name="CastFacetTrailDroplet">
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
          
                 <dsp:droplet name="CastDocFacetedPropertyDroplet">
                  <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="facetedPropertyVar" param="facetedProperty"/>
                    <c:choose>
                      <c:when test="${facetedPropertyVar == 'rating'}">
                        <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on"  checked
                                         onClick="location.href='${containerVar }?docTrail=${castCollection:encode(trailVar)}&categoryId=${categoryIdVar}&sortByValue=${sortByValue }&productListingView=${productListingView }&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab }&osearchmode=${osearchmode}'"/>
                        <dsp:include page="docFacetValueFormatter.jsp">
                          <dsp:param name="facetedProperty" param="facetedProperty"/>
                          <dsp:param name="value" value="${selectedFacet.value}"/>
                          <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>                    
                        </dsp:include>
                      </c:when>
                      <c:otherwise>
                        <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on"  checked
                                         onClick="location.href='${containerVar }?docTrail=${castCollection:encode(trailVar)}&categoryId=${categoryIdVar}&sortByValue=${sortByValue }&productListingView=${productListingView }&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab }&osearchmode=${osearchmode}'"/>
                        <dsp:include page="docFacetValueFormatter.jsp">
                          <dsp:param name="facetedProperty" param="facetedProperty"/>
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
          </dsp:oparam>
        </dsp:droplet>
        
      </c:otherwise>
    </c:choose>
</dsp:page>
