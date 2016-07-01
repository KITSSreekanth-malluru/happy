<%@ page contentType="text/html; charset=UTF-8"%>
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
        <dsp:param name="url" value="conseils-et-forums.jsp" />
      </dsp:droplet>
    </c:when>
    <c:otherwise>
      <cast:pageContainer>
	    <jsp:attribute name="cloudType">page</jsp:attribute>
	    <jsp:attribute name="bodyContent">
	      <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/lancez-vous.css"/>
	      <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
	
	
	      <dsp:setvalue value="${null}" bean="CatalogNavHistory.navHistory"/>
	      <dsp:include page="/castCatalog/breadcrumbs.jsp" flush="true">
	        <dsp:param name="fromLV" value="true"/>
	        <dsp:param name="navAction" value="push"/>
	      </dsp:include>
	
	      <div class="background">
	        <div class="themesBlockContainer">
	          <dsp:include page="troc/includes/left_menu.jsp"/>
	          <dsp:include page="troc/includes/pictureMenu.jsp"/>
	        </div>
	          <dsp:include page="troc/includes/bottomBanner.jsp"/>
	      </div>
	      <c:set var="omniturePageName" value="Lancez vous main" scope="request"/>
	      <c:set var="omnitureChannel" value="Lancez vous" scope="request"/>
	    </jsp:attribute>
	  </cast:pageContainer>
    </c:otherwise>
  </c:choose>
  
</dsp:page>
