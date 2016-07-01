<dsp:page>
	<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
	<dsp:importbean bean="/com/castorama/profile/CastLoginOnBehalfFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/userprofiling/SessionBean" />	
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
	<cast:pageContainer>
		<jsp:attribute name="bodyContent">
		<%-- Redirect if user is not admin --%>
			<dsp:droplet name="/com/castorama/droplet/HasGroupDroplet">
			  <dsp:param name="userLogin" bean="SessionBean.values.adminLogin"/>
			  <dsp:param name="group" value="Admin_Habilitations_Utilisateurs"/>			  
			  <dsp:oparam name="false">
			    <dsp:droplet name="Redirect">
		        <dsp:param name="url" value="login.jsp"/>
		      </dsp:droplet>			   
	      </dsp:oparam>
			</dsp:droplet>
      <div class="content">      
        <div class="borderBlock2 loginBox adminLoginBox">
          <h1><fmt:message key="onbehalf.header" /></h1>
          <div class="boxContent">
            <dsp:form action="onBehalf.jsp" method="post" iclass="formless">
              <label for="login">
                <fmt:message key="onbehalf.label" />
                <span><fmt:message key="onbehalf.label.text" /></span>             
              </label>
              <dsp:input bean="CastLoginOnBehalfFormHandler.value.login" iclass="textInput nobmargin" id="login" maxlength="50" />
              <div class="clear"></div>
              <!-- ERROR -->
              <dsp:droplet name="Switch">
                <dsp:param bean="CastLoginOnBehalfFormHandler.formError" name="value"/>
                <dsp:oparam name="true">
                  <div class="error">
                    <dsp:droplet name="ForEach">
                      <dsp:param bean="CastLoginOnBehalfFormHandler.formExceptions" name="array"/>
                      <dsp:param name="elementName" value="exception"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="errorCode" param="exception.errorCode"/>
                        <c:if test="${errorCode == 'incorrectOnBehalfEmail'}">
                          <dsp:valueof param="exception.message" valueishtml="true"/>
                        </c:if>                            
                      </dsp:oparam>  
                    </dsp:droplet>
                  </div>
                </dsp:oparam>
              </dsp:droplet>
              <!-- ERROR -->  
              <div class="clear"></div>
              <span class="inputButton">
                <dsp:input bean="CastLoginOnBehalfFormHandler.login" type="submit" value="Valider" />
                <dsp:input bean="CastLoginOnBehalfFormHandler.loginSuccessURL" type="hidden" value="${pageContext.request.contextPath}/home.jsp" />

                <dsp:input bean="CastLoginOnBehalfFormHandler.loginErrorURL" type="hidden" value="${pageContext.request.contextPath}/user/login.jsp" />
                <dsp:input bean="CastLoginOnBehalfFormHandler.onBehalfURL" type="hidden" value="${pageContext.request.contextPath}/user/onBehalf.jsp" />                
              </span>
            </dsp:form>
          </div>
        </div>
      </div>
      <div class="clear"><!--~--></div>
    </jsp:attribute>
	</cast:pageContainer>
</dsp:page>
