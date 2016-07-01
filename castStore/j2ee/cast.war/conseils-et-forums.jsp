<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>  
	  <cast:pageContainer>
	    <jsp:attribute name="bodyContent">    
	      
	      <dsp:importbean bean="/atg/commerce/catalog/ProductCatalog" />
	      <dsp:importbean bean="/com/castorama/droplet/CastLookupDroplet" />
	      <dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach" />
	      <dsp:importbean bean="/atg/dynamo/droplet/RQLQueryRange" />
	      <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	      <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	      <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
	      <dsp:importbean bean="/com/castorama/droplet/CastFilterLinkDroplet" />
	      
	      <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
	      
	      <dsp:droplet name="RQLQueryRange" >
		    <dsp:param name="itemDescriptor" value="fastLabConfigs" />
		    <dsp:param name="repository" bean="ProductCatalog" />
		    <dsp:param name="queryRQL" value="ALL" />
		    <dsp:param name="howMany" value="1" />
		    <dsp:param name="sortProperties" value="" />
		    <dsp:oparam name="output">
		      <dsp:getvalueof var="fastLabConfigs" param="element" />
		    </dsp:oparam>
		  </dsp:droplet>
		  <dsp:setvalue param="fastLabConfigs" value="${fastLabConfigs}" />
	      
	      <dsp:getvalueof var="selectedFilterId" param="filterTag" />
	      <c:if test="${not empty selectedFilterId}">
            <dsp:droplet name="CastLookupDroplet">
              <dsp:param name="id" value="${selectedFilterId}" />
              <dsp:param name="elementName" value="selectedFilter" />
              <dsp:param name="itemDescriptor" value="castoDocFilterTag" />
              <dsp:param name="repository" bean="ProductCatalog" />
              <dsp:oparam name="output">
                <dsp:getvalueof var="selectedFilter" param="selectedFilter" />
              </dsp:oparam>
              <dsp:oparam name="empty">
                <dsp:getvalueof var="selectedFilterId" value="" />
              </dsp:oparam>
            </dsp:droplet>
          </c:if>  
          <dsp:setvalue param="selectedFilter" value="${selectedFilter}" />
          
            <div class="breadCrV2">
          <c:choose>
            
	      <c:when test="${not empty selectedFilterId}">
			<dsp:include page="/castCatalog/breadcrumbsCollector.jsp" >
			  <dsp:param name="item" param="selectedFilter"/>
			  <dsp:param name="itemName" param="selectedFilter.tagTitle" />
			  <dsp:param name="navAction" value="pop"/>
			</dsp:include>
			<dsp:include page="/castCatalog/breadcrumbs.jsp" flush="true">
			  <dsp:param name="navAction" value="push"/>
			  <dsp:param name="fromLV" value="new"/>
			</dsp:include>
          </c:when>
          <c:otherwise>
            <dsp:setvalue value="${null}" bean="CatalogNavHistory.navHistory"/>
	        <dsp:include page="/castCatalog/breadcrumbs.jsp" flush="true">
	          <dsp:param name="fromLV" value="true"/>
	          <dsp:param name="navAction" value="push"/>
	        </dsp:include>
          </c:otherwise>
	        
          </c:choose>
	     </div>  
	      <div class="content">
	      	<div class="consForumHeader">
	      	    
	      	    <div class="aiverLVV2">
		      	    <b><dsp:valueof param="fastLabConfigs.cfPageTitle" /></b>
		      	    <br/>
		      	    <dsp:valueof param="fastLabConfigs.cfPageDescr" />
		      	
		      	    <dsp:getvalueof var="fbLinkTitle" param="fastLabConfigs.fbLinkTitle" />
		      	    <dsp:getvalueof var="fbLinkValue" param="fastLabConfigs.fbLinkValue" />
		      	    <dsp:getvalueof var="fdLinkTitle" param="fastLabConfigs.fdLinkTitle" />
		      	    <dsp:getvalueof var="fdLinkValue" param="fastLabConfigs.fdLinkValue" />
		      	    <dsp:getvalueof var="egLinkTitle" param="fastLabConfigs.egLinkTitle" />
		      	    <dsp:getvalueof var="egLinkValue" param="fastLabConfigs.egLinkValue" />
	      	    </div>
	      	    
	      	    
        		<div class="consForumTrocBox">
          			<ul class="consForumTrocBoxMiddle">
            			<li>
			              <a class="consForumGrayLink" href="${fbLinkValue}" onClick="s.tl(this,'e','ForumBrico_LancezVous');" target="_blank" title="">${fbLinkTitle}</a>
			              <a class="consForumGrayLink" href="${fdLinkValue}" onClick="s.tl(this,'e','ForumDeco_LancezVous');" target="_blank" title="">${fdLinkTitle}</a>
            			</li>
            			<li>
			              <a class="consForumGrayLink" href="${egLinkValue}" onClick="s.tl(this,'e','TrocHeures_LancezVous');" target="_blank" title="">${egLinkTitle}</a>
			            </li>
          			</ul>
                <div class="consForumTrocBoxBottom"><!-- --></div>
                <div class="consForumTrocBoxBgBorder"><!-- --></div>
        		</div>
      		</div>
	      <div class="consForumContentWr">
        	<div class="consForumContentShadowLeft">
          		<div class="consForumContentShadowRight">
            		<div id="consForumBackToTop" class="consForumContent clearfix">
              			<div class="consForumFacetWr clearfix">
              				 
					           <dsp:droplet name="/com/castorama/droplet/ConseilsForumsFiltersDroplet">
								 <dsp:oparam name="output">
								   
								   <dsp:getvalueof var="filterCategories" param="filtersCats" />
								   <div class="documentFilter n${fn:length(filterCategories)}">
								 
								   <dsp:droplet name="ForEach">
								     <dsp:param name="elementName" value="categoriesColumns"/>
							         <dsp:param name="array" param="filtersCats"/>
							         <dsp:oparam name="output">
							           
							           <dsp:getvalueof var="categoriesCols" param="categoriesColumns" />
							           <div class="rbBlockV2 n${fn:length(categoriesCols)}">
										 <div class="imageCFV2 clearfix">   
										    <dsp:droplet name="IsEmpty">
								                <dsp:param name="value" param="key.tagCatImage" />
								                <dsp:oparam name="false"> 
								                  <img src='<dsp:valueof param="key.tagCatImage.url" />'/>						                  
								                </dsp:oparam>
								            </dsp:droplet>
										    <h2><dsp:valueof param="key.tagCatTitle" /></h2>
									      </div>
									    <dsp:droplet name="ForEach">
									      <dsp:param name="elementName" value="columnFilters"/>
										  <dsp:param name="array" param="categoriesColumns"/>
										  <dsp:oparam name="outputStart">
										    <ul>
										  </dsp:oparam>
										  <dsp:oparam name="outputEnd">
										    </ul>
										  </dsp:oparam>
										  <dsp:oparam name="output">
										    
										    <dsp:droplet name="ForEach">
										      <dsp:param name="elementName" value="filter"/>
											  <dsp:param name="array" param="columnFilters"/>
											  <dsp:oparam name="outputStart">
											    <li>
											  </dsp:oparam>
											  <dsp:oparam name="outputEnd">
											    </li>
											  </dsp:oparam>
											  <dsp:oparam name="output">
											    
											    <dsp:getvalueof var="tagId" param="filter.repositoryId" />
											    <dsp:getvalueof var="chckd" value="" />
											    <c:if test="${tagId == selectedFilterId}"><dsp:getvalueof var="chckd" value="checked" /></c:if>
                           <div class="singleItemRBV2">
                              <dsp:droplet name="CastFilterLinkDroplet">
                              <dsp:param name="filterId" value="${tagId}"/>
                              <dsp:oparam name="output">
                                <dsp:getvalueof var="url" param="url"/>
                                <label for="${tagId}">
                                  <input type="radio" id="${tagId}" name="filterTags" value="${tagId}" onclick="location.href='${pageContext.request.contextPath}${url}'" ${chckd}/>
                                  <span><dsp:valueof param="filter.tagTitle" /></span>
                                </label>
                              </dsp:oparam>
                            </dsp:droplet>
                           </div>
										     
											  </dsp:oparam>
											</dsp:droplet>

										  </dsp:oparam>
										</dsp:droplet>
								      </div> 
								    </dsp:oparam>
								  </dsp:droplet>
								  
								  </div>
							    </dsp:oparam>
						      </dsp:droplet>	
					        
					      </div>  
	                                
	        <div class="documentContainer">
	         <div class="consForumCategoryWr ">
	          <c:if test="${not empty selectedFilterId}">
	            <h2 class="consForum filteredNameV2"><dsp:valueof param="selectedFilter.tagTitle" /></h2>
                
	          </c:if>

              <dsp:droplet name="RQLQueryForEach">
	            <dsp:param name="queryRQL" value="ALL" />
			    <dsp:param name="repository" bean="ProductCatalog" />
			    <dsp:param name="itemDescriptor" value="castoDocType" />
			    <dsp:param name="elementName" value="docSubType" />
			    <dsp:param name="sortProperties" value="+typeNumber" />
			    <dsp:oparam name="output">
			    <div class="consForum clearfix">
			      <dsp:getvalueof var="docSubTypeId" param="docSubType.repositoryId" />
			    
			      <c:choose>
      			    <c:when test="${not empty selectedFilterId}">
      			      
      			      <dsp:getvalueof var="currDocSubTypeDocNum" value="0" />
      			      <dsp:droplet name="ForEach">
					    <dsp:param name="array" param="selectedFilter.documentsOrder" />
					    <dsp:param name="elementName" value="doc" />
					    <dsp:oparam name="output">
					      <dsp:droplet name="IsEmpty">
			                <dsp:param name="value" param="doc.documentSubType" />
			                <dsp:oparam name="false"> 
			                  <dsp:getvalueof var="currDocSubType" param="doc.documentSubType.repositoryId" />
						      <c:if test="${currDocSubType == docSubTypeId}">
						        <dsp:getvalueof var="currDocSubTypeDocNum" value="${currDocSubTypeDocNum + 1}" />
						      </c:if>
			                </dsp:oparam>
			              </dsp:droplet>
					    </dsp:oparam>
					  </dsp:droplet>
      			      
      			      <dsp:droplet name="ForEach">
					    <dsp:param name="array" param="selectedFilter.documentsOrder" />
					    <dsp:param name="elementName" value="doc" />
					    <dsp:oparam name="empty">
					    </dsp:oparam>
					    <dsp:oparam name="outputStart">
					      <c:if test="${currDocSubTypeDocNum > 0}">
						   <div class="headerFormV2 clearfix" >   
						      <h2 class="zz"><dsp:valueof param="docSubType.typeTitle" /></h2>
						      <span class="numbItRS">
						      	(${currDocSubTypeDocNum} résultat<c:if test="${currDocSubTypeDocNum > 1}">s</c:if>)
						      </span>
						    </div>  
					      <h3 class="consForumTitleDescription"><dsp:valueof param="docSubType.typeDescription" /></h3>
					     </c:if>
					    </dsp:oparam>
					    <dsp:oparam name="outputEnd">
					     <c:if test="${currDocSubTypeDocNum > 0}">
					      <div class="consForumBackToTopWr"><font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font> &nbsp;<a href="consForumBackToTop" class="consForumBackToTop">Haut de page</a>&nbsp; <font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font></div>
					     </c:if>
					    </dsp:oparam>
					    <dsp:oparam name="output">
					      
					      <dsp:droplet name="IsEmpty">
			                <dsp:param name="value" param="doc.documentSubType" />
			                <dsp:oparam name="false"> 
			                  <dsp:getvalueof var="currDocSubType" param="doc.documentSubType.repositoryId" />
						      <c:if test="${currDocSubType == docSubTypeId}">
						        
					      <dsp:droplet name="/com/castorama/droplet/CastDocumentLinkDroplet">
	                        <dsp:param name="documentId" param="doc.repositoryId"/>
	                        <dsp:param name="navAction" value="push"/>
                            <dsp:param name="navCount" param="navCount" />
	                        <dsp:oparam name="output">
	                          <dsp:getvalueof var="url" param="url"/>
	                          <c:if test="${fn:startsWith(url, 'http') == false}">
	                            <dsp:getvalueof var="url" value="${pageContext.request.contextPath}${url}"/>
                              </c:if>
	                         <ins> 
	                         	<div class="imageRBV2">
	                         	   <a class="aazz" href="${url}">
                                	<img src='<dsp:valueof param="doc.image.url" />'/>
                                   </a>	
							    </div>
	                         	<div class="textRBV2">
	                         	  <a class="aazz" href="${url}">
	                         	    <h2><dsp:valueof param="doc.title" /></h2>
	                         	  </a>
	                                <div class="consForumDescriptionLinkWr">
								    	<p>
								    	  <dsp:droplet name="IsEmpty">
							                <dsp:param name="value" param="doc.cfDescription" />
							                <dsp:oparam name="true"> 
							                  <dsp:valueof param="doc.description" />
							                </dsp:oparam>
							                <dsp:oparam name="false"> 
							                  <dsp:valueof param="doc.cfDescription" valueishtml="true" />
							                </dsp:oparam>
							              </dsp:droplet>
								    	</p>
								  <a class="aazz" href="${url}">
								    	<span class="consForumGrayLink">
							               <fmt:message key="fl.consulter" />
								    	</span>
								      </a>	
								    </div>	
	                            
	                             </div> 
                             </ins>  
	                        </dsp:oparam>
	                      </dsp:droplet>
	                      
	                          </c:if>
			                </dsp:oparam>
			              </dsp:droplet>
		      			 
					    </dsp:oparam>
					   
					  </dsp:droplet>
      			    </c:when>
      			    <c:otherwise>
      			      
      			      <dsp:droplet name="IsEmpty">
                        <dsp:param name="value" param="docSubType.typeDefaultPromoInfo" />
                        <dsp:oparam name="true">
                        </dsp:oparam>
                        <dsp:oparam name="false">
                        
                          <dsp:droplet name="IsEmpty">
	                        <dsp:param name="value" param="docSubType.typeDefaultPromoInfo.htmlUrl" />
	                        <dsp:oparam name="true">
	                        </dsp:oparam>
	                        <dsp:oparam name="false">
	                          <h2 class="zz"><dsp:valueof param="docSubType.typeTitle" /></h2>
	                          <h3 class="consForumTitleDescription"><dsp:valueof param="docSubType.typeDescription" /></h3>
	                         
	                          <dsp:getvalueof var="promoHtmlUrl" param="docSubType.typeDefaultPromoInfo.htmlUrl" />
	                          <c:import charEncoding="utf-8" url="${staticContentPath}${promoHtmlUrl}"/>
	                          
	                          <div class="consForumBackToTopWr"><font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font> &nbsp;<a href="consForumBackToTop" class="consForumBackToTop">Haut de page</a>&nbsp; <font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font></div>
	                        </dsp:oparam>
	                      </dsp:droplet>
	                      
                        </dsp:oparam>
                      </dsp:droplet>
      			     
      			    </c:otherwise>
      			  </c:choose>
			    
			    </div>
			    </dsp:oparam>
			  </dsp:droplet>
	          
	          
	          <c:choose>
   			    <c:when test="${not empty selectedFilterId}">
   			      <dsp:droplet name="IsEmpty">
	                <dsp:param name="value" param="fastLabConfigs.cfGetHelpFilteredBlock" />
	                <dsp:oparam name="false"> 
	                  <dsp:getvalueof var="defHelpPromoHtmlUrl" param="fastLabConfigs.cfGetHelpFilteredBlock.htmlUrl" />
	                </dsp:oparam>
	              </dsp:droplet>
   			    </c:when>
   			    <c:otherwise>
   			      <dsp:droplet name="IsEmpty">
	                <dsp:param name="value" param="fastLabConfigs.cfGetHelpHomeBlock" />
	                <dsp:oparam name="false"> 
	                  <dsp:getvalueof var="defHelpPromoHtmlUrl" param="fastLabConfigs.cfGetHelpHomeBlock.htmlUrl" />
	                </dsp:oparam>
	              </dsp:droplet>
   			    </c:otherwise>
   			  </c:choose>
   			  <c:if test="${not empty defHelpPromoHtmlUrl}">
   			    <h2 class="zz"><dsp:valueof param="fastLabConfigs.cfGetHelpBlockTitle" /></h2>
   			    <h3 class="consForumTitleDescription"><dsp:valueof param="fastLabConfigs.cfGetHelpBlockDescr" /></h3>
  			    <c:import charEncoding="utf-8" url="${staticContentPath}${defHelpPromoHtmlUrl}"/>
  			    <div class="consForumBackToTopWr"><font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font> &nbsp;<a href="consForumBackToTop" class="consForumBackToTop">Haut de page</a>&nbsp; <font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font></div>
  			  </c:if>
	          
	          <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="fastLabConfigs.cfDefBottomBanner" />
                <dsp:oparam name="false"> 
                  <dsp:getvalueof var="defBannerPromoHtmlUrl" param="fastLabConfigs.cfDefBottomBanner.htmlUrl" />
                </dsp:oparam>
              </dsp:droplet>
	            
	          <c:choose>
   			    <c:when test="${not empty selectedFilterId}">
   			      <dsp:droplet name="IsEmpty">
                    <dsp:param name="value" param="selectedFilter.promoInformation" />
                    <dsp:oparam name="true"> 
                      <c:if test="${not empty defBannerPromoHtmlUrl}">
                       <div class="consForum">
		   			    <c:import charEncoding="utf-8" url="${staticContentPath}${defBannerPromoHtmlUrl}"/>
		   			    <div class="consForumBackToTopWr"><font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font> &nbsp;<a href="consForumBackToTop" class="consForumBackToTop">Haut de page</a>&nbsp; <font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font></div>
		   			   </div>
		   			  </c:if>
                    </dsp:oparam>
                    <dsp:oparam name="false">
                      <dsp:droplet name="IsEmpty">
	                    <dsp:param name="value" param="selectedFilter.promoInformation.htmlUrl" />
	                    <dsp:oparam name="true"> 
	                      <c:if test="${not empty defBannerPromoHtmlUrl}">
	                       <div class="consForum">
			   			    <c:import charEncoding="utf-8" url="${staticContentPath}${defBannerPromoHtmlUrl}"/>
			   			    <div class="consForumBackToTopWr"><font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font> &nbsp;<a href="consForumBackToTop" class="consForumBackToTop">Haut de page</a>&nbsp; <font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font></div>
			   			   </div>
			   			  </c:if>
	                    </dsp:oparam>
	                    <dsp:oparam name="false"> 
	                     <div class="consForum">
	                      <dsp:getvalueof var="promoHtmlUrl" param="selectedFilter.promoInformation.htmlUrl" />
	   			          <c:import charEncoding="utf-8" url="${staticContentPath}${promoHtmlUrl}"/>
	   			          <div class="consForumBackToTopWr"><font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font> &nbsp;<a href="consForumBackToTop" class="consForumBackToTop">Haut de page</a>&nbsp; <font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font></div>
	   			         </div>
	                    </dsp:oparam>
	                  </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
   			    </c:when>
   			    <c:otherwise>
   			      <c:if test="${not empty defBannerPromoHtmlUrl}">
   			       <div class="consForum">
	   			    <c:import charEncoding="utf-8" url="${staticContentPath}${defBannerPromoHtmlUrl}"/>
	   			    <div class="consForumBackToTopWr"><font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font> &nbsp;<a href="consForumBackToTop" class="consForumBackToTop">Haut de page</a>&nbsp; <font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font></div>
	   			   </div>
	   			  </c:if>
   			    </c:otherwise>
   			  </c:choose> 
	          
	        		</div>
				</div>
			   </div>
			  </div>
			</div>  	
	      </div>
	      
	      
	        <div class="consForumPopUpWr">
        		<div class="consForumPopUpBox">
         			<a href="#" class="consForumCloseButton"><!-- --></a>
          			<h4><span><fmt:message key="msg.popuplv.head" /></span></h4> 
    		        <ul class="consForumPopUpItemWr clearFix">
    		        
	    		        <dsp:droplet name="RQLQueryForEach">
				          <dsp:param name="queryRQL" value="ALL ORDER BY tagCatNumber" />
						  <dsp:param name="repository" bean="ProductCatalog" />
						  <dsp:param name="itemDescriptor" value="castoDocFilterTagCategory" />
						  <dsp:param name="elementName" value="ftCategory" />
						  <dsp:param name="sortProperties" value="" />
						  <dsp:oparam name="output">
						   
						    <dsp:getvalueof var="ftCatId" param="ftCategory.repositoryId" />
						   
					        <dsp:droplet name="RQLQueryForEach">
							  <dsp:param name="queryRQL" value="tagCategory=${ftCatId} ORDER BY tagNumber" />
							  <dsp:param name="repository" bean="ProductCatalog" />
							  <dsp:param name="itemDescriptor" value="castoDocFilterTag" />
							  <dsp:param name="elementName" value="currentfilterTag" />
							  <dsp:param name="sortProperties" value="" />
							  <dsp:oparam name="outputStart">
							  
							     <li>
								    
								    <dsp:droplet name="IsEmpty">
						                <dsp:param name="value" param="ftCategory.tagCatImage" />
						                <dsp:oparam name="false"> 
						                  <img src='<dsp:valueof param="ftCategory.tagCatImage.url" />'/>						                  
						                </dsp:oparam>
						            </dsp:droplet>
								    <h5><dsp:valueof param="ftCategory.tagCatTitle" /></h5>
								    
								    <div class="consForumSelectWr">
									      <select name="filtersSelect_${ftCatId}" id="filtersSelect_${ftCatId}" onchange="location.href=this.options[this.selectedIndex].value;">
									        
									        <option value="" selected disabled><fmt:message key="msg.popuplv.select"/></option>
							  
							  </dsp:oparam>
							  <dsp:oparam name="output">
							    <dsp:getvalueof var="tagId" param="currentfilterTag.repositoryId" />
							    <dsp:droplet name="CastFilterLinkDroplet">
							      <dsp:param name="filterId" value="${tagId}"/>
							      <dsp:oparam name="output">
							        <dsp:getvalueof var="url" param="url"/>
							    	<option value="${pageContext.request.contextPath}${url}"><dsp:valueof param="currentfilterTag.tagTitle" /></option>
							      </dsp:oparam>
							    </dsp:droplet>
							  </dsp:oparam>
							  <dsp:oparam name="outputEnd">
							     </select>
	                			</div>
							   
							   </li> 
							  </dsp:oparam>
							</dsp:droplet>
							         
						  </dsp:oparam>
						</dsp:droplet>
            			
          			</ul>
        		</div>
      		</div>
	    </div> 
	      
	      <c:choose>
          	<c:when test="${not empty selectedFilterId}">
	          <dsp:getvalueof var="selectedFilterTag" param="selectedFilter.tagTitle" />
	      	  <c:set var="omniturePageName" value="Lancez vous:${selectedFilterTag}" scope="request"/>
	        </c:when>
	        <c:otherwise>
	      	  <c:set var="omniturePageName" value="Lancez vous main" scope="request"/>
	        </c:otherwise>
	      </c:choose>
	      <c:set var="omnitureChannel" value="Lancez vous" scope="request"/>
	    </jsp:attribute>
	    <jsp:attribute name="canonicalUrl">
	      <dsp:getvalueof var="selectedFilterId" param="filterTag" />
	      <c:if test="${not empty selectedFilterId}">
	      <dsp:importbean bean="/com/castorama/droplet/CanonicalLinkDroplet"/>
	      <dsp:droplet name="CanonicalLinkDroplet">
	        <dsp:param name="type" value="cfFilterTag"/>
	        <dsp:oparam name="output">
	          <dsp:getvalueof var="canonicalUrl" param="url"/>
	        </dsp:oparam>
	      </dsp:droplet>
	      ${canonicalUrl}
	      </c:if>
	    </jsp:attribute>
	  </cast:pageContainer>	  
</dsp:page>
