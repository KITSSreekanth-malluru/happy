<dsp:page>

  <dsp:importbean bean="/com/castorama/document/CastDocQueryFormHandler"/>
  <dsp:importbean bean="/com/castorama/commerce/search/refinement/CastDocFacetSearchTools"/>
  <dsp:importbean bean="/com/castorama/commerce/search/refinement/CastFacetTrailDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  
  <dsp:getvalueof var="docCategoryId" param="docCategoryId"/>
  <dsp:getvalueof var="docAddFacet" param="docAddFacet"/>
  <dsp:getvalueof var="docTrail" param="docTrail"/>
  <dsp:getvalueof var="docRemoveFacet" param="docRemoveFacet"/>
  <dsp:getvalueof var="docRemoveAllFacets" param="docRemoveAllFacets"/>
  <dsp:getvalueof var="docProductListingView" param="docProductListingView"/>
  <dsp:getvalueof var="question" param="question"/>
  
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
  
  <dsp:getvalueof var="isMultiSearch" param="isMultiSearch"/>

  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
 
  <dsp:getvalueof var="ajouterDocumentSearchRequest" param="ajouterDocumentSearchRequest"/>
  
   <c:if test="${not empty ajouterDocumentSearchRequest && ajouterDocumentSearchRequest}">
     <dsp:droplet name="/com/castorama/search/droplet/AjouterDocAndMagasinTrailBuilder">
       <dsp:oparam name="output">
          <dsp:getvalueof var="ajouterDocumentTrail" param="ajouterTrail"/>
       </dsp:oparam>
     </dsp:droplet>
   </c:if>
   <dsp:droplet name="IsEmpty">
    <dsp:param name="value" bean="CastDocQueryFormHandler.searchResponse"/>
    <dsp:oparam name="true">
       <c:choose>
         <c:when test="${not empty docAddFacet || not empty docTrail || (not empty question && question != ' ')}">
         <c:choose>
           <c:when test="${not empty question && question != ' ' && !fn:contains(docTrail, 'SRCH')}">
             <c:set var="docAddFacetVar" value="SRCH:${question }"/>
           </c:when>
           <c:otherwise>
           <c:choose>
    	       <c:when test="${not empty docTrail }">
    	         <c:set var="docAddFacetVar" value="${docTrail }"/>
    	       </c:when>
    	       <c:otherwise>
    	       </c:otherwise>
           </c:choose>
          </c:otherwise>
         </c:choose>
  	      
             
         <c:if test="${empty param.pageNum || param.pageNum == '' }">
           <dsp:setvalue param="pageNum" value="1"/>
         </c:if>
         
         <dsp:getvalueof var="articlesParPage" param="articlesParPage"/>
         <c:choose>
	        <c:when test="${not empty articlesParPage && articlesParPage != ''}">
	        	<dsp:setvalue bean="CastDocQueryFormHandler.searchRequest.pageSize" value="${articlesParPage}" />
	        </c:when>
	        <c:otherwise>
	          	<dsp:getvalueof var="articlesParPage" bean="/atg/userprofiling/SessionBean.values.articlesParPage"/>
				 <c:if test="${not empty articlesParPage && articlesParPage != ''}">
					 <dsp:setvalue bean="CastDocQueryFormHandler.searchRequest.pageSize" value="${articlesParPage}" />
				 </c:if>
	        </c:otherwise>
	    </c:choose>
         
         <dsp:setvalue bean="CastDocQueryFormHandler.searchRequest.saveRequest" value="true"/>
         <dsp:setvalue bean="CastDocQueryFormHandler.searchRequest.refineConfig" value="${documentRefineConfigName}"/>
         <dsp:setvalue bean="CastDocQueryFormHandler.errorURL" value="${requestURI}"/>
         <dsp:setvalue bean="CastDocFacetSearchTools.refineConfig" value="${documentRefineConfigName}"/>
         <c:choose>
          <c:when test="${not empty ajouterDocumentSearchRequest && ajouterDocumentSearchRequest}">
            <dsp:setvalue bean="CastDocFacetSearchTools.facetTrail" value="${ajouterDocumentTrail }"/>
          </c:when>
          <c:otherwise>
            <dsp:setvalue bean="CastDocFacetSearchTools.facetTrail" value="${docAddFacetVar }"/>
          </c:otherwise>
         </c:choose>
         
         <c:if test="${empty isMultiSearch || !isMultiSearch }">
          <dsp:setvalue bean="CastDocQueryFormHandler.goToPage" paramvalue="pageNum"  />
         </c:if>
         
        </c:when>
       </c:choose>
    </dsp:oparam>
    <dsp:oparam name="false">   
    </dsp:oparam>
  </dsp:droplet>

</dsp:page>