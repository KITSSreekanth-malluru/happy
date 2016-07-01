<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>
<dsp:importbean	bean="/com/castorama/magasin/CastMagasinItineraireFormHandler" />
<dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" idtype="java.lang.String"/>
<dsp:getvalueof var="parent" param="parent"/>
<dsp:getvalueof var="magasinId" param="magasinId" />

<dsp:droplet name="/com/castorama/droplet/MagasinLookupDroplet">
		<dsp:param name="id" param="magasinId" />
		<dsp:param name="elementName" value="magasin" />
		<dsp:oparam name="output">
		   <dsp:getvalueof var="parent" param="parent"/>
		   <dsp:getvalueof var="magasinNom" param="magasin.nom" scope="request"/>
		   <dsp:getvalueof var="departamentName" param="magasin.entite.adresse.departement.nom" />
		   <dsp:getvalueof var="departamentId" param="magasin.entite.adresse.departement.numero" />
		   <dsp:getvalueof var="regionId" param="magasin.entite.adresse.departement.region.id" />  
             <dsp:getvalueof var="fromSVContext" param="fromSVContext" />
       <c:choose>
        <c:when test="${parent == 'clientspace' }">
           		
          <div class="content clientspace">
           		
        	 <dsp:getvalueof var="brElement" value="header.my.magasin"  scope="request"/>
        	 <dsp:include page="/user/includes/breadcrumbsClient.jsp">
					   <dsp:param name="element" value="${brElement}"/>
						 <dsp:param name="name" value="${magasinNom}" />						
					 </dsp:include>

		      <dsp:include page="/user/clientMenu.jsp">
            <dsp:param name="currPage" value="magasin"/>
          </dsp:include>
        </c:when>
	           		
	      <c:when test="${parent == 'search' }">
              
          <div class="content ">
              
            <dsp:include page="magasinBreadcrumb.jsp">
              <dsp:param name="magasinId" param="magasinId"/>
              <dsp:param name="regionId" value="${regionId}"/>                   
            </dsp:include>
                 
         </c:when>
           		
	       <c:otherwise>
	           		
      		<div class="content ">
      		
      		<dsp:include page="magasinBreadcrumb.jsp">
      		  <dsp:param name="magasinId" param="magasinId"/>
				    <dsp:param name="regionId" param="regionId"/>
				    <dsp:param name="departamentId" param="departamentId"/>
      		</dsp:include>
			           	
	       </c:otherwise>
       </c:choose>       	

			
       <div class="votre-magasin">
          
        <div class="productBlock">
          <div class="productImageColumn">
            <div class="productImage">
           	  <dsp:getvalueof var="photo" param="magasin.url_photo" />                          	
              <dsp:img src="${photo}" width="291" height="250" id="lrgImg" onload="jQuery(this).css('visibility', 'visible')"/>
           	</div>
           	<div class="imageViews">
           	  <h3><fmt:message key="castCatalog_productDetails.view_description" /></h3>
           		<ul>	            				
           		 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
						    <dsp:param name="array" param="magasin.listimageaux"/>
						    <dsp:param name="elementName"	value="magasinImage"/>
						    <dsp:oparam name="output">
						      <dsp:getvalueof var="index" param="index"/>
						      <c:if test="${index < 6}">
						        <dsp:getvalueof var="smallImage" param="magasinImage.small_image_url"/>
						        <dsp:getvalueof var="largeImage" param="magasinImage.large_image_url"/>
						        <c:choose>
						  	     <c:when test="${index==5 }">
						  	       <li class="last">
						  	     </c:when>
						  	     <c:otherwise>
						  	       <li>
						  	     </c:otherwise>
						        </c:choose>
                    
                    <dsp:getvalueof var="count" param="count"/>
                    <c:choose> 
                      <c:when test="${count == 1}">
                        <a href="javascript:void(0)" title="Autres vues" onclick="changeMagasinImage(this)" class="lImBorder grayBorder">
                          <img src="${largeImage}" style="display:none" id="${index }"/>
                          <img src="${smallImage}" width="43" height="43" alt="Autres vues"  title="Autres vues"/>
                        </a>
                      </c:when>
                      <c:otherwise>
                        <a href="javascript:void(0)" title="Autres vues" onclick="changeMagasinImage(this)" class="grayBorder">
                          <img src="${largeImage}" style="display:none" id="${index }"/>
                          <img src="${smallImage}" width="43" height="43" alt="Autres vues"  title="Autres vues"/>
                        </a>
                      </c:otherwise>
                    </c:choose>
							     </li>
							    </c:if>
						    </dsp:oparam>
						  </dsp:droplet>
           		</ul>
           	</div>
           	<ul class="nvLinks">  
              <li class="impri"><img title="" alt="" src="${pageContext.request.contextPath}/images/blank.gif"/>                    		
                <dsp:getvalueof var="magasinId" param="magasinId"/>
                <fmt:message key="castCatalog_label.print" var="imprimer"/>
                <dsp:a href="${pageContext.request.contextPath}/magasin/magasin-fiche-print.jsp?magasinId=${magasinId}" target="_blank" iclass="imprimer" title="${imprimer}">${imprimer}</dsp:a>
              </li>
            </ul>
           </div>
           		
           <div class="productContent">           			
            <h1><c:out value="${magasinNom }"  escapeXml="false"/></h1>
           		
           	<div class="productDecription">
           	  <%@include file="magasin-info.jspf" %>
           				
           		<c:choose>
           		 <c:when test="${parent == 'clientspace' }">
           		   <a href="${pageContext.request.contextPath}/magasin/magasin-carte-france.jsp" class="buttonChangeMag"></a>
           		 </c:when>
           		 <c:otherwise>
				     <dsp:droplet name="/atg/dynamo/droplet/Compare">
				      <dsp:param bean="/atg/userprofiling/Profile.securityStatus" name="obj1"/>
				      <dsp:param bean="/atg/userprofiling/PropertyManager.securityStatusLogin" name="obj2"/>
				       <dsp:oparam name="lessthan">
					  	  <form action="${pageContext.request.contextPath}/user/login.jsp?magasinId=${magasinId}" method="post" id="login">						    		
					    	  <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/clientSpaceHome.jsp" />							    		     
						    <input type="button" class="buttonSelMag" value=""
                                      onclick="javascript:goToURL('${pageContext.request.contextPath}/user/login.jsp?magasinId=${magasinId}');" />
						    <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.magasinId" value="${magasinId}" />
                              <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.storeId" paramvalue="magasin.storeId" />  
                              
                              <c:if test="${fromSVContext}">
                                <dsp:getvalueof var="backToProdLink" bean="/atg/userprofiling/SessionBean.values.backToProductPageURL" />
                                <input type="button" class="checkStockLevelLink"
                                      onclick="javascript:goToURL('${backToProdLink}');" />
                              </c:if>
						  
						  </form>
				       </dsp:oparam>
				       <dsp:oparam name="default">
                    		<dsp:form action="magasin-fiche.jsp" method="post" formid="favStore">
						  	  <dsp:input type="hidden" bean="CastNewsletterFormHandler.value.accesPartenairesCasto" value="true" />
	 				  		  <dsp:input type="hidden" bean="CastNewsletterFormHandler.prefStore" paramvalue="magasinId"/>
  							  <dsp:input type="hidden" bean="CastNewsletterFormHandler.value.email" beanvalue="Profile.login" />
							  <dsp:input type="hidden" bean="CastNewsletterFormHandler.repositoryId" beanvalue="Profile.login" />
							  <dsp:input bean="CastNewsletterFormHandler.updateErrorURL" type="hidden" value="${requestURI}" />
							  <dsp:input bean="CastNewsletterFormHandler.updateSuccessURL"  type="hidden" value="${pageContext.request.contextPath}/user/clientSpaceHome.jsp" />									  			
           					  <dsp:input type="submit" iclass="buttonSelMag" bean="CastNewsletterFormHandler.updateMagasin" value=""/>
           					  
           					  <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.magasinId" value="${magasinId}" />
                              <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.storeId" paramvalue="magasin.storeId" />  
                              
                              <c:if test="${fromSVContext}">
                                <dsp:getvalueof var="backToProdLink" bean="/atg/userprofiling/SessionBean.values.backToProductPageURL" />
                                <input type="button" class="checkStockLevelLink" 
                                      onclick="javascript:goToURL('${backToProdLink}');" />
                              </c:if>
                              
           					</dsp:form>
       				   </dsp:oparam>
                     </dsp:droplet>
           		 </c:otherwise>
           		</c:choose>	 
           		
           		<%@include file="magasin-news.jspf" %>           				
           					            				
						  <%@include file="magasin-service.jspf" %>
           	
           		<div class="magComment">
           		 <div class="mapImage">
           		   <dsp:getvalueof var="imgplan" param="magasin.imgplan" />                            	
                 <img src="${imgplan}" width="230" height="205" />
           					
           		   <div class="imgControlsBlock">									
								   <dsp:getvalueof var="pdfplan" param="magasin.pdfplan" />									
									 <c:if test='${not empty pdfplan && fn:contains(pdfplan, ".pdf")}'>
									   <div class="controlButton"><a class="controlPDF" title="PDF" href="${pdfplan }" target="_blank" >PDF</a></div>
									 </c:if>
								</div>
           		 </div>
           		 <div class="rightColumn">
           			<h2><fmt:message key="magasin.way.to.shop" /></h2>
								<%@include file="magasin-map-info.jspf" %>
            		<dsp:form formid="way" action="magasin-fiche.jsp" method="post">
            		  <p><fmt:message key="magasin.calculate.way" /></p>
            			<fieldset>            						
            			 <dsp:input type="hidden" bean="CastMagasinItineraireFormHandler.magasinId"	paramvalue="magasinId" /> 
										<c:choose>
											<c:when test="${parent == 'clientspace' }">
										 		<dsp:input type="hidden" bean="CastMagasinItineraireFormHandler.itineraireSuccessUrl" value="${pageContext.request.contextPath}/magasin/magasin-fiche.jsp?error=itineraire&parent=clientspace" /> 
												<dsp:input type="hidden" bean="CastMagasinItineraireFormHandler.itineraireErrorUrl" value="${pageContext.request.contextPath}/magasin/magasin-fiche.jsp?parent=clientspace" />
										 	</c:when>
										 	<c:otherwise>
										 		<dsp:input type="hidden" bean="CastMagasinItineraireFormHandler.itineraireSuccessUrl" value="${pageContext.request.contextPath}/magasin/magasin-fiche.jsp?error=itineraire" /> 
												<dsp:input type="hidden" bean="CastMagasinItineraireFormHandler.itineraireErrorUrl" value="${pageContext.request.contextPath}/magasin/magasin-fiche.jsp" />
										 	</c:otherwise>
										</c:choose>
            				<dsp:input id="rue" type="text" bean="CastMagasinItineraireFormHandler.rue"/> 
										<dsp:input id="ville" type="text" bean="CastMagasinItineraireFormHandler.ville"/>
										<dsp:input type="submit" bean="CastMagasinItineraireFormHandler.itineraire" iclass="buttonCalcul" />          							
            			</fieldset>
            		</dsp:form>
            					
            		<div class="imgControlsBlock">            					
								  <div class="controlButton">
									 <a class="controlGPS" title="Magasins-Castorama" href="/magasins/zip/Magasins-Castorama_fr.zip">GPS</a>
									</div>
									<a href="/magasins/zip/Magasins-Castorama_fr.zip" class="darkBlueLink"><fmt:message key="magasin.download.gps" /></a>
								</div>
            	</div>
           	</div>
           				
           				
           </div>
           			
          </div>
         </div>
         <c:if test="${parent == 'clientspace' }">
          <dsp:include page="../../user/includes/adviceBlock.jsp" />
         </c:if>
		    </div>
        <c:if test="${empty parent || parent != 'clientspace' }">
		    <div class="rightColumn">
			   <dsp:include page="/magasin/includes/magasinTargeter.jsp" flush="true">
				  <dsp:param name="magasinPagePromoBean" bean="/atg/registry/Slots/MagasinSlot"/>
				  <dsp:param name="name" value="banner"/>
			   </dsp:include>
		    </div>
	     </c:if>
	
	   </div>		
	 </dsp:oparam>
</dsp:droplet>
<script type="text/javascript">
<!--
	var inprue = document.getElementById('rue');
	if (inprue.value.length==0) {
		inprue.setAttribute('defaultValue', "Rue");
		inprue.setAttribute('value', "Rue");
		inprue.onfocus = inputFocus;
		inprue.onblur = inputBlur;
	}
	var inpville = document.getElementById('ville');
	if (inpville.value.length==0) {
		inpville.setAttribute('defaultValue', "Ville");
		inpville.setAttribute('value', "Ville");
		inpville.onfocus = inputFocus;
		inpville.onblur = inputBlur;
	}
//-->
</script>
</dsp:page>