<div class="deliverOrCCwr">
  <ul>
      <li class="deliverMessage" style="padding:0!important;">
        <a href="#" class="deliver toolTipHover" >
		  <dsp:getvalueof var="expeditionTrimmed" value="sous 1 semaine"/>
		  <dsp:droplet name="/com/castorama/droplet/CastShippingDroplet">
			<dsp:param name="skuId" value="${skuId}"/>
			<dsp:param name="quantity" param="quantity"/>
			  <dsp:oparam name="output">
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
				  <dsp:param name="value" param="expeditionPNS"/>
				  <dsp:oparam name="false">
					<dsp:getvalueof param="expeditionPNS.deliveryTime" var="expedition"/>
					<dsp:getvalueof var="expeditionTrimmed" value="${fn:trim(expedition)}"/>
				  </dsp:oparam>
				</dsp:droplet>
			  </dsp:oparam>
		   </dsp:droplet>		   
          <fmt:message key="msg.cart.delivery_at"/>&nbsp;${expeditionTrimmed}
          <c:if test='${isCC}'>
            <dsp:include page="freeShipping.jsp">
              <dsp:param name="sku" param="sku"/>
            </dsp:include>
          </c:if>
        </a>
    </li>
    <c:if test='${isCC}'>
      <li class="pickupMessage">
          <dsp:getvalueof var="delayTime" param="store.ccDelayPeriod"/>
          <dsp:getvalueof var="delayUnit" param="store.ccDelayPeriodUnit"/>
          <fmt:message key="header.mon.magasin.delayunit.hour" var="hour"/>
          <fmt:message key="header.mon.magasin.delayunit.day" var="day"/>
          <fmt:message key="header.mon.magasin.delayunit.days" var="days"/>
          <dsp:getvalueof var="displayDelayUnit" value="${(delayUnit == 0)?hour:(delayTime == 1?day:days)}" />
          <a href="#" class="pickup toolTipHover"><fmt:message key="header.mon.magasin.retrait" />&nbsp;${delayTime}${displayDelayUnit}</a>
      </li>
    </c:if>
  </ul>
</div>