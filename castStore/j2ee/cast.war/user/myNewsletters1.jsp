<dsp:page>
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastUnsubscribeFormHandler" />  
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/atg/userprofiling/PropertyManager" />
 
  <dsp:droplet name="/atg/dynamo/droplet/Compare">
     <dsp:param bean="Profile.securityStatus" name="obj1"/>
     <dsp:param bean="PropertyManager.securityStatusCookie" name="obj2"/>
      <dsp:oparam name="equal">
        <%-- send the user to the login form --%>
        <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/myNewsletters1.jsp" />
         <dsp:droplet name="/atg/dynamo/droplet/Redirect">
           <dsp:param name="url" value="login.jsp" />
         </dsp:droplet>
      </dsp:oparam>
  </dsp:droplet>

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
    <div class="content">
      <c:set var="brElement" value="header.my.newsletters"/>
      <dsp:include page="includes/breadcrumbsClientHeader.jsp">
        <dsp:param name="element" value="${brElement}"/>
      </dsp:include>
       <dsp:include page="clientMenu.jsp">
        <dsp:param name="currPage" value="newsletters"/>
      </dsp:include>

      <div class="clientSpace">
        <div class="formBlock">

          <script type="text/javascript">
              $(window).load(
                function() {
                    switchNewsletterTab("subscribeTab");
                });
          </script>
          <dsp:include page="includes/newslettersTabs.jsp" flush="true">
            <dsp:param name="activeNewsletterTab" value="subscribeTab" />
          </dsp:include>
          
          <div class="newsletterContent_active" id="subscribeTab" >
              
          <dsp:droplet name="Switch">
            <dsp:param bean="CastNewsletterFormHandler.formError" name="value" />
            <dsp:oparam name="true">
              <div class="errorList">
                <UL>
                  <dsp:droplet name="ProfileErrorMessageForEach">
                    <dsp:param bean="CastNewsletterFormHandler.formExceptions" name="exceptions" />
                    <dsp:oparam name="output">
                      <LI><dsp:valueof param="message" /> 
                    </dsp:oparam>
                  </dsp:droplet>
                </UL>
              </div>
            </dsp:oparam>
          </dsp:droplet>

          <div class="formMainBlock">      
            <dsp:form method="POST" action="myNewsletters1.jsp" formid="1">      
             <dsp:getvalueof var="unsubscribe" bean="CastUnsubscribeFormHandler.unsubscribe"/>
             <dsp:getvalueof var="updatedNewsleter" bean="CastNewsletterFormHandler.updated"/>
             <c:if test="${unsubscribe or updatedNewsleter}">
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
                         <fmt:message  key="msgProcessedRequest" />
                       </td></tr>
                     </tbody></table>                                    
                  </div>
               </div>
             </c:if>
            <div class="formContent grayCorner grayCornerWhite">
             <div class="cornerBorder cornerTopLeft"><!--~--></div>
             <div class="cornerBorder cornerTopRight"><!--~--></div>
             <div class="cornerBorder cornerBottomLeft"><!--~--></div>
             <div class="cornerBorder cornerBottomRight"><!--~--></div>
             <div class="cornerOverlay">              
               <div class="centerFormMessage"><fmt:message key="client.newsletters.asterisk" /></div>
               <dsp:getvalueof var="email" param="email"/>
               <dsp:input type="hidden" bean="CastNewsletterFormHandler.repositoryId" value="${email}" />
               <dsp:input type="hidden" bean="CastNewsletterFormHandler.value.email" value="${email}" />
               
               <div class="f-row grayFrowbg ">        
                 <label><strong><fmt:message key="msg.newsletter.email" />&nbsp;:</strong></label>
                 <div class="f-inputs">  
                   <dsp:getvalueof var="footerEmail" param="footerEmail"/>
                  <dsp:droplet name="IsEmpty">
                    <dsp:param name="value" value="${footerEmail}" />
                    <dsp:oparam name="true">
                          <dsp:input bean="CastNewsletterFormHandler.repositoryId" type="hidden" beanvalue="CastNewsletterFormHandler.email"/>
                          <dsp:setvalue bean="CastNewsletterFormHandler.repositoryId" beanvalue="CastNewsletterFormHandler.email"/>
                          <dsp:input bean="CastNewsletterFormHandler.email" maxlength="50" size="30" type="text"  iclass="i-text"/>
                    </dsp:oparam>
                    <dsp:oparam name="false">
                      <dsp:input bean="CastNewsletterFormHandler.email" maxlength="50" size="30" type="text"
                        value="${footerEmail}"  iclass="i-text" />
                    </dsp:oparam>
                  </dsp:droplet> *
                  <dsp:input bean="CastNewsletterFormHandler.value.accesPartenairesCasto" type="hidden" value="true"/>
                 </div>
               </div>
               <div class="f-row ">
              <label><fmt:message key="msg.newsletter.magasin" /></label>
              <div class="f-inputs formSelector" id="hideThisSelect">
                <div class="chooseShopSelectorWr">
                  <dsp:getvalueof var="query" value="entite.adresse.departement.numero != 999" />
                  <dsp:select bean="CastNewsletterFormHandler.prefStore" iclass="styled" id="geo4">
                    <dsp:option value=""><fmt:message key="msg.newsletter.choose.store" /></dsp:option>
                    <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
                      <dsp:param name="queryRQL" value="${query}" />
                      <dsp:param name="repository" value="/atg/registry/Repository/MagasinGSARepository" />
                      <dsp:param name="itemDescriptor" value="magasin" />
                      <dsp:param name="elementName" value="magasinRQL" />
                      <dsp:param name="sortProperties" value="+entite.adresse.cp,+nom" />
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="iscc" param="magasinRQL.retraitMagasin"/>
                        <dsp:option paramvalue="magasinRQL.id" iclass="${iscc?'iscc':''}">                                                
                          <dsp:valueof param="magasinRQL.entite.adresse.cp" /> - <dsp:valueof param="magasinRQL.nom" />
                        </dsp:option>
                      </dsp:oparam>
                    </dsp:droplet>
                  </dsp:select>
                  <span class="star">*</span>
                  <img src="/store/images/choosenShopSelectorBg.png" />
                </div>
                <div class="removingStore">= <fmt:message key="cc.retrait.en.magasin"/></div>
              </div>
            </div>        
            <%@ include file="includes/newsletterSubscription.jspf" %>
          </div>
          
          <div class="formButtons padded10 ">
            <span class="inputButton">          
              <dsp:input bean="CastNewsletterFormHandler.createSuccessURL" type="hidden" value="../user/myNewsletters2.jsp" />
              <dsp:input bean="CastNewsletterFormHandler.createErrorURL" type="hidden" value="myNewsletters1.jsp" />
              <dsp:input bean="CastNewsletterFormHandler.addSubscribtion" type="submit" value="Valider" />
            </span>
          </div>
         </div>
       </dsp:form>
     </div>
     </div>
    </div>
  </div>
  <dsp:include page="includes/adviceBlock.jsp" /> 
  </div>  
  </jsp:attribute>
</cast:pageContainer>
<dsp:getvalueof var="error" param="error"/>


<c:if test="${not empty error && fn:length(error)>0}">
<script type="text/javascript">
// $(window).load(function () {
  showPopup('${error}');
//  });   
</script>
</c:if>
</dsp:page>



