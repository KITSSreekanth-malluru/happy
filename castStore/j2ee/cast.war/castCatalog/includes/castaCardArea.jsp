<dsp:page>

  <dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>
  <dsp:importbean bean="/com/castorama/CastConfiguration"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="sku" param="sku"/>

  <script type="text/javascript" src="${contextPath}/js/ajax.js" ></script>

  <dsp:droplet name="PriceItem">
    <dsp:param name="item" value="${sku}"/>
    <dsp:oparam name="output">
      <dsp:param name="pricedItem" param="element"/>
      <dsp:getvalueof var="listPrice" param="pricedItem.priceInfo.listPrice"/>
      <dsp:getvalueof var="salePrice" param="pricedItem.priceInfo.salePrice"/>
      <dsp:getvalueof var="amount" param="pricedItem.priceInfo.amount"/>
      <c:choose>
        <c:when test="${amount>0}">
          <dsp:getvalueof var="price" value="${amount}"/>
        </c:when>
        <c:otherwise>
          <c:choose>
            <c:when test="${salePrice>0}">
              <dsp:getvalueof var="price" value="${salePrice}"/>
            </c:when>
            <c:otherwise>
              <dsp:getvalueof var="price" value="${listPrice}"/>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>

  <c:if test="${price>45}">
    <div class="castaCard" id="castaCard">
      <dsp:img id="cardAtout" style="display:none;" src="/images/cardCastorama/castaCard.png" alt="Castorama"/>
      <div id="cardPrice" style="display:none;"><dsp:valueof value="${price}"/></div>
      <div id="cardMode" style="display:none;">S</div>
      <dsp:getvalueof var="url" bean="CastConfiguration.urlLegalNotice"/>
      <div id="urlLegalNotice" style="display:none;">${contextPath}/${url}</div>
      <div id="div_cast_card"></div>
      <script language="javascript">
      $(document).ready(function(){
        lanceRequeteAjaxAtout();
      });
      </script>
    </div>
  </c:if>
</dsp:page>
