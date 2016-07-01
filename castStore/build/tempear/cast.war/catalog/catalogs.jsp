<dsp:page>
	<cast:pageContainer>
     <jsp:attribute name="bodyContent">
		<div class="content">

		<div class="breadcrumbs bluePage">				
		    <div class="homeBreadIco">
		      	<a href="${pageContext.request.contextPath}/home.jsp">
			        <img title="" alt="" src="${pageContext.request.contextPath}/images/icoHomeGray.gif"/>
			    </a>
			</div>
		    <div class="splitter">&gt;</div>				
		    <div class="active"><fmt:message key="header.catalogs" /></div>
		</div>
		
		<div class="lesCatalogLeft floatLeft">
			<h2><fmt:message key="catalog.header"/></h2>
			
		<c:set var="footerBreadcrumb" value="catalogue" scope="request"/>		
		
		<dsp:getvalueof var="query" value="active=true AND featured=true"/>
		<script type="text/javascript">
		function popupCentree(url) {
			largeur=1000;
			hauteur=670;
			options='scrollbar=no,resizable=yes,toolbar=no,status=no,statusbar=no,toolbar=no,menubar=no,directories=no,location=no';
			var top=(screen.height-hauteur)/2;
			var left=(screen.width-largeur)/2;
			window.open(url,"","top="+top+",left="+left+",width="+largeur+",height="+hauteur+","+options);
		} 
		</script>
		<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
			<dsp:param name="queryRQL" value="${query }" />
			<dsp:param name="repository" bean="/atg/registry/Repository/CatalogueGSARepository" />
			<dsp:param name="itemDescriptor" value="catalogue" />
			<dsp:param name="elementName" value="catalogue" />
			<dsp:param name="sortProperties" value="+displayOrder,+releaseDate"/>
			<dsp:oparam name="output">
			<dsp:getvalueof var="count" param="count"/>
			<c:if test="${count == 1 }">
				<div class="lesCataBan yellowBan floatLeft">
				<dsp:getvalueof var="browse_url" param="catalogue.browse_url"/>
				<dsp:getvalueof var="catalogWrapId" param="catalogue.catalogWrapId" />
				<dsp:getvalueof var="catalogWrapIdLink" value=""/>
				<c:if test="${not empty catalogWrapId}">
				  <dsp:getvalueof var="catalogWrapIdUrl" param="catalogue.catalogWrapId.url" />
				  <c:if test="${not empty catalogWrapIdUrl}">
				    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
				    <dsp:droplet name="/com/castorama/droplet/WrapPageLinkDroplet">
						<dsp:param name="id" param="catalogue.catalogWrapId.repositoryId" />
						<dsp:param name="contextPath" value="${contextPath}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="catalogWrapIdLink" param="url"/>
						</dsp:oparam>
					</dsp:droplet>
				  </c:if>
				</c:if>
       			<c:choose>	
				  <c:when test="${not empty catalogWrapIdLink}">
					<a href="${catalogWrapIdLink}" target="_blank"><img src='<dsp:valueof param="catalogue.image.url"/>' class="floatLeft lesCataBanImg pngImg" alt="" title="" /></a>
				  </c:when>
				  <c:otherwise>
				    <c:if test="${not empty browse_url }">
					<a href="javascript:popupCentree('catalogue_flash.jsp?browse_url=${browse_url }');"></c:if>
						<img src='<dsp:valueof param="catalogue.image.url"/>' class="floatLeft lesCataBanImg pngImg" alt="" title="" />
					<c:if test="${not empty browse_url }"></a></c:if>
				  </c:otherwise>
       			</c:choose>
					<div class="lesCataSubTitle orange"><dsp:valueof param="catalogue.promoMessage"/></div>
					<div class="lesCataSubTtlSub"><fmt:message key="catalog.catalogue" />&nbsp;<dsp:valueof param="catalogue.title"/></div>
					<div><dsp:valueof param="catalogue.description"/></div>
					<ul class="nvLinks">
					<dsp:getvalueof var="bookable" param="catalogue.bookable"/>
					<c:choose>	
						<c:when test="${bookable == true }">					
	    					<li class="blueArrow">
		        				<img alt="" src="/store/images/blank.gif" title=""/>
		            			<a href='reserveCatalog.jsp?catalogueId=<dsp:valueof param="catalogue.repositoryId"/>'><fmt:message key="catalog.reserve.this.catalog" /></a>
		       				</li>
	       				</c:when>
	       				<c:otherwise>
		       				<li class="book">
		       				  <c:choose>	
							   <c:when test="${not empty catalogWrapIdLink}">
	        					<img alt="" src="/store/images/blank.gif" title=""/>		        				
	            				<a href="${catalogWrapIdLink}" target="_blank"><fmt:message key="catalog.browse.brochure" /></a>
							   </c:when>
							   <c:otherwise>
							    <c:if test="${not empty browse_url }">
	        					 <img alt="" src="/store/images/blank.gif" title=""/>		        				
	            				 <a href="javascript:popupCentree('catalogue_flash.jsp?browse_url=${browse_url }');"><fmt:message key="catalog.browse.brochure" /></a>
		            			</c:if>
							  </c:otherwise>
				       	     </c:choose>
		       				</li> 
	       				</c:otherwise>
	       			</c:choose>                                                               
					</ul>
				</div>
			</c:if>
			<c:if test="${count == 2 }">
			<div class="lesCataBan greenBan floatRight">
			  <dsp:getvalueof var="browse_url" param="catalogue.browse_url"/>
			  <dsp:getvalueof var="catalogWrapId" param="catalogue.catalogWrapId" />
			  <dsp:getvalueof var="catalogWrapIdLink" value=""/>
			  <c:if test="${not empty catalogWrapId}">
				  <dsp:getvalueof var="catalogWrapIdUrl" param="catalogue.catalogWrapId.url" />
				  <c:if test="${not empty catalogWrapIdUrl}">
				    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
				    <dsp:droplet name="/com/castorama/droplet/WrapPageLinkDroplet">
						<dsp:param name="id" param="catalogue.catalogWrapId.repositoryId" />
						<dsp:param name="contextPath" value="${contextPath}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="catalogWrapIdLink" param="url"/>
						</dsp:oparam>
					</dsp:droplet>
				  </c:if>
			  </c:if>
			  <c:choose>	
			   <c:when test="${not empty catalogWrapIdLink}">
				<a href="${catalogWrapIdLink}" target="_blank"><img src='<dsp:valueof param="catalogue.image.url"/>' class="floatLeft lesCataBanImg pngImg" alt="" title="" /></a>
			   </c:when>
			   <c:otherwise>
				<c:if test="${not empty browse_url }">
				<a href="javascript:popupCentree('catalogue_flash.jsp?browse_url=${browse_url }');"></c:if>
					<img src='<dsp:valueof param="catalogue.image.url"/>' class="floatLeft lesCataBanImg pngImg" alt="" title="" />
				<c:if test="${not empty browse_url }"></a></c:if>
			   </c:otherwise>
      	      </c:choose>
				<div class="lesCataSubTitle green"><dsp:valueof param="catalogue.promoMessage"/></div>
				<div class="lesCataSubTtlSub"><fmt:message key="catalog.catalogue" />&nbsp;<dsp:valueof param="catalogue.title"/></div>
				<div><dsp:valueof param="catalogue.description"/></div>
				<ul class="nvLinks">			
					<dsp:getvalueof var="bookable" param="catalogue.bookable"/>
					<c:choose>	
						<c:when test="${bookable == true }">					
	    					<li class="blueArrow">
		        				<img alt="" src="/store/images/blank.gif" title=""/>
		            			<a href='reserveCatalog.jsp?catalogueId=<dsp:valueof param="catalogue.repositoryId"/>'><fmt:message key="catalog.reserve.this.catalog" /></a>
		       				</li>
	       				</c:when>
	       				<c:otherwise>
		       				<li class="book">
		       				    <c:choose>	
								   <c:when test="${not empty catalogWrapIdLink}">
		        					<img alt="" src="/store/images/blank.gif" title=""/>		        				
		            				<a href="${catalogWrapIdLink}" target="_blank"><fmt:message key="catalog.browse.brochure" /></a>
								   </c:when>
								   <c:otherwise>
								    <c:if test="${not empty browse_url }">
		        					 <img alt="" src="/store/images/blank.gif" title=""/>		        				
		            				 <a href="javascript:popupCentree('catalogue_flash.jsp?browse_url=${browse_url }');"><fmt:message key="catalog.browse.brochure" /></a>
			            			</c:if>
								  </c:otherwise>
					       	    </c:choose>
		       				</li> 
	       				</c:otherwise>
	       			</c:choose>                                                                
				</ul>				
			</div>
			</c:if>
				
			</dsp:oparam>															
		</dsp:droplet>
        <div class="clear"></div>
		<dsp:getvalueof var="query" value="active=true AND featured=false"/>
		<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
		<dsp:param name="queryRQL" value="${query}" />
			<dsp:param name="repository" bean="/atg/registry/Repository/CatalogueGSARepository" />
			<dsp:param name="itemDescriptor" value="catalogue" />
			<dsp:param name="elementName" value="catalogue" />
			<dsp:param name="sortProperties" value="+displayOrder,+releaseDate"/>
			<dsp:oparam name="output">
			<dsp:getvalueof var="index" param="index"/>
			<c:if test="${index < 30 }">
				<c:if test="${index != 0 and (index mod 3) == 0}">
					<div class="clear"></div>
				</c:if>
				<div class="lesCataMag floatLeft">
				    <dsp:getvalueof var="browse_url" param="catalogue.browse_url"/>
				    <dsp:getvalueof var="catalogWrapId" param="catalogue.catalogWrapId" />
				    <dsp:getvalueof var="catalogWrapIdLink" value=""/>
					<c:if test="${not empty catalogWrapId}">
					  <dsp:getvalueof var="catalogWrapIdUrl" param="catalogue.catalogWrapId.url" />
					  <c:if test="${not empty catalogWrapIdUrl}">
					    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
					    <dsp:droplet name="/com/castorama/droplet/WrapPageLinkDroplet">
							<dsp:param name="id" param="catalogue.catalogWrapId.repositoryId" />
							<dsp:param name="contextPath" value="${contextPath}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="catalogWrapIdLink" param="url"/>
							</dsp:oparam>
						</dsp:droplet>
					  </c:if>
				    </c:if>
				    <c:choose>	
					   <c:when test="${not empty catalogWrapIdLink}">
						<a href="${catalogWrapIdLink}" target="_blank"><div><img src='<dsp:valueof param="catalogue.image.url"/>' class="pngImg" alt="" title="" /></div></a>
					   </c:when>
					   <c:otherwise>
						<c:if test="${not empty browse_url }">
						  <a href="javascript:popupCentree('catalogue_flash.jsp?browse_url=${browse_url }');"></c:if>
							<div><img src='<dsp:valueof param="catalogue.image.url"/>' class="pngImg" alt="" title="" /></div>
						<c:if test="${not empty browse_url }"></a></c:if>
					   </c:otherwise>
		      	    </c:choose>
					<div class="lesCataMagPd">
						<div class="lesCataSubTtlSub"><fmt:message key="catalog.catalogue" />&nbsp;<dsp:valueof param="catalogue.title"/></div>
						<div><dsp:valueof param="catalogue.description"/> </div>
						<ul class="nvLinks">			
	    					<dsp:getvalueof var="bookable" param="catalogue.bookable"/>
							<c:choose>	
								<c:when test="${bookable == true }">					
			    					<li class="blueArrow">
				        				<img alt="" src="/store/images/blank.gif" title=""/>
				            			<a href='reserveCatalog.jsp?catalogueId=<dsp:valueof param="catalogue.repositoryId"/>'><fmt:message key="catalog.reserve.this.catalog" /></a>
				       				</li>
			       				</c:when>
			       				<c:otherwise>
				       				<li class="book">
				       				    <c:choose>	
										   <c:when test="${not empty catalogWrapIdLink}">
				        					<img alt="" src="/store/images/blank.gif" title=""/>		        				
				            				<a href="${catalogWrapIdLink}" target="_blank"><fmt:message key="catalog.browse.brochure" /></a>
										   </c:when>
										   <c:otherwise>
										    <c:if test="${not empty browse_url }">
				        					 <img alt="" src="/store/images/blank.gif" title=""/>		        				
				            				 <a href="javascript:popupCentree('catalogue_flash.jsp?browse_url=${browse_url }');"><fmt:message key="catalog.browse.brochure" /></a>
					            			</c:if>
										  </c:otherwise>
							       	    </c:choose>
				       				</li> 
			       				</c:otherwise>
			       			</c:choose>                                                                 
						</ul>
					</div>
				</div>
			</c:if>			
			</dsp:oparam>
		</dsp:droplet>
		<div class="clear"></div>
		</div>	
		
		<div class="lesCatalogRight floatRight">
			<%@include file="catalogueVideoTargeter.jspf" %>
		</div>
		
		</div>				
			
		</jsp:attribute>
  </cast:pageContainer>
</dsp:page>