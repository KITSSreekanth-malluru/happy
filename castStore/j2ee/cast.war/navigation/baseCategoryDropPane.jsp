<dsp:page>

	<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/For"/>
	<dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>	
	<dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet"/>
	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof var="categoryId" param="categoryId" />
	
  <dsp:getvalueof var="isRobot" value="false"/>
  <dsp:droplet name="/com/castorama/droplet/IsRobotDroplet">
    <dsp:oparam name="true">
      <dsp:getvalueof var="isRobot" value="true"/>
    </dsp:oparam>           
  </dsp:droplet>

	<dsp:droplet name="/atg/dynamo/droplet/Cache">
		<dsp:param name="key" value="dropdown_${categoryId}_${isRobot}"/>
       	<dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
        <dsp:oparam name="output">
        	<dsp:droplet name="CategoryLookup">  
				<dsp:param name="id" param="categoryId"/>
				<dsp:oparam name="output">
					<div class="upperMenuPopup">	
						<div class="menuContainer">	
						
						    <dsp:getvalueof var="freeSpaceWidth" value="976" />
						    <dsp:getvalueof var="oneColumnWidth" value="242" />
						    
						    <dsp:getvalueof var="dropPanePromo" param="element.dropPanePromo" />
							<c:if test="${not empty dropPanePromo}">
							  <dsp:getvalueof var="dropPanePromoHtml" param="element.dropPanePromo.htmlUrl" />
							  <c:if test="${not empty dropPanePromoHtml}">
								<dsp:getvalueof var="dropPanePromoWidth" param="element.dropPanePromoWidth" />
								<c:if test="${dropPanePromoWidth > 720}">
								  <dsp:getvalueof var="dropPanePromoWidth" value="720" />
								</c:if>
								<dsp:getvalueof var="dropPanePromoHeight" param="element.dropPanePromoHeight" />
								<div class="megaMenuBanner" style="width: ${dropPanePromoWidth}px; height: ${dropPanePromoHeight}px;">
								  <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
								  <c:import charEncoding="utf-8" url="${staticContentPath}${dropPanePromoHtml}"/>
								</div>
								
								<dsp:getvalueof var="freeSpaceWidth" value="${freeSpaceWidth - dropPanePromoWidth}" />
							  </c:if>
							</c:if>

							<dsp:getvalueof var="childCategories" param="element.childCategories" />
							<dsp:getvalueof var="size" value="${fn:length(childCategories)}" />
							
							<dsp:getvalueof var="columnsNumber" value="${freeSpaceWidth / oneColumnWidth}" />
							<dsp:getvalueof var="columnsNumber" value="${fn:substringBefore(columnsNumber, '.')}" />
							
							<dsp:getvalueof var="categoriesPerColumn" value="${size / columnsNumber}" />
							<dsp:getvalueof var="categoriesPerColumn" value="${fn:substringBefore(categoriesPerColumn, '.')}" />
							
							<dsp:getvalueof var="columnWithAdditionalCatsNum" value="${size % columnsNumber}" />
							
							<dsp:getvalueof var="minNumberOfCategoriesPerColumn" param="element.minNumberOfCategoriesPerColumn" />
							<c:if test="${empty minNumberOfCategoriesPerColumn}">
							  <dsp:getvalueof var="minNumberOfCategoriesPerColumn" value="6" />
							</c:if>
							
							<dsp:getvalueof var="currCatNumInCurrCol" value="0" />
							<dsp:getvalueof var="currColNum" value="0" />
							<dsp:droplet name="ForEach">
								<dsp:param name="array" value="${childCategories}"/>
								<dsp:oparam name="outputStart">
								  <ul>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
								  </ul>
								</dsp:oparam>
								<dsp:oparam name="output">
								
								  <c:choose>
								  <c:when test="${minNumberOfCategoriesPerColumn > categoriesPerColumn}">
								    <c:if test="${currCatNumInCurrCol >= minNumberOfCategoriesPerColumn}">
										<dsp:getvalueof var="currCatNumInCurrCol" value="0" />
										<dsp:getvalueof var="currColNum" value="${currColNum + 1}" />
										
										</ul><ul>
									</c:if>
								  </c:when>
								  <c:otherwise>
								    <c:if test="${not ((currCatNumInCurrCol < categoriesPerColumn) or ((currCatNumInCurrCol == categoriesPerColumn) 
												and (currColNum < columnWithAdditionalCatsNum)))}">
										<dsp:getvalueof var="currCatNumInCurrCol" value="0" />
										<dsp:getvalueof var="currColNum" value="${currColNum + 1}" />
										
										</ul><ul>
									</c:if>
								  </c:otherwise>
								  </c:choose>
									
									<dsp:getvalueof var="currCatNumInCurrCol" value="${currCatNumInCurrCol + 1}" />
									
									<dsp:droplet name="CastCategoryLinkDroplet">
										<dsp:param name="categoryId" param="element.repositoryId"/>	
										<dsp:param name="navCount" bean="/Constants.null"/>
										<dsp:param name="navAction" value="jump"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="url" param="url"/>
											<li>
												<dsp:a href="${contextPath}${url}&wrap=true">
													<dsp:valueof param="element.displayName"/>																				
												</dsp:a>
											</li>
										</dsp:oparam>
									</dsp:droplet>
									
								</dsp:oparam>
							</dsp:droplet>

						</div>
					</div>
		  		</dsp:oparam>
			</dsp:droplet>
        </dsp:oparam>
    </dsp:droplet>	
</dsp:page>	
