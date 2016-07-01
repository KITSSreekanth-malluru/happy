<dsp:page>
<dsp:getvalueof var="pageType" param="pageType"/>

<dsp:getvalueof var="isCC" param="store.retraitMagasin"/>
<dsp:getvalueof var="isProductDetails" value="${(not empty pageType) and (pageType == 'productDetails')}"/>
<dsp:getvalueof var="displayVUMMessage" value="${false}"/>
<dsp:getvalueof var="skuId" param="sku.repositoryId"/>
<dsp:getvalueof var="store" param="store"/>
<dsp:getvalueof var="quantity" param="quantity"/>
<dsp:getvalueof var="packId" param="packId"/>
<c:if test="${not empty packId}">
	<dsp:getvalueof var="quantity" value="1"/>
</c:if>

<dsp:getvalueof var="intPriceBlockClasses" value="productPrix ${pageType}PriceBlock"/>
<dsp:getvalueof var="extPriceBlockClasses" value="fPrix priceBlockUno ${pageType}PriceBlock showAddToCartButtontrue isMultiSkuProductfalse"/>
<div class="${extPriceBlockClasses} storeIsCC${storeIsCC}">
    <div class="${intPriceBlockClasses}">
      <dsp:include page="skuPriceInfo.jsp">
        <dsp:param name="pageType" value="productDetails"/>
        <dsp:param name="productId" param="productId"/>
        <dsp:param name="sku" param="sku"/>
        <dsp:param name="multiSku" value="${false}"/>
        <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}" />
		<dsp:param name="displayVUMMessage" value="${displayVUMMessage}" />
		<dsp:param name="displayVUMMessageStyle" value="vumMsg"/>
		<dsp:param name="store" value="${store}"/>
		<dsp:param name="isCC" value="${isCC}"/>
		</dsp:include>
    </div>
	
  <%--<dsp:form method="post" formid="any">--%>
	<div class="numbAndPriceV2">            
	<div class="fQty numberItemsV2">
		<span><fmt:message key="msg.cart.quantity"/></span>
		<span>${quantity}</span>
	</div>
	</div>
  <%--</dsp:form>--%>

</div>
    <%@include file="deliverOrCCwrInfo.jsp" %>
</dsp:page>
