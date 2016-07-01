<dsp:page>

  <dsp:importbean bean="/com/castorama/commerce/search/refinement/CastFacetTrailDroplet"/>
  <dsp:importbean bean="/com/castorama/search/droplet/CastDocFacetedPropertyDroplet"/>
  <dsp:getvalueof var="facetRowAmount" bean="/com/castorama/search/SearchConfiguration.facetRowAmount"/>
  
  <dsp:param name="docFST" param="docFST"/>
  
  <dsp:getvalueof var="docFacets" param="docFST.facets" scope="request" />
  
  <dsp:getvalueof var="docTrailString" param="docFST.facetTrail"/>
  <dsp:getvalueof var="docContainerVar" param="docContainer"/>
  <dsp:getvalueof var="docContextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="currentTab" param="currentTab"/>
  <dsp:getvalueof var="sortByValue" param="sortByValue"/>
  <c:set var="filterColour" value="bluePage"/>
  
  <dsp:droplet name="/atg/targeting/TargetingFirst">
    <dsp:param name="howMany" value="1" />
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:param name="targeter"
      bean="/atg/registry/RepositoryTargeters/RefinementRepository/DocumentFacetSetTargeter" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="documentFacetSetId" param="element.repositoryId" />
      <dsp:getvalueof var="documentRefineConfigName" param="element.name"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="/com/castorama/search/droplet/FacetedSearchDocAndMagasinPopUpFinder">
    <dsp:param name="trail" param="docFST.facetTrail"/>
    <dsp:param name="refineConfigId" value="${documentFacetSetId }"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="documentResMapFacetIdToTrailVar" param="resultedMapFacetIdToTrail"  vartype="java.util.Map" scope="request"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:setvalue param="resultedMapFacetIdToTrailParam" value="${documentResMapFacetIdToTrailVar }"/>
  
  <div class="productFilter">
  <h2><fmt:message key="search_filters.affinerVotreSelection"/></h2>
  <dsp:droplet name="CastFacetTrailDroplet">
	<dsp:setvalue param="trail" paramvalue="docFST.facetTrail" />
	<dsp:param name="refineConfig" value="${documentRefineConfigName}"/>
	<dsp:oparam name="output">
	    <dsp:getvalueof var="docFacetTrail" param="facetTrail" />
	    
	    <c:if test="${fn:length(docFacetTrail.facetValues) > 1 || (fn:length(docFacetTrail.facetValues) == 1 ) && !fn:contains(docFacetTrail.trailString, 'SRCH')}">
	     <div class="selectedFilter ${filterColour}">
	     <ul class="red">
       
		  <c:forEach items="${docFacetTrail.facetValues}" var="docCurrentFacetValue" varStatus="docStatus">
			<c:set var="srchFacetLabel" value="SRCH" />
			<c:if test="${docCurrentFacetValue.facet.id != srchFacetLabel}">
              <c:choose>
                <c:when test="${docCurrentFacetValue.class.name == 'atg.repository.search.refinement.FacetDisjunctionMultiValue' || docCurrentFacetValue.class.name == 'atg.repository.search.refinement.RangeFacetDisjunctionMultiValue'}">
                  <c:forEach items="${docCurrentFacetValue.values}" var="currentMultiFacetValue" varStatus="statusMultiFacet">
                    
  			    <li>
  				<dsp:droplet name="CastFacetTrailDroplet">
				<dsp:setvalue param="trail" value="${docTrailString}" />
				<dsp:setvalue param="removeFacet" value="${docCurrentFacetValue.facet.id}:${currentMultiFacetValue }" />
				
				<dsp:oparam name="output">
        
                    <dsp:getvalueof var="docFacetTrail" param="facetTrail" />
					
                      <dsp:droplet name="CastDocFacetedPropertyDroplet">
                        <dsp:param name="facetId" value="${docCurrentFacetValue.facet.id}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="facetedPropertyVar" param="facetedProperty"/>
                          
                          <c:choose>
                           <c:when test="${facetedPropertyVar == 'rating'}">
                            <dsp:a href="${docContainerVar }"  iclass="stars">
                              <dsp:include page="docFacetValueFormatter.jsp">
                                <dsp:param name="facetedProperty" param="facetedProperty"/>
                                <dsp:param name="value" value="${currentMultiFacetValue}"/>
                                <dsp:param name="facetId" value="${docCurrentFacetValue.facet.id}"/>
                              </dsp:include>
                              <dsp:param name="docTrail" param="facetTrail.trailString"/>
                              <dsp:param name="searchType" param="searchType"/>                
                              <dsp:param name="question" param="question"/>
                              <dsp:param name="pageNum" param="1"/>
                              <dsp:param name="currentTab" param="currentTab"/>
                              <span></span>
                            </dsp:a>
                           </c:when>
                           <c:otherwise>
                            <dsp:a href="${docContainerVar }">
                              <dsp:include page="docFacetValueFormatter.jsp">
                                <dsp:param name="facetedProperty" param="facetedProperty"/>
                                <dsp:param name="value" value="${currentMultiFacetValue}"/>
                                <dsp:param name="facetId" value="${docCurrentFacetValue.facet.id}"/>                                  
                              </dsp:include>
                              <dsp:param name="docTrail" param="facetTrail.trailString"/>
                              <dsp:param name="searchType" param="searchType"/>                
                              <dsp:param name="question" param="question"/>
                              <dsp:param name="pageNum" param="1"/>
                              <dsp:param name="currentTab" param="currentTab"/>
                              <dsp:param name="sortByValue" param="sortByValue"/>
                              <span></span>
                            </dsp:a>
                           </c:otherwise>
                         </c:choose>
                          
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
                    <dsp:setvalue param="trail" value="${docTrailString}" />
                    <dsp:setvalue param="removeFacet" value="${docCurrentFacetValue}" />
                    
                    <dsp:oparam name="output">
                
                    <dsp:getvalueof var="docFacetTrail" param="facetTrail" />
          
                      <dsp:droplet name="CastDocFacetedPropertyDroplet">
                        <dsp:param name="facetId" value="${docCurrentFacetValue.facet.id}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="facetedPropertyVar" param="facetedProperty"/>
                          
                          <c:choose>
                           <c:when test="${facetedPropertyVar == 'rating'}">
                            <dsp:a href="${docContainerVar }"  iclass="stars">
                              <dsp:include page="docFacetValueFormatter.jsp">
                                <dsp:param name="facetedProperty" param="facetedProperty"/>
                                <dsp:param name="value" value="${docCurrentFacetValue.value}"/>
                                <dsp:param name="facetId" value="${docCurrentFacetValue.facet.id}"/>
                              </dsp:include>
                              <dsp:param name="docTrail" param="facetTrail.trailString"/>
                              <dsp:param name="searchType" param="searchType"/>                
                              <dsp:param name="question" param="question"/>
                              <dsp:param name="pageNum" param="1"/>
                              <dsp:param name="currentTab" param="currentTab"/>
                              <span></span>
                            </dsp:a>
                           </c:when>
                           <c:otherwise>
                            <dsp:a href="${docContainerVar }">
                              <dsp:include page="docFacetValueFormatter.jsp">
                                <dsp:param name="facetedProperty" param="facetedProperty"/>
                                <dsp:param name="value" value="${docCurrentFacetValue.value}"/>
                                <dsp:param name="facetId" value="${docCurrentFacetValue.facet.id}"/>                                  
                              </dsp:include>
                              <dsp:param name="docTrail" param="facetTrail.trailString"/>
                              <dsp:param name="searchType" param="searchType"/>                
                              <dsp:param name="question" param="question"/>
                              <dsp:param name="pageNum" param="1"/>
                              <dsp:param name="currentTab" param="currentTab"/>
                              <dsp:param name="sortByValue" param="sortByValue"/>
                              <span></span>
                            </dsp:a>
                           </c:otherwise>
                         </c:choose>
                          
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
     <dsp:setvalue param="trail" value="${docTrailString}" />
     <dsp:oparam name="output">
       <dsp:getvalueof var="selectedFacets" param="facetTrail.facetValues"/>
       
     </dsp:oparam>
   </dsp:droplet>
  
   <dsp:droplet name="/com/castorama/search/droplet/CastFacetPriorityDroplet">
    
    <dsp:param name="refineConfig" value="${documentFacetSetId }" />
    <dsp:param name="isDocument" value="true" />
    <dsp:param name="repository" bean="/atg/searchadmin/RefinementConfigRepository" />
     
    
     <dsp:oparam name="output">
      
     <dsp:getvalueof var="orderedFacetsIds" param="orderedFacetsIds"/>
     <c:forEach items="${orderedFacetsIds}" var="orderedFacetsId">
     
       <c:forEach items="${docFacets}" var="docFacet">
       <c:choose>
       <c:when test="${docFacet.facet.id == orderedFacetsId}">
       
          <c:set var="existingDocFacet" value="true"/>
          <dsp:droplet name="/com/castorama/search/droplet/CastFacetValuesDroplet">
          <dsp:param name="facetValues" value="${docFacet.facetValueNodes}"/>
          <dsp:oparam name="oparam">
          <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
          <dsp:param name="value" param="facetValuesResultList"/>
          <dsp:oparam name="false">
          <dsp:getvalueof var="facetValuesResultListVar" param="facetValuesResultList"/>
          
          <c:if test="${docFacet.facet.class.name != 'atg.repository.search.refinement.SearchFacet' && docFacet.facet.refinementElement.property == 'rating'}">
              ${ castCollection:reverse(facetValuesResultListVar) }
          </c:if>
          <h3><fmt:message key="${docFacet.facet.label}" /></h3>
          <div class="filterChGroup">
          <c:set var="selectedValuesExist" value="false"/>
          
          <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
          
            <c:choose>
              <c:when test="${selectedFacet.facet.class.name == 'atg.repository.search.refinement.SearchFacet' }">
              </c:when>
              <c:otherwise>
               
              <c:if test="${selectedFacet.facet.id != srchFacetLabel && !(selFacetStatus.count == 1 && selectedFacet.facet.refinementElement.property == 'castDocAncestorCategories.$repositoryId') && selectedFacet.facet.id == docFacet.facet.id}">
                <div>
                  <dsp:include page="docRemoveFacet.jsp">
                    <dsp:param name="selectedFacet" value="${selectedFacet}"/>
                    <dsp:param name="trailString" value="${docTrailString}"/>
                    <dsp:param name="sortByValue" param="sortByValue"/>
                    <dsp:param name="filterColour" value="${filterColour}"/>
                  </dsp:include>
                </div>
                <c:set var="selectedValuesExist" value="true"/>
              </c:if>
             </c:otherwise>
            </c:choose>
              
            </c:forEach>
            
            <dsp:getvalueof var="facetValuesResultListVar" param="facetValuesResultList"/>
           
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
               
                <c:forEach items="${facetValuesResultListVar}" var="facetValueNode" varStatus="status1" end="${endIndexVar}">
                    <div>
                    
                      <dsp:droplet name="CastFacetTrailDroplet">
                        <dsp:param name="trail" param="docFST.facetTrail"/>
                          <dsp:getvalueof var="trailString" param="docFST.facetTrail"/>
                          <c:if test="${ empty trailString and ! empty queryResponse.question}">
                            <dsp:setvalue param="addFacet" value="SRCH:${queryResponse.question}:${facetValueNode.facetValue}" />
                          </c:if>
                          <c:if test="${ ! empty trailString or empty queryResponse.question }">
                            <dsp:setvalue param="addFacet" value="${facetValueNode.facetValue}" />
                          </c:if>
                          <dsp:param name="addFacet" param="addFacet"/>
                          <dsp:param name="refineConfig" value="facet set"/>
                          
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="facetTrail" param="facetTrail" />
                            
                            <dsp:getvalueof var="docTrailVar" param="facetTrail.trailString" />
                            <dsp:getvalueof var="sortByValue" param="sortByValue"/>
                            <dsp:getvalueof var="question" param="question"/>
                            <dsp:getvalueof var="searchType" param="searchType"/>

                            <dsp:getvalueof var="osearchmode" param="osearchmode"/>
                            <c:if test="${empty osearchmode and not empty searchType}">
                            	<dsp:getvalueof var="osearchmode" value="reg"/>
	                        </c:if>
                            
                            
                            <dsp:getvalueof var="docContainerVarValid" value="${fn:replace(docContainerVar, '\\'', '&#92;&#39;')}"/>
                            <dsp:getvalueof var="docContainerVarValid" value="${fn:replace(docContainerVarValid, '\"', '&#92;&#34;')}"/>
                            <input id="${facetValueNode.facetValue}" type="checkbox" defaultvalue="on" 
                                   onClick="location.href='${docContainerVarValid }?docTrail=${castCollection:encode(docTrailVar)}&question=${castCollection:encode(question) }&searchType=${searchType }&pageNum=1&currentTab=${currentTab}&sortByValue=${sortByValue}&osearchmode=${osearchmode}'"/>
                            
                            <label for="${facetValueNode.facetValue}">
                              <dsp:include page="docFacetValueFormatter.jsp">
                                <dsp:param name="facetedProperty" value="${docFacet.facet.refinementElement.property}"/>
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
                <dsp:include page="/search/ajouter/addMoreDocument.jsp">
                  <dsp:param name="ajouterFacetId" value="${orderedFacetsId}" />
                  <dsp:param name="facetValuesResultList" value="${facetValuesResultListVar}"/>
                  <dsp:param name="categoryId" param="categoryId"/>
                  <dsp:param name="sortByValue" param="sortByValue"/>
                  <dsp:param name="productListingView" param="productListingView"/>
                  <dsp:param name="question" param="question"/>
                  <dsp:param name="searchType" param="searchType"/>
                  <dsp:param name="docFST" param="docFST"/>
                  <dsp:param name="facetedProperty" value="${facetHolder.facet.refinementElement.property}"/>
                  <dsp:param name="queryResponse" param="queryResponse"/>
                  <dsp:param name="docContainer" value="${docContainerVar}"/>
                  <dsp:param name="currentTab" param="currentTab"/>
                </dsp:include>
                
              </c:when>
              <c:otherwise>
                <%@ include file="/search/ajouter/documentPopUp.jspf" %>
              </c:otherwise>
            </c:choose>
            
           </div>
        </dsp:oparam>
        <dsp:oparam name="true">
        
          <h3><fmt:message key="${docFacet.facet.label}" /></h3>
          <div class="filterChGroup">
           <c:forEach items="${selectedFacets}" var="selectedFacet">
            
            <c:if test="${selectedFacet.facet.id == docFacet.facet.id }">
              <dsp:include page="docRemoveFacet.jsp">
                <dsp:param name="selectedFacet" value="${selectedFacet}"/>
                <dsp:param name="trailString" value="${docTrailString}"/>
                <dsp:param name="sortByValue" param="sortByValue"/>
                <dsp:param name="filterColour" value="${filterColour}"/>
              </dsp:include>
            </c:if>
          </c:forEach>
          <%@ include file="/search/ajouter/documentPopUp.jspf" %>
         </div>
        </dsp:oparam>
        </dsp:droplet>
    
      </dsp:oparam>
     </dsp:droplet>
     </c:when>
       
     </c:choose>
        
    </c:forEach>
   
   <c:choose>
    <c:when test="${empty existingDocFacet || ! existingDocFacet }">
      <c:set var="i" value="0"/>
      <c:forEach items="${selectedFacets}" var="selectedFacet" varStatus="selFacetStatus">
        <c:if test="${selectedFacet.facet.class.name != 'atg.repository.search.refinement.SearchFacet' }">
        <c:if test="${selectedFacet.facet.id != srchFacetLabel && !(selFacetStatus.count == 1 && selectedFacet.facet.refinementElement.property == 'castDocAncestorCategories.$repositoryId')}">
          <c:if test="${selectedFacet.facet.id == orderedFacetsId && selectedFacet.facet.id != docFacet.facet.id}">
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
            <c:if test="${selectedFacet.facet.id != srchFacetLabel && !(selFacetStatus.count == 1 && selectedFacet.facet.refinementElement.property == 'castDocAncestorCategories.$repositoryId')}">
              <c:if test="${selectedFacet.facet.id == orderedFacetsId}">
              
               <div>
                 <dsp:include page="docRemoveFacet.jsp">
                  <dsp:param name="selectedFacet" value="${selectedFacet}"/>
                  <dsp:param name="trailString" value="${docTrailString}"/>
                  <dsp:param name="sortByValue" param="sortByValue"/>
                  <dsp:param name="filterColour" value="${filterColour}"/>
                 </dsp:include>
               </div>
              </c:if>
            </c:if>
            </c:if>
          </c:forEach>
          
          <%@ include file="/search/ajouter/documentPopUp.jspf" %>
        </div>
      </c:if>
    </c:when>
    <c:otherwise>
      <c:set var="existingDocFacet" value="false"/>
    </c:otherwise>
   </c:choose>
       
   </c:forEach>
   </dsp:oparam>
   </dsp:droplet>
  </div>
</dsp:page>