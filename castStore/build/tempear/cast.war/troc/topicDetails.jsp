<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductCatalog" />

  <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange" >
    <dsp:param name="itemDescriptor" value="fastLabConfigs" />
    <dsp:param name="repository" bean="ProductCatalog" />
    <dsp:param name="queryRQL" value="ALL" />
    <dsp:param name="howMany" value="1" />
    <dsp:param name="sortProperties" value="" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="enableNewConseilsAndForums" param="element.enableNewCF" />
    </dsp:oparam>
  </dsp:droplet>
  
  <c:choose>
    <c:when test="${not empty enableNewConseilsAndForums and enableNewConseilsAndForums}">
      <dsp:droplet name="/com/castorama/droplet/PermanentRedirectDroplet">
        <dsp:param name="url" value="../conseils-et-forums.jsp" />
      </dsp:droplet>
    </c:when>
    <c:otherwise>

  <cast:pageContainer>
    <jsp:attribute name="cloudType">page</jsp:attribute>
    <jsp:attribute name="bodyContent">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/lancez-vous.css"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
    <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
    <dsp:droplet name="/com/castorama/droplet/ThematiqueLookup">
      <dsp:param name="thematiqueId" param="topicId"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="them" param="thematique"/>

        <dsp:include page="/castCatalog/breadcrumbsCollector.jsp" >
          <dsp:param name="topicId" param="topicId"/>
          <dsp:param name="navAction" value="push"/>
        </dsp:include>

        <dsp:include page="/castCatalog/breadcrumbs.jsp" flush="true">
          <dsp:param name="navAction" value="push"/>
          <dsp:param name="topicId" param="thematique"/>
        </dsp:include>

        <div class="background">
          <div class="themesBlockContainer" >
            <dsp:include page="includes/left_menu.jsp"/>
            <dsp:include page="includes/lancezVousTopicInfo.jsp">
              <dsp:param name="topic" value="${them}"/>
            </dsp:include>
          </div>
          <div class="content width800">
            <dsp:droplet name="IsEmpty">
              <dsp:param name="value" param="thematique.topDocumentsList"/>
              <dsp:oparam name="false">
           	 <div class="lv_documents_area">
                  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                    <dsp:param name="array" param="thematique.topDocumentsList"/>
                    <dsp:setvalue param="doc" paramvalue="element"/>
                    <dsp:oparam name="outputStart">
                      <div class="documents_container bottom_border">
                        <div class="top_doc_header">
                          <div class="top_doc_title"><dsp:valueof param="thematique.topDocsTitle"/></div>
                          <div class="top_doc_description"><dsp:valueof param="thematique.topDocsDescription"/></div>
                        </div>
                        <div class="top_documents_body">
                    </dsp:oparam>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="doc" param="doc"/>
                      <dsp:getvalueof var="docImageUrl" param="doc.image.url"/>
                      <dsp:getvalueof var="docTitle" param="doc.title"/>
                      <div class="document_item_container">
                        <dsp:droplet name="/com/castorama/droplet/CastDocumentLinkDroplet">
                          <dsp:param name="documentId" param="doc.repositoryId"/>
                          <dsp:param name="navAction" value="push"/>
                          <dsp:param name="navCount" bean="CatalogNavHistory.navCount" />
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="url" param="url"/>
                              <c:if test="${fn:startsWith(url, 'http') == false}">
                                  <dsp:getvalueof var="url" value="${pageContext.request.contextPath}${url}"/>
                              </c:if>
                              <a href="${url}">
                                <div class="doc_image"><img src="${pageContext.request.contextPath}/..${docImageUrl}"/></div>
                                <div class="doc_title">${docTitle}</div>
                                <div class="doc_text"><fmt:message key="troc.topicDetails.voir" /></div>
                              </a>
                          </dsp:oparam>
                        </dsp:droplet>
                      </div>
                    </dsp:oparam>
                    <dsp:oparam name="outputEnd">
                      </div>
                      </div>
                    </dsp:oparam>
                  </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
            <dsp:droplet name="IsEmpty">
              <dsp:param name="value" param="thematique.bottomDocumentsList"/>
              <dsp:oparam name="true">
			<dsp:droplet name="IsEmpty">
              		<dsp:param name="value" param="thematique.topDocumentsList"/>
				<dsp:oparam name="false">
					</div>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="false">
			<dsp:droplet name="IsEmpty">
              		<dsp:param name="value" param="thematique.topDocumentsList"/>
				<dsp:oparam name="true">
					<div class="lv_documents_area">
				</dsp:oparam>
			</dsp:droplet>
                  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                    <dsp:param name="array" param="thematique.bottomDocumentsList"/>
                    <dsp:setvalue param="doc" paramvalue="element"/>
                    <dsp:oparam name="outputStart">
                      <div class="documents_container">
                        <div class="top_doc_header">
                          <div class="top_doc_title"><dsp:valueof param="thematique.bottomDocsTitle"/></div>
                          <div class="top_doc_description"><dsp:valueof param="thematique.bottomDocsDescription"/></div>
                        </div>
                        <div class="top_documents_body">
                    </dsp:oparam>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="doc" param="doc"/>
                      <dsp:getvalueof var="docImageUrl" param="doc.image.url"/>
                      <dsp:getvalueof var="docTitle" param="doc.title"/>
                      <dsp:droplet name="/com/castorama/droplet/CastDocumentLinkDroplet">
                        <dsp:param name="documentId" param="doc.repositoryId"/>
                        <dsp:param name="navAction" value="push"/>
                        <dsp:param name="navCount" bean="CatalogNavHistory.navCount" />
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="url" param="url"/>
                          <c:if test="${fn:startsWith(url, 'http') == false}">
                            <dsp:getvalueof var="url" value="${pageContext.request.contextPath}${url}"/>
                          </c:if>
                          <div class="document_item_container">
                            <a href="${url}">
                              <div class="doc_image"><img src="${pageContext.request.contextPath}/..${docImageUrl}"/></div>
                              <div class="doc_title">${docTitle}</div>
                              <div class="doc_text"><fmt:message key="troc.topicDetails.voir" /></div>
                            </a>
                          </div>
                        </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                    <dsp:oparam name="outputEnd">
                      </div>
                      </div>
                    </dsp:oparam>
                  </dsp:droplet>
		</div>
              </dsp:oparam>
            </dsp:droplet>
          </div>
          <div class="rightNavigationArea">
            <dsp:include  page="/castCatalog/includes/rightBannersPanelLV.jsp"/>
          </div>
        </div>
        <dsp:getvalueof var="topicTitle" param="thematique.title"/>
        <c:set var="omniturePageName" value="Lancez vous:${topicTitle}" scope="request"/>
        <c:set var="omnitureChannel" value="Lancez vous" scope="request"/>
        </dsp:oparam>
      </dsp:droplet>
    </jsp:attribute>
    <jsp:attribute name="canonicalUrl">
      <dsp:importbean bean="/com/castorama/droplet/CanonicalLinkDroplet"/>
      <dsp:droplet name="CanonicalLinkDroplet">
        <dsp:param name="type" value="topic"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="canonicalUrl" param="url"/>
        </dsp:oparam>
      </dsp:droplet>
      ${canonicalUrl}
    </jsp:attribute>
  </cast:pageContainer>
  
    </c:otherwise>
  </c:choose>
  
</dsp:page>
