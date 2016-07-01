<dsp:page>
<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler"/>
<dsp:importbean bean="/com/castorama/profile/CastLoginFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/userprofiling/PropertyManager" />

<dsp:getvalueof var="isPasswordChanged" param="isPasswordChanged" />
<dsp:getvalueof var="magasinId" param="magasinId" />
<dsp:getvalueof var="selectFavoriteStoreSuccessURL" param="selectFavoriteStoreSuccessURL" />

<dsp:getvalueof var="shoppingListAction" param="shoppingListAction"/>
<dsp:getvalueof var="fromComparisonPage" param="fromComp"/>

<%-- in case request comes from stock visualization context --%>
<c:choose>
  <c:when test="${not empty selectFavoriteStoreSuccessURL}">
    <c:choose>
      <c:when test="${not empty magasinId}">
        <c:set var="loginSuccessURL" value="${selectFavoriteStoreSuccessURL}" />
        <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${loginSuccessURL}" />
      </c:when>
      <c:otherwise>
        <c:set var="loginSuccessURL" value="${pageContext.request.contextPath}/user/myProfile.jsp?fromSVContext=true" />
        <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${selectFavoriteStoreSuccessURL}" />
      </c:otherwise>
    </c:choose>
  </c:when>
  <c:when test="${not empty shoppingListAction and not empty fromComparisonPage}">
  	<dsp:getvalueof var="shoppingListSkuId" param="shoppingListSkuId"/> 
  	<dsp:getvalueof var="loginSuccessURL" value="${pageContext.request.contextPath}/shoppingList/shoppingListManager.jsp?action=add&skuIds=${shoppingListSkuId}&fromComp=true" />
  	<dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${loginSuccessURL}"/>
  </c:when>
  <c:otherwise>
    <dsp:getvalueof var="loginSuccessURL" bean="SessionBean.values.loginSuccessURL" />
  </c:otherwise>
