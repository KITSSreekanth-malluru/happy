<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <dsp:getvalueof var="formExceptions" param="formExceptions" />
  <dsp:getvalueof var="formError" param="formError" />
  <dsp:getvalueof var="bean" param="bean" />

  <c:choose>
    <c:when test="${not empty bean }">
      <c:if test="${empty formExceptions}">    
        <dsp:getvalueof var="formExceptions" bean="${bean}.formExceptions" />
      </c:if>
      <c:if test="${empty formError}">
        <dsp:getvalueof var="formError" bean="${bean}.formError" />
      </c:if>
   </c:when>
   <c:otherwise>
      <c:if test="${empty formExceptions}">    
        <dsp:getvalueof var="formExceptions" bean="CastProfileFormHandler.formExceptions" />
      </c:if>
      <c:if test="${empty formError}">
        <dsp:getvalueof var="formError" bean="CastProfileFormHandler.formError" />
      </c:if>
   </c:otherwise>
  </c:choose>

  <c:if test="${not empty formExceptions}">
  <dsp:droplet name="Switch">
    <dsp:param value="${formError }" name="value" />
    <dsp:oparam name="true">
      <dsp:droplet name="/com/castorama/droplet/CorrectErrorList">
        <dsp:param name="exceptionList" value="${formExceptions }" />
        <dsp:param name="errorCode" value="incorrectCity" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="exceptions" param="exceptions"/>
            
            <dsp:droplet name="Switch">
            <dsp:param param="exceptions.empty" name="value" />
            <dsp:oparam name="false">
              <div class="errorList">
                <UL>
                  <dsp:droplet name="ProfileErrorMessageForEach">
                    <dsp:param value="${exceptions }" name="exceptions" />
                    <dsp:oparam name="output">
                        <LI>
                          <dsp:droplet name="Switch">
                            <dsp:param name="value" param="message"/>
                            <dsp:oparam name="msgRemoveIllegalItems">
                              <dsp:getvalueof var="showMsgRemoveIllegalItems" value="${true}"/>
                            </dsp:oparam>
				            <dsp:oparam name="default">
                        	  <dsp:valueof param="message" valueishtml="true"/>
				            </dsp:oparam>
				          </dsp:droplet>
                        </LI>
                    </dsp:oparam>
                  </dsp:droplet>
                  
                  <dsp:include page="newsletterErrorBlock.jsp"/>
                  
                </UL>
              </div>
            </dsp:oparam>
            <dsp:oparam name="true">
              <div class="errorList">
                <ul>
                  <dsp:include page="newsletterErrorBlock.jsp"/>
                </ul>
              </div>
            </dsp:oparam>
          </dsp:droplet><%-- end of switch --%>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
    <dsp:oparam name="false">
      <div class="errorList">
      <UL>
        <dsp:include page="newsletterErrorBlock.jsp"/>
      </UL>
      </div>
    </dsp:oparam>
  </dsp:droplet>
  </c:if>


  <c:if test="${showMsgRemoveIllegalItems}">
    <div class="grayCorner grayCornerGray preMessageLayer">
      <div class="grayBlockBackground"><!--~--></div>
      <div class="cornerBorder cornerTopLeft"><!--~--></div>
      <div class="cornerBorder cornerTopRight"><!--~--></div>
      <div class="cornerBorder cornerBottomLeft"><!--~--></div>
      <div class="cornerBorder cornerBottomRight"><!--~--></div>
      <div class="preMessage deliveryPageFix">
        <table cellpadding="0" cellspacing="0" class="emilateValignCenter">
          <tr>
            <td class="center darkRed"><fmt:message key="msg.cart.remove.items" /></td>
          </tr>
        </table>
      </div>
    </div>
  </c:if>
</dsp:page>