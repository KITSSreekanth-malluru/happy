<dsp:page>
    <!-- DEBUG: first row of filters_for_pivor.jsp -->
    <dsp:importbean
        bean="/atg/commerce/search/refinement/CommerceFacetTrailDroplet" />
    <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />
    <dsp:importbean
        bean="/com/castorama/search/droplet/CastFacetedPropertyDroplet" />
    <dsp:importbean bean="/com/castorama/droplet/CastQueryStringDroplet" />


    <dsp:getvalueof var="facetRowAmount"
        bean="/com/castorama/search/SearchConfiguration.facetRowAmount" />

    <dsp:getvalueof var="queryResponse" param="queryResponse" />
    <dsp:getvalueof var="facetHolders" param="produitsFST.facets"
        scope="request" />
    <dsp:getvalueof var="trailString" param="produitsFST.facetTrail" />
    <dsp:getvalueof var="containerVar" param="container" />
    <dsp:getvalueof var="url" param="categoryUrl" />

    <dsp:getvalueof var="contextPath"
        bean="/OriginatingRequest.contextPath" />
    <dsp:getvalueof var="currentTab" param="currentTab" />
    <dsp:getvalueof var="categoryIdVar" param="categoryId" />
    <dsp:getvalueof var="ajouterTrail" param="ajouterTrail" />

    <!-- DEBUG: set value to CarouselSessionBean.values.facetedSearchQueryString -->
    <dsp:setvalue
        bean="/com/castorama/carousel/CarouselSessionBean.values.facetedSearchQueryString"
        value="${queryStringVar}&trail=${trailString}" />
 	<dsp:getvalueof var="isCC" bean="/atg/userprofiling/Profile.currentLocalStore.retraitMagasin"/>
    <!-- DEBUG: start  CategoryLookup droplet for refine config -->
    <dsp:droplet name="CategoryLookup">
        <dsp:param name="id" param="categoryId" />
        <dsp:param name="elementName" value="category" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="refineConfigObj" param="category.refineConfig"
                vartype="java.lang.Object" scope="request" />
            <dsp:getvalueof var="cat" param="category" vartype="java.lang.Object"
                scope="request" />
            <c:set var="refineConfigId" value="${refineConfig.id }" />
            <c:set var="pvt" value="${cat}" />
        </dsp:oparam>
    </dsp:droplet>
    <!-- DEBUG: start  CategoryLookup droplet for filterColour -->
    <dsp:droplet name="CategoryLookup">
        <dsp:param name="id" param="categoryId" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="filterColour" param="element.style.facetStyle" />
        </dsp:oparam>
    </dsp:droplet>

    <c:if test="${empty filterColour || fn:length(filterColour)==0}">
        <c:set var="filterColour" value="bluePage" />
    </c:if>

    <!-- DEBUG: start  TargetingFirst droplet -->
    <dsp:droplet name="/atg/targeting/TargetingFirst">
        <dsp:param name="howMany" value="1" />
        <dsp:param name="fireViewItemEvent" value="false" />
        <dsp:param name="targeter"
            bean="/atg/registry/RepositoryTargeters/RefinementRepository/CategoryFacetTargeter" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="produitFacetSetId" param="element.repositoryId" />
        </dsp:oparam>
    </dsp:droplet>

    <!-- DEBUG: start  FacetedSearchPopUpFinder droplet -->
    <dsp:droplet
        name="/com/castorama/search/droplet/FacetedSearchPopUpFinder">
        <dsp:param name="trail" param="produitsFST.facetTrail" />
        <dsp:param name="startCategory" param="categoryId" />
        <dsp:param name="refineConfigId" value="${refineConfigId }" />
        <dsp:param name="catgoryFacetId" value="${produitFacetSetId }" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="additionalFacetValuesToFacetMap"
                param="resultedMapFacetIdToTrail" vartype="java.util.Map"
                scope="request" />
        </dsp:oparam>
    </dsp:droplet>
    <dsp:setvalue param="resultedMapFacetIdToTrailParam"
        value="${additionalFacetValuesToFacetMap }" />

    <div class="productFilter">
    <h2><fmt:message key="search_filters.affinerVotreSelection" /></h2>
    <!-- DEBUG: start  CommerceFacetTrailDroplet droplet --> <dsp:droplet
        name="CommerceFacetTrailDroplet">
        <!-- <dsp:setvalue param="trail" paramvalue="produitsFST.facetTrail" /> -->
        <dsp:param name="refineConfig" value="${refineConfigObj }" />

        <dsp:oparam name="output">
            <dsp:getvalueof var="facetTrail" param="facetTrail" />

            <c:if
                test="${fn:length(facetTrail.facetValues) > 1 || (fn:length(facetTrail.facetValues) == 1 && (!fn:contains(facetTrail.trailString, 'SRCH') && facetTrail.facetValues[0].facet.refinementElement.property != 'ancestorCategories.$repositoryId'))}">
                <div class="selectedFilter ${filterColour}">
                <ul class="red">
                    <c:forEach items="${facetTrail.facetValues}"
                        var="currentFacetValue" varStatus="status">

                        <c:choose>
                            <c:when
                                test="${currentFacetValue.class.name == 'atg.repository.search.refinement.FacetDisjunctionMultiValue' || currentFacetValue.class.name == 'atg.repository.search.refinement.RangeFacetDisjunctionMultiValue'}">
                                <%-- %><c:if test="${not empty currentFacetValue.values && currentFacetValue.facet.refinementElement.property == 'rating'}">
                  ${ castCollection:reverseArray(currentFacetValue.values) }
                </c:if>--%>
                                <c:forEach items="${currentFacetValue.values}"
                                    var="currentMultiFacetValue" varStatus="statusMultiFacet">
                                    <%@ include file="/search/includes/removeMultiFacetsBlock_for_pivot.jspf" %>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <%@ include file="/search/includes/removeFacetsBlock_for_pivot.jspf" %>
              </c:otherwise>
            </c:choose>
          </c:forEach>
          </ul>
         </div> 
        </c:if>
    </dsp:oparam>
    <dsp:oparam name="error">
    </dsp:oparam>
  </dsp:droplet>
  <!-- DEBUG: end CommerceFacetTrailDroplet droplet -->
  <!-- DEBUG: start 2nd CommerceFacetTrailDroplet droplet -->
  <dsp:droplet name="CommerceFacetTrailDroplet">
   <!-- <dsp:setvalue param="trail" value="${trailString}" /> -->
   <dsp:oparam name="output">
    <dsp:getvalueof var="selectedFacets" param="facetTrail.facetValues"/>
    </dsp:oparam>
  </dsp:droplet>
  <!-- DEBUG: end 2nd CommerceFacetTrailDroplet droplet -->
  <!-- DEBUG: start generation url trail for category and marque -->
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
    <!-- <dsp:setvalue param="trail" paramvalue="produitsFST.facetTrail" />  -->
    <dsp:param name="refineConfig" value="${refineConfigObj }"/>
    <dsp:oparam name="output">
        <dsp:getvalueof var="facetTrail" param="facetTrail" />
        <dsp:getvalueof var="urlTrail" value=""/>
        <c:if test="${fn:length(facetTrail.facetValues) > 0  }">
          <c:forEach items="${facetTrail.facetValues}" var="fTrail">
            <c:choose>
              <c:when test="${fTrail.facet.label == 'facet.label.MarqueCommerciale' || fTrail.facet.label == 'facet.label.Category'}">
              <!-- label=${fTrail.facet.label}<br/> -->
                <dsp:getvalueof var="separator" value=":"/>
                <c:if test="${empty urlTrail}">
                  <dsp:getvalueof var="separator" value=""/>
                </c:if>
                <dsp:getvalueof var="urlTrail" value="${urlTrail}${separator}${fTrail}"/>
              </c:when>
              <c:otherwise>
                <dsp:getvalueof var="containsTrailParamInQuerystring" value="true"/>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </c:if>
    </dsp:oparam>
  </dsp:droplet>
  </c:if>
  <dsp:getvalueof var="urlTrail" value="${fn:replace(urlTrail, '|', '_')}"/>
  <script>
