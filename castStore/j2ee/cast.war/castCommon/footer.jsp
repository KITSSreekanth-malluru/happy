<dsp:page>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/com/castorama/droplet/FooterInfoDroplet" />
    <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
    <dsp:importbean bean="/atg/commerce/catalog/ProductCatalog" />

    <dsp:getvalueof var="productId" param="productId"/>
    <dsp:getvalueof var="categoryId" param="categoryId"/>
    <dsp:getvalueof var="isRobot" value="false"/>

    <dsp:droplet name="/com/castorama/droplet/IsRobotDroplet">
        <dsp:oparam name="true">
            <dsp:getvalueof var="isRobot" value="true"/>
        </dsp:oparam>
    </dsp:droplet>

    <dsp:droplet name="CategoryLookup">
        <dsp:param name="id" param="categoryId" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="categoryName" param="element.displayName"/>
            <dsp:getvalueof var="pivotCategory" param="element.pivot"/>
        </dsp:oparam>
    </dsp:droplet>

    <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
    <dsp:getvalueof var="channelParam" param="omniture-channel"/>
    <c:choose>
        <c:when test="${fn:contains(requestURI, 'roductDetails.jsp') }">
            <dsp:getvalueof var="omnitureLocation" value="FicheProduit"/>
        </c:when>
        <c:when test="${fn:contains(requestURI, 'basicPivotCategoryTemplate.jsp') }">
            <dsp:getvalueof var="omnitureLocation" value="PageListe"/>
        </c:when>
        <c:when test="${fn:contains(requestURI, 'basicCategoryTemplate.jsp') }">
            <dsp:getvalueof var="omnitureLocation" value="PageCategorie"/>
        </c:when>
        <c:when test="${fn:contains(requestURI, 'searchResults.jsp') }">
            <dsp:getvalueof var="omnitureLocation" value="MoteurRecherche"/>
        </c:when>
        <c:when test="${channelParam == 'Home'}">
            <dsp:getvalueof var="omnitureLocation" value="HP"/>
        </c:when>
        <c:when test="${channelParam == 'Lancez vous'}">
            <dsp:getvalueof var="omnitureLocation" value="LancezVous"/>
        </c:when>
        <c:otherwise>
            <dsp:getvalueof var="omnitureLocation" value="Others"/>
        </c:otherwise>
    </c:choose>
    <div id="footer">
        <%-- Left Column --%>
        <div class="leftCol">
          <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange" >
    <dsp:param name="itemDescriptor" value="footerMenuItem" />
    <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog" />
    <dsp:param name="queryRQL" value="ALL ORDER BY position" />
   <dsp:param name="howMany" value="100" />

	  <dsp:oparam name="outputStart">
        <ul class="linkList footerLinksLeft lightBg">
		<dsp:droplet name="/com/castorama/droplet/WrapPageLinkDroplet">
                    <dsp:param name="id" value="espace-presse"/>
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="link" param="url"/>
                    </dsp:oparam>
         </dsp:droplet>
      </dsp:oparam>
	  <dsp:oparam name="outputEnd">
         </ul>
      </dsp:oparam>
      <dsp:oparam name="output">
              <dsp:getvalueof id="url" param="element.url" idtype="java.lang.String"/>
		<dsp:getvalueof id="isActiveLink" param="element.isActiveLink" idtype="java.lang.Boolean"/>
		<c:if test="${isActiveLink == 'true'}" > 
            <li> 
				${url}
			</li>
        </c:if> 
      </dsp:oparam>
	</dsp:droplet>
	
	<dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange" >
    <dsp:param name="itemDescriptor" value="nosServicesMenuItem" />
    <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog" />
    <dsp:param name="queryRQL" value="isActiveLink = true ORDER BY position" />
    <dsp:param name="howMany" value="100" />
	<dsp:oparam name="outputStart">
	 <h2><fmt:message key="footer.services"/></h2>
       <ul class="cyan nosServ">
      </dsp:oparam>
	  <dsp:oparam name="outputEnd">
         </ul>
      </dsp:oparam>
      <dsp:oparam name="output">
	<dsp:getvalueof id="url" param="element.url" idtype="java.lang.String"/>
	<dsp:getvalueof id="isActiveLink" param="element.isActiveLink" idtype="java.lang.Boolean"/>
       <c:if test="${isActiveLink == 'true'}" > 
               <dsp:getvalueof var="url" param="element.url"/>
           <li> 
				${url}
	  </li>
        </c:if> 
      </dsp:oparam>
	</dsp:droplet>
        </div>
        <%-- Right Column --%>
        <div class="rightCol">
            <div class="bottomLine" style="height: 16px;">
                <%@ include file="includes/footer-breadcrumbs.jspf" %>
            </div>

            <dsp:droplet name="FooterInfoDroplet">
                <dsp:param name="categoryId" param="categoryId" />
                <dsp:oparam name="output">
                    <dsp:droplet name="IsNull">
                        <dsp:param name="value" param="children" />
                        <dsp:oparam name="false">
                            <%-- CHILDREN --%>
                            <dsp:include page="/castCommon/includes/footer-column.jsp" flush="true">
                                <dsp:param name="elements" param="children"/>
                                <dsp:param name="totalCount" param="totalCount"/>
                                <dsp:param name="linesPerColumn" param="linesPerColumn"/>
                            </dsp:include>
                        </dsp:oparam>
                    </dsp:droplet>
                    <dsp:droplet name="IsNull">
                        <dsp:param name="value" param="siblings" />
                        <dsp:oparam name="false">
                            <%-- SIBLINGS --%>
                            <dsp:include page="/castCommon/includes/footer-column.jsp" flush="true">
                                <dsp:param name="elements" param="siblings"/>
                                <dsp:param name="totalCount" param="totalCount"/>
                                <dsp:param name="linesPerColumn" param="linesPerColumn"/>
                            </dsp:include>
                        </dsp:oparam>
                    </dsp:droplet>
                    <dsp:droplet name="IsNull">
                        <dsp:param name="value" param="topNavigationCategories" />
                        <dsp:oparam name="false">
                            <%-- TOP CATEGORIES --%>
                            <dsp:include page="/castCommon/includes/footer-column.jsp" flush="true">
                                <dsp:param name="elements" param="topNavigationCategories"/>
                                <dsp:param name="totalCount" param="totalCount"/>
                                <dsp:param name="linesPerColumn" param="linesPerColumn"/>
                            </dsp:include>
                        </dsp:oparam>
                    </dsp:droplet>
                </dsp:oparam>
            </dsp:droplet>
        </div>
    </div>
</dsp:page>