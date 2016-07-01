<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
  <dsp:importbean bean="/com/castorama/invite/InviteFormHanlder" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:getvalueof id="requestURI" bean="/OriginatingRequest.requestURI" idtype="java.lang.String"/> 
  <dsp:getvalueof var="result" param="res"/>   
  
  <dsp:droplet name="/atg/dynamo/droplet/Compare">
    <dsp:param bean="Profile.securityStatus" name="obj1" converter="number"/>
    <dsp:param bean="PropertyManager.securityStatusBasicAuth" name="obj2" converter="number"/>
    <dsp:oparam name="greaterthan">
    </dsp:oparam>
    <dsp:oparam name="default">
      <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/parrainerUnAmi.jsp" />
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="login.jsp" />
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>


       
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <div class="content">
        <c:set var="brElement" value="header.sponsor" scope="request"/>
        <dsp:include page="includes/breadcrumbsClientHeader.jsp">
        <dsp:param name="element" value="${brElement}" />
      </dsp:include>
      <c:set var="footerBreadcrumb" value="client" scope="request"/>
      <dsp:include page="clientMenu.jsp">
	      <dsp:param name="currPage" value="friend"/>
	    </dsp:include>
      <div class="clientSpace">
      <div class="formBlock">
          <c:if test="${'ok' != result}">                       
             
           </c:if>
       <div class="formMainBlock"> 
                <c:choose>
                  <c:when test="${'ok' == result}">                      
                    <div class="formContent grayCorner grayCornerGray">
                        <div class="cornerBorderGrayBg cornerTopLeft"></div>
                        <div class="cornerBorderGrayBg cornerTopRight"></div>
                        <div class="cornerBorderGrayBg cornerBottomLeft"></div>
                        <div class="cornerBorderGrayBg cornerBottomRight"></div>
                        <div class="cornerOverlay">
                          <div class="preMessage">          
                            <table cellspacing="0" cellpadding="0" class="emilateValignCenter">
                              <tbody><tr>
                                <td class="center">
                                  <fmt:message key="invite.ok"/>
                                </td>
                              </tr>
                            </tbody></table>                                    
                          </div>
                        </div>
                     </div>
                  </c:when>
                  <c:otherwise>
                    <div id="ReferrerSlot" class="ReferrerSlot first">                
                      <dsp:droplet name="/atg/targeting/TargetingRandom">
                        <dsp:param bean="/atg/registry/Slots/EspaceReferrerPromoSlot" name="targeter"/>
                        <dsp:param name="fireViewItemEvent" value="false"/>
                        <dsp:oparam name="output">
                            <img class="dottedBorder" src="<dsp:valueof param='element.media.slotImage.url'/>" alt="<dsp:valueof param='element.displayName'/>" title="<dsp:valueof param='element.displayName'/>"/>
                        </dsp:oparam>
                        <dsp:oparam name="empty">                   
                        </dsp:oparam>
                      </dsp:droplet> 
                    </div>
                    
                    <div id="RefereeSlot" class="ReferrerSlot">
                      <dsp:droplet name="/atg/targeting/TargetingRandom">
                        <dsp:param bean="/atg/registry/Slots/EspaceRefereePromoSlot" name="targeter"/>
                        <dsp:param name="fireViewItemEvent" value="false"/>
                        <dsp:oparam name="output">
                            <img class="dottedBorder" src="<dsp:valueof param='element.media.slotImage.url'/>" alt="<dsp:valueof param='element.displayName'/>" title="<dsp:valueof param='element.displayName'/>"/>
                        </dsp:oparam>
                        <dsp:oparam name="empty">                   
                        </dsp:oparam>
                      </dsp:droplet>  
                    </div> 
                    
                    <div class="clear"></div>
                    
                    <dsp:droplet name="Switch">
                       <dsp:param bean="InviteFormHanlder.formError" name="value" />
                       <dsp:oparam name="true">
                        <div class="errorList">
                           <UL>
                             <dsp:droplet name="ProfileErrorMessageForEach">
                               <dsp:param bean="InviteFormHanlder.formExceptions" name="exceptions" />
                               <dsp:oparam name="output">
                                <LI> <dsp:valueof param="message" /></LI> 
                               </dsp:oparam>
                             </dsp:droplet>
                           </UL>
                         </div>
                       </dsp:oparam>
                     </dsp:droplet> 
             
                    <dsp:form method="POST" action="${requestURI}" formid="login">
                      <dsp:input bean="InviteFormHanlder.errorURL" type="hidden" value="${requestURI}" />
                      <dsp:input bean="InviteFormHanlder.successURL" type="hidden" value="${requestURI}?res=ok" />
                      <div class="formContent grayCorner grayCornerGray referFriend">
                        <div class="cornerOverlay">
                          <h2 class="bigheaderfont"><fmt:message key="invite.header"/> </h2>
                                                   
                          <div><fmt:message key="invite.subheader1"/><br>
                          <strong><fmt:message key="invite.subheader2"/></strong>
                          </div> 
                          <br>
                          <br>                         
                          <div class="f-row">
                            <label class="required"><fmt:message key="invite.email"/> * :</label>
                            <div class="f-inputs">
                              <dsp:input bean="InviteFormHanlder.email" type="text" maxlength="50"/>
                            </div>
                          </div>
                          <div class="f-row">
                            <label class="required"><fmt:message key="invite.lastName"/> * :</label>
                            <div class="f-inputs">
                               <dsp:input bean="InviteFormHanlder.lastName" type="text" maxlength="64"/>
                            </div>
                          </div>
                          <div class="f-row">
                            <label class="required"><fmt:message key="invite.firstName"/> * :</label>
                            <div class="f-inputs">
                               <dsp:input bean="InviteFormHanlder.firstName" type="text" maxlength="64"/>
                            </div>
                          </div>             
                           <div class="f-row">
                          <div class="formButtons padded10">
                            <span class="inputButton">
                              <dsp:input bean="InviteFormHanlder.invite" type="submit" value="VALIDER" />
                            </span>
                          </div>
                          </div>
                         </div>
                      </div>
                    </dsp:form>
                  </c:otherwise>
                </c:choose>                 
           </div>     
     </div> 
     
     </div>
  
    <div class="advContainer">
     <dsp:include page="includes/adviceBlock.jsp" />
    </div>  
  </div>  
  </jsp:attribute>
  </cast:pageContainer>
</dsp:page>



