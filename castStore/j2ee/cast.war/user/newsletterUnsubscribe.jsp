<dsp:page>
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastUnsubscribeFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/atg/userprofiling/PropertyManager" />

  <dsp:droplet name="/atg/dynamo/droplet/Compare">
    <dsp:param bean="Profile.securityStatus" name="obj1" />
    <dsp:param bean="PropertyManager.securityStatusCookie" name="obj2" />
    <dsp:oparam name="equal">
      <%-- send the user to the login form --%>
      <dsp:setvalue
        bean="/atg/userprofiling/SessionBean.values.loginSuccessURL"
        value="${pageContext.request.contextPath}/user/myNewsletters1.jsp" />
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="login.jsp" />
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <div class="content">
        <c:set var="brElement" value="header.my.newsletters" />
        <dsp:include page="includes/breadcrumbsClientHeader.jsp">
          <dsp:param name="element" value="${brElement}" />
        </dsp:include>
        
        <dsp:include page="clientMenu.jsp">
          <dsp:param name="currPage" value="newsletters" />
        </dsp:include>
        
        <div class="clientSpace">
          <div class="formBlock">
                                                               
              <script type="text/javascript">
                  $(window).load(
                    function() {
                        switchNewsletterTab("unsubscribeTab");
                    });
              </script>

            <dsp:include page="includes/newslettersTabs.jsp" flush="true">
              <dsp:param name="activeNewsletterTab" value="unsubscribeTab" />
            </dsp:include>
            
            <dsp:droplet name="Switch">
              <dsp:param bean="CastNewsletterFormHandler.formError"
                name="value" />
              <dsp:oparam name="true">
                <div class="errorList">
                  <UL>
                    <dsp:droplet name="ProfileErrorMessageForEach">
                      <dsp:param
                        bean="CastNewsletterFormHandler.formExceptions"
                        name="exceptions" />
                      <dsp:oparam name="output">
                        <LI><dsp:valueof param="message" /> 
                      
                      </dsp:oparam>
                    </dsp:droplet>
                  </UL>
                </div>
              </dsp:oparam>
            </dsp:droplet>
     
  <dsp:getvalueof var="unsubsForm" bean="/OriginatingRequest.requestURI" idtype="java.lang.String">
    <div class="clear">
                    <!--~-->
                  </div>  
    <div class="popupContentContainer">
      <div class="popupForm">
        <div class="formMainBlock">
         <dsp:form method="post" action="${unsubsForm}" formid="add">
                    
           <div class=" formContent grayCorner grayCornerGray">
           <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
           <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
           <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
           <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
           <div class="cornerOverlay">
              <dsp:droplet name="Switch">
                <dsp:param bean="CastUnsubscribeFormHandler.formError"
                                  name="value" />
                <dsp:oparam name="true">
                  <div class="errorList">
                    <UL>
                    <dsp:droplet name="ProfileErrorMessageForEach">
                      <dsp:param bean="CastUnsubscribeFormHandler.formExceptions"
                                          name="exceptions" />
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="message" param="message" />
                        <LI><dsp:valueof value="${message}" />
                      
                                        </dsp:oparam>
                    </dsp:droplet>
                    </UL>
                  </div>
                </dsp:oparam>
              </dsp:droplet>
                <div class="f-row">       
                  <label><strong><fmt:message
                                      key="msg.newsletter.unsubscribeEmail" />&nbsp;:</strong></label>
                  <div class="f-inputs">                                                                      
                    <dsp:input bean="CastUnsubscribeFormHandler.email"
                                    maxlength="50" size="30" type="text"
                                    iclass="i-text" /> *
                  </div>
                </div>  
                <div class="f-row">
                  <div class="f-inputs">
                    <label style="width: auto;">
                      <dsp:input type="radio" iclass="i-radio"
                                      bean="CastUnsubscribeFormHandler.unsubscribeAction"
                                      value="newsletters"
                                      checked="false"
                                      name="unsubscribeAction" /> &nbsp;
                      <fmt:message key="client.newsletters.unsubscribe.castorama" />
                    </label>
                  </div>
                </div>
                <div class="f-row">
                  <div class="f-inputs">
                    <label style="width: auto;">
                      <dsp:input type="radio" iclass="i-radio"
                                      bean="CastUnsubscribeFormHandler.unsubscribeAction"
                                      value="offers" checked="false"
                                      name="unsubscribeAction" /> &nbsp;
                      <fmt:message key="client.newsletters.unsubscribe.partners" />
                    </label>        
                  </div>
                </div>
                        
              </div>
              </div>
              <div class="formButtons centered top10px">
                <span class="inputButton">
                  <dsp:input bean="CastUnsubscribeFormHandler.unsubscribe" type="submit" value="Valider" />
                </span>    
                             
                <dsp:input bean="CastUnsubscribeFormHandler.updateErrorURL" type="hidden" value="../user/newsletterUnsubscribe.jsp" />
                <dsp:input bean="CastUnsubscribeFormHandler.updateSuccessURL" type="hidden" value="../user/myNewsletters1.jsp" />
                           
              </div>
          </dsp:form>
        </div>
        <div class="clear">
                        <!--~-->
                      </div>
      </div>
  </dsp:getvalueof>
     </div>
          </div>
        </div>
        <dsp:include page="includes/adviceBlock.jsp" /> 
      </div>  
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>

<!-- ########################################################################################## -->




