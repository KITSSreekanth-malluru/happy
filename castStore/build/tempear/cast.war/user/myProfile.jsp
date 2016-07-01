<%@ taglib prefix="dsp"
  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/PropertyManager" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>  
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/userprofiling/SessionBean"/>

  <dsp:getvalueof var="fromSVContext" param="fromSVContext" />
  <dsp:getvalueof var="prodId" param="prodId" />
  <dsp:getvalueof var="skuId" param="skuId" />

  <dsp:getvalueof var="successURL" bean="/OriginatingRequest.requestURI" idtype="java.lang.String" />
  <c:if test="${fromSVContext}">
    <dsp:getvalueof var="successURL" bean="SessionBean.values.loginSuccessURL"/>
  </c:if>

   <dsp:droplet name="/atg/dynamo/droplet/Compare">
     <dsp:param bean="Profile.securityStatus" name="obj1"/>
     <dsp:param bean="PropertyManager.securityStatusCookie" name="obj2"/>
			<dsp:oparam name="lessthan">
				<!-- send the user to the login form -->
			  <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/myProfile.jsp" />
		     <dsp:droplet name="Redirect">
		       <dsp:param name="url" value="login.jsp" />
		     </dsp:droplet>
			</dsp:oparam>
		  <dsp:oparam name="equal">
        <!-- send the user to the login form -->
        <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/myProfile.jsp" />
         <dsp:droplet name="Redirect">
           <dsp:param name="url" value="login.jsp" />
         </dsp:droplet>
      </dsp:oparam>
	</dsp:droplet>    

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">

              <script type="text/javascript">
                  var pageName = "myProfile";
              </script>

  <div class="clientSpace">
  <div class="content">
    <c:set var="brElement" value="header.my.profil" scope="request"/>
    <dsp:include page="includes/breadcrumbsClientHeader.jsp">
      <dsp:param name="element" value="${brElement}"/>
    </dsp:include>
    <c:set var="footerBreadcrumb" value="client" scope="request"/>
    <dsp:include page="clientMenu.jsp">
      <dsp:param name="currPage" value="profile"/>
    </dsp:include>

    <div class="formBlock">
      
    <dsp:getvalueof var="myProfileForm" bean="/OriginatingRequest.requestURI" idtype="java.lang.String">
      
      <dsp:include page="includes/profileErrorBlock.jsp"></dsp:include>
      
      <div class="formMainBlock">
        <dsp:form method="POST" action="${myProfileForm}" formid="login">
          <dsp:getvalueof var="updateProfile" bean="CastProfileFormHandler.updated"/>
          <dsp:getvalueof var="updateNewsleter" bean="CastNewsletterFormHandler.updated"/>
          <c:if test="${updateProfile or updateNewsleter}">
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
                      <fmt:message  key="msgProcessedRequest"/>                      
                    </td>
                  </tr>
                </tbody></table>                                    
              </div>
            </div>
          </c:if>
          <dsp:input bean="CastProfileFormHandler.updateErrorURL" type="hidden" value="${myProfileForm}" />
          <dsp:input bean="CastProfileFormHandler.updateSuccessURL" type="hidden" value="${successURL}" />
          <dsp:include page="includes/myIDPassword.jsp" >
            <dsp:param name="parent" value="myProfile"/>
          </dsp:include>      
          
          <div class="formButtons">
                      <span class="inputButton">
                        <dsp:input bean="CastProfileFormHandler.updateLoginPassword"
                type="submit" value="Modifier" />             
            </span>                
                  </div> 
        </dsp:form>
        </div>
        <div class="clearRight"><!--~--></div>
        
        <div class="formMainBlock">
          <dsp:form method="POST" action="${myProfileForm}" formid="castCardInfo" onsubmit="deleteCastoramaCardValue(); validateCastoramaCardNumberWithoutInfo();">
            <dsp:input bean="CastProfileFormHandler.updateErrorURL" type="hidden" value="${myProfileForm}" />
            <dsp:input bean="CastProfileFormHandler.updateSuccessURL" type="hidden" value="${successURL}" />
            <dsp:getvalueof var="content" value="profile" />
            <dsp:getvalueof var="bean" value="CastProfileFormHandler" />
            <dsp:include page="includes/myInformation.jsp" >
		  		<dsp:param name="bean" value="${bean}"/>
		  		<dsp:param name="content"  value="${content}"/>
		  		<dsp:param name="page"  value="myProfile"/>
		  	</dsp:include>         
            <div class="formButtons">
            <span class="inputButton">
              <dsp:input bean="CastProfileFormHandler.updateMyInformation" type="submit" value="Modifier"/>
            </span>
            </div>   
          </dsp:form>
        </div>
        <div class="clearRight"><!--~--></div> 
         
        <div class="formMainBlock">
          <dsp:form method="POST" action="${myProfileForm}" formid="address">
            <dsp:input bean="CastProfileFormHandler.updateErrorURL" type="hidden" value="${myProfileForm}" />
            <dsp:input bean="CastProfileFormHandler.updateSuccessURL" type="hidden" value="${successURL}" />
            <dsp:getvalueof var="content" value="profile" />
            <dsp:getvalueof var="bean" value="CastProfileFormHandler" />
            <dsp:getvalueof var="useKeyUpName" value="true" />
            <%@ include file="myAddress.jspf" %>            
            <div class="formButtons">
            <span class="inputButton">
              <dsp:input bean="CastProfileFormHandler.updatePrimaryAddress" type="submit" value="Modifier" />
            </span></div>     
          </dsp:form>
        </div>
        <div class="clearRight"><!--~--></div> 
        <div class="formMainBlock">

        <dsp:form method="POST" action="${myProfileForm}" formid="additionalInfo">
          <dsp:input bean="CastNewsletterFormHandler.updateErrorURL"
            type="hidden" value="${myProfileForm}" />
          <dsp:input bean="CastNewsletterFormHandler.updateSuccessURL"
            type="hidden" value="${successURL}" />
          <dsp:input type="hidden" bean="CastNewsletterFormHandler.value.email" beanvalue="Profile.login" />
          <dsp:input type="hidden" bean="CastNewsletterFormHandler.repositoryId" beanvalue="Profile.login" />
          <h2><fmt:message key="msg.accout.know.better.header" /></h2>
          <dsp:include page="includes/myAdditionalInfo.jsp" />
          <div class="formButtons">
            <span class="inputButton">
              <dsp:input bean="CastNewsletterFormHandler.updateFromProfile" type="submit" value="Modifier" />
            </span>
          </div>
        </dsp:form>
        </div>
    </dsp:getvalueof>
    <div class="clear"><!--~--></div>   
    <dsp:include page="includes/adviceBlock.jsp" /> 
    

    </div>
    </div>
    
    </div>
    
    <%-- Omniture params Section begins--%>
   	<fmt:message var="omniturePageName" key="omniture.pageName.clientSpace.profile"/>
	<fmt:message var="omnitureChannel" key="omniture.channel.clientSpace"/>
	    	
	<c:set var="omniturePageName" value="${omniturePageName}" scope="request"/>
	<c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
	<%-- Omniture params Section ends--%>
    
    </jsp:attribute>
  </cast:pageContainer>

</dsp:page>
