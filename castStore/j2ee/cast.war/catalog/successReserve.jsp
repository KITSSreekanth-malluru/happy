<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
	<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
	<dsp:importbean bean="/com/castorama/commerce/clientspace/CastReserveCatalog" />
	<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
	
	<cast:pageContainer>
		<jsp:attribute name="bodyContent">
			
				<div class="breadcrumbs bluePage">
				    <div class="homeBreadIco">
				      	<a href="${pageContext.request.contextPath}/home.jsp">
					        <img title="" alt="" src="${pageContext.request.contextPath}/images/icoHomeGray.gif"/>
					    </a>
					</div>
				    <div class="splitter">&gt;</div>				
				    <div><a href="${pageContext.request.contextPath}/catalog/catalogs.jsp"><fmt:message key="header.catalogs" /></a></div>
				    <div class="splitter">&gt;</div>
				    <div class="active"><fmt:message key="catalog.reserve" /></div>
				</div>				
				<div class="content width790">
				<div class="catDetails">
				<dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
                     <dsp:param name="id" param="catalogueId"/>
                     <dsp:param name="elementName" value="catalogue"/>
                     <dsp:param name="itemDescriptor" value="catalogue"/>
                     <dsp:param name="repository" bean="/atg/registry/Repository/CatalogueGSARepository" />
                     <dsp:oparam name="output">
	                     <img src='<dsp:valueof param="catalogue.image.url"/>' class="catDetCover pngImg" />
						<h1><br/>
							<dsp:valueof param="catalogue.title"/>
						</h1>
						<div class="clear"></div><br />				   
						<div class="grayCorner grayCornerGray rounderrorMessage">
						<div class="grayBlockBackground"><!--~--></div>
						<div class="cornerBorder cornerTopLeft"><!--~--></div>
						<div class="cornerBorder cornerTopRight"><!--~--></div>
						<div class="cornerBorder cornerBottomLeft"><!--~--></div>
						<div class="cornerBorder cornerBottomRight"><!--~--></div>                                                                                    
					
						<div class="preMessage">
						
							<table cellspacing="0" cellpadding="0" class="emilateValignCenter">
								<tbody><tr>
									<td class="center">
										<fmt:message key="catalog.success.reserve" />
									</td>
								</tr>
							</tbody></table>                                    
						</div>
					</div>	                   
                    </dsp:oparam>
                  </dsp:droplet>					
				</div>
				<div class="clear"></div>				
							
			</div>
    <div class="lesCatalogRight floatRight">
      <%@include file="catalogueVideoTargeter.jspf" %>
    </div>
     </jsp:attribute>
  </cast:pageContainer>
</dsp:page>