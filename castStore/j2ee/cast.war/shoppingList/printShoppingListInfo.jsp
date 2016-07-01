<%@ page contentType="text/html; charset=UTF-8"%>

<dsp:page>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />		
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/main.css" />
	</head>
	<body class="print print-magasine">
        <fmt:message key="castCatalog_label.close" var="fermer"/>
        <fmt:message key="castCatalog_label.print" var="imprimer"/>

		<dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" idtype="java.lang.String"/>
		<dsp:getvalueof var="activateOmniture" bean="/com/castorama/CastConfiguration.activateOmniture" />
		<dsp:droplet name="/com/castorama/droplet/MagasinLookupDroplet">
			<dsp:param name="id" param="magasinId" />
			<dsp:param name="elementName" value="magasin" />
			<dsp:oparam name="output">
		
		<div class="whitePopupContent">
			<div class="whitePopupHeader">
				<img src="${pageContext.request.contextPath}/images/logo.png" id="logo" alt="Castorama.fr" class="topLogo" />
				<a href="javascript:void(0)" onclick="window.close();" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a>
				<a href="javascript:void(0)" onclick="window.print();" class="imprimer" title="${imprimer}"><span><!--~--></span>${imprimer}</a>
				<div class="clear"></div>
				<div class="addPrintInfo">
				
					<dsp:getvalueof var="result_submittedDate" vartype="java.util.Date" param="result.submittedDate" />
					<span class="printDate">Date: <%-- %><fmt:formatDate value="${date}" type="date" pattern="dd.MM.yyyy"  />--%>
					<script type="text/javascript">
					<!--
					
						var currentTime = new Date();
						var hours = currentTime.getHours();
						var minutes = currentTime.getMinutes();
						if (minutes < 10){
						minutes = "0" + minutes;
						}											
						var month = currentTime.getMonth() + 1;
						if (month < 10){
						month = "0" + month;
						}
						var day = currentTime.getDate();
						if (day < 10){
						day = "0" + day;
						}
						var year = currentTime.getFullYear();
						document.write(day + "/" + month + "/" + year + " -  " + hours + ":" + minutes );
					//-->
					</script>
					 </span>
				</div>
			</div>
			
			<div class="clear"><!--~--></div>
		
			<div class="popupContentContainer">
          
          		<div class="votre-magasin">
          
		           	<div class="productBlock">
		           		
		           		<div class="productImageColumn">
		           			<div class="productImage">
		           				<dsp:getvalueof var="photo" param="magasin.url_photo" />                          	
		                        <img src="${photo}" width="291" height="323" />
		           			</div>           			
		           		</div>
           		
           				<div class="productContent">
           					<dsp:getvalueof var="magasinNom" param="magasin.nom"/>
           					<h1><c:out value="${magasinNom }"  escapeXml="false"/></h1>
           			
           					<div class="productDecription"> 
           						<%@include file="includes/magasin-info.jspf" %>           					            				
							</div>

						</div>
						
						
						
						<div class="clear"><!--~--></div>
						
						
						
						
						<div>
           			
           					<div class="productDecription"> 
		           				<%@include file="includes/magasin-news.jspf" %>   
		           				<%@include file="includes/magasin-service.jspf" %> 
           				
		           				<div class="magComment">
		           					<div class="mapImage">
		           						<dsp:getvalueof var="imgplan" param="magasin.imgplan" />                            	
		                            	<img src="${imgplan}" width="230" height="205" />           						
		           					</div>
		
		           					<div class="rightColumn">
		           						<h2><fmt:message key="magasin.way.to.shop" /></h2>
		           						<dsp:getvalueof var="accestransportcommun" param="magasin.accestransportcommun" />
										<c:if test="${not empty accestransportcommun}">		            					
										<p><strong><fmt:message key="magasin.transport.commun" /></strong></p>
										<ul>
											<li>            						
											<c:out value="${accestransportcommun }" escapeXml="false"/></li>
										</ul>
										</c:if>
		            					<%@include file="includes/magasin-map-info.jspf" %>           					
		            				</div>
								</div>  				
							</div>
						</div>
					</div>
				</div>
           
			</div>
	       	<div class="whitePopupHeader">
				<a href="javascript:void(0)" onclick="window.print();" class="imprimer" title="${imprimer}"><span><!--~--></span>${imprimer}</a>
			</div>
			<div class="clear"></div> 
		</div>
	
	</dsp:oparam>
	</dsp:droplet>
	
  <c:if test="${activateOmniture}">        
    <dsp:include page="/includes/googleAnalytics.jspf"/>
</c:if>
	
	</body>
	</html>
	
</dsp:page>

