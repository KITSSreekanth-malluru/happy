<dsp:page>
  <dsp:importbean bean="/com/castorama/mail/EmailAFriendFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

  <dsp:getvalueof var="productId" param="productId" />

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <script type="text/javascript" src="${contextPath}/js/contactUs.js" ></script>        

  <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
    <dsp:param name="id" param="productId"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="productLink" param="url"/>
      <div class="whitePopupContainer" id="emailAFriend">
        <div class="whitePopupContent popupFormContainer">
          <div class="whitePopupHeader">
            <h1><fmt:message key="email.a.friend.popup.header"/></h1>
            <fmt:message key="castCatalog_label.close" var="fermer"/>
            <a href="javascript:void(0)" onclick="hidePopup(this)" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a>
          </div>
          <div class="clear"><!--~--></div>
          <div class="popupContentContainer">
            <dsp:form method="post" action="${originatingRequest.contextPath}${productLink}" id="sendToFriend">             
              <div class="popupForm">
                <div class="formMainBlock">
                  <div class=" formContent grayCorner grayCornerGray">
                    <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
                    <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
                    <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
                    <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
                    <div class="cornerOverlay">
                      <dsp:droplet name="Switch">
                        <dsp:param bean="Profile.transient" name="value" />
                        <dsp:oparam name="false">
                          <dsp:getvalueof var="mailFrom" bean="Profile.email"/>
                        </dsp:oparam>
                      </dsp:droplet>
                    
                      <script>
                        var mailFrom = "${mailFrom}";
                        function clearForm() {
                          $('#fromEmail').val(mailFrom);
                          $('#toEmail').val('');
                          $('#messageId').val('');
                          $('#errorsArea1').html('');
                          $('#errorsArea2').html('');
                        }
                      </script>
                      <div class="f-row">
                        <label><fmt:message key="email.a.friend.popup.your.address"/></label>
                        <div class="f-inputs">
                          <dsp:input type="text" bean="EmailAFriendFormHandler.emailFrom" iclass="i-text" maxlength="50" id="fromEmail"/>
                          <div class="f-error" id="errorsArea1">
                            <dsp:droplet name="ForEach">
                              <dsp:param bean="EmailAFriendFormHandler.formExceptions" name="array"/>
                              <dsp:param name="elementName" value="exception"/>
                              <dsp:oparam name="output">
                                <dsp:getvalueof var="errorCode" param="exception.errorCode"/>
                                <c:if test="${errorCode == 'msg.email.a.friend.from.incorrect'}">
                                  <dsp:valueof param="exception.message" valueishtml="true"/>
                                </c:if>
                                <c:if test="${errorCode == 'msg.email.a.friend.from.empty'}">
                                  <dsp:valueof param="exception.message" valueishtml="true"/>
                                </c:if>
                              </dsp:oparam>
                            </dsp:droplet>
                          </div>
                        </div>
                      </div>
                      <div class="f-row">
                        <label><fmt:message key="email.a.friend.popup.friend.address"/></label>
                        <div class="f-inputs">
                          <dsp:input type="text" bean="EmailAFriendFormHandler.emailTo" iclass="i-text" maxlength="50"  id="toEmail"/>
                          <div class="f-error" id="errorsArea2">
                            <dsp:droplet name="ForEach">
                              <dsp:param bean="EmailAFriendFormHandler.formExceptions" name="array"/>
                              <dsp:param name="elementName" value="exception"/>
                              <dsp:oparam name="output">
                                <dsp:getvalueof var="errorCode" param="exception.errorCode"/>
                                <c:if test="${errorCode == 'msg.email.a.friend.to.incorrect'}">
                                  <dsp:valueof param="exception.message" valueishtml="true"/>
                                </c:if>
                                <c:if test="${errorCode == 'msg.email.a.friend.to.empty'}">
                                  <dsp:valueof param="exception.message" valueishtml="true"/>
                                </c:if>
                              </dsp:oparam>
                            </dsp:droplet>
                          </div>
                        </div>
                      </div>
                      <div class="attentionForm"><fmt:message key="email.a.friend.popup.attention"/></div>
                    </div>
                  </div>

                  <div class="popupTxtar">
                    <div><h3>Message <span>(optionnel)</span></h3></div>
                    <div>
                      <dsp:textarea bean="EmailAFriendFormHandler.message" id="messageId"  onkeydown="limitText(this,2000);" onkeyup="limitText(this,2000);"/>
                    </div>
                  </div>
                  <div class="formButtons">
                    <span class="inputButton">
                      <dsp:input type="submit" value="Envoyer" bean="EmailAFriendFormHandler.send"/>
                      <dsp:input bean="EmailAFriendFormHandler.sendSuccessURL" type="hidden" value="${originatingRequest.contextPath}${productLink}"/>
                      <dsp:input bean="EmailAFriendFormHandler.sendErrorURL" type="hidden" value="${originatingRequest.contextPath}${productLink}"/>
                      <dsp:input bean="EmailAFriendFormHandler.hasErrors" type="hidden" id="hasErrors"/>
                      <dsp:input bean="EmailAFriendFormHandler.productId" type="hidden" value="${productId}"/>
                    </span>
                  </div>
                  <div class="clear"><!--~--></div>
                </div>
              </div>
            </dsp:form>
          </div>
        </div>
      </div>
    </dsp:oparam>
  </dsp:droplet>

  <script>
    var control = document.getElementById('hasErrors');
    if(control && 'true'==control.value) {
      showPopup('emailAFriend');
    }
  </script>

</dsp:page>