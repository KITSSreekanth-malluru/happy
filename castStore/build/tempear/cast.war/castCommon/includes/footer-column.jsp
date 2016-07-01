<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
	
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="topNavigationCategories" param="topNavigationCategories" />
	<dsp:getvalueof var="totalCount" param="totalCount" />
	<dsp:getvalueof var="linesPerColumn" param="linesPerColumn" />
	
	<c:set var="secondColumnIndex" value="${linesPerColumn}" />
	<c:set var="thirdColumnIndex" value="${linesPerColumn * 2 }" />
	<c:set var="lastColumnIndex" value="${linesPerColumn * 3 }" />
	<c:set var="modifiedOnThisIteration" value="false"/>
	
	<c:set var="bulletStyle" value="darkblue"/>
	<c:set var="lastColumnIsSet" value="false"/>
	<c:set var="lineIndex" value="1"/>		
	
	<div class="subCol lightBg">
	
		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="elements"/>
			<dsp:param name="elementName" value="topCategory"/>
			<dsp:oparam name="output">
			
			  <dsp:getvalueof var="topCatId" param="topCategory.repositoryId"/>
			  <c:if test="${topCatId != 'cat510004' && topCatId != 'cat510005'}" >
				
				 <%-- <dsp:droplet name="/com/castorama/droplet/FooterColorDroplet">
				    <dsp:param name="categoryId" param="topCategory.repositoryId"/>
				    <dsp:oparam name="output">
				       <dsp:getvalueof var="bulletStyle" param="style.listStyle"/> 
				    </dsp:oparam>
				</dsp:droplet> --%>
				
				<c:choose>							
					<c:when test="${totalCount <= 5}">
						<c:set var="lineIndex" value="${lineIndex + 1}" />
						<dsp:getvalueof var="children" param="topCategory.fixedChildCategories" />
						<h2 <c:if test="${empty children}"> class="mb9"</c:if>><dsp:valueof param="topCategory.displayName"/></h2>
						<c:choose>
							<c:when test="${lineIndex == 2}">
								</div><div class="subCol lightBg">
							</c:when>
							<c:when test="${lineIndex == 3}">
								</div><div class="subCol lightBg">
							</c:when>
							<c:when test="${lineIndex == 4}">
								</div><div class="subCol lastSubCol lightBg">
							</c:when>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${(lineIndex == lastColumnIndex)}">
								<c:if test="${not lastColumnIsSet}">
									</div><div class="subCol lastSubCol lightBg">
									<c:set var="lastColumnIsSet" value="true"/>
								</c:if>	
							</c:when> 
							<c:when test="${((lineIndex == secondColumnIndex) or (lineIndex == thirdColumnIndex))}">
								</div><div class="subCol lightBg">
							</c:when> 
						</c:choose>
						
						<dsp:getvalueof var="children" param="topCategory.fixedChildCategories" />
						<h2 <c:if test="${empty children}"> class="mb9"</c:if>><dsp:valueof param="topCategory.displayName"/></h2>
						<c:if test="${not empty children}">
							<ul class="${bulletStyle}">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="topCategory.fixedChildCategories"/>
									<dsp:param name="elementName" value="childCategory"/>
									<dsp:oparam name="output">
										<c:set var="lineIndex" value="${lineIndex + 1}" />
										<li>
											<dsp:getvalueof var="displayName" param="childCategory.displayName" />
											<dsp:droplet name="/com/castorama/droplet/SearchLinkDroplet">
												<dsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
												<dsp:param name="question" value="${displayName}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="url" param="url"/>
													<dsp:getvalueof var="searchLink" value="${url}"/>
												</dsp:oparam>
											</dsp:droplet>											
											<dsp:a href="${searchLink}?osearchmode=tagcloud">
												<dsp:valueof param="childCategory.displayName" />
											</dsp:a>
										</li>	
										<c:choose>
											<c:when test="${lineIndex == lastColumnIndex}">
												<c:if test="${not lastColumnIsSet}">
													</ul></div><div class="subCol lastSubCol lightBg"><ul class="${bulletStyle}">
													<c:set var="lastColumnIsSet" value="true"/>
												</c:if>
											</c:when>
											<c:when test="${(lineIndex == secondColumnIndex) or (lineIndex == thirdColumnIndex)}">
												</ul></div><div class="subCol lightBg"><ul class="${bulletStyle}">
											</c:when>
										</c:choose>
									</dsp:oparam>
								</dsp:droplet>
							</ul>
						</c:if>
						<c:set var="lineIndex" value="${lineIndex + 1}" />
						
					</c:otherwise>
				</c:choose>
				
			  </c:if>
			</dsp:oparam> 
		</dsp:droplet>
		
		<a class="arrowedLink darkBlue_whiteArrow" href="${pageContext.request.contextPath}/index/azIndex.jsp" title='<fmt:message key="top.menu.products"/>'><fmt:message key="footer.column.link.azIndex"/></a>
		<a class="arrowedLink darkBlue_whiteArrow" href="#"><fmt:message key="footer.column.link.return.top"/></a>
	</div>

</dsp:page>
