<dsp:page>
<dsp:getvalueof var="omnitureDeliveryTime" param="omnitureDeliveryTime"/>
<dsp:getvalueof var="omnitureProductDeliveryTime" param="omnitureProductDeliveryTime"/>
<dsp:getvalueof var="activateOmniture" bean="/com/castorama/CastConfiguration.activateOmniture" />
<!-- SiteCatalyst code version: H.20.3.
Copyright 1996-2009 Adobe, Inc. All Rights Reserved
More info available at http://www.omniture.com -->
<c:if test="${activateOmniture}">        
    <script language="JavaScript" type="text/javascript" src="/js/s_code.js"></script>
</c:if>
<script language="JavaScript" type="text/javascript"><!--
	s_pName="<dsp:valueof param="omniturePageName" valueishtml="true"/>"
	s_channel="<dsp:valueof param="omnitureChannel"/>"
	s_prodList="<dsp:valueof param="omnitureProducts"/>"
	s_prodPrice="<dsp:valueof param="omniturePrice"/>"
	s_selectedDeliveryMethod="<dsp:valueof param="omnitureSelectedDeliveryMethod"/>"
	s_orderID="<dsp:valueof param="omniturePurchaseID"/>"
	s_pState="<dsp:valueof param="omniturePState"/>"
	s_newsletterRegistration="<dsp:valueof param="omnitureNewsletterRegistration"/>"
	s_searchString="<dsp:valueof param="omniture-searchKeyword"/>"
	s_searchResultCount="<dsp:valueof param="omniture-searchResults"/>"
	s_accountStatus="<dsp:valueof param="accountStatus"/>"
	s_processingFee="<dsp:valueof param="omnitureProcessingFees"/>"
	s_shippingFee="<dsp:valueof param="omnitureShippingFees"/>"
	s_savedCartContent="<dsp:valueof param="omnitureSavedCartContent"/>"
	s_FullFilterTypes="<dsp:valueof param="omnitureFullFilterTypes"/>"
	s_LastFilterType="<dsp:valueof param="omnitureLastFilterType"/>"
	s_LastFilterValue="<dsp:valueof param="omnitureLastFilterValue"/>"
	s_highlitedProduct="<dsp:valueof param="omnitureHighlitedProduct"/>"
<c:choose>
  <c:when test="${not empty omnitureDeliveryTime }">
	s_productDeliveryTime="<dsp:valueof value="${omnitureDeliveryTime}" valueishtml="true"/>"
  </c:when>
  <c:otherwise>
	s_productDeliveryTime="<dsp:valueof value="${omnitureProductDeliveryTime}" valueishtml="true"/>"
  </c:otherwise>
</c:choose>
	s_promoCodeAdded="<dsp:valueof param="omnitureOmniturePromoCode"/>"
	s_discountInformation="<dsp:valueof param="omnitureDiscountInformations"/>"
	s_promoCodeUsed="<dsp:valueof param="omniturePromoCodeUsed"/>"
	s_promoCodeAmount="<dsp:valueof param="omniturePromoCodeAmount"/>"
	s_productStdShippingFee="<dsp:valueof param="omnitureProductStdShippingFee"/>"
	s_loginStatus="<dsp:valueof param="omnitureLoginStatus"/>"
	s_userId="<dsp:valueof param="omnitureUserId"/>"
	s_stockVisuPageType="<dsp:valueof param="omnitureStockVisuPageType"/>"
	s_stockVisuProdID="<dsp:valueof param="omnitureStockVisuProdID"/>"
	s_stockVisuStatus="<dsp:valueof param="omnitureStockVisuStatus"/>"
	s_searchType="<dsp:valueof param="omnitureSearchType"/>"
	s_curLoc="<dsp:valueof param="omnitureCurrentLocation"/>"
	s_xForwardedFor="<dsp:valueof param="xForwardedFor"/>"    
	
/************* DO NOT ALTER ANYTHING BELOW THIS LINE ! **************/


//--></script>
<noscript><a href="https://www.omniture.com" rel="nofollow" title="Web Analytics"><img
src="https://advcastov4dev.122.2O7.net/b/ss/advcastov4dev/1/H.20.3--NS/0"
height="1" width="1" border="0" alt="" /></a></noscript><!--/DO NOT REMOVE/-->
<!-- End SiteCatalyst code version: H.20.3. -->

</dsp:page>