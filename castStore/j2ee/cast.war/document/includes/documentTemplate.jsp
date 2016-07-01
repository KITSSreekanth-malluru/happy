<dsp:page>

  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
  

  
  <dsp:getvalueof var="displayBanner" vartype="java.lang.Boolean" bean="/com/castorama/CastConfiguration.displayAdviceBanners" />

  <c:if test="${not empty displayBanner && displayBanner}">
	  <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
	    <dsp:param name="id" param="documentId"/>
	    <dsp:param name="elementName" value="document"/>
	    <dsp:param name="itemDescriptor" value="castoramaDocument"/>
	    <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog"/>
	    <dsp:oparam name="output">
	      <dsp:getvalueof var="promoTemplate" param="document.promoTemplate" />
	      <c:if test="${not empty promoTemplate}">
			    <dsp:getvalueof var="layoutType" param="document.promoTemplate.layoutType" />
			    <dsp:include page="../../castCatalog/includes/catalogPromoTemplates/${layoutType}.jsp" flush="true">
			      <dsp:param name="promoInformation" param="document.promoTemplate.promoInformation" />
			    </dsp:include> 
			    <br /> 
	      </c:if>    
	    </dsp:oparam>
	  </dsp:droplet>
  </c:if>
 
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/util/ProductTabs"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:getvalueof var="documentId" param="documentId"/>
  
  
  <div class="clear"></div>
  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
    <div class="carouselV2">
      <dsp:param name="value" param="productId"/>
      <dsp:oparam name="true"> 
        <div id="carousel" class="carousel carThumbsContainer">
          <dsp:include page="/castCatalog/carouselMotherCategory.jsp">
            <dsp:param name="documentId" param="documentId"/>
          </dsp:include>
        </div>
      </dsp:oparam>
      <dsp:oparam name="false">
        <dsp:include page="/document/includes/documentProduct.jsp">
          <dsp:param name="productId" param="productId"/>
        </dsp:include>
      </dsp:oparam>
     </div>
    </dsp:droplet>
      <c:choose>
	    <c:when test="${!isSearchResult}">
	      <dsp:include page="/castCatalog/breadcrumbsCollector.jsp" >
	        <dsp:param name="documentId" param="documentId"/>
	        <dsp:param name="navAction" param="push"/>
	      </dsp:include>
	    
	      <dsp:include page="/castCatalog/breadcrumbs.jsp" flush="true">
	        <dsp:param name="documentId" param="documentId"/>
	        <dsp:param name="navAction" param="navAction"/>
	        <dsp:param name="navCount" param="navCount"/>
	      </dsp:include>
	    </c:when>
      </c:choose>

	<div class="content">
  	 <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
      <dsp:param name="id" param="documentId"/>
      <dsp:param name="elementName" value="document"/>
      <dsp:param name="itemDescriptor" value="castoramaDocument"/>
      <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog"/>
      <dsp:oparam name="output">
	    <dsp:getvalueof var="relativeURL" param="document.relativeURL"/>
        <c:if test="${not empty relativeURL }">
          <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
          <c:import url="${staticContentPath}${relativeURL}" charEncoding="utf-8"/>
        </c:if>
      </dsp:oparam>
      </dsp:droplet>
    </div>

    <fmt:message var="review" key="castCatalog_tabNames.review" />
    <dsp:setvalue bean="ProductTabs.tabs.ratingCommentsTab.page" value="ratingCommentsTab.jsp" />
    <dsp:setvalue bean="ProductTabs.tabs.ratingCommentsTab.title" value="${review}" />
    <dsp:setvalue bean="ProductTabs.tabs.ratingCommentsTab.visible" value="false" />
    <%@ include file="/castCatalog/includes/productTabbedPane.jspf" %>
  	
</dsp:page>