//  alert('urlTrail = ${urlTrail}');
//  alert('containsTrailParamInQuerystring = ${containsTrailParamInQuerystring}');
  </script>
  <!-- DEBUG: end generation url trail for category and marque -->
  <!-- DEBUG: start CastFacetPriorityDroplet droplet -->

   <dsp:droplet name="/com/castorama/search/droplet/CastFacetPriorityDroplet">
     <c:choose>
      <c:when test="${not empty refineConfigObj}">
        <dsp:param name="refineConfig" value="${refineConfigObj}" />
      </c:when>
      <c:otherwise>
        <dsp:param name="refineConfig" value="globalFacetsRC" />
        <dsp:param name="repository" bean="/atg/search/repository/RefinementRepository" />
      </c:otherwise>
     </c:choose>
     
     <dsp:oparam name="output">
     <dsp:getvalueof var="orderedFacetsIds" param="orderedFacetsIds"/>
     <c:forEach items="${orderedFacetsIds}" var="orderedFacetsId">

       <c:forEach items="${facetHolders}" var="facetHolder">
     
       <c:choose>
       <c:when test="${facetHolder.facet.id == orderedFacetsId && ((facetHolder.facet.refinementElement.property != 'ccMode' && facetHolder.facet.refinementElement.property != 'availability')  || (facetHolder.facet.refinementElement.property == 'ccMode' && not empty isCC && isCC) || (facetHolder.facet.refinementElement.property == 'availability' && (empty isCC || not isCC)))}">
        <c:set var="existingFacetHolder" value="true"/>
          <dsp:droplet name="/com/castorama/search/droplet/CastFacetValuesDroplet">
            <dsp:param name="facetValues" value="${facetHolder.facetValueNodes}"/>
            <dsp:param name="pivotCategoryId" param="categoryId"/>
            <dsp:param name="pivotCategory" value="${pvt}"/>
            <dsp:param name="isSearchForCategory" param="isSearchForCategory"/>
            <dsp:oparam name="oparam">
              <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
              <dsp:param name="value" param="facetValuesResultList"/>
              <dsp:oparam name="false">
              <dsp:getvalueof var="facetValuesResultListVar" param="facetValuesResultList"/>
              <c:if test="${facetHolder.facet.class.name != 'atg.repository.search.refinement.SearchFacet' && facetHolder.facet.refinementElement.property == 'rating'}">
                  ${ castCollection:reverse(facetValuesResultListVar) }
              </c:if>
               <c:set var="isSelectedFacetValueExistForCurrentFacet" value="false"/>
               <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
                <c:choose>
                  <c:when test="${selectedFacet.facet.class.name == 'atg.repository.search.refinement.SearchFacet' }">
                  </c:when>
                  <c:otherwise>
                 
                    <c:if test="${selectedFacet.facet.id != srchFacetLabel && !(selFacetStatus.count == 1 && selectedFacet.facet.refinementElement.property == 'ancestorCategories.$repositoryId')}">
                      <c:if test="${selectedFacet.facet.id == facetHolder.facet.id }">
                        <c:set var="isSelectedFacetValueExistForCurrentFacet" value="true"/>
                      </c:if>
                    </c:if>
                 </c:otherwise>
                </c:choose>
                
             </c:forEach>
              <c:if test="${fn:length(facetValuesResultListVar) > 1 || (not empty isSelectedFacetValueExistForCurrentFacet && (isSelectedFacetValueExistForCurrentFacet || isSelectedFacetValueExistForCurrentFacet == 'true'))}">
              <h3><fmt:message key="${facetHolder.facet.label}" /></h3>
              <div class="filterChGroup">
              <c:set var="selectedValuesExist" value="false"/>
              
              <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
                <c:choose>
                  <c:when test="${selectedFacet.facet.class.name == 'atg.repository.search.refinement.SearchFacet' }">
                  </c:when>
                  <c:otherwise>
                   
                    <c:if test="${selectedFacet.facet.id != srchFacetLabel && !(selFacetStatus.count == 1 && selectedFacet.facet.refinementElement.property == 'ancestorCategories.$repositoryId')}">
                      <c:if test="${selectedFacet.facet.id == facetHolder.facet.id }">
                        <dsp:include page="removeFacet_for_pivot.jsp">
                          <dsp:param name="selectedFacet" value="${selectedFacet}"/>
                          <dsp:param name="trailString" value="${trailString}"/>
                          <dsp:param name="filterColour" value="${filterColour}"/>
                          <dsp:param name="categoryUrl" value="${url}"/>
                        </dsp:include>
                        <c:set var="selectedValuesExist" value="true"/>
                      </c:if>
                    </c:if>
                   </c:otherwise>
                  </c:choose>
                  
                </c:forEach>
                
                <c:choose>
                  <c:when test="${ not empty selectedValuesExist && !selectedValuesExist }">
                  
                    <c:if test="${fn:length(facetValuesResultListVar) > 1}">
                      <c:choose>
                        <c:when test="${fn:length(facetValuesResultListVar) > facetRowAmount}">
                          <c:set var="endIndexVar" value="${facetRowAmount - 1}"/>
                        </c:when>
                        <c:otherwise>
                          <c:set var="endIndexVar" value="${fn:length(facetValuesResultListVar)}"/>
                        </c:otherwise>
                       </c:choose>
                       
                      <c:forEach items="${facetValuesResultListVar}" var="facetValueNode" varStatus="status1" end="${endIndexVar}">
                      
                          <div>
                            
                            <dsp:droplet name="CommerceFacetTrailDroplet">
                              <dsp:param name="trail" param="produitsFST.facetTrail"/>
                                <dsp:getvalueof var="trailString" param="produitsFST.facetTrail"/>
                                <c:if test="${ empty trailString and ! empty queryResponse.question}">
                                  <dsp:setvalue param="addFacet" value="SRCH:${queryResponse.question}:${facetValueNode.facetValue}" />
                                </c:if>
                                <c:if test="${ ! empty trailString or empty queryResponse.question }">
                                  <dsp:setvalue param="addFacet" value="${facetValueNode.facetValue}" />
                                </c:if>
                                <dsp:param name="addFacet" param="addFacet"/>
                                <dsp:param name="refineConfig" value=" ${refineConfigObj }"/>
                                
                                <dsp:oparam name="output">
                                  <dsp:getvalueof var="facetTrail" param="facetTrail" />
                                  
                                  <dsp:getvalueof var="trailVar" param="facetTrail.trailString" />
                                  <dsp:getvalueof var="categoryIdVar" param="categoryId"/>
                                  <dsp:getvalueof var="sortByValue" param="sortByValue"/>
                                  <dsp:getvalueof var="productListingView" param="productListingView"/>
                                  <dsp:getvalueof var="question" param="question"/>
                                  <dsp:getvalueof var="searchType" param="searchType"/>
                                  
                                  <dsp:param name="searchType" param="searchType"/>
                                  
                                  <dsp:getvalueof var="startSep" value="?"/>
                                  <c:if test="${fn:contains(containerVar, '?') }">
                                    <dsp:getvalueof var="startSep" value="&"/>
                                  </c:if>
                                  <c:if test="${not empty facetHolder.facet.refinementElement.property && not empty facetValueNode.facetValue.value}">
                                    <dsp:getvalueof var="lastFilter" value="${facetHolder.facet.refinementElement.property}:${facetValueNode.facetValue.value }"/>
                                  </c:if>
                                    <dsp:getvalueof var="osearchmode" param="osearchmode"/>
                                    <c:if test="${empty osearchmode and not empty searchType}">
                                        <dsp:getvalueof var="osearchmode" value="reg"/>
                                    </c:if>
                                    <dsp:getvalueof var="facetedProperty" value="${facetHolder.facet.refinementElement.property}"/>
                                    <dsp:getvalueof var="includeInputFlag" value="${not empty facetedProperty && (facetedProperty == 'rating' || facetedProperty == 'price' || facetedProperty == 'availability')}"/>
                                    
                                    <dsp:droplet name="CastQueryStringDroplet">
                                      <c:choose>
                                        <c:when test="${(not empty includeInputFlag && includeInputFlag == 'true') || not empty trail || (not empty containsTrailParamInQuerystring && containsTrailParamInQuerystring == 'true')}">
                                          <dsp:param name="trail" value="${trailVar}"/>
                                          <dsp:param name="names" value="trail,sortByValue,productListingView,question,searchType,pageNum,currentTab,lastFilter,osearchmode"/>
                                        </c:when>
                                        <c:otherwise>
                                          <dsp:param name="names" value="sortByValue,productListingView,question,searchType,pageNum,currentTab,lastFilter,osearchmode"/>
                                        </c:otherwise>
                                      </c:choose>
                                      <dsp:param name="sortByValue" value="${sortByValue}"/>
                                      <dsp:param name="productListingView" value="${productListingView}"/>
                                      <dsp:param name="question" value="${question}"/>
                                      <dsp:param name="searchType" value="${searchType}"/>
                                      <dsp:param name="pageNum" value="1"/>
                                      <dsp:param name="currentTab" value="${currentTab}"/>
                                      <dsp:param name="lastFilter" value="${lastFilter}"/>
                                      <dsp:param name="osearchmode" value="${osearchmode}"/>
                                      <dsp:oparam name="output">
                                        <dsp:getvalueof var="qString" param="qString"/>
                                      </dsp:oparam>
                                    </dsp:droplet>
                                    <dsp:include page="facetValueFormatter_for_pivot.jsp">
                                      <c:choose>
                                        <c:when test="${not empty includeInputFlag && includeInputFlag == 'true'}">
                                            <dsp:param name="trail" value="${trail}"/>
                                        </c:when>
                                        <c:otherwise>
                                          <dsp:getvalueof var="currentTrail" value="${facetValueNode.facetValue}"/>
                                          <c:choose>
                                            <c:when test="${not empty urlTrail}">
                                              <dsp:param name="trailVar" value="${urlTrail}:${currentTrail}"/>
                                            </c:when>
                                            <c:otherwise>
                                              <dsp:param name="trailVar" value="${currentTrail}"/>
                                            </c:otherwise>
                                          </c:choose>
                                        </c:otherwise>
                                      </c:choose>
                                      <dsp:param name="facetedProperty" value="${facetHolder.facet.refinementElement.property}"/>
                                      <dsp:param name="facetValue" value="${facetValueNode.facetValue}"/>
                                      <dsp:param name="value" value="${facetValueNode.facetValue.value}"/>
                                      <dsp:param name="facetId" value="${facetValueNode.facetValue.facet.id}"/>
                                      <dsp:param name="categoryUrl" value="${url}"/>
                                      <dsp:param name="qString" value="${qString}"/>
                                      <dsp:param name="includeInput" value="true"/>
                                    </dsp:include>
                                    
                              </dsp:oparam>
                              <dsp:oparam name="error">
                              </dsp:oparam>
                            </dsp:droplet>
                          </div>
                      </c:forEach>
                      
                     </c:if>
                     <dsp:include page="/search/ajouter/addMore_for_pivot.jsp">
                        <dsp:param name="ajouterFacetId" value="${orderedFacetsId}" />
                        <dsp:param name="facetValuesResultList" value="${facetValuesResultListVar}"/>
                        <dsp:param name="categoryId" param="categoryId"/>
                        <dsp:param name="sortByValue" param="sortByValue"/>
                        <dsp:param name="productListingView" param="productListingView"/>
                        <dsp:param name="question" param="question"/>
                        <dsp:param name="searchType" param="searchType"/>
                        <dsp:param name="produitsFST" param="produitsFST"/>
                        <dsp:param name="facetedProperty" value="${facetHolder.facet.refinementElement.property}"/>
                        <dsp:param name="queryResponse" param="queryResponse"/>
                        <dsp:param name="docContainer" value="${containerVar}"/>
                        <dsp:param name="refineConfigObj" value="${refineConfigObj}"/>
                        <dsp:param name="currentTab" param="currentTab"/>
                        <dsp:param name="containsTrailParamInQuerystring" value="${containsTrailParamInQuerystring}"/>
                        <dsp:param name="categoryUrl" value="${url}"/>
                        <dsp:param name="urlTrail" value="${urlTrail}"/>
                     </dsp:include>
                    
                   </c:when>
                   <c:otherwise>
                     <%@ include file="/search/ajouter/popUp_for_pivot.jspf" %>
                   </c:otherwise>
                 </c:choose>
                 
                </div>
                
              </c:if>
            </dsp:oparam>
            <dsp:oparam name="true">
            <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
              
              <c:choose>
                <c:when test="${selectedFacet.facet.class.name == 'atg.repository.search.refinement.SearchFacet' }">
                </c:when>
                <c:otherwise>
                 
                  <c:if test="${selectedFacet.facet.id != srchFacetLabel && !(selFacetStatus.count == 1 && selectedFacet.facet.refinementElement.property == 'ancestorCategories.$repositoryId')}">
                    <c:if test="${selectedFacet.facet.id == facetHolder.facet.id }">
                      <c:set var="selectedValuesExist" value="true"/>
                    </c:if>
                  </c:if>
                 </c:otherwise>
                </c:choose>
                
             </c:forEach>
             <c:if test="${not empty selectedValuesExist && (selectedValuesExist || selectedValuesExist == 'true')}">
              <h3><fmt:message key="${facetHolder.facet.label}" /></h3>
              <div class="filterChGroup">
                <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
                  <c:if test="${selectedFacet.facet.id == facetHolder.facet.id && !(selFacetStatus.count == 1 && selectedFacet.facet.refinementElement.property == 'ancestorCategories.$repositoryId')}">
                    <dsp:include page="removeFacet_for_pivot.jsp">
                      <dsp:param name="selectedFacet" value="${selectedFacet}"/>
                      <dsp:param name="trailString" value="${trailString}"/>
                      <dsp:param name="filterColour" value="${filterColour}"/>
                      <dsp:param name="categoryUrl" value="${url}"/>
                    </dsp:include>
                  </c:if>
                </c:forEach>
                <%@ include file="/search/ajouter/popUp_for_pivot.jspf" %>
             </div>
             </c:if>
            </dsp:oparam>
            </dsp:droplet>
        
          </dsp:oparam>
         </dsp:droplet>
        </c:when>
       
     </c:choose>
        
    </c:forEach>
    
   <c:choose>
    <c:when test="${empty existingFacetHolder || ! existingFacetHolder }">
      <c:set var="i" value="0"/>
      <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
        <c:if test="${selectedFacet.facet.class.name != 'atg.repository.search.refinement.SearchFacet' }">
        <c:if test="${selectedFacet.facet.id != srchFacetLabel && !(selFacetStatus.count == 1 && selectedFacet.facet.refinementElement.property == 'ancestorCategories.$repositoryId')}">
          <c:if test="${selectedFacet.facet.id == orderedFacetsId && selectedFacet.facet.id != facetHolder.facet.id}">
           <c:set var="i" value="${i + 1 }"/>
           <c:set var="facetLabel" value="${selectedFacet.facet.label }"/>
           <c:set var="selectedFacetId" value="${selectedFacet.facet.id }"/>
          </c:if>
        </c:if>
        </c:if>
      </c:forEach>
      <c:if test="${i > 0 }">
        <h3><fmt:message key="${facetLabel}"/></h3>
        <div class="filterChGroup">
          <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
            <c:if test="${selectedFacet.facet.class.name != 'atg.repository.search.refinement.SearchFacet' }">
            <c:if test="${selectedFacet.facet.id != srchFacetLabel && !(selFacetStatus.count == 1 && selectedFacet.facet.refinementElement.property == 'ancestorCategories.$repositoryId')}">
              <c:if test="${selectedFacet.facet.id == orderedFacetsId}">
                <dsp:include page="removeFacet_for_pivot.jsp">
                  <dsp:param name="selectedFacet" value="${selectedFacet}"/>
                  <dsp:param name="trailString" value="${trailString}"/>
                  <dsp:param name="filterColour" value="${filterColour}"/>
                  <dsp:param name="categoryUrl" value="${url}"/>
                </dsp:include>
              </c:if>
            </c:if>
            </c:if>
          </c:forEach>
          <%@ include file="/search/ajouter/popUp_for_pivot.jspf" %>
          <c:set var="facetLabel" value=""/>
          <c:set var="selectedFacetId" value=""/>
        </div>
      </c:if>
    </c:when>
    <c:otherwise>
      <c:set var="existingFacetHolder" value="false"/>
    </c:otherwise>
   </c:choose>
   
   </c:forEach>
   </dsp:oparam>
   </dsp:droplet>
  </div>
    <!-- DEBUG: last row of filters_for_pivor.jsp -->
</dsp:page>