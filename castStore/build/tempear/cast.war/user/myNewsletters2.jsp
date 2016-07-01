<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:page>
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <div class="clientSpace">
        <div class="content">
          <c:set var="brElement" value="header.my.newsletters"/>
          <dsp:include page="includes/breadcrumbsClientHeader.jsp">
            <dsp:param name="element" value="${brElement}"/>
          </dsp:include>
      
		       <dsp:include page="clientMenu.jsp">
		        <dsp:param name="currPage" value="newsletters"/>
		      </dsp:include>
          <dsp:getvalueof var="email" param="email"/>
          <dsp:setvalue bean="CastNewsletterFormHandler.repositoryId" value="${email}"/>      
          <dsp:setvalue bean="CastNewsletterFormHandler.email" value="${email}"/>
          
          <div class="formBlock">
     
      
          <script type="text/javascript">
              $(window).load(
                function() {
                    switchNewsletterTab("editSubscriptionTab");
                });
          </script>
          <dsp:include page="includes/newslettersTabs.jsp" flush="true">
            <dsp:param name="activeNewsletterTab" value="editSubscriptionTab" />
          </dsp:include>
      
          <!-- <div class="newsletterContent_active" id="editSubscriptionTab" > -->

      
          <dsp:droplet name="Switch">
            <dsp:param bean="CastNewsletterFormHandler.formError" name="value" />
            <dsp:oparam name="true">
              <div class="errorList">
                <UL>
                  <dsp:droplet name="ProfileErrorMessageForEach">
                    <dsp:param bean="CastNewsletterFormHandler.formExceptions" name="exceptions" />
                    <dsp:oparam name="output">
                      <LI> <dsp:valueof param="message" /> 
                     </dsp:oparam>
                   </dsp:droplet>
                 </UL>
             </div>
            </dsp:oparam>
          </dsp:droplet>  
      
          <div class="formMainBlock">
            <dsp:form method="POST" action="myNewsletters2.jsp" formid="additionalInfo">         
              <dsp:getvalueof var="saved" bean="CastNewsletterFormHandler.saved"/>
              <dsp:getvalueof var="validatedEmail" bean="CastNewsletterFormHandler.validatedEmail"/>
            
              <dsp:input bean="CastNewsletterFormHandler.updateErrorURL" type="hidden" value="../user/myNewsletters2.jsp" />
              <dsp:input bean="CastNewsletterFormHandler.updateSuccessURL"  type="hidden" value="../user/myNewsletters2.jsp" />
            
              <c:if test="${saved == true}">
                <dsp:input bean="CastNewsletterFormHandler.repositoryId" value="${email}" type="hidden"/>
                <dsp:input bean="CastNewsletterFormHandler.email" value="${email}" type="hidden"/> 
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
                          <fmt:message key="msgSavedSubscription" />
                        </td>
                      </tr>
                      </tbody></table>                                    
                  </div>
                </div>                
              </c:if>
              
              <c:if test="${saved != true}">
                <div class="formContent grayCorner grayCornerGray">
                  <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
                  <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
                  <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
                  <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
                  <div class="cornerOverlay hightRows">
                    <b><fmt:message key="msg.newsletter.editSubscriptionTab.enterEmail" /></b>
                    <div class="f-row">       
                      <label><strong><fmt:message
                                          key="msg.newsletter.email" />&nbsp;:</strong></label>
                      <div class="f-inputs"> 
                        <dsp:input bean="CastNewsletterFormHandler.email" maxlength="50" size="30" type="text" iclass="i-text" /> *
                      </div>
                    </div> 
                  </div>
                </div>
                <div class="centered top10px" style="margin-bottom: 15px;">
                  <span class="inputButton">
                    <dsp:input bean="CastNewsletterFormHandler.validateEmail" type="submit" value="Valider" onclick="return setNewsletterUpdateURLs('validateEmail')"/> 
                  </span>
                </div>
              </c:if>
              
              <c:if test="${validatedEmail == true or saved == true}">
                <dsp:include page="includes/myAdditionalInfo.jsp" />      
                <div class="centered top10px">
                  <span class="inputButton">
                    <dsp:input bean="CastNewsletterFormHandler.updateToKnowYouBetter" type="submit" value="Valider" onclick="return setNewsletterUpdateURLs('updateSettings')"/> 
                  </span>
                </div>
              </c:if>
            </dsp:form>
          </div>
        </div>
          <dsp:include page="includes/adviceBlock.jsp" /> 
        
          </div>
        </div>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>