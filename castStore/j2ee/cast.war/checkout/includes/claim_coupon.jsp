<dsp:importbean bean="/com/castorama/checkout/CastCouponFormHandler"/>
<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<div class="shoppingCart">
  <h2><fmt:message key="msg.cart.coupon.text.1" /></h2>
  <dsp:getvalueof id="couponForm" bean="/OriginatingRequest.requestURI" idtype="java.lang.String">
    <dsp:form action="<%=couponForm%>" method="post" formid="claimCouponForm">
      <dsp:input bean="CastCouponFormHandler.claimCouponSuccessURL" beanvalue="/OriginatingRequest.requestURI" type="hidden"/>
      <dsp:input bean="CastCouponFormHandler.claimCouponErrorURL" beanvalue="/OriginatingRequest.requestURI" type="hidden"/>
      <div class="unregisterContent">
        <h3><fmt:message key="msg.cart.coupon.text.2" /></h3>
        <div>
          <dsp:input bean="CastCouponFormHandler.couponClaimCode" name="couponClaimCode" id="couponClaimCode" type="text" maxlength="255" size="20"  iclass="unregisterInput"/>
          <dsp:input bean="CastCouponFormHandler.claimCoupon" type="submit" value="Enregistrer" id="ENREGISTRER"/>
        </div>
      </div>

      <!-- COUPON ERROR MESSAGE -->
      <div class="CouponError">
        <dsp:droplet name="Switch">
          <dsp:param bean="CastCouponFormHandler.formError" name="value"/>
          <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
              <dsp:param bean="CastCouponFormHandler.formExceptions" name="array"/>
              <dsp:param name="elementName" value="exception"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="errorCode" param="exception.errorCode"/>
                <c:choose> 
                  <c:when test="${errorCode == 'anonymousProfileUserResource'}">
                    <input type="hidden" name="" id="anonymousProfileUserResource"/>
                  </c:when>
                  <c:when test="${errorCode == 'notCompatibleWithOthersCoupons'}">
                    <div class="whitePopupContainer" style="position: absolute; display: block; top: 100px;">
                    <div class="whitePopupContent popupMessageContainer">
                    <div class="clear"><!--~--></div>  
                    <div class="popupContentContainer">
                      <div class="popupForm">
                        <div class="formMainBlock">
                          <fmt:message key="msg.cart.no_compatible_with_others"/>
                          <div class="formButtons">
                            <span class="inputButton gray">
                              <input onclick="hidePopup(this)" type="button" value="Non"/>
                            </span>
                            <span class="inputButton" >
                              <dsp:input bean="CastCouponFormHandler.applyLast" type="submit" value="Oui"/>
                            </span>
                          </div>
                        </div>
                        <div class="clear"><!--~--></div>
                      </div> 
                    </div>
                    </div>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <h3><dsp:valueof param="exception.message" valueishtml="true"/></h3>
                  </c:otherwise>
                </c:choose>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </div>

      <dsp:droplet name="Switch">
        <dsp:param bean="Profile.transient" name="value"/>
        <dsp:oparam name="true">
          <dsp:input bean="CastProfileFormHandler.loginSuccessURL" beanvalue="/OriginatingRequest.requestURI" type="hidden"/>
          <dsp:input bean="CastProfileFormHandler.loginErrorURL" beanvalue="/OriginatingRequest.requestURI" type="hidden"/>
          <!-- always display empty username/login fields to our visitor -->
          <dsp:input bean="CastProfileFormHandler.extractDefaultValuesFromProfile" type="hidden" value="false"/>
          <!-- IDENTITY BLOCK -->
          <div class="identifiant" id="identifiant" style="display: none;">
            <!-- ANONYMOUS COUPON ERROR MESSAGE -->
            <h3>
              <dsp:droplet name="Switch">
                <dsp:param bean="CastCouponFormHandler.formError" name="value"/>
                <dsp:oparam name="true">
                  <dsp:droplet name="ForEach">
                    <dsp:param bean="CastCouponFormHandler.formExceptions" name="array"/>
                    <dsp:param name="elementName" value="exception"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="errorCode" param="exception.errorCode"/>
                      <c:if test="${errorCode == 'anonymousProfileUserResource'}">
                        <fmt:message key="msg.cart.anonymous_coupon"/>
                      </c:if>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:oparam>
              </dsp:droplet>
            </h3>

            <div class="f-row">
              <h3><fmt:message key="msg.cart.coupon.user" /></h3><dsp:input bean="CastProfileFormHandler.value.login" iclass="unregisterInput" maxlength="255" size="20" id="login"/>
            </div>

            <!-- LOGIN ERROR -->
            <dsp:droplet name="Switch">
              <dsp:param bean="CastProfileFormHandler.formError" name="value"/>
              <dsp:oparam name="true">
                <h3>
                  <dsp:droplet name="ForEach">
                    <dsp:param bean="CastProfileFormHandler.formExceptions" name="array"/>
                    <dsp:param name="elementName" value="exception"/>
                    <dsp:oparam name="output">
                      <input type="hidden" name="" id="showLoginSection"/>
                      <dsp:getvalueof var="errorCode" param="exception.errorCode"/>
                      <c:choose>
                        <c:when test="${errorCode == 'passwordIsSentByEmail'}">
                          <dsp:valueof param="exception.message" valueishtml="true"/>
                        </c:when>
                        <c:when test="${errorCode == 'incorrectEmail'}">
                          <dsp:valueof param="exception.message" valueishtml="true"/>
                        </c:when>
                        <c:when test="${errorCode == 'missingEmail'}">
                          <dsp:valueof param="exception.message" valueishtml="true"/>
                        </c:when>
                        <c:when test="${errorCode == 'missedLogin'}">
                          <dsp:valueof param="exception.message" valueishtml="true"/>
                        </c:when>
                        <c:when test="${errorCode == 'incorrectLogin'}">
                          <dsp:valueof param="exception.message" valueishtml="true"/>
                        </c:when>
                      </c:choose>
                    </dsp:oparam>
                  </dsp:droplet>
                </h3>
              </dsp:oparam>
            </dsp:droplet>

            <div class="f-row">
              <h3><fmt:message key="msg.cart.coupon.pass" /></h3><dsp:input bean="CastProfileFormHandler.value.password" iclass="unregisterInput" maxlength="255" size="20" id="password" type="password"/>
            </div>

            <!-- PASSWORD ERROR -->
            <dsp:droplet name="Switch">
              <dsp:param bean="CastProfileFormHandler.formError" name="value"/>
              <dsp:oparam name="true">
                <div class="CouponError">
                  <dsp:droplet name="ForEach">
                    <dsp:param bean="CastProfileFormHandler.formExceptions" name="array"/>
                    <dsp:param name="elementName" value="exception"/>
                    <dsp:oparam name="output">
                      <input type="hidden" name="" id="showLoginSection"/>
                      <dsp:getvalueof var="errorCode" param="exception.errorCode"/>
                      <c:if test="${errorCode == 'missedPasswordLogin'}">
                        <dsp:valueof param="exception.message" valueishtml="true"/>
                      </c:if>
                      <c:if test="${errorCode == 'incorrectPasswordLogin'}">
                        <dsp:valueof param="exception.message" valueishtml="true"/>
                      </c:if>
                    </dsp:oparam>
                  </dsp:droplet>
                </div>
              </dsp:oparam>
            </dsp:droplet>

            <div class="identifiant_buttons">
              <dsp:input bean="CastCouponFormHandler.login" type="submit" value="Valider" iclass="validerButton"/>
              <span><dsp:a page="/user/createAccount.jsp" ><fmt:message key="msg.cart.coupon.create.new" /></dsp:a></span>
            </div>
          </div>
          <!-- END OF IDENTITY BLOCK -->
        </dsp:oparam>
      </dsp:droplet>
    </dsp:form>
  </dsp:getvalueof>

  <!-- BLOCK WITH PROMOTION DETAILS  -->
  <c:if test="${not empty sessionScope.claimedPromotions}">
    <dsp:droplet name="ForEach">
      <dsp:param name="array" value="${sessionScope.claimedPromotions}" />
      <dsp:param name="elementName" value="promotion"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="beginUsable" param="promotion.beginUsable"/>
        <dsp:getvalueof var="endUsable" param="promotion.endUsable"/>
        <dsp:getvalueof var="promoName" param="promotion.displayName"/>
        <c:set var="claimedPromotion" value=""/>
        <c:if test="${not empty beginUsable}"><c:set var="claimedPromotion" value="${claimedPromotion} Du ${beginUsable} "/></c:if>
        <c:if test="${not empty endUsable}"><c:set var="claimedPromotion" value="${claimedPromotion} au ${endUsable} "/></c:if>
        <c:if test="${(not empty beginUsable) && (not empty endUsable)}"><c:set var="claimedPromotion" value="${claimedPromotion}: "/></c:if>
        <c:set var="claimedPromotion" value="${claimedPromotion}${promoName}"/>
        <div class="promo">
          <c:out value="${claimedPromotion}" />
        </div>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>

</div>