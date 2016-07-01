<dsp:page>

<dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
<dsp:importbean bean="/com/castorama/CastDeliveryFormHandler"/>
<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Range"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>
<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="orderDeliveryType" bean="/atg/commerce/ShoppingCart.currentlySelected.deliveryType"/>
<dsp:getvalueof var="castoramaCardNumber" value="CastDeliveryFormHandler.castoramaCardValue"/>
<dsp:getvalueof var="castCardNumberCorrect" value="CastDeliveryFormHandler.castoramaCardCorrect"/>
<dsp:getvalueof var="selectedOrderId"  bean="/atg/commerce/ShoppingCart.currentlySelected.id"/>


<dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
<dsp:droplet name="/atg/dynamo/droplet/Compare">
  <dsp:param bean="Profile.securityStatus" name="obj1"/>
  <dsp:param bean="PropertyManager.securityStatusLogin" name="obj2"/>
  <dsp:oparam name="lessthan">
    <!-- send the user to the login form -->
    <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="../checkout/full/delivery.jsp"/>
    <dsp:droplet name="Redirect">
      <dsp:param name="url" value="../../user/login.jsp"/>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<cast:pageContainer>
<jsp:attribute name="bodyContent">
  <script type="text/javascript" src="${contextPath}/js/contactUs.js"></script>

  <dsp:setvalue param="selectedStep" value="1"/>
  <%@include file="checkout_steps.jspf" %>

  <%@ include file="../includes/delivery_popup.jsp" %>

  <fmt:message var="msgBack" key="msg.delivery.back"/>
  <c:choose>
    <c:when test="${orderDeliveryType=='clickAndCollect'}">
      <fmt:message var="msgAssign" key="msg.delivery.assign.cc"/>
    </c:when>
    <c:otherwise>
      <fmt:message var="msgAssign" key="msg.delivery.assign"/>
    </c:otherwise>
  </c:choose>


  <script type="text/javascript">
    function deliveryAction(action, address) {
      document.getElementsByName('/com/castorama/CastDeliveryFormHandler.action')[0].value = action;
      document.getElementsByName('/com/castorama/CastDeliveryFormHandler.addressName')[0].value = address;
      document.deliveryForm.submit();
    }
    function modifyAddress(address) {
      deliveryAction('Modify', address);
    }
    function updateAddress(address) {
      deliveryAction('Update', address);
    }
    function deleteAddress(address) {
      deliveryAction('Delete', address);
    }
    function backAddress(address) {
      deliveryAction('Back', address);
    }
    function assignAddress(address) {
      deliveryAction('Assign', address);
    }
    function newAddress(address) {
      deliveryAction('New', '');
    }
    function ccDeliveryAddress(address) {
      deliveryAction('UpdatePrAddr', '');
    }

    function verifyStockLevelWithPopupDelay(address_name, url, requestTimeout, popupDelayTime) {
        $("#stockIsNotEnough").hide();
        showPopup('stockVerificationPopup');
        setTimeout(function () {
            verifyStockLevel(address_name, url, requestTimeout);
        }, popupDelayTime);
    }

    function verifyStockLevelCallback(result, address_name) {
        var stockVerification = $("#stockVerificationPopup");
        switch (result) {
            case "notEnough":
                $("#stockVerification").hide();
                $("#stockIsNotEnough").show();
                break;
            case "enough":
                assignAddress(address_name);
                break;
            case "failed":
                assignAddress(address_name);
                break;
        }
    }

    function verifyStockLevel(address_name, url, requestTimeout) {

        var ajaxRequest = createXMLHttpRequest();
        ajaxRequest.open("GET", url, true);
        ajaxRequest.timeout = requestTimeout;
        ajaxRequest.onreadystatechange = function () {
            if (ajaxRequest.readyState == 4 && ajaxRequest.status == 200) {
                var response = $.trim(ajaxRequest.responseText);
                var responseObject = JSON.parse(response);
                var responseStatus = responseObject.processStatus;
                if (responseStatus == "STOCK_IS_NOT_ENOUGH") {
                    verifyStockLevelCallback("notEnough", address_name);
                } else {
                    verifyStockLevelCallback("enough", address_name)
                }
            }
        };
        ajaxRequest.ontimeout = function () {
            verifyStockLevelCallback("failed", address_name);
        };
        ajaxRequest.onerror = function () {
            verifyStockLevelCallback("failed", address_name);
        };
        ajaxRequest.send(null);
    }
  </script>
 
  <div class="content">
    <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
      <dsp:param name="value" bean="/atg/commerce/ShoppingCart.currentlySelected.commerceItems"/>
      <dsp:oparam name="true">
        <%@ include file="/checkout/full/includes/emptyShoppingMessageBlock.jspf" %>
      </dsp:oparam>
      <dsp:oparam name="false">
        <h1 class="title">
          <c:if test="${orderDeliveryType=='deliveryToHome'}">
            <fmt:message key="msg.delivery.ref"/>
          </c:if>
          <c:if test="${orderDeliveryType=='clickAndCollect'}">
            <fmt:message key="cc.retrait.en.magasin"/>
          </c:if>
        </h1>

        <div class="leftPanel">

          <dsp:include page="/user/includes/profileErrorBlock.jsp">
            <dsp:param name="bean" value="/com/castorama/CastDeliveryFormHandler"/>
          </dsp:include>

            <dsp:form name="castoramaCardForm" id="castoramaCardForm">
                <dsp:input type="hidden" bean="${castoramaCardNumber}" id="castoramaCardNumber"/>
                <dsp:input type="hidden" bean="${castCardNumberCorrect}" id="isCastoramaCardNumberCorrect"/>
                <dsp:input type="hidden" bean="CastDeliveryFormHandler.updateCastoramaCard" value=""/>
            </dsp:form>

          <dsp:form action="${originatingRequest.requestURL}" method="post" name="deliveryForm" id="deliveryForm">

			<div id="castoramaCardBlockDelivery" >
			<div class="formMainBlock">
			<div class="formContent grayCorner grayCornerGray">
			<div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
			<div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
			<div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
			<div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
			<div class="cornerOverlay" >
			<div id="castoramaCardSmall">&nbsp;</div>
			<div id="castoramaCardInfo">
				<h3><fmt:message key="msg.delivery.castorama.card.presence"/></h3>
				<b><fmt:message key="msg.delivery.castorama.card.benefits"/></b><br/><br/>
				<fmt:message key="msg.delivery.castorama.card.number"/>
				<dsp:input bean="${castoramaCardNumber}" maxlength="19" size="30" type="text"  iclass="i-text" name="castoramaCardNumber" id="castoramaCardNumber" onclick="validateCastoramaCardNumber(this.value);" onkeyup="validateCastoramaCardNumber(this.value);" />
				<dsp:input type="hidden" bean="${castCardNumberCorrect}" id="castoramaCardNumberServerValue" value="true"/>
				<span id="validateCardNumber">
				<span id="redCross" style="display: none; visibility: hidden;"><img src="/images/icoRedCross.png" />&nbsp;&nbsp;</span>
				<span id="greenCheck" style="display: none; visibility: hidden;"><img src="/images/icoGreenCheck.png" />&nbsp;&nbsp;</span>
				<img src="/images/icoBlueQuestion.png" id="icoBlueQuestion" alt="<fmt:message key="msg.account.informations.castorama.card.tooltip" />" />
				</span>
			</div>
			<div id="castoramaCardBacksideBig">&nbsp;</div>
			<div id="arrowBlueCastoramaCardBarcode">&nbsp;</div>
			</div></div></div></div>
			<br/>

            <dsp:input type="hidden" bean="CastDeliveryFormHandler.clickAndCollectDelivery"
                       value="${orderDeliveryType == 'clickAndCollect'}"/>
            <dsp:input type="hidden" bean="CastDeliveryFormHandler.addressName" value=""/>

            <div class="contactBlockLayout">

              <dsp:getvalueof var="addresses" bean="Profile.secondaryAddresses"/>
              <dsp:test var="objectSize" value="${addresses}"/>

              <c:choose>
                <c:when test="${objectSize.size==0 or orderDeliveryType=='clickAndCollect'}">
                  <dsp:include page="../../user/includes/address_info.jsp">
                    <dsp:param name="index" value="0"/>
                    <dsp:param name="address_type" value="msg.delivery.primary"/>
                    <dsp:param name="one" value="true"/>
                    <dsp:param name="address" bean="Profile.billingAddress"/>
                    <dsp:param name="control" value="modify.assign"/>
                    <dsp:param name="isCCOrder" value="${orderDeliveryType=='clickAndCollect'}"/>
                    <dsp:param name="selectedOrderId" value="${selectedOrderId}"/>
                  </dsp:include>
                </c:when>
                <c:otherwise>
                  <dsp:include page="../../user/includes/address_info.jsp">
                    <dsp:param name="index" value="0"/>
                    <dsp:param name="address_type" value="msg.delivery.primary"/>
                    <dsp:param name="address" bean="Profile.billingAddress"/>
                    <dsp:param name="control" value="modify.assign"/>
                  </dsp:include>
                </c:otherwise>
              </c:choose>
              <c:if test="${orderDeliveryType == 'deliveryToHome'}">
                <dsp:droplet name="IsEmpty">
                  <dsp:param bean="Profile.secondaryAddresses" name="value"/>
                  <dsp:oparam name="false">
                    <dsp:droplet name="ForEach">
                      <dsp:param name="sortProperties" value="+_key"/>
                      <dsp:param bean="Profile.secondaryAddresses" name="array"/>
                      <dsp:oparam name="output">
                        <dsp:include page="../../user/includes/address_info.jsp">
                          <dsp:param name="index" value="index"/>
                          <dsp:param name="address_type" value="msg.delivery.secondary"/>
                          <dsp:param name="address_name" param="key"/>
                          <dsp:param name="address" param="element"/>
                          <dsp:param name="control" value="modify.delete.assign"/>
                        </dsp:include>
                      </dsp:oparam>
                    </dsp:droplet>
                  </dsp:oparam>
                </dsp:droplet>
              </c:if>
              <div class="clear"><!--~--></div>
            </div>
            <div class="clear"><!--~--></div>
            <div class="formMainBlock">
              <c:if test="${orderDeliveryType == 'deliveryToHome'}">
                <c:choose>
                  <c:when test="${objectSize.size<3}">
                    <h2><fmt:message key="msg.delivery.newaddress"/></h2>

                    <div class="formContent grayCorner grayCornerGray">
                      <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
                      <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
                      <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
                      <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
                      <div class="cornerOverlay">
                        <dsp:getvalueof var="content" value="profile"/>
                        <dsp:getvalueof var="bean" value="CastDeliveryFormHandler"/>
                        <dsp:include page="/user/includes/contentMyAddress.jsp">
                          <dsp:param name="content" value="${content}"/>
                          <dsp:param name="bean" value="${bean}"/>
                        </dsp:include>
                      </div>
                    </div>
                    <div class="formButtons">
                      <span class="ccBascetButton">
                        <button class="ccBascetButton" type="button"
                                onclick="javascript:backAddress();">${msgBack}</button>
                      </span>
                      <span class="ccBascetButton blue">
                        <button class="ccBascetButton" type="button"
                                onclick="javascript:newAddress();">${msgAssign}</button>
                      </span>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div class="clear"><!--~--></div>
                    <div class="formButtons">
                      <span class="inputButton gray left">
                        <input type="button" value="${msgBack}" onclick="javascript:backAddress();"/>
                      </span>
                    </div>
                    <br/>
                  </c:otherwise>
                </c:choose>
              </c:if>
              <dsp:getvalueof var="billingAddress" bean="Profile.billingAddress.address1"/>
              <c:if test="${orderDeliveryType == 'clickAndCollect' and empty billingAddress}">
                <h2><fmt:message key="msg.delivery.newaddress.cc"/></h2>

                <div class="formContent grayCorner grayCornerGray">
                  <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
                  <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
                  <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
                  <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
                  <div class="cornerOverlay">
                    <dsp:getvalueof var="content" value="profile"/>
                    <dsp:getvalueof var="bean" value="CastDeliveryFormHandler"/>
                    <dsp:include page="/user/includes/contentMyAddress.jsp">
                      <dsp:param name="content" value="${content}"/>
                      <dsp:param name="bean" value="${bean}"/>
                      <dsp:param name="isCCOrder" value="true"/>
                    </dsp:include>
                  </div>
                </div>
                <div class="formButtons">
                  <span class="ccBascetButton">
                    <button class="ccBascetButton" type="button" onclick="javascript:backAddress();">${msgBack}</button>
                  </span>
                  <span class="ccBascetButton blue">
                    <button class="ccBascetButton" type="button"
                            onclick="javascript:ccDeliveryAddress();">${msgAssign}</button>
                  </span>
                </div>
              </c:if>
            </div>
            <dsp:input type="hidden" bean="CastDeliveryFormHandler.action" value=""/>
            
            <br/>
            <br/>
            <c:if test="${orderDeliveryType == 'deliveryToHome'}">
              <%@include file="gift.jspf" %>
            </c:if>
            
          </dsp:form>
          <div class="clear"><!--~--></div>
          <div class="clear"></div>
        </div>

        <div class="rightPanel">
          <dsp:getvalueof var="ccDeliveryMessage" bean="Profile.currentLocalStore.ccDeliveryMessage"/>
          <c:if test="${orderDeliveryType == 'clickAndCollect' and not empty ccDeliveryMessage}">
            <div class="infoLevraison">
                ${ccDeliveryMessage}
            </div>
            <br/>
          </c:if>
          <c:if test="${orderDeliveryType == 'deliveryToHome'}">
            <div class="infoLevraison">
              <h4><fmt:message key="msg.delivery.info"/></h4><br/>
              <dsp:valueof bean="/com/castorama/CastConfiguration.deliveryPageMessage" valueishtml="true"/>
            </div>
            <br/>
          </c:if>
		  <dsp:include page="../cart_info.jsp">
            <dsp:param name="cTotalWeight" value="${cTotalWeight}"/>
            <dsp:param name="cDeliveriesCount" value="${cDeliveriesCount}"/>
            <dsp:param name="showCastCardPrice" value="${true}"/>
          </dsp:include>
        </div>
      </dsp:oparam>
    </dsp:droplet>
  </div>
  <div class="clear"><!--~--></div> 
  <dsp:include page="../../user/includes/adviceBlock.jsp">
    <dsp:param name="hideFAQBannner" value="true"/>
  </dsp:include>
  <div class="clear"><!--~--></div>
  <%@include file="includes/stockVerification.jspf" %>
  

</jsp:attribute>
</cast:pageContainer>
<div id="popupDiv"></div>
</dsp:page>

<script>
function castoramaCardToolTip(targetItem, tooltipName) {
	
	$("body").append("<div class='"+tooltipName+"' ><div class=\"whiteCornerLeft\">&nbsp;</div><p>"+$(targetItem).attr('alt')+"</p></div>");
	var myTooltip = $("."+tooltipName);
	myTooltip.hide();

	$(targetItem).removeAttr("alt").mouseover(function(){
	myTooltip.show(); 
	}).mousemove(function(kmouse){
	myTooltip.css({left:kmouse.pageX+15, top:kmouse.pageY-15});
	}).mouseout(function(){
	myTooltip.hide(); });
}

$(document).ready(function(){
	castoramaCardToolTip("#icoBlueQuestion","castoramaCardTooltip");
});

</script>
