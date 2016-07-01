<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <div class="productListe">
    <table class="productsTable grayTable" cellspacing="0" cellpadding="0">
      <colgroup>
        <col width="96"/>
        <col/>
        <col/>
        <col width="123"/>
        <col width="61"/>
      </colgroup>
      <tbody>
        <dsp:droplet name="ForEach">
          <dsp:param name="array" param="results"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="count" param="count"/>
            <c:choose>
              <c:when test="${count mod 2 == 0}">
                <tr class="odd">
              </c:when>
              <c:otherwise>
                <tr>
              </c:otherwise>
            </c:choose>
            <c:if test="${not empty productsList }">
	            <c:set var="productsList" value="${productsList}," scope="request"/>
	          </c:if>
            <dsp:include page="../../castCatalog/listProductTemplate.jsp">
              <dsp:param name="productId" param="element.document.properties.$repositoryId"/>
              <dsp:param name="isSearchResult" param="isSearchResult"/>
            </dsp:include>
            </tr>
          </dsp:oparam>
        </dsp:droplet>
      </tbody>
    </table>
  </div>
  <c:set var="omnitureProducts" value="${productsList}" scope="request"/>
</dsp:page>