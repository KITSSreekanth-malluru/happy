<dsp:page>

  <dsp:importbean bean="/atg/commerce/search/refinement/CommerceFacetTrailDroplet"/>
  <dsp:importbean bean="/com/castorama/search/droplet/CastFacetedPropertyDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastQueryStringDroplet"/>

  <dsp:getvalueof var="selectedFacet" param="selectedFacet"/>
  <dsp:getvalueof var="trailString" param="trailString"/>
  
  <dsp:getvalueof var="filterColour" param="filterColour"/>
  <dsp:getvalueof var="searchType" param="searchType"/>
  <dsp:getvalueof var="osearchmode" param="osearchmode"/>
  <dsp:getvalueof var="url" param="categoryUrl"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  
  
  <c:if test="${empty osearchmode and not empty searchType}">
      <dsp:getvalueof var="osearchmode" value="reg"/>
  </c:if>
  
  <dsp:droplet name="CastFacetedPropertyDroplet">
    <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="facetedPropertyVarOuter" param="facetedProperty"/>
      <dsp:getvalueof var="ajouterSearchRequest" param="ajouterSearchRequest"/>
      <c:if test="${not empty ajouterSearchRequest && ajouterSearchRequest}">
        <dsp:droplet name="/com/castorama/search/droplet/AjouterTrailBuilder">
          <dsp:oparam name="output">
            <dsp:getvalueof var="ajouterTrail" param="ajouterTrail" scope="request"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>

      <dsp:getvalueof var="urlTrail" value="${trailString}"/>
      <c:if test="${fn:length(trailString) - fn:length(fn:replace(trailString,':','')) != 1}">
        <dsp:droplet name="CommerceFacetTrailDroplet">
          <dsp:param name="refineConfig" value="${refineConfigObj }"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="facetTrail" param="facetTrail" />
            <dsp:getvalueof var="urlTrail" value=""/>
            <dsp:getvalueof var="queryTrailFacetIds" value=""/>
            <c:if test="${fn:length(facetTrail.facetValues) > 0  }">
              <c:forEach items="${facetTrail.facetValues}" var="fTrail">
                <c:choose>
                  <c:when test="${fTrail.facet.label == 'facet.label.MarqueCommerciale' || fTrail.facet.label == 'facet.label.Category'}">
                    <dsp:getvalueof var="separator" value=":"/>
                    <c:if test="${empty urlTrail}">
                      <dsp:getvalueof var="separator" value=""/>
                    </c:if>
                    <dsp:getvalueof var="urlTrail" value="${urlTrail}${separator}${fTrail}"/>
                  </c:when>
                  <c:otherwise>
                    <dsp:getvalueof var="queryTrailFacetIds" value="${queryTrailFacetIds}${fTrail.facet.id}:"/>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
      <dsp:getvalueof var="urlTrail" value="${fn:replace(urlTrail, '|', '_')}"/>
      
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
                      
                      <dsp:droplet name="CastQueryStringDroplet">
                        <c:if test="${trailVar != urlTrail}">
                          <dsp:param name="trail" value="${trailVar}"/>
                        </c:if>
                        <dsp:param name="sortByValue" value="${sortByValue}"/>
                        <dsp:param name="productListingView" value="${productListingView}"/>
                        <dsp:param name="question" value="${question}"/>
                        <dsp:param name="searchType" value="${searchType}"/>
                        <dsp:param name="pageNum" value="1"/>
                        <dsp:param name="currentTab" value="${currentTab}"/>
                        <dsp:param name="lastFilter" value="${lastFilter}"/>
                        <dsp:param name="osearchmode" value="${osearchmode}"/>
                        <dsp:param name="names" value="trail,sortByValue,productListingView,question,searchType,pageNum,currentTab,lastFilter,osearchmode"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="qString" param="qString"/>
                        </dsp:oparam>
                      </dsp:droplet>
                      
                      <c:choose>
                        <c:when test="${fn:length(urlTrail) - fn:length(fn:replace(urlTrail, ':', '')) != 1}">
                          <dsp:getvalueof var="fullUrl" value="${contextPath}/${fn:replace(castCollection:encode(urlTrail), '%3A', '/')}${url}?${qString}"/>
                        </c:when>
                        <c:otherwise>
                          <dsp:getvalueof var="fullUrl" value="${contextPath}${url}?${qString}"/>
                          <dsp:getvalueof var="urlTrail" value=""/>
                        </c:otherwise>
                      </c:choose>
                      
                      <c:choose>
                        <c:when test="${facetedPropertyVar == 'rating'}">
                            <dsp:include page="facetValueFormatter_for_pivot.jsp">
                              <dsp:param name="facetedProperty" param="facetedProperty"/>
                              <dsp:param name="facetValue" value="${selectedFacet}"/>
                              <dsp:param name="value" value="${currentMultiFacetValue}"/>
                              <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                              <dsp:param name="trailVar" value="${urlTrail}"/>
                              <dsp:param name="categoryUrl" value="${url}"/>
                              <dsp:param name="qString" value="${qString}"/>
                              <dsp:param name="checked" value="checked"/>
                              <dsp:param name="includeInput" value="true"/>
                              <dsp:param name="labelType" value="labelStars"/>
                            </dsp:include>
                        </c:when>
                        <c:otherwise>
                            <dsp:include page="facetValueFormatter_for_pivot.jsp">
                              <dsp:param name="trailVar" value="${urlTrail}"/>
                              <dsp:param name="categoryUrl" value="${url}"/>
                              <dsp:param name="qString" value="${qString}"/>
                              <dsp:param name="checked" value="checked"/>
                              <dsp:param name="includeInput" value="true"/>
                            
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

              <dsp:getvalueof var="urlTrail" value=""/>
              <c:if test="${fn:length(facetTrail.facetValues) > 0  }">
                <c:forEach items="${facetTrail.facetValues}" var="fTrail" end="${(fn:length(facetTrail.facetValues))-1}" >
                  <c:if test="${fTrail.facet.label == 'facet.label.MarqueCommerciale' || fTrail.facet.label == 'facet.label.Category'}">
                    <dsp:getvalueof var="separator" value=":"/>
                    <c:if test="${empty urlTrail}">
                      <dsp:getvalueof var="separator" value=""/>
                    </c:if>
                  <dsp:getvalueof var="urlTrail" value="${urlTrail}${separator}${fTrail}"/>
                </c:if>
                </c:forEach>
              </c:if>
              <dsp:getvalueof var="urlTrail" value="${fn:replace(urlTrail, '|', '_')}"/>
              
              
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
                  <dsp:getvalueof var="queryTrailIdsArray" value="${fn:split(queryTrailFacetIds,':') }"/>
                  <dsp:droplet name="CastQueryStringDroplet">
                    <c:if test="${trailVar != urlTrail && !(fn:contains(queryTrailFacetIds,selectedFacet.facet.id) && fn:length(queryTrailIdsArray)==1)}">
                      <dsp:param name="trail" value="${trailVar}"/>
                    </c:if>
                    <dsp:param name="sortByValue" value="${sortByValue}"/>
                    <dsp:param name="productListingView" value="${productListingView}"/>
                    <dsp:param name="question" value="${question}"/>
                    <dsp:param name="searchType" value="${searchType}"/>
                    <dsp:param name="pageNum" value="1"/>
                    <dsp:param name="currentTab" value="${currentTab}"/>
                    <dsp:param name="lastFilter" value="${lastFilter}"/>
                    <dsp:param name="osearchmode" value="${osearchmode}"/>
                    <!-- <dsp:param name="names" value="trail,sortByValue,productListingView,question,searchType,pageNum,currentTab,lastFilter,osearchmode"/> -->
                    <dsp:param name="names" value="trail,sortByValue,productListingView,question,searchType,pageNum,currentTab,lastFilter,osearchmode"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="qString" param="qString"/>
                    </dsp:oparam>
                  </dsp:droplet>
                  
                  <c:choose>
                    <c:when test="${fn:length(urlTrail) - fn:length(fn:replace(urlTrail, ':', '')) != 1}">
                      <dsp:getvalueof var="fullUrl" value="${contextPath}/${fn:replace(castCollection:encode(urlTrail), '%3A', '/')}${url}?${qString}"/>
                    </c:when>
                    <c:otherwise>
                      <dsp:getvalueof var="fullUrl" value="${contextPath}${url}?${qString}"/>
                      <dsp:getvalueof var="urlTrail" value=""/>
                    </c:otherwise>
                  </c:choose>
                  
                  <c:choose>
                    <c:when test="${facetedPropertyVar == 'rating' || facetedPropertyVar == 'price' || facetedPropertyVar == 'availability'}">
                      
                      <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on"  checked
                                       onClick="location.href='${fullUrl}'"/>
                       
                      
                     <dsp:include page="facetValueFormatter_for_pivot.jsp">
                        <dsp:param name="trailVar" value="${urlTrail}"/>
                        <dsp:param name="facetedProperty" param="facetedProperty"/>
                        <dsp:param name="facetValue" value="${selectedFacet}"/>
                        <dsp:param name="value" value="${selectedFacet.value}"/>
                        <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                        <dsp:param name="categoryUrl" value="${url}"/>
                        <dsp:param name="qString" value="${qString}"/>
                        <dsp:param name="checked" value="checked"/>
                        <dsp:param name="includeInput" value="false"/>
                      </dsp:include>
                    </c:when>
                    <c:otherwise>
                      <dsp:include page="facetValueFormatter_for_pivot.jsp">
                        <dsp:param name="trailVar" value="${urlTrail}"/>
                        <dsp:param name="facetedProperty" param="facetedProperty"/>
                        <dsp:param name="facetValue" value="${selectedFacet}"/>
                        <dsp:param name="value" value="${selectedFacet.value}"/>
                        <dsp:param name="facetId" value="${selectedFacet.facet.id}"/>
                        <dsp:param name="categoryUrl" value="${url}"/>
                        <dsp:param name="qString" value="${qString}"/>
                        <dsp:param name="checked" value="checked"/>
                        <dsp:param name="includeInput" value="true"/>
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
