<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<dsp:page>

<dsp:importbean bean="/com/castorama/CastPaymentFormHandler"/>
<dsp:importbean bean="/com/castorama/util/PaymentTabs"/>
<dsp:importbean bean="/com/castorama/droplet/CastOrderCheckoutValidatorDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
<dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
<dsp:getvalueof var="orderDeliveryType" bean="/atg/commerce/ShoppingCart.currentlySelected.deliveryType"/>

<dsp:droplet name="/atg/dynamo/droplet/Compare">
  <dsp:param bean="Profile.securityStatus" name="obj1"/>
  <dsp:param bean="PropertyManager.securityStatusLogin" name="obj2"/>
  <dsp:oparam name="lessthan">
    <!-- send the user to the login form -->
    <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="../home.jsp"/>
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
      <dsp:param name="url" value="../../user/login.jsp"/>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<dsp:getvalueof var="order" bean="/atg/commerce/ShoppingCart.currentlySelected"/>
<fmt:formatNumber var="total" value="${order.priceInfo.total*100}" maxFractionDigits="0" groupingUsed="false"/>
<dsp:droplet name="CastOrderCheckoutValidatorDroplet">
  <dsp:param name="examinedOrder" bean="/atg/commerce/ShoppingCart.currentlySelected"/>
  <dsp:param name="pbx_total" value="${total}"/>
  <dsp:oparam name="false">
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
      <dsp:param name="url" value="../../checkout/cart.jsp"/>
    </dsp:droplet>
  </dsp:oparam>

</dsp:droplet>

