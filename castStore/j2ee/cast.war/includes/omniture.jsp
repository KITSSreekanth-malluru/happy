<%@ taglib prefix="dsp"
  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
  <dsp:importbean bean="/atg/commerce/search/refinement/RefinementValueDroplet" />
  <dsp:importbean bean="/com/castorama/droplet/CastShippingDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/castorama/mail/EmailAFriendFormHandler"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/search/repository/FacetSearchTools"/>

  <dsp:getvalueof var="omniturePageName" param="omniture-pageName"/>
  <dsp:getvalueof var="channelParam" param="omniture-channel"/>
  
  <dsp:getvalueof var="currentCategoryId" param="categoryId" />
  
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  
  <dsp:getvalueof var="handle_create" param="HANDLE_CREATE"/>
  <dsp:getvalueof var="newsletters" param="newsletters"/>
  <dsp:getvalueof var="createSubscr" param="createSubscr"/>
  <dsp:getvalueof var="savedCartContent" param="savedCartContent" />
  <dsp:getvalueof var="xForwardedFor" param="xForwardedFor"/>
  <dsp:getvalueof var="featuredSkuId" param="featuredSkuId" />

  <c:choose>
    <c:when test="${fn:contains(requestURI, 'delivery.jsp') || fn:contains(requestURI, 'payment.jsp')}">
      <dsp:getvalueof var="orderDeliveryType" bean="/atg/commerce/ShoppingCart.currentlySelected.deliveryType"/>
    </c:when>
    <c:when test="${fn:contains(requestURI, 'confirmation.jsp')}">
      <dsp:getvalueof var="orderDeliveryType" bean="/atg/commerce/ShoppingCart.last.deliveryType"/>
    </c:when>
  </c:choose>
  
  <%@ include file="omnitureFacets.jspf" %>
  
  <c:if test="${!fn:contains(requestURI, 'delivery') }">    
    <c:if test="${not empty handle_create && handle_create == 'true' }">  
      <fmt:message var="omniturePState" key="omniture.state.account"/>
    </c:if>
    <c:if test="${not empty createSubscr && createSubscr=='true'}">
      <fmt:message var="omniturePState" key="omniture.state.newsletters"/>
      <c:if test="${empty newsletters}">
        <dsp:getvalueof var="reseiveOffers" bean="CastNewsletterFormHandler.value.reseiveOffers"/>
	      <dsp:getvalueof var="receiveEmail" bean="CastNewsletterFormHandler.value.receiveEmail"/>
	      <c:choose>
	        <c:when test="${reseiveOffers && !receiveEmail}">
	          <fmt:message var="omnitureNewsletterRegistration" key="omniture.newsletters.partners"/>
	        </c:when>
	        <c:when test="${reseiveOffers &&  receiveEmail}">
	          <fmt:message var="omnitureNewsletterRegistration" key="omniture.newsletters.both"/>
	        </c:when>
	        <c:when test="${!reseiveOffers &&  receiveEmail}">
	          <fmt:message var="omnitureNewsletterRegistration" key="omniture.newsletters.newsletter"/>
	        </c:when>
	        <c:otherwise>
	          <fmt:message var="omnitureNewsletterRegistration" key="omniture.newsletters.none"/>
	        </c:otherwise>
	      </c:choose>
      </c:if>
    </c:if>
  </c:if>
  <c:if test="${not empty newsletters}">
    <fmt:message var="omnitureNewsletterRegistration" key="omniture.newsletters.${newsletters}"/>
  </c:if>
  
  <%@ include file="omnitureProdList.jspf" %>

  <c:choose>
    <c:when test="${fn:contains(requestURI, '404.jsp') }">
      <%-- omniture tags for error page 404.jsp --%>
      <fmt:message var="omniturePState" key="omniture.state.404"/>
    </c:when>
    <c:when test="${fn:contains(requestURI, 'delivery.jsp') }">
      <%-- omniture tags for delivery.jsp --%>
      <fmt:message var="omniturePState" key="omniture.state.delivery"/>
      <c:choose>
        <c:when test="${empty handle_create}">  
          <fmt:message var="accountStatus" key="omniture.accountStatus.exist"/>
        </c:when>
        <c:when test="${not empty handle_create && handle_create=='true'}"> 
          <fmt:message var="accountStatus" key="omniture.accountStatus.new"/>
        </c:when>
      </c:choose>
      <fmt:message var="omniturePageName" key="omniture.pageName.purchase.delivery"/>
      <fmt:message var="omnitureChannel" key="omniture.channel.purchase"/>
      
      <dsp:getvalueof var="reseiveOffers" bean="CastNewsletterFormHandler.value.reseiveOffers"/>
      <dsp:getvalueof var="receiveEmail" bean="CastNewsletterFormHandler.value.receiveEmail"/>
      <c:choose>
        <c:when test="${reseiveOffers && !receiveEmail}">
          <fmt:message var="omnitureNewsletterRegistration" key="omniture.newsletters.partners"/>
        </c:when>
        <c:when test="${reseiveOffers &&  receiveEmail}">
          <fmt:message var="omnitureNewsletterRegistration" key="omniture.newsletters.both"/>
        </c:when>
        <c:when test="${!reseiveOffers &&  receiveEmail}">
          <fmt:message var="omnitureNewsletterRegistration" key="omniture.newsletters.newsletter"/>
        </c:when>
        <c:otherwise>
          <fmt:message var="omnitureNewsletterRegistration" key="omniture.newsletters.none"/>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:when test="${fn:contains(requestURI, 'payment.jsp') }">
      <%-- omniture tags for payment.jsp --%>
      <fmt:message var="omniturePState" key="omniture.state.payment"/>      
      <fmt:message var="omniturePageName" key="omniture.pageName.purchase.payment"/>
      <fmt:message var="omnitureChannel" key="omniture.channel.purchase"/>
    </c:when>
     <c:when test="${fn:contains(requestURI, 'searchResults.jsp') }">
      <%-- omniture tags for searchResults.jsp --%>
        
        
        <dsp:getvalueof var="pageNum" param="pageNum" />
        <c:if test="${(empty pageNum or (not empty pageNum and pageNum==1))}">
          <fmt:message var="omniturePState" key="omniture.state.search"/>
        </c:if>
        
        <c:if test="${empty omnitureFullFilterTypes or omnitureFullFilterTypes=='None' }">
          <%-- clear facets value is facets are 'None' on search results page --%>
          <c:set var="omnitureFullFilterTypes" value="" />
          <c:set var="omnitureLastFilterType" value="" />
          <c:set var="omnitureLastFilterValue" value="" />
          <c:set var="omnitureProducts" value="" />
       </c:if>
    </c:when>
	  <c:when test="${fn:contains(requestURI, 'confirmation.jsp') }">
      <%-- omniture tags for confirmation.jsp --%>
      <fmt:message var="omniturePState" key="omniture.state.confirmation"/>        
      <fmt:message var="omniturePageName" key="omniture.pageName.purchase.confirmation"/>
      <fmt:message var="omnitureChannel" key="omniture.channel.purchase"/>
      <dsp:getvalueof var="order" bean="/atg/commerce/ShoppingCart.last" />
      <c:set var="omniturePurchaseID" value="${order.id}"/>
      <c:set var="omnitureProcessingFees" value="${order.processingFees}"/>
      <c:set var="omnitureShippingFees" value="${order.shippingFees}"/>      
      <dsp:droplet name="/com/castorama/droplet/DiscountInformationDroplet">
        <dsp:param name="prodList" value="${omnitureProducts}"/>
        <dsp:param name="orderDeliveryType" value="${orderDeliveryType}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="omnitureDiscountInformations" param="discountInfo"/>
          <dsp:getvalueof var="omniturePromoCodeUsed" param="promoCodeUsed"/>
          <dsp:getvalueof var="omniturePromoCodeAmount" param="promoCodeAmount"/>
          <dsp:getvalueof var="omnitureProductStdShippingFee" param="productStdShippingFee"/>
        </dsp:oparam>
      </dsp:droplet>                
    </c:when>
    <c:when test="${fn:contains(requestURI, 'cart.jsp') }">
      <%-- omniture tags for cart.jsp --%>
      <fmt:message var="omniturePState" key="omniture.state.cart"/>
      <fmt:message var="omniturePageName" key="omniture.pageName.purchase.cart"/>
      <fmt:message var="omnitureChannel" key="omniture.channel.purchase"/>
      <c:if test="${not empty sessionScope.claimedCouponId}">
        <dsp:getvalueof var="omniturePromoCode" value="${sessionScope.claimedCouponId}" />
      </c:if>
                
    </c:when>
    <c:when test="${fn:contains(requestURI, 'preshopping.jsp') }">
      <%-- omniture tags for preshopping.jsp --%>
      <dsp:getvalueof var="omnitureProducts" bean="/atg/commerce/ShoppingCart.lastAddedCommerceItemFromTwoOrders.auxiliaryData.catalogRef.CodeArticle"/>      
      <fmt:message var="omniturePageName" key="omniture.pageName.precart"/>
      <fmt:message var="omnitureChannel" key="omniture.channel.purchase"/> 
      
      <dsp:getvalueof var="totalCommerceItemCount" bean="/atg/commerce/ShoppingCart.totalCommerceItemCount"/>
      <dsp:getvalueof var="lastItemQuantity" bean="/atg/commerce/ShoppingCart.lastAddedCommerceItemFromTwoOrders.quantity"/>
      
      <c:choose>
        <c:when test="${totalCommerceItemCount==lastItemQuantity }">
          <fmt:message var="omniturePState" key="omniture.state.preshopping.add.fst"/>
        </c:when>
        <c:otherwise>
          <fmt:message var="omniturePState" key="omniture.state.preshopping.add.more"/>
        </c:otherwise>
      </c:choose>  
         
    </c:when>
    <c:when test="${fn:contains(requestURI, 'roductDetails.jsp') }">
      <%-- omniture tags for product detailes pages: productDetails.jsp, multiSkuProductDetails.jsp, groupedProductDetails.jsp  --%>      
      <dsp:getvalueof var="hasErrors" bean="EmailAFriendFormHandler.hasErrors"/>
      <c:choose>
      <c:when test="${not empty hasErrors and hasErrors=='false' }">
        <fmt:message var="omniturePState" key="omniture.state.product.tell.friend"/>
      </c:when>
      <c:otherwise>
        <fmt:message var="omniturePState" key="omniture.state.product.view"/>
      </c:otherwise>
      </c:choose>
      
      <dsp:getvalueof var="skuId" param="skuId"/>
      <c:if test="${empty skuId }">
        <c:set var="skuId" value="${omnitureSkuId}"/>
      </c:if>
      <c:if test="${not empty skuId }">
      
		   <dsp:droplet name="CastShippingDroplet">
        <dsp:param name="skuId" value="${skuId}"/>
        <dsp:param name="quantity" value="1"/>
          <dsp:oparam name="output">
            <dsp:droplet name="IsNull">
              <dsp:param name="value" param="expeditionPNS"/>
              <dsp:oparam name="false">
                <dsp:getvalueof var="omnitureProductDeliveryTime" param="expeditionPNS.deliveryTime"/>
                <c:set var="omnitureProductDeliveryTime" value="${fn:replace(omnitureProductDeliveryTime, 'sous ', '')}" />
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
       </dsp:droplet>
       
			 <dsp:droplet name="/com/castorama/droplet/StockAvailabilityDroplet">
              <dsp:param name="skuId" value="${skuId}"/>
              <dsp:param name="store" bean="Profile.currentLocalStore" />
              <dsp:param name="svAvailableMap" value="${svAvailableMap}" />
			  <dsp:oparam name="soldOnlyInStore">
			    <%-- rewrite  omnitureProductDeliveryTime if product soldOnlyInStore --%>
			   <c:set var="omnitureProductDeliveryTime" value="Seulement en Magasin" />			  
			  </dsp:oparam>
			  <dsp:oparam name="ccSoldOnlyInStore">
			    <%-- rewrite  omnitureProductDeliveryTime if product ccSoldOnlyInStore --%>
			   <c:set var="omnitureProductDeliveryTime" value="" />			  
			  </dsp:oparam>
			</dsp:droplet>
			
      </c:if>
    </c:when>
  </c:choose>
  <%-- check user is login and get user's id --%>
  <dsp:getvalueof var="omnitureLoginStatus" bean="/atg/userprofiling/Profile.transient"/>
  <dsp:getvalueof var="omnitureUserId" bean="/atg/userprofiling/Profile.id"/>  
  <dsp:getvalueof var="omnitureUserId" value="${!omnitureLoginStatus ? omnitureUserId : ''}"/>
  <dsp:getvalueof var="omnitureLoginStatus" value="${!omnitureLoginStatus ? 'logged-in' : 'anonymous'}"/>  
  
  <%-- sets value for s_pName param --%>
  <%@ include file="omniturePageName.jspf" %>
  <dsp:getvalueof var="stockFavoriteStoreId" bean="CastNewsletterFormHandler.value.id_magasin_ref.repositoryId"/>
  
  <%-- set value for s_SearchType --%>
  <dsp:getvalueof var="pageNum" param="pageNum"/>
  <c:if test="${(not empty pageNum && pageNum == 1) || (empty pageNum)}">
    <dsp:getvalueof var="omnitureSearchType" value="${osearchmode}"/>
  </c:if>
  
  <%-- Concatenation of the following static string "FicheProduit:" AND the variable Id_Product --%>
  <c:if test="${omnitureChannel == 'FicheProduit' && not empty choosenSkuId}">
    <dsp:getvalueof var="omniturePageName" value="FicheProduit:${choosenSkuId}"/>
  </c:if>
  
  <%-- set variables to request scope to use on tag_commander.jsp page--%>
  <dsp:getvalueof var="omniturePageName" value="${omniturePageName}" scope="request"/>
  <dsp:getvalueof var="omniturePState" value="${omniturePState}" scope="request"/>
  <dsp:getvalueof var="omnitureNewsletterRegistration" value="${omnitureNewsletterRegistration}" scope="request"/>
  <dsp:getvalueof var="omnitureSearchKeyword" value="${omnitureSearchKeyword}" scope="request"/>
  <dsp:getvalueof var="omnitureSearchResults" value="${omnitureSearchResults}" scope="request"/>
  <dsp:getvalueof var="omnitureProducts" value="${omnitureProducts}" scope="request"/>
  <c:choose>
	  <c:when test="${not empty omnitureDeliveryTime }">
        <dsp:getvalueof var="omnitureDeliveryTime" value="${fn:replace(omnitureDeliveryTime, '&nbsp;', ' ')}"/>
		<dsp:getvalueof var="tagCommanderProductDeliveryTime" value="${omnitureDeliveryTime}" scope="request"/>
	  </c:when>
	  <c:otherwise>
		<dsp:getvalueof var="tagCommanderProductDeliveryTime" value="${omnitureProductDeliveryTime}" scope="request"/>
	  </c:otherwise>
  </c:choose>
  <dsp:getvalueof var="omniturePurchaseID" value="${omniturePurchaseID}" scope="request"/>
  <dsp:getvalueof var="omniturePromoCodeUsed" value="${omniturePromoCodeUsed}" scope="request"/>
  
  <%-- set value for s_prodPrice --%>
  <dsp:getvalueof var="omniturePrice" value="${fn:replace(allTaxIncludedPrice, ',', '.')}" />
  
  <%-- set value for s_selectedDeliveryMethod --%>
  <c:choose>
    <c:when test="${orderDeliveryType == 'deliveryToHome'}">
      <dsp:getvalueof var="omnitureSelectedDeliveryMethod" value="home delivery"/>
      <dsp:getvalueof var="tagCommanderProductDeliveryMode" value="${omnitureSelectedDeliveryMethod}" scope="request"/>
    </c:when>
    <c:when test="${orderDeliveryType == 'clickAndCollect'}">
      <dsp:getvalueof var="omnitureSelectedDeliveryMethod" value="in store pick-up"/>
      <dsp:getvalueof var="tagCommanderProductDeliveryMode" value="${omnitureSelectedDeliveryMethod}" scope="request"/>
    </c:when>
  </c:choose>
  
  <%-- set value for s_curLoc --%>
  <dsp:getvalueof var="currentLocalStoreId" bean="Profile.wrappedCurrentLocalStore.id"/>
  <c:choose>
    <c:when test="${currentLocalStoreId == '999'}">
      <dsp:getvalueof var="omnitureCurrentLocation" value="web"/>
    </c:when>
    <c:otherwise>
      <dsp:getvalueof var="omnitureCurrentLocation" bean="Profile.wrappedCurrentLocalStore.storeId"/>
    </c:otherwise>
  </c:choose>
   <input id="omniturePageName" value="${omniturePageName }" type="hidden"/>
  <input id="omnitureChannel" value="${omnitureChannel }" type="hidden"/>
  <input id="omnitureFavoriteStoreId" value="${stockFavoriteStoreId}" type="hidden"/>
  <dsp:include page="/includes/omnitureCode.jsp">
    <dsp:param name="omniturePageName"           value="${omniturePageName}"/>
    <dsp:param name="omnitureChannel"            value="${omnitureChannel}"/>
    <dsp:param name="omnitureLoginStatus"        value="${omnitureLoginStatus}"/>
	<dsp:param name="omnitureUserId"			 value="${omnitureUserId}"/>
    <dsp:param name="omnitureProducts"           value="${omnitureProducts}"/>
    <dsp:param name="omniturePrice"              value="${omniturePrice}"/>
    <dsp:param name="omnitureSelectedDeliveryMethod"  value="${omnitureSelectedDeliveryMethod}"/>
    <dsp:param name="omniturePurchaseID"         value="${omniturePurchaseID}"/>
    <dsp:param name="omniture-searchKeyword"     value="${omnitureSearchKeyword}"/>
    <dsp:param name="omniture-searchResults"     value="${omnitureSearchResults}"/>
    <dsp:param name="omniturePState"             value="${omniturePState}"/>
    <dsp:param name="accountStatus"              value="${accountStatus}" />
    <dsp:param name="omnitureDeliveryTime"       value="${omnitureDeliveryTime}"/>
    <dsp:param name="omnitureProcessingFees"     value="${omnitureProcessingFees}"/>
    <dsp:param name="omnitureShippingFees"       value="${omnitureShippingFees}"/>
    <dsp:param name="omnitureNewsletterRegistration"  value="${omnitureNewsletterRegistration}"/>
    <dsp:param name="omnitureSavedCartContent"   value="${savedCartContent}"/>
    <dsp:param name="omnitureFullFilterTypes"    value="${omnitureFullFilterTypes}"/>
    <dsp:param name="omnitureLastFilterType"     value="${omnitureLastFilterType}"/>
    <dsp:param name="omnitureLastFilterValue"    value="${omnitureLastFilterValue}"/>
    <dsp:param name="omnitureHighlitedProduct"    value="${highlitedProduct}"/>
    <dsp:param name="omnitureProductDeliveryTime" value="${omnitureProductDeliveryTime}"/>
    <dsp:param name="omnitureOmniturePromoCode"  value="${omniturePromoCode}"/>
    <dsp:param name="omnitureDiscountInformations"  value="${omnitureDiscountInformations}"/>
    <dsp:param name="omniturePromoCodeUsed"  value="${omniturePromoCodeUsed}"/>
    <dsp:param name="omniturePromoCodeAmount"  value="${omniturePromoCodeAmount}"/>
    <dsp:param name="omnitureProductStdShippingFee"  value="${omnitureProductStdShippingFee}"/>
    <dsp:param name="omnitureStockVisuPageType"  value="${omnitureStockVisuPageType}"/>
    <dsp:param name="omnitureStockVisuProdID"  value="${omnitureStockVisuProdID}"/>
    <dsp:param name="omnitureStockVisuStatus"  value="${omnitureStockVisuStatus}"/>
	<dsp:param name="omnitureSearchType" value="${omnitureSearchType}"/>
    <dsp:param name="omnitureCurrentLocation" value="${omnitureCurrentLocation}"/>
	<dsp:param name="xForwardedFor" value="${xForwardedFor}"/>
	
  </dsp:include>

  <script type="text/javascript">
  <!--
  if (sendOmnitureIfPageLoad == true){
      sendOmnitureInfo();
  }
  //-->
  </script>
  <dsp:setvalue bean="SessionBean.values.stockVisuStatus" value=""/>
  <dsp:setvalue bean="SessionBean.values.stockVisuProdID" value=""/>
</dsp:page>