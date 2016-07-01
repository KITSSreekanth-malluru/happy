<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
  <%-- getting values from omniture variables --%>
  <dsp:getvalueof var="tagCommanderPState" value="${omniturePState}"/>
  <dsp:getvalueof var="tagCommanderPageName" value="${omniturePageName}"/>
  <dsp:getvalueof var="tagCommanderNewsletterRegistration" value="${omnitureNewsletterRegistration}"/>
  <dsp:getvalueof var="tagCommanderSearchKeyword" value="${omnitureSearchKeyword}"/>
  <dsp:getvalueof var="tagCommanderSearchResults" value="${omnitureSearchResults}"/>
  <dsp:getvalueof var="tagCommanderProducts" value="${omnitureProducts}"/>
  <dsp:getvalueof var="tagCommanderPromoCodeUsed" value="${omniturePromoCodeUsed}"/>
  
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  
  <%-- server side --%>
  <dsp:getvalueof var="isUserNotLogin" bean="/atg/userprofiling/Profile.transient"/>
  <dsp:getvalueof var="tagCommanderUserId" bean="/atg/userprofiling/Profile.id"/>  
  <dsp:getvalueof var="tagCommanderUserId" value="${!isUserNotLogin ? tagCommanderUserId : ''}"/>
  <dsp:getvalueof var="tagCommanderLoginStatus" value="${!isUserNotLogin ? 'logged-in' : 'anonymous'}"/>
  
  <%-- set value for s_SearchType --%>
  <dsp:getvalueof var="pageNum" param="pageNum"/>
  <c:if test="${(not empty pageNum && pageNum == 1) || (empty pageNum)}">
    <dsp:getvalueof var="tagCommanderSearchType" value="${osearchmode}"/>
  </c:if>
  
  <%-- on product's detail page --%>
  <c:if test="${not empty tagCommanderProductId or not empty tagCommanderGroupedProductId}">
    <c:if test="${empty tagCommanderProductId}">
      <dsp:getvalueof var="tagCommanderProductId" value="${tagCommanderGroupedProductId}"/>
    </c:if>
    
    <dsp:droplet name="/atg/commerce/catalog/ProductLookup">
      <dsp:param name="id" value="${tagCommanderProductId}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="tagCommanderProductName" param="element.displayName"/>
        <dsp:getvalueof var="tagCommanderProdImgUrl" param="element.largeImage.url"/>
        
        <c:if test="${empty tagCommanderGroupedProductId}">
	        <dsp:droplet name="/com/castorama/droplet/BrandLookupDroplet">
	          <dsp:param name="product" param="element"/>
	          <dsp:oparam name="output">
	            <dsp:getvalueof var="tagCommanderProdVendor" param="brand.name"/>
	          </dsp:oparam>
	        </dsp:droplet>
        </c:if>
        
        <dsp:droplet name="/com/castorama/CastPriceRangeDroplet">
          <dsp:param name="productId" param="element.repositoryId"/>
          <dsp:oparam name="output">
            <%-- var 'skuId' contains id of SKU with lowest price --%>
            <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
            <dsp:droplet name="/atg/commerce/inventory/InventoryLookup">
              <dsp:param name="itemId" param="sku.repositoryId"/>
              <dsp:param name="useCache" value="true"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="stockLevel" param="inventoryInfo.stockLevel"/>
                <c:choose>
                  <c:when test="${stockLevel == 0}">
                    <dsp:getvalueof var="tagCommanderProductStock" value="no"/>
                  </c:when>
                  <c:otherwise>
                    <dsp:getvalueof var="tagCommanderProductStock" value="yes"/>
                  </c:otherwise>
                </c:choose>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
        
        <dsp:getvalueof var="tagCommanderProductUnitPriceATI" value="${allTaxIncludedPrice}"/>
        <dsp:getvalueof var="tagCommanderProductUnitPriceTF" value="${taxFreePrice}"/>
        
      </dsp:oparam>
    </dsp:droplet>
    
    <dsp:getvalueof var="tagCommanderProductCategory" value="${fn:replace(productOmniturePageName,':',',')}"/>
    <dsp:getvalueof var="tagLength" value="${fn:length(tagCommanderProductCategory)-1}"/>
    <dsp:getvalueof var="tagCommanderProductCategory" value="${fn:substring(tagCommanderProductCategory,0,tagLength)}"/>
  </c:if>

  <c:choose>
    <c:when test="${fn:contains(requestURI, 'cart.jsp') }">
      <dsp:getvalueof var="cart" bean="/atg/commerce/ShoppingCart.current"/>
    </c:when>
    <c:when test="${fn:contains(requestURI, 'confirmation.jsp') }">
      <dsp:getvalueof var="cart" bean="/atg/commerce/ShoppingCart.last"/>
      <dsp:getvalueof var="tagCommanderCurrencyCode" bean="/atg/commerce/ShoppingCart.last.priceInfo.currencyCode"/>
      <dsp:getvalueof var="tagCommanderZip" bean="/atg/commerce/ShoppingCart.last.shippingGroups[0].shippingAddress.postalCode"/>
      <dsp:getvalueof var="tagCommanderOrderEmail" bean="/atg/userprofiling/Profile.email" /> 
    </c:when>
    <c:otherwise>
      <dsp:getvalueof var="cart" bean="/atg/commerce/ShoppingCart.currentlySelected"/>
    </c:otherwise>
  </c:choose>
  
  <dsp:getvalueof var="tagCommanderCartId" value="${cart.id}"/>
  <dsp:getvalueof var="tagCommanderNbProducts" value="${cart.totalCommerceItemCount}"/>
  
  <c:if test="${fn:contains(requestURI, 'confirmation.jsp')}">
    <dsp:droplet name="/com/castorama/droplet/WebOrdersDroplet">
      <dsp:param name="targeter" bean="/atg/registry/RepositoryTargeters/Orders/WebOrders" />
      <dsp:oparam name="empty">
        <dsp:getvalueof var="tagCommanderNewCustomer" value="yes"/>
      </dsp:oparam>
      <dsp:oparam name="output">
        <dsp:getvalueof var="tagCommanderNewCustomer" value="no"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
  
  <%-- add server side logic here --%>
  
  <dsp:include page="/includes/tag_commanderCode.jsp">
    <%-- pass parameters to set to tc_vars[] array --%>
    
    <dsp:param name="tagCommanderPState" value="${tagCommanderPState}"/>
    
    <dsp:param name="tagCommanderPageName" value="${tagCommanderPageName}"/>
    <dsp:param name="tagCommanderUserId" value="${tagCommanderUserId}"/>
    <dsp:param name="tagCommanderNewsletterRegistration" value="${tagCommanderNewsletterRegistration}"/>
    <dsp:param name="tagCommanderLoginStatus" value="${tagCommanderLoginStatus}"/>
    <dsp:param name="tagCommanderSearchKeyword" value="${tagCommanderSearchKeyword}"/>
    <dsp:param name="tagCommanderSearchResults" value="${tagCommanderSearchResults}"/>
    <dsp:param name="tagCommanderSearchType" value="${tagCommanderSearchType}"/>
    
    <dsp:param name="tagCommanderProductName" value="${tagCommanderProductName}"/>
    <dsp:param name="tagCommanderProdImgUrl" value="${tagCommanderProdImgUrl}"/>
    <dsp:param name="tagCommanderProductUnitPriceATI" value="${tagCommanderProductUnitPriceATI}"/>
    <dsp:param name="tagCommanderProductUnitPriceTF" value="${tagCommanderProductUnitPriceTF}"/>

    <dsp:param name="tagCommanderProductStock" value="${tagCommanderProductStock}"/>
    <dsp:param name="tagCommanderProductCategory" value="${tagCommanderProductCategory}"/>
    <dsp:param name="tagCommanderProdVendor" value="${tagCommanderProdVendor}"/>
    
    <dsp:param name="tagCommanderProducts" value="${tagCommanderProducts}"/>
    <dsp:param name="tagCommanderProductDeliveryTime" value="${tagCommanderProductDeliveryTime}"/>
    <dsp:param name="tagCommanderProductDeliveryMode" value="${tagCommanderProductDeliveryMode}"/>
    <dsp:param name="tagCommanderCartId" value="${tagCommanderCartId}"/>
    <dsp:param name="tagCommanderPromoCodeUsed" value="${tagCommanderPromoCodeUsed}"/>

    <dsp:param name="tagCommanderOrderAmountTFWithoutSF" value="${tagCommanderOrderAmountTFWithoutSF}"/>
    <dsp:param name="tagCommanderOrderAmountTFWithSF" value="${tagCommanderOrderAmountTFWithSF}"/>
    <dsp:param name="tagCommanderOrderAmountATIWithoutSF" value="${tagCommanderOrderAmountATIWithoutSF}"/>
    <dsp:param name="tagCommanderOrderAmountATIWithSF" value="${tagCommanderOrderAmountATIWithSF}"/>
    <dsp:param name="tagCommanderDiscountAmountTF" value="${tagCommanderDiscountAmountTF}"/>
    <dsp:param name="tagCommanderDiscountAmountATI" value="${tagCommanderDiscountAmountATI}"/>
    <dsp:param name="tagCommanderShippingAmountTF" value="${tagCommanderShippingAmountTF}"/>
    <dsp:param name="tagCommanderShippingAmountATI" value="${tagCommanderShippingAmountATI}"/>
    
    <dsp:param name="tagCommanderNewCustomer" value="${tagCommanderNewCustomer}"/>
    <dsp:param name="tagCommanderPaymentMethod" value="${tagCommanderPaymentMethod}"/>
    <dsp:param name="tagCommanderCurrencyCode" value="${tagCommanderCurrencyCode}"/>

    <dsp:param name="tagCommanderZip" value="${tagCommanderZip}"/>
    
    <dsp:param name="tagCommanderOrderEmail" value="${tagCommanderOrderEmail}"/>
    <dsp:param name="tagCommanderNbProducts" value="${tagCommanderNbProducts}"/>
    
    <dsp:param name="tagCommanderProductsInfo" value="${tagCommanderProductsInfo}"/>
  </dsp:include>
</dsp:page>