<cast:pageContainer>
<jsp:attribute name="bodyContent">

  <fmt:message var="omnitureChannel" key="omniture.channel.purchase"/>
  <fmt:message var="omniturePageNameCB" key="omniture.pageName.purchase.payment.cb"/>
  <fmt:message var="omniturePageNameVisa" key="omniture.pageName.purchase.payment.visa"/>
  <fmt:message var="omniturePageNameMC" key="omniture.pageName.purchase.payment.mc"/>
  <fmt:message var="omniturePageNameAE" key="omniture.pageName.purchase.payment.ae"/>
  <fmt:message var="omniturePageNameCC" key="omniture.pageName.purchase.payment.cc"/>
  <fmt:message var="omniturePageNameCCasto" key="omniture.pageName.purchase.payment.ccasto"/>
  <fmt:message var="omniturePageNameTel" key="omniture.pageName.purchase.payment.tel"/>
  <fmt:message var="omniturePageNameCheque" key="omniture.pageName.purchase.payment.cheque"/>
  <fmt:message var="omniturePageNameVirement" key="omniture.pageName.purchase.payment.virement"/>
  
  
  <script type="text/javascript">
    var globalCardType = null;
    function paimentSend(formId, cardType) {
      globalCardType = cardType;

      window.setTimeout('asyncSendOmnitureInfo()', 0);

      var paimentForm = document.getElementById(formId);
      paimentForm['PBX_TYPECARTE'].value = cardType;
      paimentForm.submit();

      return false;
    }
    function asyncSendOmnitureInfo() {
      try {
        if (globalCardType == "CB") {
          s_sendOmnitureInfo("${omniturePageNameCB}", "${omnitureChannel}");
        } else if (globalCardType == "VISA") {
          s_sendOmnitureInfo("${omniturePageNameVisa}", "${omnitureChannel}");
        } else if (globalCardType == "EUROCARD_MASTERCARD") {
          s_sendOmnitureInfo("${omniturePageNameMC}", "${omnitureChannel}");
        } else if (globalCardType == "AMEX") {
          s_sendOmnitureInfo("${omniturePageNameAE}", "${omnitureChannel}");
        } else if (globalCardType == "SVS") {
          s_sendOmnitureInfo("${omniturePageNameCC}", "${omnitureChannel}");
        }
      } catch (e) {
      }

      return true;
    }
    function showSofinco(link) {
      if ("" != link) {
        try {
          s_sendOmnitureInfo("${omniturePageNameCCasto}", "${omnitureChannel}");
        } catch (e) {
        }

        document.getElementById('routeSofinco').src = link;
        showPopup('sofinco');
      }
    }
    function sendOmniturePaymentInfoDisableButton(_obj, pType) {
      try {
        if (pType == "cheque") {
          s_sendOmnitureInfo("${omniturePageNameCheque}", "${omnitureChannel}");
        } else if (pType == "tel") {
          s_sendOmnitureInfo("${omniturePageNameTel}", "${omnitureChannel}");
        } else if (pType == "virement") {
          s_sendOmnitureInfo("${omniturePageNameVirement}", "${omnitureChannel}");
        }
      } catch (e) {
      }

      return disableButton(_obj);
    }
  </script>

  <dsp:setvalue param="selectedStep" value="2"/>
  <%@include file="checkout_steps.jspf" %>
  <%@ include file="../includes/delivery_popup.jsp" %>    
  <div class="content">


    <dsp:getvalueof var="errorCode" param="erreur"/>
    <c:if test="${null != errorCode && '00000' != errorCode }">
      <dsp:droplet name="/com/castorama/droplet/PayboxCodeDetailsDroplet">
        <dsp:param name="code" value="${errorCode}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="details" param="details"/>
          <c:if test="${null != details && '' != details }">
            <div class="errorList">
              <ul>
                <li>
                  <dsp:valueof param="details"/>
                </li>
              </ul>
            </div>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
    <h1 class="title"><fmt:message key="msg.payment.ref"/></h1>

    <div class="leftPanel" id="paymentContainer">
      <dsp:getvalueof id="selectedPayment" value="1"/>

      <script>
        // TODO move script to external file
        function selectMethod(index) {
          var container = document.getElementById("paymentContainer");

          var Tabs = container.firstChild;
          do {
            if (Tabs.tagName == "DIV") {
              if (Tabs.id.indexOf("divContent") != -1) {
                if (Tabs.id == "divContent" + index) {
                  Tabs.style.display = "block";
                } else {
                  Tabs.style.display = "none";
                }
              }
            }
          } while (Tabs = Tabs.nextSibling);
          var ul = document.getElementById("divHeader");
          var li = ul.firstChild;

          do {
            if (li.tagName == "LI") {
              var className = li.className;
              if (className.indexOf(" active") != -1) {
                className = className.substring(0, className.indexOf(" active"));
              }
              if (li.id == "divHeader" + index) {
                li.className = className + " active";
              } else {
                li.className = className;
              }
            }
          } while (li = li.nextSibling);
        }

        function disable(index) {
          var ul = document.getElementById("divHeader" + index);
          var a = ul.firstChild;
          if (a) {
            a.href = "javascript:void(0);";
            a.style.cursor = "default";
            a.style.opacity = "0.4";
            a.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=40)";
          }
        }
      </script>

      <div class="tabsArea paymentType">
        <div class="tabsAreaBottomLine"><!--~--></div>
        <ul id="divHeader">
          <dsp:droplet name="ForEach">
            <dsp:param name="array" bean="PaymentTabs.allTabs"/>
            <dsp:oparam name="output">
              <dsp:getvalueof id="groupId" param="element.id"/>
              <dsp:getvalueof id="index" param="index"/>
              <li class="payment${groupId} ${index==selectedPayment?' active':''}" id="divHeader${index}"><a
                  href="javascript:selectMethod(${index})"><fmt:message key="payment.method.${groupId}"/></a></li>
            </dsp:oparam>
          </dsp:droplet>
        </ul>
      </div>

      <dsp:getvalueof var="mail" bean="/atg/userprofiling/Profile.eMail"/>
      <dsp:droplet name="/com/castorama/droplet/SofincoDroplet">
        <dsp:param name="orderAmount" value="${order.priceInfo.total}"/>
        <dsp:param name="orderReference" value="${order.id}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="sofincoLink" param="urlString"/>
        </dsp:oparam>
        <dsp:oparam name="empty">
          <c:set var="sofincoLink" value=""/>
        </dsp:oparam>
      </dsp:droplet>
        <%-- disableAsynch - parameter used for disabling asynch payment methods for order with "immediate withdrawal" items--%>
      <dsp:droplet name="/com/castorama/droplet/StockAvailabilityOrderProductsDroplet">
        <dsp:param name="examinedOrder" bean="/atg/commerce/ShoppingCart.currentlySelected"/>
        <dsp:oparam name="true">
          <c:set var="disableAsynchVar" value="true"/>
        </dsp:oparam>
        <dsp:oparam name="false">
          <c:set var="disableAsynchVar" value="false"/>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:droplet name="ForEach">
        <dsp:param name="array" bean="PaymentTabs.allTabs"/>
        <dsp:oparam name="output">
          <dsp:getvalueof id="groupId" param="element.jspPage"/>
          <dsp:getvalueof id="index" param="index"/>
          <div class="tabContent width664" style="display:${index==selectedPayment?'block':'none'}"
               id="divContent${index}">
            <dsp:include page="${groupId}">
              <dsp:param name="index" value="${index}"/>
              <dsp:param name="order" value="${order}"/>
              <dsp:param name="total" value="${total}"/>
              <dsp:param name="mail" value="${mail}"/>
              <dsp:param name="sofincoLink" value="${sofincoLink}"/>
              <dsp:param name="disableAsynch" value="${disableAsynchVar }"/>
              <dsp:param name="isCCOrder" value="${orderDeliveryType=='clickAndCollect'}"/>
            </dsp:include>
          </div>
        </dsp:oparam>
      </dsp:droplet>

      <dsp:form>
        <dsp:input type="hidden" bean="CastPaymentFormHandler.cartURL" value="../cart.jsp"/>
        <dsp:input type="hidden" bean="CastPaymentFormHandler.cancelURL" value="delivery.jsp"/>
        <dsp:input type="hidden" bean="CastPaymentFormHandler.successURL" value="confirmation.jsp"/>
        <dsp:input type="hidden" bean="CastPaymentFormHandler.commitOrderSuccessURL"
                   value="${contextPath }/checkout/full/confirmation.jsp"/>
        <div class="formButtons">
    <span class="ccBascetButtonForm">
            <fmt:message var="msgBackCart" key="msg.payment.back.to.cart"/>
      <dsp:input type="submit" bean="CastPaymentFormHandler.cart" iclass="ccBascetButtonForm" value="${msgBackCart}"/>
    </span>
    
		<span class="ccBascetButtonForm">
            <fmt:message var="msgBack" key="msg.delivery.back"/>
      <dsp:input type="submit" bean="CastPaymentFormHandler.cancel" iclass="ccBascetButtonForm" value="${msgBack}"/>
    </span>
        </div>
      </dsp:form>

      <div class="clear"></div>

      <div class="clear"><!--~--></div>
      <dsp:include page="../../user/includes/adviceBlock.jsp">
        <dsp:param name="hideFAQBannner" value="true"/>
      </dsp:include>

      <div class="clear"></div>
    </div>

    <div class="rightPanel">
      <dsp:include page="../cart_info.jsp">
        <dsp:param name="cTotalWeight" value="${cTotalWeight}"/>
        <dsp:param name="cDeliveriesCount" value="${cDeliveriesCount}"/>
        <dsp:param name="showCastCardPrice" value="${true}"/>
      </dsp:include>
    </div>
  </div>
  <div class="clear"><!--~--></div>
  <%@include file="includes/sofinco.jspf" %>
  <div class="clear"><!--~--></div>
   <%@include file="includes/secure3D.jspf" %>
        
   
</jsp:attribute>
</cast:pageContainer>
</dsp:page>

