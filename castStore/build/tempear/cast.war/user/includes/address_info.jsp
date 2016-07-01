<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/com/castorama/CastDeliveryFormHandler"/>
  <dsp:importbean bean="/com/castorama/integration/webservice/inventory/WebServiceConfiguration"/>
  <dsp:importbean bean="/com/castorama/integration/webservice/inventory/RequestUrlBuilderDroplet"/>

  <dsp:getvalueof var="address_name" param="address_name" />
  <dsp:getvalueof var="address_type" param="address_type" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="isCCOrder" param="isCCOrder" />
  <dsp:getvalueof var="billingAddress" param="address.address1" />
  <dsp:getvalueof var="selectedOrderId" param="selectedOrderId"/>
  <dsp:getvalueof var="ajaxRequestUrlTemplate" bean="WebServiceConfiguration.ajaxRequestUrlTemplate"/>
  <dsp:getvalueof var="ajaxRequestTimeout" bean="WebServiceConfiguration.ajaxRequestTimeout"/>
  <dsp:getvalueof var="inventoryCheckIsActive" bean="WebServiceConfiguration.inventoryCheckIsActive"/>
  <dsp:getvalueof var="popupDelayTime" bean="WebServiceConfiguration.popupDelayTime"/>
  
<div class="wrapperContactContent">  
  <dsp:droplet name="Switch">
    <dsp:param name="value" param="one" />
    <dsp:oparam name="true">
      <div class="contactContentBlock outerButtons horizontalExpanded">
    </dsp:oparam>
    <dsp:oparam name="default">
      <div class="contactContentBlock outerButtons <c:if test="${!isCCOrder}">deliveryToHome</c:if>">
    </dsp:oparam>
  </dsp:droplet>
  
    <h3><fmt:message key="${address_type}" /></h3>
    <dsp:getvalueof var="address_name" param="address_name"/>
    <c:if test="${not empty  address_name}">
      <div class="subtitle"><fmt:message key="msg.address.nom" />&nbsp;<dsp:valueof param="address_name"/></div>
    </c:if>
    
    <c:choose>
      <c:when test="${isCCOrder and empty billingAddress}">
        <p class="emptyBillingAddress"><fmt:message key="msg.address.cc.empty" /></p>
        <script>
          $('.contactContentBlock').css('height','auto');
        </script>
      </c:when>
      <c:otherwise>
        <div class="contactContent">

        <dsp:include page="contentAddressInfo.jsp">
          <dsp:param name="address" param="address" />
        </dsp:include>
          
          <dsp:droplet name="Switch">
            <dsp:param name="value" param="control" />
            <dsp:oparam name="modify.assign">
              <a href="javascript:void(0)" id="modifierPrAdresseLink"><fmt:message key="msg.address.modify.primary" /></a>
            </dsp:oparam>
            <dsp:oparam name="modify.delete.assign">
              <a href="javascript:void(0)" id="${address_name}" name="modifierAdresse">
                <fmt:message key="msg.address.modifier" />
              </a> 
              <a href="javascript:deleteAddress('${address_name}');" >
                <fmt:message key="msg.address.delete" />
              </a>
            </dsp:oparam>
            <dsp:oparam name="default" />
          </dsp:droplet>
        </div>
      </c:otherwise>
    </c:choose>
  </div>

  <c:choose>
      <c:when test="${isCCOrder && inventoryCheckIsActive}">
        <c:if test="${not empty billingAddress}">

          <dsp:droplet name="RequestUrlBuilderDroplet">
              <dsp:param name="orderId" value="${selectedOrderId}"/>
              <dsp:oparam name="output">
                  <dsp:getvalueof var="requestUrl" param="requestUrl"/>
              </dsp:oparam>
          </dsp:droplet>

          <fmt:message var="msgBack" key="msg.delivery.back" />
          <fmt:message var="msgAssign" key="msg.delivery.assign.cc" />
            <div class="formButtons">
                <span class="ccBascetButton">
                    <button class="ccBascetButton" type="button" onclick="javascript:backAddress();">${msgBack}</button>
                </span>
                <span class="ccBascetButton blue">
                    <button class="ccBascetButton" type="button"
                            onclick="verifyStockLevelWithPopupDelay('${address_name}','${requestUrl}',${ajaxRequestTimeout},${popupDelayTime}); urchinTracker('${requestUrl}');">
                            ${msgAssign}
                    </button>
                </span>
            </div>
        </c:if>
      </c:when>
      <c:otherwise>
        <fmt:message var="msgAssign" key="msg.delivery.assign" />
        <div class="formButtons">
          <span class="ccBascetButton blue">
            <button class="ccBascetButton" type="button" onclick="javascript:assignAddress('${address_name}');" >${msgAssign}</button>
          </span>
        </div>
      </c:otherwise>
  </c:choose>
</div>
  <dsp:getvalueof var="titleEditAddress" value="Modifier une adresse"/>
       <script>
     <!--  
         $("#modifierPrAdresseLink").click(function () {         
              var html = $.ajax({
                url: "/store/user/includes/addressPopup.jsp",
                data: "popupContainerID=modifierPrAdresse&title=${titleEditAddress}&content=profile&backURL=${contextPath}/checkout/full/delivery.jsp&isCCOrder=${isCCOrder}",
                async: false
               }).responseText;
               $('#popupDiv').html(html);
               showPopup('modifierPrAdresse');  
         });
         $("a[name='modifierAdresse']").click(function () {
              var addressName = $(this).attr("id");          
              var html = $.ajax({
                url: "/store/user/includes/addressPopup.jsp",
                data: "popupContainerID=modifierAdresse&title=${titleEditAddress}&type=edit&backURL=${contextPath}/checkout/full/delivery.jsp&addressName=" + addressName,
                async: false
               }).responseText;
               $('#popupDiv').html(html);
               showPopup('modifierAdresse');  
         });
       //-->  
     </script>
</dsp:page>