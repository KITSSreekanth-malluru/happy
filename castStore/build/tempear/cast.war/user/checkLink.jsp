<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page> 

<dsp:importbean bean="/com/castorama/droplet/IsLinkExistDroplet"/>

    <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
    <dsp:getvalueof var="changePasswordErrorURL" bean="/OriginatingRequest.requestURI"/>
  
    <dsp:droplet name="IsLinkExistDroplet"> 
        <dsp:oparam name="output">

            <dsp:param name="userId" param="userId"/>
            <dsp:param name="key" param="key"/>
                
            <dsp:getvalueof var="userId" param="userId" />
            <dsp:getvalueof var="key" param="key" />
                    
            <cast:pageContainer>
                <jsp:attribute name="bodyContent">
        
                    <div class="content">
                    <div class="borderBlock2 loginBox changePassword">
                    <h1><fmt:message key="msg.change.password.form" /></h1>
                    <div class="boxContent">
        
                        <dsp:form method="post" iclass="formless">
                            <label for="password" class="top15px"><fmt:message key="msg.change.password" /> * :</label>
                            <dsp:input id="password" type="password" iclass="textInput nobmargin" maxlength="20"
                                bean="CastProfileFormHandler.value.password" /><br />
                                
                            <div class="clear"></div>
                            <dsp:droplet name="Switch">
                                <dsp:param bean="CastProfileFormHandler.formError" name="value"/>
                                <dsp:oparam name="true">
                                    <div class="error">
                                        <dsp:droplet name="ForEach">
                                            <dsp:param bean="CastProfileFormHandler.formExceptions" name="array"/>
                                            <dsp:param name="elementName" value="exception"/>
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="errorCode" param="exception.errorCode"/>
                                                <c:if test="${errorCode == 'incorrectPassword'}">
                                                    <dsp:valueof param="exception.message" valueishtml="true"/>
                                                </c:if>
                                                <c:if test="${errorCode == 'missedPassword'}">
                                                    <dsp:valueof param="exception.message" valueishtml="true"/>
                                                </c:if>
                                            </dsp:oparam>  
                                        </dsp:droplet>
                                    </div>
                                </dsp:oparam>
                            </dsp:droplet>
                                
                            <label for="password" class="top15px"><fmt:message key="msg.change.password.confirm" /> * :</label>
                            <dsp:input id="password" type="password" iclass="textInput nobmargin" maxlength="20"
                                bean="CastProfileFormHandler.value.confirmPassword" />
                                     
                            <div class="clear"></div>
                            <dsp:droplet name="Switch">
                                <dsp:param bean="CastProfileFormHandler.formError" name="value"/>
                                <dsp:oparam name="true">
                                    <div class="error">
                                        <dsp:droplet name="ForEach">
                                            <dsp:param bean="CastProfileFormHandler.formExceptions" name="array"/>
                                            <dsp:param name="elementName" value="exception"/>
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="errorCode" param="exception.errorCode"/>
                                                <c:if test="${errorCode == 'missedConfirmPassword'}">
                                                    <dsp:valueof param="exception.message" valueishtml="true"/>
                                                </c:if>
                                                <c:if test="${errorCode == 'incorrectConfirmPassword'}">
                                                    <dsp:valueof param="exception.message" valueishtml="true"/>
                                                </c:if>
                                            </dsp:oparam>  
                                        </dsp:droplet>
                                    </div>
                                </dsp:oparam>
                            </dsp:droplet>                

                            <dsp:input bean="CastProfileFormHandler.value.login" type="hidden" value="${userId}" />

                            <div class="formButtons">
                                <span class="inputButton">
                                    <dsp:input bean="CastProfileFormHandler.updatePassword" type="submit" value="Modifier" />
                                    <dsp:input bean="CastProfileFormHandler.updatePasswordSuccessURL" type="hidden" value="${contextPath }/user/login.jsp?isPasswordChanged=true"/>
                                    <dsp:input bean="CastProfileFormHandler.updatePasswordErrorURL" type="hidden" value="${changePasswordErrorURL}?key=${key}"/>             
                                </span>                
                            </div>
                        </dsp:form>
                        
                    </div></div></div>
    
                </jsp:attribute>
            </cast:pageContainer>

        </dsp:oparam>
        <dsp:oparam name="empty">
            <dsp:include page="../global/404.jsp" /> 
        </dsp:oparam>
    </dsp:droplet>
    
</dsp:page>