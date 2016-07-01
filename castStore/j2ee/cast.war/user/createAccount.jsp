<%@ taglib prefix="dsp"
  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="magasinId" param="magasinId" />
  <dsp:getvalueof var="bean" value="CastProfileFormHandler" />
  <dsp:getvalueof var="content" value="profile" />

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <script type="text/javascript" src="${contextPath}/js/sponkey.js" ></script>
      <script type="text/javascript">
          var pageName = "createAccount";

		   $(document).ready(function(){  
		     $("#createAccount").click(function () {   
		         $("#createAccount").attr("disabled","disabled");  
		         $("#createAccountAction").click();  
		     });  
		   });
		  </script> 
    
	    <div class="pageTitle blueTitle">
	      <h1><fmt:message key="msg.accout.header" /></h1>
	    </div>
	    <div class="content">
	     
	     <dsp:include page="includes/profileErrorBlock.jsp"></dsp:include>    
        
        <dsp:form method="POST" action="createAccount.jsp" formid="account" name="create_account" id="create_account">
          <dsp:input bean="CastProfileFormHandler.createNewUser" type="hidden" value="false" />
          <dsp:getvalueof var="loginSuccessURL" bean="SessionBean.values.loginSuccessURL" scope="request"/>
		  <c:if test="${not empty loginSuccessURL && fn:contains(loginSuccessURL, 'submitReview')}">
		  	<dsp:input bean="CastProfileFormHandler.lightUser" type="hidden" value="true" />
		  </c:if>
		  
          <c:choose>
            <c:when test="${not empty loginSuccessURL && 
                           ((not empty magasinId) || (fn:contains(loginSuccessURL, 'fromMaListe')) ||
                               (fn:contains(loginSuccessURL, 'fromSVContext')) || (fn:contains(loginSuccessURL, 'shoppingListManager')) ||
                               (fn:contains(loginSuccessURL, 'delivery') || fn:contains(loginSuccessURL, 'submitReview')))}">

                  <dsp:input bean="CastProfileFormHandler.createSuccessURL" type="hidden" 
                              value="${loginSuccessURL}" name="loginSuccessURL"/>
            </c:when>
            <c:otherwise>
              <dsp:input bean="CastProfileFormHandler.createSuccessURL" type="hidden" value="clientSpaceHome.jsp" />
            </c:otherwise>
          </c:choose>
          
          <div class="formMainBlock">  
            <dsp:input bean="CastProfileFormHandler.createErrorURL" type="hidden" value="createAccount.jsp?magasinId=${magasinId}" />
            <dsp:include page="includes/myIDPassword.jsp">
              <c:choose>
	            <c:when test="${not empty loginSuccessURL && fn:contains(loginSuccessURL, 'submitReview')}">
	              <dsp:param name="parent"  value="createLightAccount"/>
	            </c:when>
	            <c:otherwise>
	              <dsp:param name="parent"  value="createAccount"/>
	            </c:otherwise>
	          </c:choose>
            </dsp:include>
          </div>
          
        <c:if test="${empty loginSuccessURL || (not empty loginSuccessURL && !(fn:contains(loginSuccessURL, 'submitReview')))}">
		  	<div class="formMainBlock">
		  		<dsp:include page="includes/myInformation.jsp" >
		  			<dsp:param name="bean" value="${bean}"/>
		  			<dsp:param name="content"  value="${content}"/>
		  			<dsp:param name="page"  value="createAccount"/>
		  		</dsp:include>
		  	</div>
		</c:if>
          
          <c:if test="${empty loginSuccessURL || (not empty loginSuccessURL && !(fn:contains(loginSuccessURL, 'submitReview')))}">
		      <div class="formMainBlock">
		        <dsp:getvalueof var="useKeyUpName" value="true" />
		        <dsp:getvalueof var="newAccount" value="true" />
		        <%@ include file="myAddress.jspf" %>
		      </div>
		  </c:if>
        
	        <dsp:input type="hidden" bean="CastNewsletterFormHandler.value.email" beanvalue="Profile.login" />  
	        <dsp:input type="hidden" bean="CastNewsletterFormHandler.repositoryId" beanvalue="Profile.login" />
	        
	        <c:if test="${empty loginSuccessURL || (not empty loginSuccessURL && !(fn:contains(loginSuccessURL, 'submitReview')))}">
		        <div class="formMainBlock">
		          <h2><fmt:message key="msg.accout.know.better.header" /></h2>
	              <dsp:include page="includes/myAdditionalInfo.jsp" >
	                <dsp:param name="magasinId" param="magasinId" />
	              </dsp:include>
		        </div>
	        </c:if>
            
	        <div class="formMainBlock">
	          <dsp:include page="includes/myNewslettersBlock.jsp" />
	        </div>
        
		      <div class="formButtons">
		        <span class="inputButton">                
		          <dsp:input bean="CastProfileFormHandler.create" type="submit" value="Valider" style="display:none;" id="createAccountAction"/>
		          <input type="submit" value="Valider" id="createAccount"/> 
		        </span>                
		      </div>
        </dsp:form>
    
   
      </div><%-- end of content --%>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>
