<%@ taglib prefix="dsp"
  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>	
	<cast:pageContainer>
		<jsp:attribute name="bodyContent">
		<dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
		<dsp:importbean bean="/atg/targeting/TargetingRandom"/>
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
		<div class="content azcontent">
		
		<div class="breadcrumbs bluePage">
		    <div class="homeBreadIco">
		      	<a href="/store/home.jsp">
			        <img src="/store/images/icoHomeGray.gif" alt="" title=""/>
			    </a>
			</div>
			<div class="splitter">></div>
		    <div class="active"><fmt:message key="index.brand"/></div>
		</div>
		
		<div class="lightBg azSwitcher">
			<a href="azIndex.jsp" class="arrowedLink darkBlue_whiteArrow"><fmt:message key="index.product"/></a>
		</div>
		
		<%@include file="includes/brandsCategories.jspf" %>	

    <dsp:droplet name="/com/castorama/droplet/IndexedMapDroplet">
      <dsp:param name="indexedItem" value="marque" />                                   
      <dsp:oparam name="output">
        <dsp:include page="includes/index.jsp">
		      <dsp:param name="indexedMap" param="indexedMap"/>
		      <dsp:param name="indexedItem" value="marque" /> 
		    </dsp:include>
      </dsp:oparam>
    </dsp:droplet>
		
		<div class="rightSideBanners">
			<dsp:droplet name="TargetingRandom">
				<dsp:param name="targeter" bean="/atg/registry/RepositoryTargeters/ProductCatalog/BrandIndexPromo"/>
				<dsp:param name="fireViewItemEvent" value="false"/>
				<dsp:param name="howMany" value="15"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="url" param="element.url"/>
					<dsp:getvalueof var="title" param="element.title"/>
					<dsp:getvalueof var="mediaURL" param="element.image.url"/>
					
					<div class="bannerHolder">
						<a href="${url }"><dsp:img title="${title}" alt="${title}" src="${mediaURL}"/></a>
					</div>
		              
				</dsp:oparam>
			</dsp:droplet>

		</div>
		
		</jsp:attribute>
	</cast:pageContainer>
</dsp:page>