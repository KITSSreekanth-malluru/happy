<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>

  <dsp:getvalueof var="fromSVContext" param="fromSVContext" />
  <dsp:getvalueof var="prodId" param="prodId" />
  <dsp:getvalueof var="skuId" param="skuId" />

	<cast:pageContainer>
		<jsp:attribute name="bodyContent">
		<dsp:getvalueof var="departamentId" param="departamentId"/>	
		<dsp:getvalueof var="regionId" param="regionId"/>

		<c:if test="${not empty departamentId }">
			<dsp:droplet name="/com/castorama/droplet/DepartamentLookupDroplet">
				<dsp:param name="id" param="departamentId" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="regionIdFromDep" param="element.region.id" />
				</dsp:oparam>
			</dsp:droplet>
		  </c:if>
           <dsp:include page="includes/magasinBreadcrumb.jsp">
             <dsp:param name="magasinId" param="magasinId"/>
             <dsp:param name="regionId" param="regionId"/>
             <dsp:param name="departamentId" param="departamentId"/>
           </dsp:include>

            <div class="content width800">
            
	            <div class="votre-magasin">
	            	<c:if test="${empty regionIdFromDep }">
	            		<dsp:getvalueof var="regionIdFromDep" value="${regionId}"/>
	            	</c:if>
		        	<p><strong><fmt:message key="magasin.search.castorama" /></strong></p>    

		            <img src="${pageContext.request.contextPath}/images/header-selection-magasin.gif" alt="Je sélectionne mon magasin" class="karte-header" />		            
		            
		            <div class="karte-left">
		            	<%@ include file="magasinDynamicStyles.jspf"%>
		            	<p><strong><fmt:message key="magasin.search.click.map" /></strong></p>
		            	
		            	<div class="karte-region">
							
							<dsp:getvalueof var="query" value="entite.adresse.departement.numero != 999"/>
							<c:if test="${not empty regionIdFromDep}">
								<dsp:getvalueof var="query" value="entite.adresse.departement.region.id = ${regionIdFromDep}"/>
							</c:if>						
							<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
									<dsp:param name="queryRQL" value="${query}" />
									<dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
									<dsp:param name="itemDescriptor" value="magasin" />
									<dsp:param name="elementName" value="magasinRQL" />
									<%-- gere en dur, ne doit pas arriver ! --%>
									<dsp:oparam name="empty">
										Probl&egrave;me lors du chargement de la r&eacute;gion.
									</dsp:oparam>
									<%-- Affichage de la carte de la region --%>
									<dsp:oparam name="outputStart">	
										<dsp:getvalueof var="region" param="magasinRQL.entite.adresse.departement.region.nom"/>											
										<dsp:getvalueof var="img" param='magasinRQL.entite.adresse.departement.region.img'/>									
										<img src="${img }" class="karte-big" />
									</dsp:oparam><%-- outputStart --%>
									<dsp:oparam name="output">
									
										<div id="magasin_<dsp:valueof param='magasinRQL.id'/>">
											
												<a href="magasin-fiche.jsp?magasinId=<dsp:valueof param='magasinRQL.id'/>&regionId=${regionId}&departamentId=${departamentId}&fromSVContext=${fromSVContext}">
													<!-- Bug du 20/09/2006 - 0000018 - minor -->
													<%-- <dsp:valueof param='magasinRQL.entite.adresse.ville' /> --%>
													<dsp:valueof param='magasinRQL.nom' />
												</a>
											
										 </div>
									</dsp:oparam><%-- output --%>															
								</dsp:droplet>
							
							<div class="karte-small">
				            	<a href="${pageContext.request.contextPath}/magasin/magasin-carte-france.jsp?fromSVContext=${fromSVContext}">
									<img src="${pageContext.request.contextPath}/images/france_small.gif" 
										width="45" height="45" alt="Retour &agrave; la carte de France des magasins" />								
								</a>
								<a href="${pageContext.request.contextPath}/magasin/magasin-carte-france.jsp?fromSVContext=${fromSVContext}"><fmt:message key="magasin.back" /></a>
							</div>
										
		            	</div>
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
			<c:if test="${not empty departamentId }">
				<dsp:droplet name="/com/castorama/droplet/DepartamentLookupDroplet">
					<dsp:param name="id" param="departamentId" />
					<dsp:param name="elementName" value="departament" />
					<dsp:oparam name="output">		
						<dsp:getvalueof var="departamentName" param="departament.nom" />					
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
		   	<fmt:message var="omniturePageName" key="omniture.pageName.shops.area"/>
			<fmt:message var="omnitureChannel" key="omniture.channel.shops"/>
			    	
			<c:set var="omniturePageName" value="${omniturePageName}${departamentName}" scope="request"/>
			<c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
			<%-- Omniture params Section ends--%>
			
			
		</jsp:attribute>
	</cast:pageContainer>
</dsp:page>
