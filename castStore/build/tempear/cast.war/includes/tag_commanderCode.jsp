<dsp:page>
  <dsp:getvalueof var="tagCommanderProductUnitPriceATIvar" param="tagCommanderProductUnitPriceATI"/>
  <dsp:getvalueof var="tagCommanderProductUnitPriceTFvar" param="tagCommanderProductUnitPriceTF"/>
  <dsp:getvalueof var="tagCommanderOrderAmountTFWithoutSFvar" param="tagCommanderOrderAmountTFWithoutSF"/>
  <dsp:getvalueof var="tagCommanderOrderAmountTFWithSFvar" param="tagCommanderOrderAmountTFWithSF"/>
  <dsp:getvalueof var="tagCommanderOrderAmountATIWithoutSFvar" param="tagCommanderOrderAmountATIWithoutSF"/>
  <dsp:getvalueof var="tagCommanderOrderAmountATIWithSFvar" param="tagCommanderOrderAmountATIWithSF"/>
  <dsp:getvalueof var="tagCommanderDiscountAmountTFvar" param="tagCommanderDiscountAmountTF"/>
  <dsp:getvalueof var="tagCommanderDiscountAmountATIvar" param="tagCommanderDiscountAmountATI"/>
  <dsp:getvalueof var="tagCommanderShippingAmountATIvar" param="tagCommanderShippingAmountATI"/>
  <dsp:getvalueof var="tagCommanderShippingAmountTFvar" param="tagCommanderShippingAmountTF"/>
  
  <!-- TAGCOMMANDER START //--> 
  <SCRIPT language="javascript"> 
    <!--
    var tc_vars = new Array(); 
    // Tree Structure
    
    tc_vars["template_name"] = "<dsp:valueof param="tagCommanderPState"/>"; 
    tc_vars["page_name"] = "<dsp:valueof param="tagCommanderPageName"/>"; 
    
    // User    
    tc_vars["user_id"] = "<dsp:valueof param="tagCommanderUserId"/>"; 
    tc_vars["user_nl_subscription"] = "<dsp:valueof param="tagCommanderNewsletterRegistration"/>";
    tc_vars["user_logged_status"] = "<dsp:valueof param="tagCommanderLoginStatus"/>";
    
    // Search
    tc_vars["search_kw"] = "<dsp:valueof param="tagCommanderSearchKeyword"/>";
    tc_vars["search_nb_results"] = "<dsp:valueof param="tagCommanderSearchResults"/>";
    tc_vars["search_type"] = "<dsp:valueof param="tagCommanderSearchType"/>";
    
    // Products
    tc_vars["product_name"] = "<dsp:valueof param="tagCommanderProductName"/>";
    tc_vars["product_img_url"] = "<dsp:valueof param="tagCommanderProdImgUrl"/>";
    tc_vars["product_id"] = "<dsp:valueof param="tagCommanderProducts"/>";
    tc_vars["product_id_list"] = "<dsp:valueof param="tagCommanderProducts"/>";
    tc_vars["product_unit_price_ati"] = "<dsp:valueof value="${tagCommanderProductUnitPriceATIvar}"/>";
    tc_vars["product_unit_price_tf"] = "<dsp:valueof value="${tagCommanderProductUnitPriceTFvar}"/>";
    tc_vars["product_delivery_time"] = "<dsp:valueof param="tagCommanderProductDeliveryTime"/>";
    tc_vars["product_stock"] = "<dsp:valueof param="tagCommanderProductStock"/>";
    tc_vars["product_category"] = "<dsp:valueof param="tagCommanderProductCategory"/>";
    tc_vars["product_vendor"] = "<dsp:valueof param="tagCommanderProdVendor"/>"; 
    
    // Order
    <c:choose>
      <c:when test="${fn:contains(pageContext.request.requestURL, 'store/checkout/full/confirmation.jsp')}">
        tc_vars["order_id"] = "<dsp:valueof value="${omniturePurchaseID}"/>";
        tc_vars["cart_id"] = "<dsp:valueof param="tagCommanderCartId"/>";
        tc_vars["order_amount_tf_without_sf"] = "<fmt:formatNumber value="${tagCommanderOrderAmountTFWithoutSFvar}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>";
        tc_vars["order_amount_tf_with_sf"] = "<fmt:formatNumber value="${tagCommanderOrderAmountTFWithSFvar}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>";
        tc_vars["order_amount_ati_without_sf"] = "<fmt:formatNumber value="${tagCommanderOrderAmountATIWithoutSFvar}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>";
        tc_vars["order_amount_ati_with_sf"] = "<fmt:formatNumber value="${tagCommanderOrderAmountATIWithSFvar}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>";
        tc_vars["order_discount_tf"] = "<fmt:formatNumber value="${tagCommanderDiscountAmountTFvar}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>";
        tc_vars["order_discount_ati"] = "<fmt:formatNumber value="${tagCommanderDiscountAmountATIvar}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>";
        tc_vars["order_newcustomer"] = "<dsp:valueof param="tagCommanderNewCustomer"/>";
        tc_vars["order_ship_ati"] = "<fmt:formatNumber value="${tagCommanderShippingAmountATIvar}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>";
        tc_vars["order_ship_tf"] = "<fmt:formatNumber value="${tagCommanderShippingAmountTFvar}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>";
        tc_vars["order_payment_methods"] = "<dsp:valueof param="tagCommanderPaymentMethod"/>";
        tc_vars["order_delivery_mode"] = "<dsp:valueof param="tagCommanderProductDeliveryMode"/>";
        tc_vars["order_status"] = "";
        tc_vars["order_promo_codes"] = "<dsp:valueof param="tagCommanderPromoCodeUsed"/>";
        tc_vars["order_currency"] = "<dsp:valueof param="tagCommanderCurrencyCode"/>";
        tc_vars["order_email"] = "<dsp:valueof param="tagCommanderOrderEmail"/>";
        tc_vars["order_nb_products"] = "<dsp:valueof param="tagCommanderNbProducts"/>";
        tc_vars["order_zipcode"] = "<dsp:valueof param="tagCommanderZip"/>";
        tc_vars["order_state"] = "";
      </c:when>
      <c:when test="${fn:contains(pageContext.request.requestURL, 'store/checkout/cart.jsp') || fn:contains(pageContext.request.requestURL, 'store/checkout/full/payment.jsp')}">
        tc_vars["order_id"] = "";
        tc_vars["cart_id"] = "<dsp:valueof param="tagCommanderCartId"/>";
        tc_vars["order_amount_tf_without_sf"] = "";
        tc_vars["order_amount_tf_with_sf"] = "";
        tc_vars["order_amount_ati_without_sf"] = "";
        tc_vars["order_amount_ati_with_sf"] = "";
        tc_vars["order_discount_tf"] = "";
        tc_vars["order_discount_ati"] = "";
        tc_vars["order_newcustomer"] = "";
        tc_vars["order_ship_ati"] = "";
        tc_vars["order_ship_tf"] = "";
        tc_vars["order_payment_methods"] = "";
        tc_vars["order_delivery_mode"] = "";
        tc_vars["order_status"] = "";
        tc_vars["order_promo_codes"] = "";
        tc_vars["order_currency"] = "";
        tc_vars["order_email"] = "";
        tc_vars["order_nb_products"] = "";
        tc_vars["order_zipcode"] = "";
        tc_vars["order_state"] = "";
      </c:when>
      <c:otherwise>
        tc_vars["order_id"] = "";
        tc_vars["cart_id"] = "";
        tc_vars["order_amount_tf_without_sf"] = "";
        tc_vars["order_amount_tf_with_sf"] = "";
        tc_vars["order_amount_ati_without_sf"] = "";
        tc_vars["order_amount_ati_with_sf"] = "";
        tc_vars["order_discount_tf"] = "";
        tc_vars["order_discount_ati"] = "";
        tc_vars["order_newcustomer"] = "";
        tc_vars["order_ship_ati"] = "";
        tc_vars["order_ship_tf"] = "";
        tc_vars["order_payment_methods"] = "";
        tc_vars["order_delivery_mode"] = "";
        tc_vars["order_status"] = "";
        tc_vars["order_promo_codes"] = "";
        tc_vars["order_currency"] = "";
        tc_vars["order_email"] = "";
        tc_vars["order_nb_products"] = "";
        tc_vars["order_zipcode"] = "";
        tc_vars["order_state"] = "";
      </c:otherwise>
    </c:choose>
    
    // Products
    tc_vars["order_products"] = new Array();
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param name="array" param="tagCommanderProductsInfo"/>
      <dsp:oparam name="output">
        tc_vars["order_products"][<dsp:valueof param="index"/>] = new Array( 
        <dsp:valueof param="element" valueishtml="true"/>
        );
      </dsp:oparam>
    </dsp:droplet>
    
    //--> 
  </SCRIPT>
  <SCRIPT type="text/javascript" src="/js/tc_Castoramafr_1_load.js"></SCRIPT>
  <SCRIPT type="text/javascript" src="/js/tc_Castoramafr_1_exec.js"></SCRIPT>
  
  <dsp:getvalueof var="isSecure" value="${pageContext.request.secure}"/>
  <NOSCRIPT>
    <c:choose>
      <c:when test="${isSecure}">
        <IFRAME src="https://manager.tagcommander.com/utils/noscript.php?id=1&mode=iframe&site=39&template_name=&page_name=&order_id=&order_amount_tf_without_sf=&order_amount_tf_with_sf=&order_amount_ati_without_sf=&order_amount_ati_with_sf==" width="1" height="1"></IFRAME>
      </c:when>
      <c:otherwise>
        <IFRAME src="http://redirect39.tagcomander.com/utils/noscript.php?id=1&mode=iframe&template_name=&page_name=&order_id=&order_amount_tf_without_sf=&order_amount_tf_with_sf=&order_amount_ati_without_sf=&order_amount_ati_with_sf=" width="1" height="1"></IFRAME>
      </c:otherwise>
    </c:choose>
  </NOSCRIPT>
  <!-- TAGCOMMANDER END //-->
</dsp:page>