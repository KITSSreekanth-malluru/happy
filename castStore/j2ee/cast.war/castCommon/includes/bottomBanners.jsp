<dsp:page>

  <dsp:importbean bean="/atg/registry/Slots/HomePagePromo7Slot"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePagePromo8Slot"/>
  <dsp:importbean bean="/atg/registry/Slots/HomePagePromo9Slot"/>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  
  <dsp:getvalueof var="cardPriceBanner" param="cardPriceBanner"/>
  <c:choose>
    <c:when test="${cardPriceBanner}">
      <div class="leftCol fourBanners">
    </c:when>
    <c:otherwise>
      <div class="leftCol">
    </c:otherwise>
  </c:choose>
  
    <div class="giftCard">
      <dsp:include page="../promoInformationTargeter.jsp">
        <dsp:param name="homePagePromoBean" bean="HomePagePromo7Slot"/>
        <dsp:param name="width" value="196"/>
        <dsp:param name="height" value="137"/>
        <dsp:param name="flashId" value="HomePagePromo6Slot"/>
      </dsp:include>
    </div>
    <div class="giftCard">
      <dsp:include page="../promoInformationTargeter.jsp">
        <dsp:param name="homePagePromoBean" bean="HomePagePromo8Slot"/>
        <dsp:param name="width" value="196"/>
        <dsp:param name="height" value="137"/>
        <dsp:param name="flashId" value="HomePagePromo7Slot"/>
      </dsp:include>
    </div>
    
    <c:choose>
      <c:when test="${cardPriceBanner}">
        <dsp:importbean bean="/atg/registry/Slots/CastCardPriceSlot"/>
        
        <div class="giftCard">
	      <dsp:include page="../promoInformationTargeter.jsp">
	        <dsp:param name="homePagePromoBean" bean="HomePagePromo9Slot"/>
	        <dsp:param name="width" value="196"/>
	        <dsp:param name="height" value="137"/>
	        <dsp:param name="flashId" value="HomePagePromo8Slot"/>
	      </dsp:include>
	     </div>
	     <div class="giftCard lastGiftCard">
	      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	      <dsp:include page="../promoInformationTargeter.jsp">
	        <dsp:param name="homePagePromoBean" bean="CastCardPriceSlot"/>
	        <dsp:param name="width" value="196"/>
	        <dsp:param name="height" value="137"/>
	        <dsp:param name="url" value="${contextPath}/offres-carte-castorama"/>
	      </dsp:include>
	     </div>
      </c:when>
      <c:otherwise>
         <div class="giftCard lastGiftCard">
	      <dsp:include page="../promoInformationTargeter.jsp">
	        <dsp:param name="homePagePromoBean" bean="HomePagePromo9Slot"/>
	        <dsp:param name="width" value="196"/>
	        <dsp:param name="height" value="137"/>
	        <dsp:param name="flashId" value="HomePagePromo8Slot"/>
	      </dsp:include>
	     </div>
      </c:otherwise>
    </c:choose>
    
  </div>
</dsp:page>
