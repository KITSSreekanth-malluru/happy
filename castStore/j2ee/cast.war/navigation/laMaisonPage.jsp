<cast:pageContainer>
	<jsp:attribute name="bodyContent">
   
   <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
   <dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet" />
   <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
   <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
    
  <dsp:include page="/castCatalog/breadcrumbsCollector.jsp" >       
    <dsp:param name="categoryId" param="categoryId"/>       
    <dsp:param name="navAction" param="navAction"/>
    <dsp:param name="navCount" param="navCount"/>
  </dsp:include>
  
  <dsp:include page="/castCatalog/breadcrumbs.jsp" flush="true">
    <dsp:param name="categoryId" param="categoryId" /> 
  </dsp:include>
	
	<div class="customContentPage">
	
		<div class="advertiseBlocks">
			<c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/laMaisonHead.html"/>
		</div>
	
		<div class="content width800">
			<c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/laMaisonBody.html"/>
		</div>
	
		<div class="rightColumn">
			<div class="rightNavigationArea brownPage">
				<div class="menuPoint open">
					<h2 onclick="shuffleMenu(this)" class="title"><fmt:message key="laMaison.page.products"/></h2>
					<div class="content">
						<ul class="separatedLi">
							<dsp:droplet name="/com/castorama/droplet/LaMaisonDroplet">
								<dsp:param name="categoryId" param="categoryId"/>
								<dsp:oparam name="output">				
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="children"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="name" param="element.displayName"/>
											<dsp:getvalueof var="children" param="element.fixedChildCategories" />
											<dsp:droplet name="CastCategoryLinkDroplet">
												<dsp:param name="categoryId" param="element.repositoryId" />
												<dsp:param name="navCount" bean="CatalogNavHistory.navCount" />
												<dsp:param name="navAction" value="push" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="url" param="url" />
												</dsp:oparam>
											</dsp:droplet>
											<li>
												<dsp:a href="${contextPath}${url}">${name}</dsp:a>
												<br>
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" param="element.fixedChildCategories"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="count" param="count"/>
														<dsp:getvalueof var="size" param="size"/>
														<dsp:getvalueof var="display" param="element.displayName"/>
														<c:out value="${display}"/><c:if test="${count != size}">, </c:if> 
													</dsp:oparam>
												</dsp:droplet>
											</li>			
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>	
					      	</dsp:droplet>
						</ul>
					</div>
				</div>                                 
			</div>
		</div>
		
	</div>
        
	</jsp:attribute>
</cast:pageContainer>