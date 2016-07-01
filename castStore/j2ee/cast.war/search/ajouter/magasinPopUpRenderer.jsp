<dsp:page>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="ajouterFacetId" param="ajouterFacetId"/>

  <dsp:getvalueof var="trailVar" param="trail"/>
  <dsp:getvalueof var="categoryIdVar" param="categoryId"/>
  <dsp:getvalueof var="sortByValue" param="sortByValue"/>
  <dsp:getvalueof var="question" param="question"/>
  <dsp:getvalueof var="productListingView" param="productListingView"/>
  <dsp:getvalueof var="searchType" param="searchType"/>
  <dsp:getvalueof var="pageNum" param="pageNum"/>
  <dsp:getvalueof var="ajouterTrail" param="ajouterTrail"/>
  <dsp:getvalueof var="magasinContainerVar" param="magasinContainer"/>
  <dsp:getvalueof var="currentTab" param="currentTab"/>
  <dsp:getvalueof var="ajouterColumnWidth" bean="/com/castorama/search/SearchConfiguration.ajouterColumnWidth"/>
  <dsp:getvalueof var="ajouterRowAmount" bean="/com/castorama/search/SearchConfiguration.ajouterRowAmount"/>
  <dsp:getvalueof var="ajouterSingleColumnWidth" bean="/com/castorama/search/SearchConfiguration.ajouterSingleColumnWidth"/>
  
  
  <dsp:droplet name="/com/castorama/search/droplet/AjouterMagasinFacetValuesFinder">
    <dsp:param name="currentAjouterFacetId" value="${ajouterFacetId}"/>
    <dsp:param name="currentAjouterFacetTrail" value="${ajouterTrail}"/>
    <dsp:oparam name="output">
  
      <dsp:droplet name="/com/castorama/search/droplet/CastFacetValuesDroplet">
        <dsp:param name="facetValues" param="ajouterResultedFacetValueNodes"/>
        <dsp:param name="pivotCategoryId" value="${categoryIdVar}"/>
        <dsp:param name="isSearchForCategory" param="isSearchForCategory"/>
        <dsp:oparam name="oparam">
          <dsp:droplet name="/com/castorama/search/droplet/CastFacetedPropertyDroplet">
            <dsp:param name="facetId" param="ajouterFacetId"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="facetedPropertyVar" param="facetedProperty"/>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:getvalueof var="facetValuesResultListVar" param="facetValuesResultList"/>
          <c:if test="${not empty facetValuesResultListVar && facetedPropertyVar == 'rating'}">
            ${ castCollection:reverse(facetValuesResultListVar) }
          </c:if>
          <dsp:droplet name="/com/castorama/search/droplet/AjouterDocAndMagasinValuesCombiner">
            <dsp:param name="facetId" value="${ajouterFacetId }"/>
            <dsp:param name="trail" value="${trailVar }"/>
            <dsp:param name="ajouterFacetFalueNodeList" value="${facetValuesResultListVar}"/>
            
            <dsp:oparam name="output">
              <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                <dsp:param name="value" param="ajouterFacetValueList"/>
                <dsp:oparam name="false">
            
                  <dsp:getvalueof var="formid" vartype="java.lang.String" value="ajouter_${ajouterFacetId}"/>
                  <dsp:getvalueof var="osearchmode" param="osearchmode"/>
                  <c:if test="${empty osearchmode and not empty searchType}">
                  	<dsp:getvalueof var="osearchmode" value="reg"/>
                  </c:if>
             
                  <c:choose>
                    <c:when test="${not empty magasinContainerVar && fn:contains(magasinContainerVar, '?')}">
                      <c:set var="actionContainerVar" value="${magasinContainerVar}&formid=${formid }&osearchmode=${osearchmode}"/>
                    </c:when>
                    <c:otherwise>
                       <c:set var="actionContainerVar" value="${magasinContainerVar}?formid=${formid }&osearchmode=${osearchmode}"/>
                    </c:otherwise>
                  </c:choose>
                  <c:if test="${empty question }">
                    <dsp:getvalueof var="question" param="question"/>
                  </c:if>
                  <div class="icoPpTop">
                    <img title="" alt="" src="${contextPath }/images/icoPpTop.gif"/>
                  </div>
                  <dsp:form action="${actionContainerVar }" method="post" id="${formid }" iclass="ajouterListMagasinForm${ajouterFacetId}">
                    <input type="hidden" name="trail" value="${trailVar}"/>
                    <input type="hidden" name="categoryId" value="${categoryIdVar}"/>
                    <input type="hidden" name="sortByValue" value="${sortByValue }"/>
                    <input type="hidden" name="productListingView" value="${productListingView }"/>
                    <input type="hidden" name="question" value="${question }"/>
                    <input type="hidden" name="searchType" value="${searchType }"/>                
                    <input type="hidden" name="pageNum" value="1"/>
                    <input type="hidden" name="currentTab" value="${currentTab }"/>
                    
                    <input type="hidden" name="ajouterFacetId" value="${ajouterFacetId }"/>
                    <input type="hidden" name="previousTrail" value="${trailVar }"/>
                    <input type="hidden" name="ajouterMagasinSearchRequest" value="true"/>
                    
                    <a href="javascript:void(0);" onclick="$('div#ajouterPopup_${ajouterFacetId}').hide();"><img src="${contextPath}/images/icoClose.gif" style="float:right;" /></a>
                    
                    <dsp:droplet name="/com/castorama/search/droplet/AjouterTableRenderer">
                      <dsp:param name="arrayToRender" param="ajouterFacetValueList"/>
                      <dsp:param name="maxRowCount" value="${ajouterRowAmount}"/>
                      <dsp:param name="elementName" value="ajouterFacetValue"/>
                      <dsp:oparam name="outputTableStart">
                        <table cellpadding="0" cellspacing="0" class="ajouterListTable" >
                        <dsp:getvalueof var="columnCountVar" param="columnCount"/>
                        <c:choose>
                          <c:when test="${columnCountVar == 1}">
                          	<c:set var="width" value="${ajouterSingleColumnWidth}"/>
                          </c:when>
                          <c:otherwise>
                          	<c:set var="width" value="${columnCountVar * ajouterColumnWidth}"/>
                          </c:otherwise>
                        </c:choose>
                        <style type="text/css">
                          .ajouterListMagasinForm${ajouterFacetId} {
                            width: ${width}px;
                          }
                        </style>
                      </dsp:oparam>
                      <dsp:oparam name="outputTableEnd">
                        </table>
                      </dsp:oparam>
                      <dsp:oparam name="outputRowStart">
                        <tr>
                      </dsp:oparam>
                      <dsp:oparam name="outputRowEnd">
                        </tr>
                      </dsp:oparam>
                      <dsp:oparam name="outputColumnStart">
                        <td>
                      </dsp:oparam>
                      <dsp:oparam name="outputColumnEnd">
                        </td>
                      </dsp:oparam>
                      <dsp:oparam name="output">
                     
                        <dsp:getvalueof var="facetValue" param="ajouterFacetValue.facetValue"/>
                        <div class="iOpt" style="width: ${ajouterColumnWidth}px">
                          <dsp:droplet name="/atg/dynamo/droplet/Switch">
                            <dsp:param name="value" param="ajouterFacetValue.selectedFacetValue"/>
                            <dsp:oparam name="true"> 
                              <input type="checkbox" name="ajouter_${ajouterFacetId}" id="ajouter_${facetValue}" value="${facetValue.value }" checked/>
                            </dsp:oparam>
                            <dsp:oparam name="false"> 
                              <input type="checkbox" name="ajouter_${ajouterFacetId}" id="ajouter_${facetValue}" value="${facetValue.value }"/>
                            </dsp:oparam>
                          </dsp:droplet>
                          
                          <label for="ajouter_${ajouterFacetId}:${selectedFacetValueVar.value }">
                            <dsp:include page="/search/includes/docFacetValueFormatter.jsp">
                              <dsp:param name="facetedProperty" value="${facetedPropertyVar}"/>
                              <dsp:param name="value" value="${facetValue.value}"/>
                              <dsp:param name="facetId" value="${facetValue.facet.id}"/>
                            </dsp:include>
                          </label>
                        </div>
                      </dsp:oparam>
                    </dsp:droplet>
                                                                                                      
                    <div class="ppOpt">
                      <img class="ppImg" title="" alt="" src="${contextPath }/images/icoAjouter.gif"/>
                      <a id="select_${ajouterFacetId}" onclick="$('#ajouterPopup_${ajouterFacetId}').find('input').attr('checked', 'checked')" href="javascript:void(0)"><fmt:message key="search_filters.popUp_toutSelection"/></a>
                      <a id="deselect_${ajouterFacetId}" onclick="$('#ajouterPopup_${ajouterFacetId}').find('input').removeAttr('checked')" href="javascript:void(0)"><fmt:message key="search_filters.popUp_deselect"/></a>                                 
                    </div>
                    <fmt:message var="ajouterSubmit" key="search_ajouter.submit"/>
                    <div class="formButtons">
                      <span class="inputButton medBlueBorder">
                        <input id="submit_${ajouterFacetId}" type="submit" defaultvalue="${ajouterSubmit}" value="${ajouterSubmit}"/>
                      </span>
                    </div>
                            
                  </dsp:form>
                </dsp:oparam>
              </dsp:droplet>
              
            </dsp:oparam>
          </dsp:droplet>
          
        </dsp:oparam>
      </dsp:droplet>
      
    </dsp:oparam>
  </dsp:droplet>
  
  <c:set var="ajouterFacetId" value=""/>
</dsp:page>