<dsp:page>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceItem"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceDroplet"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>  
  <dsp:getvalueof var="listPrice" param="pricedItem.priceInfo.listPrice"/>
  <dsp:getvalueof var="salePrice" param="pricedItem.priceInfo.amount"/>
  <dsp:getvalueof var="currencyCode" vartype="java.lang.String" param="pricedItem.priceInfo.currencyCode"/>  
  <dsp:droplet name="CastPriceDroplet">
    <dsp:param name="listPrice" value="${listPrice}"/>
    <dsp:param name="salePrice" value="${salePrice}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="percent" param="percent"/>
      <dsp:getvalueof var="discount" param="economy"/>
      <dsp:getvalueof var="onDiscount" param="onDiscount"/>
      <m:jsonObject name="price">
        <c:choose>
          <c:when test="${onDiscount}">
            <json:property name="listPrice">
              <%-- KS :temporarily commented: due to last changes: all info from Castorama.fr : price ,aisle stock and shelf shouldn't be shown<fmt:formatNumber value="${listPrice}" type="number" maxFractionDigits="2" minFractionDigits="2"/>--%>
            </json:property>
            <json:property name="salePrice">
              <%-- KS :temporarily commented: due to last changes: all info from Castorama.fr : price ,aisle stock and shelf shouldn't be shown  <fmt:formatNumber value="${salePrice}" type="number" maxFractionDigits="2" minFractionDigits="2"/>--%>
            </json:property>
          </c:when>
          <c:otherwise>
            <json:property name="listPrice">
              <%-- KS :temporarily commented: due to last changes: all info from Castorama.fr : price ,aisle stock and shelf shouldn't be shown <fmt:formatNumber value="${listPrice}" type="number" maxFractionDigits="2" minFractionDigits="2"/>--%>
            </json:property>
          </c:otherwise>
        </c:choose>
      </m:jsonObject>
     
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>