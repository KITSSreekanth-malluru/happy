<c:choose>
  <c:when test="${addToCartMode == 'web' || addToCartMode == 'both'}">
    <c:set var="deliverClasses" value="deliverMessage" />
  </c:when>
  <c:otherwise>
    <c:set var="deliverClasses" value="deliverMessage hidden" />
  </c:otherwise>
</c:choose>

<div class="deliverOrCCwr">
  <ul>
    <c:if test="${!isMultiSkuProduct or isProductPageTabs}">
      <li class="${deliverClasses}">
        <a href="#" class="deliver toolTipHover">
          <span><fmt:message key="msg.cart.delivery_at"/></span>
          <span class="deliveryProd${skuId} hidden">sous 1 semaine</span>
          <c:if test='${storeIsCC}'>
            <dsp:include page="freeShipping.jsp">
              <dsp:param name="sku" param="sku"/>
            </dsp:include>
          </c:if>
        </a>
        <dsp:getvalueof var="deliveryTooltip" bean="Profile.catalog.deliveryTooltip"/>
        <c:if test="${(not empty deliveryTooltip) && (pageType == 'productDetails')}">
          <div class="deliverToolTipWr">
            <div class="contentContainer">${deliveryTooltip}</div>
            <img src="${contextPath}/images/blank.gif" />
          </div>
        </c:if>
    </li>
    <c:if test='${storeIsCC}'>
      <li class="pickupMessage">
        <c:if test="${addToCartMode == 'local' || addToCartMode == 'both'}">
          <dsp:getvalueof var="delayTime" bean="Profile.currentLocalStore.ccDelayPeriod"/>
          <dsp:getvalueof var="delayUnit" bean="Profile.currentLocalStore.ccDelayPeriodUnit"/>
          <fmt:message key="header.mon.magasin.delayunit.hour" var="hour"/>
          <fmt:message key="header.mon.magasin.delayunit.day" var="day"/>
          <fmt:message key="header.mon.magasin.delayunit.days" var="days"/>
          <dsp:getvalueof var="displayDelayUnit" value="${(delayUnit == 0)?hour:(delayTime == 1?day:days)}" />
          <a href="javascript:void(0);" class="pickup toolTipHover"><fmt:message key="header.mon.magasin.retrait" />&nbsp;${delayTime}${displayDelayUnit}</a>
          <dsp:getvalueof var="ccTooltip" bean="Profile.catalog.ccTooltip"/>
          <c:if test="${(not empty ccTooltip) && (pageType == 'productDetails')}">
            <div class="deliverToolTipWr">
                <div class="contentContainer">${ccTooltip}</div>
               <img src="${contextPath}/images/blank.gif" />
              </div>
            </c:if>
          </c:if>
        </li>
      </c:if>
    </c:if>
  </ul>
</div>