<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/com/castorama/droplet/CastPriceItem"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <dsp:getvalueof var="hasProductsWithDiscount" param="hasProductsWithDiscount"/>
  
  <dsp:getvalueof var="currencyCode" param="currencyCode"/>
  
  <dsp:getvalueof var="mode" param="mode"/>

  <dsp:getvalueof var="quantityVar" param="commerceItem.quantity" vartype="java.lang.String"/>
  
  <dsp:getvalueof var="onSaleParam" param="commerceItem.priceInfo.onSale"/>
  <dsp:getvalueof var="onSaleDiscountDisplay" param="commerceItem.priceInfo.onSaleDiscountDisplay"/>
  <dsp:getvalueof var="showCastCardPrice" param="showCastCardPrice"/>
  <dsp:getvalueof var="showedInComparator" param="showedInComparator"/>
  
  <dsp:getvalueof var="tauxTVA" param="thisSku.tauxTVA"/>
    
  <c:set var="itemDiscount" value="0"/>
  <dsp:droplet name="ForEach">
    <dsp:param name="array" param="commerceItem.priceInfo.adjustments"/>
    <dsp:param name="elementName" value="adjustment"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="totalAdjustment" param="adjustment.TotalAdjustment"/>
      <dsp:getvalueof var="adjustmentDescription" param="adjustment.adjustmentDescription"/>
      <c:if test="${totalAdjustment < 0 && ((not empty onSaleDiscountDisplay && onSaleDiscountDisplay) || (not empty onSaleParam && not onSaleParam))}">
        <c:set var="itemDiscount" value="${itemDiscount + totalAdjustment}"/>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>

  <c:set var="itemDiscount" value="${fn:replace(itemDiscount, '-', '')}" />
      
  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="thisProductId"/>
    <dsp:param name="elementName" value="product"/>
    <dsp:oparam name="output">
      <dsp:droplet name="CastPriceItem">
        <dsp:param name="item" param="thisSku"/>
        <dsp:param name="elementName" value="pricedSku"/>
        <dsp:oparam name="output">
          <dsp:param name="pricedItem" param="pricedSku"/>

          <dsp:getvalueof var="skusBundleLinks" param="thisSku.bundleLinks" />
          <c:if test="${fn:length(skusBundleLinks) > 0}" >
            <dsp:getvalueof var="parentProdsSet" param="thisSku.parentProducts.updatedValue"/>
            <dsp:param name="parentProdsList" value="${castCollection:list(parentProdsSet)}"/>
            <dsp:getvalueof var="productType" param="parentProdsList[0].type"/>
          </c:if>
          
          <dsp:getvalueof var="listPrice" param="pricedItem.priceInfo.listPrice"/>
          <dsp:getvalueof var="salePrice" param="pricedItem.priceInfo.amount"/>
          <dsp:getvalueof var="cardPrice" param="pricedItem.priceInfo.cardPrice" scope="request"/>
          <dsp:getvalueof var="discounted" param="pricedItem.priceInfo.discounted"/>
          <dsp:getvalueof var="onSaleParam" param="pricedItem.priceInfo.onSale"/>
          <dsp:getvalueof var="onSaleDiscountDisplay" param="pricedItem.priceInfo.onSaleDiscountDisplay"/>
          <dsp:getvalueof var="isValidByDateCardPrice" param="pricedItem.priceInfo.IsValidByDateCardPrice"/>
          <dsp:getvalueof var="currencyCode" param="pricedItem.priceInfo.currencyCode"/>
          <c:if test="${mode == 'confirmation'}">
            <dsp:getvalueof var="listPrice" param="commerceItem.priceInfo.listPrice"/>
            <dsp:getvalueof var="amount" param="commerceItem.priceInfo.amount"/>
            <dsp:getvalueof var="salePrice" value="${amount/quantityVar}"/>
            <dsp:getvalueof var="cardPrice" param="commerceItem.priceInfo.cardPrice" scope="request"/>
            <dsp:getvalueof var="discounted" param="commerceItem.priceInfo.discounted"/>
            <dsp:getvalueof var="onSaleParam" param="commerceItem.priceInfo.onSale"/>
            <dsp:getvalueof var="onSaleDiscountDisplay" param="commerceItem.priceInfo.onSaleDiscountDisplay"/>
            <dsp:getvalueof var="isValidByDateCardPrice" param="commerceItem.priceInfo.IsValidByDateCardPrice"/>
            <dsp:param name="enabledCastCart" bean="/atg/commerce/ShoppingCart.last.payeCarteAtout"/>
            <dsp:getvalueof var="currencyCode" param="commerceItem.priceInfo.currencyCode"/>
          </c:if>
          <dsp:getvalueof var="needToDisplayPromotion" value="${!(onSaleParam && !onSaleDiscountDisplay) || productType == 'casto-pack'}"/>
          
          <c:if test="${productType == 'casto-pack'}" >
            <dsp:getvalueof var="realPackPrice" value="0" />
            <dsp:getvalueof var="pack_prc_tf" value="0" />
            <dsp:getvalueof var="packAverageTaxTVA" value="0" />
            <dsp:getvalueof var="packTotalItemCount" value="0" />
            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
              <dsp:param name="array" param="thisSku.bundleLinks"/>
              <dsp:param name="elementName" value="skuLink"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="quantity" param="skuLink.quantity"/>
                <dsp:getvalueof var="packedSkuTaxTVA" param="skuLink.item.tauxTVA"/>
                <dsp:getvalueof var="packAverageTaxTVA" value="${packAverageTaxTVA + packedSkuTaxTVA * quantity}" />
                <dsp:getvalueof var="packTotalItemCount" value="${packTotalItemCount + quantity}" />
                <dsp:droplet name="CastPriceItem">
                  <dsp:param name="item" param="skuLink.item"/>
                  <dsp:param name="elementName" value="pricedSku"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="packedSkuPrice" param="pricedSku.priceInfo.amount"/>
                    <fmt:formatNumber var="packedSkuPrice" value="${packedSkuPrice}" minFractionDigits="6" groupingUsed="false"/>
                    <dsp:getvalueof var="packedSkuPrice" value="${fn:replace(packedSkuPrice, ',', '.')}" />
                    <dsp:getvalueof var="realPackPrice" value="${realPackPrice + packedSkuPrice * quantity}" />
                    <dsp:getvalueof var="packedSkuTFPrice" value="${packedSkuPrice/ (1+(packedSkuTaxTVA/100))}" />
                    <dsp:getvalueof var="pack_prc_tf" value="${pack_prc_tf + packedSkuTFPrice * quantity }" />
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
            <fmt:formatNumber var="packAverageTaxTVA" value="${packAverageTaxTVA}" minFractionDigits="6" groupingUsed="false"/>
            <dsp:getvalueof var="packAverageTaxTVA" value="${fn:replace(packAverageTaxTVA, ',', '.')}" />
            <dsp:getvalueof var="packAverageTaxTVA" value="${packAverageTaxTVA / packTotalItemCount}" />
            
            <dsp:droplet name="CastPriceDroplet">
              <dsp:param name="listPrice" value="${realPackPrice}"/>
              <dsp:param name="salePrice" value="${salePrice}"/>
              <dsp:param name="storeIsLocal" value="${storeIsLocal}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="discount" param="economy"/>
                <fmt:formatNumber var="discount" value="${discount}" minFractionDigits="6" groupingUsed="false"/>
                <dsp:getvalueof var="discount" value="${fn:replace(discount, ',', '.')}" />
                <c:if test="${empty discount || discount == ''}" >
                  <dsp:getvalueof var="discount" value="0" />
                </c:if>
                <dsp:getvalueof var="packTFDiscount" value="${discount/(1+(packAverageTaxTVA/100))}" />
                
                <fmt:formatNumber var="prc_ati" value="${salePrice}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                <dsp:getvalueof var="pack_prc_tf" value="${pack_prc_tf - packTFDiscount}" />
                <fmt:formatNumber var="prc_tf" value="${pack_prc_tf}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${prc_ati}','${prc_tf}'" scope="request"/>
                <dsp:getvalueof var="tagCommanderOrderAmountTFWithoutSF" value="${tagCommanderOrderAmountTFWithoutSF + pack_prc_tf * quantityVar}" scope="request"/>
                <dsp:getvalueof var="tagCommanderTotalOrderTaxTVA" value="${tagCommanderTotalOrderTaxTVA + packAverageTaxTVA * quantityVar}" scope="request"/>
                
                <fmt:formatNumber var="pd_ati" value="${discount}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                <fmt:formatNumber var="pd_tf" value="${packTFDiscount}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                <dsp:getvalueof var="tagCommanderDiscountAmountATI" value="${tagCommanderDiscountAmountATI + discount * quantityVar}" scope="request"/>
                <dsp:getvalueof var="tagCommanderDiscountAmountTF" value="${tagCommanderDiscountAmountTF + packTFDiscount * quantityVar}" scope="request"/>
                <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${pd_ati}','${pd_tf}'" scope="request"/>
              </dsp:oparam>
            </dsp:droplet>
          </c:if>
            
          <dsp:droplet name="CastPriceDroplet">
            <dsp:param name="listPrice" value="${listPrice}"/>
            <dsp:param name="salePrice" value="${salePrice}"/>
            <dsp:param name="cardPrice" value="${cardPrice}"/>
            <dsp:param name="showCastCardPrice" value="${(isValidByDateCardPrice == 'true')}"/>
            <dsp:getvalueof var="enabledCastCart" param="enabledCastCart"/>
            <dsp:param name="storeIsLocal" value="${storeIsLocal}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="percent" param="percent"/>
              <dsp:getvalueof var="discount" param="economy"/>
              <dsp:getvalueof var="onDiscount" param="onDiscount"/>
              <dsp:getvalueof var="cardPriceShowed" param="cardPriceShowed"/>
              <dsp:getvalueof var="showM2PriceFirst" param="sku.showM2PriceFirst"/>
              <dsp:getvalueof var="PUPUV" param="sku.PUPUV"/>
              <dsp:getvalueof var="showM2PriceFirst" value="${showM2PriceFirst && not empty PUPUV && PUPUV > 0 && showedInComparator}"/>
              <c:choose>
                <c:when test="${onDiscount}">
                  <c:choose>
                    <c:when test="${(salePrice < listPrice) 
                                      && ((onSaleParam && onSaleDiscountDisplay) 
                                          || (discounted && needToDisplayPromotion)) 
                                      && (empty cardPriceShowed || !cardPriceShowed)}">
                        <c:choose>
                          <c:when test="${showM2PriceFirst}">
                            <div class="oldprice">
                              <fmt:formatNumber value="${listPrice/PUPUV}" type="currency" currencyCode="${currencyCode}"/><span>/m<sup>2</sup></span>
                            </div>
                            <div class="newprice">
                              <fmt:formatNumber value="${salePrice/PUPUV}" type="currency" currencyCode="${currencyCode}"/><span>/m<sup>2</sup></span>
                            </div>
                          </c:when>
                          <c:otherwise>
                            <div class="oldprice">
                              <fmt:formatNumber value="${listPrice}" type="currency" currencyCode="${currencyCode}"/>
                            </div>
                            <div class="newprice">
                              <fmt:formatNumber value="${salePrice}" type="currency" currencyCode="${currencyCode}"/>
                            </div>
                          </c:otherwise>
                        </c:choose>
                      
                      <c:if test="${productType != 'casto-pack'}" >
                        <fmt:formatNumber var="salePriceFract" value="${salePrice}" minFractionDigits="6" groupingUsed="false"/>
                        <dsp:getvalueof var="salePriceFract" value="${fn:replace(salePriceFract, ',', '.')}" />
                       
                        <fmt:formatNumber var="prc_ati" value="${salePrice}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                        <fmt:formatNumber var="prc_tf" value="${salePriceFract/(1+(tauxTVA/100))}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                      
                        <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${prc_ati}','${prc_tf}'" scope="request"/>
                        <dsp:getvalueof var="tagCommanderOrderAmountTFWithoutSF" value="${tagCommanderOrderAmountTFWithoutSF + salePriceFract/(1+(tauxTVA/100)) * quantityVar}" scope="request"/>
                        <dsp:getvalueof var="tagCommanderTotalOrderTaxTVA" value="${tagCommanderTotalOrderTaxTVA + tauxTVA * quantityVar}" scope="request"/>
                      </c:if>
                      
                      <c:if test="${productType != 'casto-pack'}" >
                         <fmt:formatNumber var="itemDiscountFract" value="${itemDiscount}" minFractionDigits="6" groupingUsed="false"/>
                         <dsp:getvalueof var="itemDiscountFract" value="${fn:replace(itemDiscountFract, ',', '.')}" />
                       
                         <fmt:formatNumber var="pd_ati" value="${itemDiscount}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                         <fmt:formatNumber var="pd_tf" value="${itemDiscountFract/(1+(tauxTVA/100))}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                       
                         <dsp:getvalueof var="tagCommanderDiscountAmountATI" value="${tagCommanderDiscountAmountATI + itemDiscount}" scope="request"/>
                         <dsp:getvalueof var="tagCommanderDiscountAmountTF" value="${tagCommanderDiscountAmountTF + itemDiscountFract/(1+(tauxTVA/100))}" scope="request"/>
                         <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${pd_ati}','${pd_tf}'" scope="request"/>
                       </c:if>
                      
                    </c:when>
                    <c:when test="${(cardPrice < listPrice) 
                                      && (cardPriceShowed)}">
                        <c:choose>
                          <c:when test="${showM2PriceFirst}">
                            <div class="oldprice">
                              <fmt:formatNumber value="${listPrice/PUPUV}" type="currency" currencyCode="${currencyCode}"/><span>/m<sup>2</sup></span>
                            </div>
                            <div class="newprice">
                              <fmt:formatNumber value="${cardPrice/PUPUV}" type="currency" currencyCode="${currencyCode}"/><span>/m<sup>2</sup></span>
                            </div>
                          </c:when>
                          <c:otherwise>
                            <div class="oldprice">
                              <fmt:formatNumber value="${listPrice}" type="currency" currencyCode="${currencyCode}"/>
                            </div>
                            <div class="newprice">
                              <fmt:formatNumber value="${cardPrice}" type="currency" currencyCode="${currencyCode}"/>
                            </div>
                          </c:otherwise>
                        </c:choose>
                      <c:if test="${productType != 'casto-pack'}" >
                       <fmt:formatNumber var="cardPriceFract" value="${cardPrice}" minFractionDigits="6" groupingUsed="false"/>
                       <dsp:getvalueof var="cardPriceFract" value="${fn:replace(cardPriceFract, ',', '.')}" />
                      
                       <fmt:formatNumber var="prc_ati" value="${cardPrice}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                       <fmt:formatNumber var="prc_tf" value="${cardPriceFract/(1+(tauxTVA/100))}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                       
                       <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${prc_ati}','${prc_tf}'" scope="request"/>
                       <dsp:getvalueof var="tagCommanderOrderAmountTFWithoutSF" value="${tagCommanderOrderAmountTFWithoutSF + cardPriceFract/(1+(tauxTVA/100)) * quantityVar}" scope="request"/>
                       <dsp:getvalueof var="tagCommanderTotalOrderTaxTVA" value="${tagCommanderTotalOrderTaxTVA + tauxTVA * quantityVar}" scope="request"/>
                      </c:if>
                      
                      <c:if test="${productType != 'casto-pack'}" >  
                        <fmt:formatNumber var="itemDiscountFract" value="${listPrice - cardPrice}" minFractionDigits="6" groupingUsed="false"/>
                        <dsp:getvalueof var="itemDiscountFract" value="${fn:replace(itemDiscountFract, ',', '.')}" />
                        
                        <fmt:formatNumber var="pd_ati" value="${listPrice - cardPrice}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                        <fmt:formatNumber var="pd_tf" value="${itemDiscountFract/(1+(tauxTVA/100))}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                        
                        <dsp:getvalueof var="tagCommanderDiscountAmountATI" value="${tagCommanderDiscountAmountATI + listPrice - cardPrice}" scope="request"/>
                        <dsp:getvalueof var="tagCommanderDiscountAmountTF" value="${tagCommanderDiscountAmountTF + itemDiscountFract/(1+(tauxTVA/100))}" scope="request"/>
                        <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${pd_ati}','${pd_tf}'" scope="request"/>
                      </c:if>
                      
                    </c:when>
                    <c:otherwise>
                      <div class="price">
                        <c:choose>
                          <c:when test="${showM2PriceFirst}">
                            <fmt:formatNumber value="${salePrice/PUPUV}" type="currency" currencyCode="${currencyCode}"/><span>/m<sup>2</sup></span>
                          </c:when>
                          <c:otherwise>
                        <fmt:formatNumber value="${salePrice}" type="currency" currencyCode="${currencyCode}"/>
                        </c:otherwise>
                         </c:choose>
                      </div>
                      
                      <c:if test="${productType != 'casto-pack'}" >
                        <fmt:formatNumber var="salePriceFract" value="${salePrice}" minFractionDigits="6" groupingUsed="false"/>
                        <dsp:getvalueof var="salePriceFract" value="${fn:replace(salePriceFract, ',', '.')}" />
                      
                        <fmt:formatNumber var="prc_ati" value="${salePrice}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                        <fmt:formatNumber var="prc_tf" value="${salePriceFract/(1+(tauxTVA/100))}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                    
                        <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${prc_ati}','${prc_tf}'" scope="request"/>
                        <dsp:getvalueof var="tagCommanderOrderAmountTFWithoutSF" value="${tagCommanderOrderAmountTFWithoutSF + salePriceFract/(1+(tauxTVA/100)) * quantityVar}" scope="request"/>
                        <dsp:getvalueof var="tagCommanderTotalOrderTaxTVA" value="${tagCommanderTotalOrderTaxTVA + tauxTVA * quantityVar}" scope="request"/>
                      </c:if>
                      
                      <c:if test="${productType != 'casto-pack'}" >
                        <fmt:formatNumber var="itemDiscountFract" value="${itemDiscount}" minFractionDigits="6" groupingUsed="false"/>
                        <dsp:getvalueof var="itemDiscountFract" value="${fn:replace(itemDiscountFract, ',', '.')}" />
                        
                        <fmt:formatNumber var="pd_ati" value="${itemDiscount}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                        <fmt:formatNumber var="pd_tf" value="${itemDiscountFract/(1+(tauxTVA/100))}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                      
                        <dsp:getvalueof var="tagCommanderDiscountAmountATI" value="${tagCommanderDiscountAmountATI + itemDiscount}" scope="request"/>
                        <dsp:getvalueof var="tagCommanderDiscountAmountTF" value="${tagCommanderDiscountAmountTF + itemDiscountFract/(1+(tauxTVA/100))}" scope="request"/>
                        <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${pd_ati}','${pd_tf}'" scope="request"/>
                      </c:if>
                      
                    </c:otherwise>
                  </c:choose>
                </c:when>
                <c:otherwise>
                  <c:if test="${salePrice > listPrice and mode!='confirmation'}">
                    <dsp:getvalueof var="listPrice" value="${salePrice}"/>
                  </c:if>
                  
                  <div class="price">
                    <c:choose>
                      <c:when test="${showM2PriceFirst}">
                        <fmt:formatNumber value="${listPrice/PUPUV}" type="currency" currencyCode="${currencyCode}"/><span>/m<sup>2</sup></span>
                      </c:when>
                      <c:otherwise>
                         
                         <fmt:formatNumber value="${listPrice}" type="currency" currencyCode="${currencyCode}"/>
                      </c:otherwise>
                    </c:choose>
                  </div>
                  
                  <c:if test="${productType != 'casto-pack'}" >
                    <fmt:formatNumber var="listPriceFract" value="${listPrice}" minFractionDigits="6" groupingUsed="false"/>
                    <dsp:getvalueof var="listPriceFract" value="${fn:replace(listPriceFract, ',', '.')}" />
                  
                    <fmt:formatNumber var="prc_ati" value="${listPrice}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                    <fmt:formatNumber var="prc_tf" value="${listPriceFract/(1+(tauxTVA/100))}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                
                    <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${prc_ati}','${prc_tf}'" scope="request"/>
                    <dsp:getvalueof var="tagCommanderOrderAmountTFWithoutSF" value="${tagCommanderOrderAmountTFWithoutSF + listPriceFract/(1+(tauxTVA/100)) * quantityVar}" scope="request"/>
                    <dsp:getvalueof var="tagCommanderTotalOrderTaxTVA" value="${tagCommanderTotalOrderTaxTVA + tauxTVA * quantityVar}" scope="request"/>
                  </c:if>
                  
                  <c:if test="${productType != 'casto-pack'}" >
                    <fmt:formatNumber var="itemDiscountFract" value="${itemDiscount}" minFractionDigits="6" groupingUsed="false"/>
                    <dsp:getvalueof var="itemDiscountFract" value="${fn:replace(itemDiscountFract, ',', '.')}" />
                        
                    <fmt:formatNumber var="pd_ati" value="${itemDiscount}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                    <fmt:formatNumber var="pd_tf" value="${itemDiscountFract/(1+(tauxTVA/100))}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                  
                    <dsp:getvalueof var="tagCommanderDiscountAmountATI" value="${tagCommanderDiscountAmountATI + itemDiscount}" scope="request"/>
                    <dsp:getvalueof var="tagCommanderDiscountAmountTF" value="${tagCommanderDiscountAmountTF + itemDiscountFract/(1+(tauxTVA/100))}" scope="request"/>
                    <dsp:getvalueof var="tagCommanderProductsInfoTemp" value="${tagCommanderProductsInfoTemp},'${pd_ati}','${pd_tf}'" scope="request"/>
                  </c:if>
                  
                </c:otherwise>
              </c:choose>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>