</c:choose>


  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
			<div class="content">
			    <div class="error accountLockout">
                <c:if test="${isPasswordChanged}">
                    <div class="black"><fmt:message key="login.header.changed.password" /></div>
                </c:if>
                <dsp:droplet name="Switch">
                    <dsp:param bean="CastLoginFormHandler.formError" name="value"/>
                        <dsp:oparam name="true">
                            <dsp:droplet name="ForEach">
                                <dsp:param bean="CastLoginFormHandler.formExceptions" name="array"/>
                                <dsp:param name="elementName" value="exception"/>
                                <dsp:oparam name="output">
                                    <dsp:getvalueof var="errorCode" param="exception.errorCode"/>      
                                    <c:if test="${errorCode == 'accountLockout'}">
                                        <dsp:valueof param="exception.message" valueishtml="true"/>
                                        <dsp:getvalueof var="isAccountLockout" value="true" />
                                    </c:if>
                                </dsp:oparam>  
                            </dsp:droplet>
                        </dsp:oparam>
                    </dsp:droplet>
                </div></div>
                
				<div class="borderBlock2 loginBox leftSideBox">
					<h1><fmt:message key="login.header.login" /></h1>
					<div class="boxContent">				         
						<dsp:form method="post" iclass="formless">
							<dsp:input bean="CastLoginFormHandler.loginErrorURL" beanvalue="/OriginatingRequest.requestURI" type="hidden"/>						
					         <!-- always display empty username/login fields to our visitor -->
					         <dsp:input bean="CastLoginFormHandler.extractDefaultValuesFromProfile" type="hidden" value="false"/>
							<label for="login">
								<fmt:message key="login.email" /> :
								<span>(<fmt:message key="login.enter.email" />)</span>
							</label>
						<dsp:droplet name="/atg/dynamo/droplet/Compare">
					     <dsp:param bean="Profile.securityStatus" name="obj1"/>
					     <dsp:param bean="PropertyManager.securityStatusCookie" name="obj2"/>
					      <dsp:oparam name="lessthan">
					        <dsp:input bean="CastLoginFormHandler.value.login" iclass="textInput nobmargin" id="login" maxlength="255" value=""/>
					      </dsp:oparam>
					      <dsp:oparam name="default">
                  <dsp:input bean="CastLoginFormHandler.value.login" iclass="textInput nobmargin" id="login" maxlength="255"/>
                </dsp:oparam>
					  </dsp:droplet>    
							
							
							<div class="clear"></div>
							<!-- ERROR -->
								<dsp:droplet name="Switch">
						           <dsp:param bean="CastLoginFormHandler.formError" name="value"/>
						           <dsp:oparam name="true">
						             <div class="error sentChangePasswordEmail">
						             	<dsp:droplet name="ForEach">
							                <dsp:param bean="CastLoginFormHandler.formExceptions" name="array"/>
							                <dsp:param name="elementName" value="exception"/>
							              	<dsp:oparam name="output">
									      		<dsp:getvalueof var="errorCode" param="exception.errorCode"/>
									      		
									      		<c:if test="${errorCode == 'passwordIsSentByEmail'}">
									         		<dsp:valueof param="exception.message" valueishtml="true"/>
									         	</c:if>
									      		<c:if test="${errorCode == 'incorrectEmail'}">
									         		<dsp:valueof param="exception.message" valueishtml="true"/>
									         	</c:if>
									      		<c:if test="${errorCode == 'missingEmail'}">
									         		<dsp:valueof param="exception.message" valueishtml="true"/>
									         	</c:if>
									      		<c:if test="${errorCode == 'missedLogin'}">
									         		<dsp:valueof param="exception.message" valueishtml="true"/>
									         	</c:if>
									         	<c:if test="${errorCode == 'incorrectLogin'}">
									         		<dsp:valueof param="exception.message" valueishtml="true"/>
									         	</c:if>
									      	</dsp:oparam>  
									    </dsp:droplet>
						              </div>
						           </dsp:oparam>
						        </dsp:droplet>
								<!-- ERROR -->
								
							<div class="clear"></div>
							<label for="password" class="top15px">
								<fmt:message key="login.password" /> :
							</label>
							<dsp:input bean="CastLoginFormHandler.value.password" iclass="textInput nobmargin"  id="password" type="password" value="" maxlength="20"/>
							<div class="clear"></div>
							<!-- ERROR -->
							<c:if test="${!isAccountLockout}">
								<dsp:droplet name="Switch">
						           <dsp:param bean="CastLoginFormHandler.formError" name="value"/>
						           <dsp:oparam name="true">
						              <div class="error">
						             	<dsp:droplet name="ForEach">
							                <dsp:param bean="CastLoginFormHandler.formExceptions" name="array"/>
							                <dsp:param name="elementName" value="exception"/>
							              	<dsp:oparam name="output">
									      		<dsp:getvalueof var="errorCode" param="exception.errorCode"/>
									      		<c:if test="${errorCode == 'missedPasswordLogin'}">
									         		<dsp:valueof param="exception.message" valueishtml="true"/>
									         	</c:if>
									         	<c:if test="${errorCode == 'incorrectPasswordLogin'}">
									         		<dsp:valueof param="exception.message" valueishtml="true"/>
									         	</c:if>
									         	<c:if test="${errorCode == 'invalidLoginDifferentUser'}">
                                                    <dsp:valueof param="exception.message" valueishtml="true"/>
                                                </c:if>
									      	</dsp:oparam>  
									    </dsp:droplet>
						              </div>
						           </dsp:oparam>
						        </dsp:droplet>
                            </c:if>
								<!-- ERROR -->
							<div class="clear"></div>
							<a href="javascript:forgotPassword();">&gt; <fmt:message key="login.forgot.password" /></a>
							<script>
								function forgotPassword() {
								    document.getElementById('forgotPasswordEmail').click();
								}
							</script>
							<div class="clear"></div>
							<label for="fremember" class="checkbox remember">
							     <dsp:droplet name="/atg/dynamo/droplet/Compare">
			               <dsp:param bean="Profile.securityStatus" name="obj1"/>
			               <dsp:param bean="PropertyManager.securityStatusCookie" name="obj2"/>
			                <dsp:oparam name="lessthan">
			                  <dsp:input type="checkbox" id="fremember" iclass="i-checkbox"  bean="CastLoginFormHandler.value.autoLogin" checked="false"/>
			                </dsp:oparam>
			                <dsp:oparam name="default">
			                  <dsp:input type="checkbox" id="fremember" iclass="i-checkbox"  bean="CastLoginFormHandler.value.autoLogin"/>
			                </dsp:oparam>
			            </dsp:droplet> 
							   
							   <fmt:message key="login.remember.password" />
							</label>
							<div class="clear"></div>
							<span class="inputButton"><dsp:input bean="CastLoginFormHandler.login" type="submit" value="Valider"/></span>

							<dsp:input bean="CastLoginFormHandler.forgotPassword" type="submit" value="" id="forgotPasswordEmail" style="display:none"/>
							<dsp:input bean="CastLoginFormHandler.magasinId" paramvalue="magasinId" type="hidden"/>
					        <%-- If SessionBean.values.loginSuccessURL is empty, set successURL to profile.jsp--%>
					        <%-- <dsp:getvalueof var="loginSuccessURL" bean="SessionBean.values.loginSuccessURL"/>--%>
					        <dsp:getvalueof var="contextReferer" bean="/OriginatingRequest.referer" />
					        
					        <%-- In IE browser if one of this variables contains '?_DARGS=...' parameter, than URL-redirection fails.--%>
					        <c:if test="${fn:contains(loginSuccessURL, '_DARGS')}">
					            <c:set var="loginSuccessURL" value="${fn:substringBefore(loginSuccessURL, '_DARGS')}"/>
					        </c:if>
					        <c:if test="${fn:contains(contextReferer, '_DARGS')}">
					            <c:set var="contextReferer" value="${fn:substringBefore(contextReferer, '_DARGS')}"/>
					        </c:if>
					        
                  <c:set var="replaceURL" value="login.jsp" />
                  <c:if test="${not empty magasinId}" >
                    <c:set var="replaceURL" value="login.jsp?magasinId=${magasinId}"/>
                  </c:if>
                  <dsp:input bean="CastLoginFormHandler.logoutSuccessURL" type="hidden" value="${pageContext.request.contextPath}/user/${replaceURL}"/>
					        <c:choose>
					          <c:when test="${not empty loginSuccessURL && !fn:contains(loginSuccessURL, 'onBehalf.jsp') && !fn:contains(loginSuccessURL, '404') && !fn:contains(loginSuccessURL, 'createAccount') && !fn:contains(loginSuccessURL, 'checkLink')}">
					            <dsp:input bean="CastLoginFormHandler.loginSuccessURL" type="hidden" value="${loginSuccessURL}"/>
					            <c:set var="loginURLType" value="${loginSuccessURL}"/>
					          </c:when>
					          <c:when test="${not empty  contextReferer && !fn:contains(contextReferer, 'login.jsp') && !fn:contains(contextReferer, 'delivery.jsp') && !fn:contains(contextReferer, 'payment.jsp') && !fn:contains(contextReferer, 'confirmation.jsp') && !fn:contains(contextReferer, 'onBehalf.jsp') && !fn:contains(contextReferer, '404') && !fn:contains(contextReferer, 'createAccount')&& !fn:contains(contextReferer, 'checkLink')}" >
					          	<dsp:input bean="CastLoginFormHandler.loginSuccessURL" type="hidden" value="${contextReferer}"/>
								<dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${contextReferer}" />
								<c:set var="loginURLType" value="${contextReferer}"/>
					          </c:when>				         
					          <c:otherwise>
							       <dsp:input bean="CastLoginFormHandler.loginSuccessURL" type="hidden" value="../home.jsp" />
							       <c:set var="loginURLType" value="home.jsp"/>
					          </c:otherwise>
					        </c:choose>
					        
					        <dsp:input bean="CastLoginFormHandler.onBehalfURL" type="hidden" value="${pageContext.request.contextPath}/user/onBehalf.jsp" />
						</dsp:form>
					</div>
				</div>

				<div class="borderBlock2 loginBox rightSideBox">
					<h1><fmt:message key="login.create.account.castorama" /></h1>
					<div class="boxContent avantagebox">
						<h2><fmt:message key="login.advantage" /></h2>
						<ul>
							<li><fmt:message key="login.manage.personal.info" /></li>
							<li><fmt:message key="login.visualiser.disponibilite" /></li>
							<li><fmt:message key="login.receive.offers" /></li>
							<li><fmt:message key="login.track.orders" /></li>
							<li><fmt:message key="login.manage.card" /></li>
						</ul>
						<span class="inputButton centered">
						<dsp:form method="post" iclass="formless" id="logoutForm2">
						
  					  <input type="button" value='<fmt:message key="login.create.account" />' id="createAccountButton"/>  					  
	   				  
	   				 <dsp:droplet name="/atg/dynamo/droplet/Compare">
               <dsp:param bean="Profile.securityStatus" name="obj1"/>
               <dsp:param bean="PropertyManager.securityStatusCookie" name="obj2"/>
                <dsp:oparam name="lessthan">

                  <c:set var="replaceURL" value="createAccount.jsp" />

                  <%-- in case of creating profile during setting magasin as favorite one stock visualization tab -
                       drill down magasinId to createAccount.jsp page --%>
				  <c:if test="${not empty magasinId}" >
					<c:set var="replaceURL" value="createAccount.jsp?magasinId=${magasinId}"/>
				  </c:if>

                  <script>
                  $('#createAccountButton').click(function(){
                    window.location.replace('${replaceURL}');
                    });
                  </script>
                  <%-- nothing --%>
                </dsp:oparam>
                <dsp:oparam name="default">
                  <%-- logout if auto-loged or loged in --%>
                  <c:set var="replaceURL" value="createAccount.jsp" />
                  <%-- in case of creating profile during setting magasin as favorite one stock visualization tab -
                       drill down magasinId to createAccount.jsp page --%>
                    <c:if test="${not empty magasinId}" >
                      <c:set var="replaceURL" value="createAccount.jsp?magasinId=${magasinId}"/>
                    </c:if>
                  <dsp:input bean="CastLoginFormHandler.logoutSuccessURL" type="hidden" value="${pageContext.request.contextPath}/user/${replaceURL}"/>
                  <dsp:input bean="CastLoginFormHandler.logout" type="submit" value="Valider" id="logout2" style="display:none"/>
                  <script>
                  $('#createAccountButton').click(function(){
                    $('#logout2').click();
                  });
                  </script>
                </dsp:oparam>
            </dsp:droplet>
            
						</dsp:form>
						</span>
					</div>
				</div>
			
				
			</div>
            <div class="clear"><!--~--></div>
            
            <%-- Omniture params Section begins--%>
	    	<c:choose>
				<c:when test="${fn:contains(loginURLType, 'delivery.jsp')}">
					<fmt:message var="omniturePageName" key="omniture.pageName.purchase.login"/>
					<fmt:message var="omnitureChannel" key="omniture.channel.purchase"/>		
				</c:when>
				<c:otherwise>
					<fmt:message var="omniturePageName" key="omniture.pageName.clientSpace.login"/>
					<fmt:message var="omnitureChannel" key="omniture.channel.clientSpace"/>
				</c:otherwise>
			</c:choose>
			<c:set var="omniturePageName" value="${omniturePageName}" scope="request"/>
			<c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
			<%-- Omniture params Section ends--%>

    </jsp:attribute>
  </cast:pageContainer>

</dsp:page>
