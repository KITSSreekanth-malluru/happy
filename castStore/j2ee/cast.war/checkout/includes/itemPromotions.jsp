<dsp:page>
  <dsp:importbean bean="/com/castorama/droplet/ItemPromotionsDroplet" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/For"/>

  <dsp:getvalueof var="showCardPrice" param="enabledCastCart"/>
  <dsp:getvalueof var="cardPriceCommerceItem" param="commerceItem.priceInfo.cardPrice" />
  <dsp:getvalueof var="isValidByDateCardPrice" param="commerceItem.priceInfo.IsValidByDateCardPrice" />
  <dsp:getvalueof var="cssStyle" param="style"/>
  <td class="productItem_left ${cssStyle}">
    <c:choose>
      <c:when test="${showCardPrice && cardPriceCommerceItem>0 && isValidByDateCardPrice}">
          <div class="boxCartInnerWr">
            <div class="boxCartPromotion cartPrix">
              <div class="boxCartPromotionImg">
                <img src="/images/cardCastorama/castaCard.png" alt="" title="" />
              </div>
              <p><fmt:message key="shoppinglist.castCardPromoution.text"/></p>
            </div>
            <div class="noCurrentBox"><!-- --></div>
          </div>
      </c:when>
      <c:otherwise>
        <dsp:droplet name="ItemPromotionsDroplet">
          <dsp:param name="commerceItem" param="commerceItem" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="itemsPromotions" param="itemsPromotions" />
            <dsp:droplet name="ForEach">
              <dsp:param name="array" param="itemsPromotions" />
              <dsp:param name="elementName" value="promotion" />
              
              <dsp:oparam name="outputStart">
                  <div class="boxItemsPromotions">
              </dsp:oparam>
              
              <dsp:oparam name="output">
                <dsp:getvalueof var="promoName" param="promotion.displayName" />
                <dsp:getvalueof var="description" param="promotion.description" />
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="promotion.media" />
                  <dsp:param name="elementName" value="media" />
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="mediaUrl" param="media.url" />
                  </dsp:oparam>
                </dsp:droplet>
                <c:if test="${not empty description}">
                  <c:choose>
                    <c:when test="${fn:startsWith(promoName, 'VenteFlash')==true}">
                      <dsp:getvalueof var="mediaUrl" value="../images/icoTimer.gif" />
                    </c:when>
                    <c:when test="${empty mediaUrl}">
                      <dsp:getvalueof var="mediaUrl" value="../images/icoOfferte.gif" />
                    </c:when>
                  </c:choose>
                  <div class="boxCartInnerWr">
                    <div class="boxCartPromotion">
                      <div class="boxCartPromotionImg">
                        <img src="${mediaUrl}" alt="" title=""/>
                      </div>
                      <p><dsp:valueof value="${description}" valueishtml="true" /></p>
                    </div>
                    <div class="noCurrentBox"><!-- --></div>
                  </div>
                </c:if>
                <dsp:getvalueof var="mediaUrl" value="" />
              </dsp:oparam>
              
              <dsp:oparam name="outputEnd">
                  </div>
              </dsp:oparam>
              
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </c:otherwise>
    </c:choose>
  </td>
</dsp:page>
