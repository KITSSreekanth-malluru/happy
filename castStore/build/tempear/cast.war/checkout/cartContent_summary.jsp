<dsp:page>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>

  <dsp:getvalueof var="pricesList" param="pricesList"/>
  <dsp:getvalueof var="currencyCode" param="currencyCode"/>
  <dsp:getvalueof var="cssStyle" param="cssStyle"/>
  <dsp:getvalueof var="cssStyleWidth" param="cssStyleWidth"/>
  <dsp:getvalueof var="orderDeliveryType" param="thisOrder.deliveryType"/>

  <dsp:getvalueof var="total" value="${pricesList[5]}"/>

  <td class="productItem_left ${cssStyle}">
    <c:if test="${total > 0}">
      <div class="boxCartInnerWr">
        <dsp:param name="order" param="thisOrder"/>
        <%@ include file="includes/cartTotalTable.jspf" %>
        <div class="noCurrentBox"><!-- --></div>
      </div>
    </c:if>
  </td>
</dsp:page>