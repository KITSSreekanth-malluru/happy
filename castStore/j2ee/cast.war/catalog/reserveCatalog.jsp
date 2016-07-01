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
                <h1>
                  <fmt:message key="catalog.reserve.title">
                    <fmt:param><dsp:valueof param="catalogue.title"/></fmt:param>
                  </fmt:message>
                </h1>
                <p>
                  <fmt:message key="catalog.reserve.content">
                    <fmt:param><dsp:valueof param="catalogue.title"/></fmt:param>
                    <fmt:param><dsp:valueof param="catalogue.releaseDate" date="dd MMMM yyyy" locale="fr_FR"/></fmt:param>
                  </fmt:message>
                </p>
                <p><strong>
                  <fmt:message key="catalog.reserve.quantity.limit">
                    <fmt:param><dsp:valueof param="catalogue.availableCoppies" converter="number" number="#" locale="fr_FR"/></fmt:param>
                  </fmt:message>
                </strong></p>   
                               
            </dsp:oparam>
          </dsp:droplet>          
        </div>
        <div class="clear"></div>
        <dsp:form method="POST" action="reserveCatalog.jsp" formid="account">
                 
          <div class="formMainBlock">
		        <dsp:include page="/user/includes/profileErrorBlock.jsp">
	           <dsp:param name="bean" value="/com/castorama/commerce/clientspace/CastReserveCatalog"/>
	          </dsp:include>
            
            <dsp:getvalueof var="content" value="reserveCatalogue" />
            <dsp:getvalueof var="bean" value="CastReserveCatalog" />
            <dsp:getvalueof var="isReserveCatalog" value="${true}" />
            
            <%@ include file="../user/myAddress.jspf" %>
          </div>
          <div class="formMainBlock">
            <dsp:input type="hidden" bean="CastNewsletterFormHandler.value.email" beanvalue="Profile.login" />
            <dsp:input type="hidden" bean="CastNewsletterFormHandler.repositoryId" beanvalue="Profile.login" />
            <h2><fmt:message key="msg.accout.know.better.header" /></h2>
            <dsp:include page="../user/includes/myAdditionalInfo.jsp" />          
          </div>
        
	        <div class="formMainBlock">
	          <h2><fmt:message key="header.my.newsletters" /></h2>
	          <div class="formContent grayCorner grayCornerGray">
	              <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
	            <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
	            <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
	            <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
	            <div class="cornerOverlay">
	              <div class="f-row">
                 <label class="required"><fmt:message key="msg.accout.address.email" /> :</label>
                    <div class="f-inputs">
                    <dsp:input bean="CastReserveCatalog.value.email" maxlength="50" size="30" type="text" iclass="i-text" beanvalue="Profile.email"/></div>
                </div>
	              <%@ include file="../user/includes/newsletterSubscription.jspf" %>    
	            </div>
	          </div>
	        </div>
          <div class="formButtons">
            <span class="inputButton">
              <dsp:getvalueof var="catalogueId" param="catalogueId"/>
              <dsp:input bean="CastReserveCatalog.value.catalogueId" type="hidden" paramvalue="catalogueId"/>
              <dsp:input bean="CastReserveCatalog.createErrorURL" type="hidden" value="reserveCatalog.jsp?catalogueId=${catalogueId }"/>
              <dsp:input bean="CastReserveCatalog.createSuccessURL" type="hidden" value="successReserve.jsp?catalogueId=${catalogueId }"/>                
              <dsp:input bean="CastReserveCatalog.create" type="submit" value="ENREGISTRER"/>
             </span>                
          </div>
        </dsp:form>
              
      </div>
      <div class="lesCatalogRight floatRight">
        <%@include file="catalogueVideoTargeter.jspf" %>
      </div>
     </jsp:attribute>
  </cast:pageContainer>
</dsp:page>