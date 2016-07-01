<dsp:page>

  <dsp:importbean bean="/com/castorama/commerce/search/refinement/CastFacetTrailDroplet"/>
  <dsp:importbean bean="/com/castorama/search/droplet/CastDocFacetedPropertyDroplet"/>
  
  <dsp:getvalueof var="facetRowAmount" bean="/com/castorama/search/SearchConfiguration.facetRowAmount"/>
  
  <dsp:getvalueof var="magasinFacets" param="magasinFST.facets" scope="request" />
 
  <dsp:getvalueof var="magasinTrailString" param="magasinFST.facetTrail"/>
  <dsp:getvalueof var="magasinContainerVar" param="magasinContainer"/>
  <dsp:getvalueof var="magasinContextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="currentTab" param="currentTab"/>
  <dsp:getvalueof var="sortByValue" param="sortByValue"/>
  <c:set var="filterColour" value="bluePage"/>

  <dsp:droplet name="/atg/targeting/TargetingFirst">
    <dsp:param name="howMany" value="1" />
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:param name="targeter" bean="/atg/registry/RepositoryTargeters/RefinementRepository/MagasinFacetSetTargeter" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="magasinFacetSetId" param="element.repositoryId" />
      <dsp:getvalueof var="magasinRefineConfigName" param="element.name"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:droplet name="/com/castorama/search/droplet/FacetedSearchDocAndMagasinPopUpFinder">
    <dsp:param name="trail" param="magasinFST.facetTrail"/>
    <dsp:param name="startCategory" param="categoryId"/>
    <dsp:param name="refineConfigId" value="${magasinFacetSetId }"/>    
    <dsp:oparam name="output">
      <dsp:getvalueof var="magasinResMapFacetIdToTrailVar" param="resultedMapFacetIdToTrail"  vartype="java.util.Map" scope="request"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:setvalue param="resultedMapFacetIdToTrailParam" value="${magasinResMapFacetIdToTrailVar }"/>

  <div class="productFilter">
  <h2><fmt:message key="search_filters.affinerVotreSelection"/></h2>
  <dsp:droplet name="CastFacetTrailDroplet">
	<dsp:setvalue param="trail" paramvalue="magasinFST.facetTrail" />
	<dsp:param name="refineConfig" value="${magasinRefineConfigName }"/>
	<dsp:oparam name="output">
	    <dsp:getvalueof var="magasinFacetTrail" param="facetTrail" />
	    
	    <c:if test="${fn:length(magasinFacetTrail.facetValues) > 1 || (fn:length(magasinFacetTrail.facetValues) == 1 ) && !fn:contains(magasinFacetTrail.trailString, 'SRCH')}">
	     <div class="selectedFilter bluePage">
	     <ul class="red">
        
		  <c:forEach items="${magasinFacetTrail.facetValues}" var="magasinCurrentFacetValue" varStatus="magasinStatus">
            
            <c:set var="srchFacetLabel" value="SRCH" />
            
            <c:if test="${magasinCurrentFacetValue.facet.id != srchFacetLabel}">
              <c:choose>
                <c:when test="${magasinCurrentFacetValue.class.name == 'atg.repository.search.refinement.FacetDisjunctionMultiValue' || magasinCurrentFacetValue.class.name == 'atg.repository.search.refinement.RangeFacetDisjunctionMultiValue'}">
                  <%-- %><c:if test="${not empty currentFacetValue.values && currentFacetValue.facet.refinementElement.property == 'rating'}">
                    ${ castCollection:reverseArray(currentFacetValue.values) }
                  </c:if>--%>
                  <c:forEach items="${magasinCurrentFacetValue.values}" var="currentMultiFacetValue" varStatus="statusMultiFacet">
                    <li>
      				  <dsp:droplet name="CastFacetTrailDroplet">
      					<dsp:setvalue param="trail" value="${magasinTrailString}" />
      					<dsp:setvalue param="removeFacet" value="${magasinCurrentFacetValue.facet.id}:${currentMultiFacetValue }" />
      					
      					<dsp:oparam name="output">
                
                            <dsp:getvalueof var="magasinFacetTrail" param="facetTrail" />
      						
                              <dsp:droplet name="CastDocFacetedPropertyDroplet">
                                <dsp:param name="facetId" value="${magasinCurrentFacetValue.facet.id}"/>
                                <dsp:oparam name="output">
                                  <dsp:getvalueof var="facetedPropertyVar" param="facetedProperty"/>
                                    <dsp:a href="${magasinContainerVar }">
                                      <dsp:include page="docFacetValueFormatter.jsp">
                                        <dsp:param name="facetedProperty" param="facetedProperty"/>
                                        <dsp:param name="value" value="${currentMultiFacetValue}"/>
                                        <dsp:param name="facetId" value="${magasinCurrentFacetValue.facet.id}"/>
                                      </dsp:include>
                                      <dsp:param name="magasinTrail" param="facetTrail.trailString"/>
                                      <dsp:param name="searchType" param="searchType"/>                
                                      <dsp:param name="question" param="question"/>
                                      <dsp:param name="pageNum" param="1"/>
                                      <dsp:param name="currentTab" param="currentTab"/>
                                      <dsp:param name="sortByValue" param="sortByValue"/>
                                      <span></span>
                                    </dsp:a>
                                </dsp:oparam>
                              </dsp:droplet>
      					</dsp:oparam>
      				  </dsp:droplet>
      				</li>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <li>
  				  <dsp:droplet name="CastFacetTrailDroplet">
  					<dsp:setvalue param="trail" value="${magasinTrailString}" />
  					<dsp:setvalue param="removeFacet" value="${magasinCurrentFacetValue}" />
  					
  					<dsp:oparam name="output">
            
                        <dsp:getvalueof var="magasinFacetTrail" param="facetTrail" />
  						
                          <dsp:droplet name="CastDocFacetedPropertyDroplet">
                            <dsp:param name="facetId" value="${magasinCurrentFacetValue.facet.id}"/>
                            <dsp:oparam name="output">
                              <dsp:getvalueof var="facetedPropertyVar" param="facetedProperty"/>
                                <dsp:a href="${magasinContainerVar }">
                                  <dsp:include page="docFacetValueFormatter.jsp">
                                    <dsp:param name="facetedProperty" param="facetedProperty"/>
                                    <dsp:param name="value" value="${magasinCurrentFacetValue.value}"/>
                                    <dsp:param name="facetId" value="${magasinCurrentFacetValue.facet.id}"/>
                                  </dsp:include>
                                  <dsp:param name="magasinTrail" param="facetTrail.trailString"/>
                                  <dsp:param name="searchType" param="searchType"/>                
                                  <dsp:param name="question" param="question"/>
                                  <dsp:param name="pageNum" param="1"/>
                                  <dsp:param name="currentTab" param="currentTab"/>
                                  <dsp:param name="sortByValue" param="sortByValue"/>
                                  <span></span>
                                </dsp:a>
                              
                            </dsp:oparam>
                          </dsp:droplet>
  					   </dsp:oparam>
  				    </dsp:droplet>
  				  </li>
                </c:otherwise>
              </c:choose>
			</c:if>
			
		  </c:forEach>
		  </ul>
		 </div> 
		</c:if>
	</dsp:oparam>
    <dsp:oparam name="error">
    </dsp:oparam>
  </dsp:droplet>
   
  <dsp:droplet name="CastFacetTrailDroplet">
     <dsp:setvalue param="trail" value="${magasinTrailString}" />
     <dsp:oparam name="output">
       <dsp:getvalueof var="selectedFacets" param="facetTrail.facetValues"/>
       
     </dsp:oparam>
   </dsp:droplet>
  
   <dsp:droplet name="/com/castorama/search/droplet/CastFacetPriorityDroplet">
    
    <dsp:param name="refineConfig" value="${magasinFacetSetId }" />
    <dsp:param name="repository" bean="/atg/searchadmin/RefinementConfigRepository" />
    <dsp:param name="isDocument" value="true" />
     
    
     <dsp:oparam name="output">
      
     <dsp:getvalueof var="orderedFacetsIds" param="orderedFacetsIds"/>
     
     <c:forEach items="${orderedFacetsIds}" var="orderedFacetsId">
     
       <c:forEach items="${magasinFacets}" var="magasinFacet">
       <c:choose>
       <c:when test="${magasinFacet.facet.id == orderedFacetsId}">
       
          <c:set var="existingMagasinFacet" value="true"/>
          <dsp:droplet name="/com/castorama/search/droplet/CastFacetValuesDroplet">
          <dsp:param name="facetValues" value="${magasinFacet.facetValueNodes}"/>
          <dsp:oparam name="oparam">
          <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
          <dsp:param name="value" param="facetValuesResultList"/>
          <dsp:oparam name="false">
          <dsp:getvalueof var="facetValuesResultListVar" param="facetValuesResultList"/>
          
          <c:if test="${fn:length(facetValuesResultListVar) > 1}">
          <h3><fmt:message key="${magasinFacet.facet.label}" /></h3>
          <div class="filterChGroup">
          
           <c:set var="selectedValuesExist" value="false"/>
           <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
          
            <c:choose>
              <c:when test="${selectedFacet.facet.class.name == 'atg.repository.search.refinement.SearchFacet' }">
              </c:when>
              <c:otherwise>
               
              <c:if test="${selectedFacet.facet.id != srchFacetLabel && selectedFacet.facet.id == magasinFacet.facet.id}">
                  <div>
                    <dsp:include page="magasinRemoveFacet.jsp">
                      <dsp:param name="selectedFacet" value="${selectedFacet}"/>
                      <dsp:param name="magasinTrailString" value="${magasinTrailString}"/>
                      <dsp:param name="sortByValue" param="sortByValue"/>
                      <dsp:param name="filterColour" value="${filterColour}"/>
                    </dsp:include>
                  </div>
                  <c:set var="selectedValuesExist" value="true"/>
                </c:if>
               </c:otherwise>
              </c:choose>
              
            </c:forEach>
        
        
            <c:choose>
              <c:when test="${ not empty selectedValuesExist && !selectedValuesExist }">
                <c:choose>
                  <c:when test="${fn:length(facetValuesResultListVar) > facetRowAmount}">
                    <c:set var="endIndexVar" value="${facetRowAmount - 1}"/>
                  </c:when>
                  <c:otherwise>
                    <c:set var="endIndexVar" value="${fn:length(facetValuesResultListVar)}"/>
                  </c:otherwise>
                </c:choose>
                 
                <dsp:getvalueof var="facetValuesResultListVar" param="facetValuesResultList"/>
                <c:forEach items="${facetValuesResultListVar}" var="facetValueNode" varStatus="status1" end="${endIndexVar}">
                    
                  <div>
                  
                    <dsp:droplet name="CastFacetTrailDroplet">
                      <dsp:param name="trail" param="magasinFST.facetTrail"/>
                        <dsp:getvalueof var="trailString" param="magasinFST.facetTrail"/>
                        <c:if test="${ empty trailString and ! empty queryResponse.question}">
                          <dsp:setvalue param="addFacet" value="SRCH:${queryResponse.question}:${facetValueNode.facetValue}" />
                        </c:if>
                        <c:if test="${ ! empty trailString or empty queryResponse.question }">
                          <dsp:setvalue param="addFacet" value="${facetValueNode.facetValue}" />
                        </c:if>
                        <dsp:param name="addFacet" param="addFacet"/>
                        <dsp:param name="refineConfig" value="${magasinRefineConfigName }"/>
                        
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="facetTrail" param="facetTrail" />
                          
                          <dsp:getvalueof var="magasinTrailVar" param="facetTrail.trailString" />
                          <dsp:getvalueof var="sortByValue" param="sortByValue"/>
                          <dsp:getvalueof var="question" param="question"/>
                          <dsp:getvalueof var="searchType" param="searchType"/>
                          
                            <dsp:getvalueof var="osearchmode" param="osearchmode"/>
                            <c:if test="${empty osearchmode and not empty searchType}">
                            	<dsp:getvalueof var="osearchmode" value="reg"/>
	                        </c:if>
                          
                          <dsp:getvalueof var="magasinContainerVarValid" value="${fn:replace(magasinContainerVar, '\\'', '&#92;&#39;')}"/>
                          <dsp:getvalueof var="magasinContainerVarValid" value="${fn:replace(magasinContainerVarValid, '\"', '&#92;&#34;')}"/>
                          <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on" 
                                 onClick="location.href='${magasinContainerVarValid }?magasinTrail=${castCollection:encode(magasinTrailVar)}&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab}&osearchmode=${osearchmode}'"/>
                          
                          <label for="${facetValueNode.facetValue}">
                            <dsp:include page="docFacetValueFormatter.jsp">
                              <dsp:param name="facetedProperty" param="facetedProperty"/>
                              <dsp:param name="value" value="${facetValueNode.facetValue.value}"/>
                              <dsp:param name="facetId" value="${facetValueNode.facetValue.facet.id}"/>
                            </dsp:include>
                          </label>
                      </dsp:oparam>
                      <dsp:oparam name="error">
                      </dsp:oparam>
                    </dsp:droplet>
                  </div>
                    
                </c:forEach>
                
                <dsp:include page="/search/ajouter/addMoreMagasin.jsp">
                  <dsp:param name="ajouterFacetId" value="${orderedFacetsId}" />
                  <dsp:param name="facetValuesResultList" value="${facetValuesResultListVar}"/>
                  <dsp:param name="categoryId" param="categoryId"/>
                  <dsp:param name="sortByValue" param="sortByValue"/>
                  <dsp:param name="productListingView" param="productListingView"/>
                  <dsp:param name="question" param="question"/>
                  <dsp:param name="searchType" param="searchType"/>
                  <dsp:param name="magasinFST" param="magasinFST"/>
                  <dsp:param name="facetedProperty" param="facetedProperty"/>
                  <dsp:param name="queryResponse" param="queryResponse"/>
                  <dsp:param name="magasinContainer" value="${magasinContainerVar}"/>
                  <dsp:param name="currentTab" param="currentTab"/>
                </dsp:include>
                
              </c:when>
              <c:otherwise>
                <%@ include file="/search/ajouter/magasinPopUp.jspf" %>
              </c:otherwise>
            </c:choose>
            
            </div>
            </c:if>
        </dsp:oparam>
        <dsp:oparam name="true">
        
          <h3><fmt:message key="${magasinFacet.facet.label}" /></h3>
          <div class="filterChGroup">
            <c:forEach items="${selectedFacets}" var="selectedFacet">
            
            <c:if test="${selectedFacet.facet.id == magasinFacet.facet.id }">
              <dsp:include page="magasinRemoveFacet.jsp">
                <dsp:param name="selectedFacet" value="${selectedFacet}"/>
                <dsp:param name="magasinTrailString" value="${magasinTrailString}"/>
                <dsp:param name="sortByValue" param="sortByValue"/>
                <dsp:param name="filterColour" value="${filterColour}"/>
              </dsp:include>
            </c:if>
          </c:forEach>
          
          <%@ include file="/search/ajouter/magasinPopUp.jspf" %>
         </div>
        </dsp:oparam>
       </dsp:droplet>
    
      </dsp:oparam>
     </dsp:droplet>
     </c:when>
       
     </c:choose>
        
    </c:forEach>
   
   <c:choose>
    <c:when test="${empty existingMagasinFacet || ! existingMagasinFacet }">
      <c:set var="i" value="0"/>
      <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
        <c:if test="${selectedFacet.facet.class.name != 'atg.repository.search.refinement.SearchFacet' }">
        <c:if test="${selectedFacet.facet.id != srchFacetLabel }">
          <c:if test="${selectedFacet.facet.id == orderedFacetsId && selectedFacet.facet.id != magasinFacet.facet.id}">
           <c:set var="i" value="${i + 1 }"/>
           <c:set var="facetLabel" value="${selectedFacet.facet.label }"/>
          </c:if>
        </c:if>
        </c:if>
      </c:forEach>
      <c:if test="${i > 0 }">
        <h3><fmt:message key="${facetLabel}"/></h3>
        <div class="filterChGroup">
          <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
            <c:if test="${selectedFacet.facet.class.name != 'atg.repository.search.refinement.SearchFacet' }">
            <c:if test="${selectedFacet.facet.id != srchFacetLabel }">
              <c:if test="${selectedFacet.facet.id == orderedFacetsId}">
              
                <div>
                 <dsp:include page="magasinRemoveFacet.jsp">
                  <dsp:param name="selectedFacet" value="${selectedFacet}"/>
                  <dsp:param name="magasinTrailString" value="${magasinTrailString}"/>
                  <dsp:param name="sortByValue" param="sortByValue"/>
                  <dsp:param name="filterColour" value="${filterColour}"/>
                 </dsp:include>
               </div>
              </c:if>
            </c:if>
            </c:if>
          </c:forEach>
          <%@ include file="/search/ajouter/magasinPopUp.jspf" %>
        </div>
      </c:if>
    </c:when>
    <c:otherwise>
      <c:set var="existingMagasinFacet" value="false"/>
    </c:otherwise>
   </c:choose>
       
   </c:forEach>
   </dsp:oparam>
   </dsp:droplet>
  </div>
</dsp:page>