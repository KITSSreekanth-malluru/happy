<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>

  <dsp:getvalueof var="fromSVContext" param="fromSVContext" />
  <dsp:getvalueof var="prodId" param="prodId" />
  <dsp:getvalueof var="skuId" param="skuId" />

  <c:if test="${fromSVContext}">
    <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
      <dsp:param name="productId" value="${prodId}"/>
      <dsp:param name="navAction" value="jump"/>
      <dsp:param name="skuId" param="skuId"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="productURL" param="url"/>
      </dsp:oparam>
    </dsp:droplet>
    <dsp:getvalueof var="productURL" value="${pageContext.request.contextPath}${productURL}#stockArea"/>
    <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.backToProductPageURL" 
                  value="${productURL}" />
  </c:if>

	<cast:pageContainer>
		<jsp:attribute name="bodyContent">	
          	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
							
			<div class="breadcrumbs bluePage">
       	<div class="homeBreadIco"><a href="${pageContext.request.contextPath}">
       	<img src="${pageContext.request.contextPath}/images/icoHomeGray.gif" alt="" title="" /></a></div>
           <div class="splitter">&gt;</div>
        <div class="active">France</div>					
       </div>
	            
            <div class="content width800">
            
	            <div class="votre-magasin">
	            
		        	<p><strong><fmt:message key="magasin.search.castorama" /></strong></p>    
		            
		            <img src="${contextPath }/images/header-selection-magasin.gif" alt="Je sélectionne mon magasin" class="karte-header" />
		            
		            
		            <div class="karte-left">
		            	<p><strong><fmt:message key="magasin.search.click.map" /></strong></p>
		            	<%@ include file="/magasin/francemap.jspf" %>
		            </div>
		           
		           <%@ include file="/magasin/includes/magasin-search-block.jspf" %>
		            
		            
				</div>
	            </div>
			
			<div class="rightColumn">
			<dsp:include page="/magasin/includes/magasinTargeter.jsp" flush="true">
				<dsp:param name="magasinPagePromoBean" bean="/atg/registry/Slots/MagasinSlot"/>
				<dsp:param name="name" value="banner"/>
			</dsp:include>
			</div>
			
			<%-- Omniture params Section begins--%>
		   	<fmt:message var="omniturePageName" key="omniture.pageName.shops.home"/>
			<fmt:message var="omnitureChannel" key="omniture.channel.shops"/>
			    	
			<c:set var="omniturePageName" value="${omniturePageName}" scope="request"/>
			<c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
			<%-- Omniture params Section ends--%>
			
		</jsp:attribute>
	</cast:pageContainer>
</dsp:page>
