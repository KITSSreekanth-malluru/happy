<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>

<dsp:importbean bean="/atg/userprofiling/PropertyManager" />
<dsp:importbean bean="/com/castorama/integration/experian/ExperianRequestDroplet"/>

<dsp:getvalueof var="orderDeliveryType" bean="/atg/commerce/ShoppingCart.last.deliveryType"/>

  <dsp:droplet name="/atg/dynamo/droplet/Compare">
     <dsp:param bean="Profile.securityStatus" name="obj1"/>
     <dsp:param bean="PropertyManager.securityStatusLogin" name="obj2"/>
      <dsp:oparam name="lessthan">
        <!-- send the user to the login form -->
        <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="../home.jsp" />
         <dsp:droplet name="/atg/dynamo/droplet/Redirect">
           <dsp:param name="url" value="../../user/login.jsp" />
         </dsp:droplet>
      </dsp:oparam>
  </dsp:droplet> 


<cast:pageContainer>
<jsp:attribute name="bodyContent">

  <dsp:setvalue param="selectedStep" value="3" />
  <%@include file="checkout_steps.jspf" %>
  <div class="content confirmationPage">
    <h1 class="title">
      <c:if test="${orderDeliveryType == 'deliveryToHome'}">
        <fmt:message key="msg.confirmation.ref" />
      </c:if>
      <c:if test="${orderDeliveryType == 'clickAndCollect'}">
        <fmt:message key="msg.confirmation.cc.ref" />
      </c:if>
    </h1>
    <div class="leftPanel" id="paymentContainer">
      <dsp:droplet name="ExperianRequestDroplet">
        <dsp:param name="email" bean="Profile.email" />
        <dsp:param name="action" value="subscribe" />
        <dsp:param name="page" value="confirmCB.jsp" />
        <dsp:oparam name="output">
        </dsp:oparam>
      </dsp:droplet>
      <c:if test="${orderDeliveryType == 'deliveryToHome'}">
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param name="value" bean="ShoppingCart.last.paymentGroups[0].paymentMethod"/>
          <dsp:oparam name="Cheque">
            <dsp:include page="includes/confirmCheck.jsp"/>
          </dsp:oparam>
          <dsp:oparam name="Call-Center">
            <dsp:include page="includes/confirmPhone.jsp"/>
          </dsp:oparam>
          <dsp:oparam name="Virement">
            <dsp:include page="includes/confirmTransfer.jsp"/>
          </dsp:oparam>
          <dsp:oparam name="Atout"> 
            <dsp:include page="includes/confirmCB.jsp"/>
          </dsp:oparam>
          <dsp:oparam name="Cadeau"> 
            <dsp:include page="includes/confirmCB.jsp"/>
          </dsp:oparam>
          <dsp:oparam name="default">
            <dsp:include page="includes/confirmCB.jsp"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
      <c:if test="${orderDeliveryType == 'clickAndCollect'}">
        <dsp:include page="includes/confirmCCOrder.jsp"/>
      </c:if>
    </div>
    <div class="rightPanel">
      <dsp:getvalueof var="ccDeliveryMessage" bean="Profile.currentLocalStore.ccDeliveryMessage"/>
      <c:if test="${orderDeliveryType == 'clickAndCollect' and not empty ccDeliveryMessage}">
        <div class="infoLevraison">
          ${ccDeliveryMessage}
        </div>
      </c:if>
      <c:if test="${orderDeliveryType == 'deliveryToHome'}">
        <div class="infoLevraison">
          <h4><fmt:message key="msg.delivery.info" /></h4><br />
          <dsp:valueof bean="/com/castorama/CastConfiguration.deliveryPageMessage" valueishtml="true"/>
        </div>
      </c:if>
    </div>
    <div class="clear"></div>

    <fmt:message key="confirmation.button.track.order" var="trackOrder"/>
    <fmt:message key="confirmation.button.access.account" var="accessAccount"/>
    <fmt:message key="confirmation.button.return.home" var="returnHome"/>

    <div class="formButtons right">
      <c:if test="${orderDeliveryType == 'deliveryToHome'}">
        <span class="ccBlueButton">
          <input class="ccBlueButton" type="submit" value="${trackOrder}" defaultvalue="${trackOrder}" onclick="window.location.replace('../../user/ordersHistory.jsp');return false;"/>
        </span>
      </c:if>
      <span class="ccBlueButton">
        <input class="ccBlueButton" type="submit" value="${accessAccount}" defaultvalue="${accessAccount}" onclick="window.location.replace('../../user/clientSpaceHome.jsp');return false;"/>
      </span>
      <span class="ccBlueButton">
        <input class="ccBlueButton" type="submit" value="${returnHome}" defaultvalue="${returnHome}" onclick="window.location.replace('../../home.jsp');return false;"/>
      </span>
    </div>
    <div class="clear"></div>
    <div class="borderedBlock">
      <h2><fmt:message key="confirmation.order.info"/></h2>
      <div>
        <div class="contactContentBlock">
          <h3><fmt:message key="msg.delivery.primary" /></h3>
          <div class="contactContent">
            <dsp:include page="../../user/includes/contentAddressInfo.jsp">
              <dsp:param name="address" bean="Profile.billingAddress" />
            </dsp:include>
          </div>
        </div>
        <c:if test="${orderDeliveryType == 'deliveryToHome'}">
        <div class="contactContentBlock">
          <h3><fmt:message key="msg.delivery.secondary"/></h3>
          <div class="subtitle">
            <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
              <dsp:param name="value" bean="ShoppingCart.last.shippingGroups[0].adresseLibelle"/>
              <dsp:oparam name="false"> 
                <fmt:message key="confirmation.order.delivery.address.name"/> : <dsp:valueof bean="ShoppingCart.last.shippingGroups[0].adresseLibelle"/>
              </dsp:oparam>
            </dsp:droplet>
          </div>
          <div class="contactContent">
            <dsp:include page="../../user/includes/contentAddressInfo.jsp">
              <dsp:param name="address" bean="ShoppingCart.last.shippingGroups[0].shippingAddress" />
            </dsp:include>
          </div>
        </div>
        </c:if>
        <div class="contactContentBlock">
          <h3><fmt:message key="confirmation.order.payment"/></h3>
          <div class="contactContent">
            <p>
            <dsp:droplet name="/com/castorama/droplet/OrderPaymentDroplet" >
              <dsp:param name="paymentMethod" bean="ShoppingCart.last.paymentGroups[0].paymentMethod"/>
              <dsp:param name="orderId" bean="ShoppingCart.last.id"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="paymentAmount" param="paymentAmount" />
                <dsp:valueof param="paymentType" /> : <span class="blue">
                  <c:choose>
                    <c:when test="${null == paymentAmount}" >
                      <dsp:valueof param="paymentDescription" />
                    </c:when>
                    <c:otherwise>
                      <dsp:valueof param="paymentAmount" converter="euro" locale="fr_FR"/>
                    </c:otherwise>
                  </c:choose>
                </span>
                <%-- for tag Commander --%>
                <dsp:getvalueof var="tagCommanderPaymentMethod" param="paymentType" scope="request"/>
              </dsp:oparam>
            </dsp:droplet>
            </p>
            <p class="total"><fmt:message key="confirmation.order.payment.total"/> : <span class="blue"><dsp:valueof bean="ShoppingCart.last.priceInfo.total" converter="euro" locale="fr_FR"/> </span></p>
          </div>
        </div>
      </div>
      <div class="clear"></div>
      
    </div>
    
    <dsp:include page="../cartContent.jsp">
      <dsp:param name="mode" value="confirmation"/>
      <dsp:param name="isCCOrder" value="${orderDeliveryType == 'clickAndCollect'}"/>
    </dsp:include>
    
    <div class="formButtons right">
      <c:if test="${orderDeliveryType == 'deliveryToHome'}">
        <span class="ccBlueButton">
          <input class="ccBlueButton" type="submit" value="${trackOrder}" defaultvalue="${trackOrder}" onclick="window.location.replace('../../user/ordersHistory.jsp');return false;"/>
        </span>
      </c:if>
      <span class="ccBlueButton">
        <input class="ccBlueButton" type="submit" value="${accessAccount}" defaultvalue="${accessAccount}" onclick="window.location.replace('../../user/clientSpaceHome.jsp');return false;"/>
      </span>
      <span class="ccBlueButton">
        <input class="ccBlueButton" type="submit" value="${returnHome}" defaultvalue="${returnHome}" onclick="window.location.replace('../../home.jsp');return false;"/>
      </span>
    </div>
  </div>


<div class="clear"><!--~--></div>
  <%@include file="includes/zanox.jspf" %>
<div class="clear"><!--~--></div>

</jsp:attribute>
</cast:pageContainer>

</dsp:page>
