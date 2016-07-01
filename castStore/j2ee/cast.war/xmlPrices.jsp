<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/com/castorama/droplet/CreateXMLFile"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceItem"/>
  <dsp:importbean bean="/com/castorama/droplet/UnitPriceCodeLookupDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/MasterCatalogFilterDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CategoriesHierarchyDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup" />
  <dsp:importbean bean="/com/castorama/droplet/CastPriceDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
  
  <dsp:getvalueof var="string" param="skuId"/>
  <dsp:getvalueof var="array" value="${castCollection:split(string)}"/>
  <dsp:getvalueof var="maxSkuCountForPricing" bean="/com/castorama/CastConfiguration.maxSkuCountForPricing"/>
  
  <price>
    <dsp:getvalueof var="skuList" value="${castCollection:column()}"/>
    <dsp:droplet name="ForEach">
      <dsp:param name="array" value="${array}"/>
      <dsp:oparam name="output">
	    <dsp:getvalueof var="currentIndex" param="index"/>
        <c:if test="${currentIndex < maxSkuCountForPricing}">

		<dsp:getvalueof var="currentSkuId" param="element"/>
        <c:if test="${!castCollection:contains(skuList, currentSkuId)}">
         ${castCollection:add(skuList, currentSkuId)}

         <dsp:droplet name="InventoryLookup">
          <dsp:param name="itemId" param="element"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="stockLevel" param="inventoryInfo.stockLevel"/>
          </dsp:oparam>
         </dsp:droplet> 

         <dsp:droplet name="SKULookup">
          <dsp:param name="id" param="element"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="set" param="element.parentProducts.updatedValue"/>
            <dsp:param name="list" value="${castCollection:list(set)}"/>
            
             <dsp:droplet name="MasterCatalogFilterDroplet">
               <dsp:param name="products" param="list"/>
               <dsp:oparam name="output">
               
                 <dsp:droplet name="ProductLookup">
	              <dsp:param name="id" param="list[0].repositoryId"/>
	              <dsp:param name="elementName" value="prod"/>
	              <dsp:oparam name="output">
	                <dsp:getvalueof var="productType" param="prod.type"/>
	                <c:if test="${productType == 'casto-grouped-product'}" >
					  <dsp:getvalueof var="listPrice" value="0" />
					  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
					    <dsp:param name="array" param="element.bundleLinks"/>
					    <dsp:param name="elementName" value="skuLink"/>
					    <dsp:oparam name="output">
					      <dsp:getvalueof var="linkedSkuQuantity" param="skuLink.quantity"/>
					      <dsp:droplet name="CastPriceItem">
					        <dsp:param name="item" param="skuLink.item"/>
					        <dsp:param name="elementName" value="pricedLinkedSku"/>
					        <dsp:oparam name="output">
					          <dsp:getvalueof var="linkedSkuLP" param="pricedLinkedSku.priceInfo.listPrice"/>
					          <dsp:getvalueof var="linkedSkuAmount" param="pricedLinkedSku.priceInfo.amount"/>
					          <dsp:getvalueof var="linkedSkuOnSale" param="pricedLinkedSku.priceInfo.onSale"/>
					          <dsp:getvalueof var="linkedSkuOnSaleDiscountDisplay" param="pricedLinkedSku.priceInfo.onSaleDiscountDisplay"/>
					          <c:if test="${(linkedSkuAmount < linkedSkuLP) and ((empty linkedSkuOnSale or !linkedSkuOnSale) || (not empty linkedSkuOnSaleDiscountDisplay and linkedSkuOnSaleDiscountDisplay))}" >
					            <dsp:getvalueof var="showGPDiscount" value="true"/>
					          </c:if>
					          
					          <c:if test="${(linkedSkuAmount < linkedSkuLP) and (not empty linkedSkuOnSale and linkedSkuOnSale) and (empty linkedSkuOnSaleDiscountDisplay or !linkedSkuOnSaleDiscountDisplay)}" >
					            <dsp:getvalueof var="linkedSkuLP" value="${linkedSkuAmount}"/>
					          </c:if>
					          <fmt:formatNumber var="linkedSkuListPrice" value="${linkedSkuLP}" minFractionDigits="6" groupingUsed="false"/>
					          <dsp:getvalueof var="linkedSkuListPrice" value="${fn:replace(linkedSkuListPrice, ',', '.')}" />
					          <dsp:getvalueof var="listPrice" value="${listPrice + linkedSkuListPrice * linkedSkuQuantity}" />
					        </dsp:oparam>
					      </dsp:droplet>
					    </dsp:oparam>
					  </dsp:droplet>
					</c:if>
	                
		            <dsp:droplet name="CastPriceItem">
		              <dsp:param name="item" param="element"/>
		              <dsp:param name="elementName" value="pricedSku"/>
		              <dsp:oparam name="output">
		                <dsp:param name="pricedItem" param="pricedSku"/>
		                <c:if test="${productType != 'casto-grouped-product'}" >
		                  <dsp:getvalueof var="listPrice" param="pricedItem.priceInfo.listPrice"/>
		                </c:if>
		                <dsp:getvalueof var="salePrice" param="pricedItem.priceInfo.amount"/>

						<dsp:getvalueof var="discounted" param="pricedItem.priceInfo.discounted"/>
                        <dsp:getvalueof var="onSaleParam" param="pricedItem.priceInfo.onSale"/>
                        <dsp:getvalueof var="onSaleDiscountDisplay" param="pricedItem.priceInfo.onSaleDiscountDisplay"/>
	                    <dsp:getvalueof var="localPriceApplied" param="pricedItem.priceInfo.localPriceApplied"/>
                        
                        <dsp:getvalueof var="cardPrice" param="pricedItem.priceInfo.cardPrice"/>
                        <dsp:getvalueof var="isValidByDateCardPrice" param="pricedItem.priceInfo.IsValidByDateCardPrice"/>
						
		                <dsp:droplet name="CastPriceDroplet">
		                  <dsp:param name="listPrice" value="${listPrice}"/>
		                  <dsp:param name="salePrice" value="${salePrice}"/>
		                  <dsp:oparam name="output">
		                    <dsp:getvalueof var="onDiscount" param="onDiscount"/>
		                    <c:choose>
		                      <c:when test="${(localPriceApplied && salePrice > listPrice) || onDiscount}">
                                <dsp:getvalueof var="list" value="${listPrice}"/>
                                <dsp:getvalueof var="sale" value="${salePrice}"/>
								<c:choose>
									<c:when test="${not empty onSaleParam && onSaleParam}">
										<dsp:getvalueof var="displayDiscount" value="${onSaleDiscountDisplay}"/>
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="displayDiscount" value="${discounted}"/>
									</c:otherwise>
								</c:choose>
		                      </c:when>
		                      <c:otherwise>
		                        <dsp:getvalueof var="list" value="${listPrice}"/>
								<dsp:getvalueof var="sale" bean="/Constants.null"/>
								<dsp:getvalueof var="displayDiscount" value="${onSaleDiscountDisplay}"/>
		                      </c:otherwise>
		                    </c:choose>
		                  </dsp:oparam>
		                </dsp:droplet>
		              </dsp:oparam>
		            </dsp:droplet>
		            
		            <c:if test="${productType == 'casto-grouped-product'}" >
		              <dsp:getvalueof var="displayDiscount" value="${displayDiscount || (not empty showGPDiscount and showGPDiscount)}"/>
		            </c:if>
            
		            <c:choose>
		              <c:when test="${not empty sale}">
		                <dsp:getvalueof var="price" value="${salePrice}"/>
		              </c:when>
		              <c:otherwise>
		                <dsp:getvalueof var="price" value="${listPrice}"/>
		              </c:otherwise>
		            </c:choose>
          
		            <dsp:droplet name="UnitPriceCodeLookupDroplet">
		              <dsp:param name="sku" param="element"/>
		              <dsp:param name="price" value="${price}"/>
		              <dsp:oparam name="output">
		                <dsp:getvalueof var="unitePrice" param="pricePerUnite"/>
		                <fmt:formatNumber var="pricePerUnite" value="${unitePrice}" type="currency"/>
		                <dsp:getvalueof var="libelle" param="libelle"/>  
		              </dsp:oparam>
		              <dsp:oparam name="empty">
		              	<dsp:getvalueof var="pricePerUnite" param="pricePerUnite" />
		              	<dsp:getvalueof var="libelle" param="libelle"/>  		
				      </dsp:oparam>		            </dsp:droplet> 
            
		            <c:choose>
		              <c:when test="${not empty libelle}">
		                <dsp:getvalueof var="pricePerUniteVar" value="${pricePerUnite}/${libelle}"/>
		              </c:when>
		              <c:otherwise>
		                <dsp:getvalueof var="pricePerUniteVar" value="${pricePerUnite}"/>
		              </c:otherwise>
		            </c:choose>
		            
		            <dsp:getvalueof var="showM2PriceFirst" param="element.showM2PriceFirst"/>
		            <c:choose>
				      <c:when test="${(not empty cardPrice) and (cardPrice > 0) and (isValidByDateCardPrice == 'true')}">
	                    <c:choose>
				          <c:when test="${showM2PriceFirst}">
				          	<dsp:droplet name="UnitPriceCodeLookupDroplet">
				              <dsp:param name="sku" param="element"/>
				              <dsp:param name="price" value="${cardPrice}"/>
				              <dsp:oparam name="output">
				                <dsp:getvalueof var="cardPrice" param="pricePerUnite"/>
		                		<fmt:formatNumber var="cardPrice" value="${cardPrice}" type="currency"/>
		                		<dsp:getvalueof var="cpLibelle" param="libelle"/>
		                		<c:if test="${not empty cpLibelle}">
		                		  <dsp:getvalueof var="cardPrice" value="${cardPrice}/${cpLibelle}"/>
		              			</c:if>
								
				              </dsp:oparam>
							  <dsp:oparam name="empty">
							  <fmt:formatNumber var="cardPrice" value="${cardPrice}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
							  </dsp:oparam>
							  
				            </dsp:droplet>
				          </c:when>
				          <c:otherwise>
			                <fmt:formatNumber var="cardPrice" value="${cardPrice}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
			              </c:otherwise>
			            </c:choose>
	                  </c:when>
			          <c:otherwise>
		                <dsp:getvalueof var="cardPrice" value=""/>
		              </c:otherwise>
		            </c:choose>
		            
		            <fmt:formatNumber var="list" value="${list}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
		            <fmt:formatNumber var="sale" value="${sale}" type="number" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
		            
		            <dsp:droplet name="CategoriesHierarchyDroplet">
		              <dsp:param name="product" param="prod"/>
		              <dsp:param name="navHistory" bean="CatalogNavHistory"/>		              
		              <dsp:oparam name="output">
		                <dsp:getvalueof var="cats" param="cats"/>
		              </dsp:oparam>
		            </dsp:droplet> 
            
		            <dsp:droplet name="CreateXMLFile">
		              <dsp:param name="skuId" param="element.repositoryId"/>
		              <dsp:param name="stockLevel" value="${stockLevel}"/>
		              <dsp:param name="productName" param="prod.displayName"/>
		              <dsp:param name="productBenefit" param="prod.productBenefit"/>
		              <dsp:param name="productPromoDescription" param="prod.productPromoDescription"/>
		              <dsp:param name="promoExpirationDate" param="prod.promoExpirationDate"/>
		              <dsp:param name="CUP" param="element.CUP"/>
		              <dsp:param name="PUPUV" param="element.PUPUV"/>
		              <dsp:param name="CUV" param="element.CUV"/>
		              <dsp:param name="skuName" param="element.displayName"/>
		              <dsp:param name="typeArticle" param="element.typeArticle"/>
		              <dsp:param name="libelleClientLong" param="element.libelleClientLong"/>
		              <dsp:param name="libelleEspaceNouveaute" param="element.LibelleEspaceNouveaute"/>
		              <dsp:param name="mentionsLegalesObligatoires" param="element.MentionsLegalesObligatoires"/>
		              <dsp:param name="garantie" param="element.Garantie"/>
		              <dsp:param name="restrictionsUsage" param="element.RestrictionsUsage"/>
		              <dsp:param name="listPrice" value="${list}"/>
		              <dsp:param name="salePrice" value="${sale}"/>
		              <dsp:param name="cardPrice" value="${cardPrice}"/>
		              <dsp:param name="showM2PriceFirst" value="${showM2PriceFirst}"/>
		              <dsp:param name="pricePerUnite" value="${pricePerUniteVar}"/>
		              <dsp:param name="parentProductIds" param="list"/>
                      <dsp:param name="displayDiscount" value="${displayDiscount}"/>
                      <dsp:param name="cats" value="${cats}"/>
                      
                      <dsp:param name="skuMiniature" param="element.miniatureImage.url" />
                      <dsp:param name="skuThumbnail" param="element.thumbnailImage.url"/>
                      <dsp:param name="skuAuxilariesMedias" param="element.auxiliaryMedia"/>
                      <dsp:param name="skuCarousel" param="element.carouselImage.url"/>
                      <dsp:param name="skuSmall" param="element.smallImage.url"/>
                      <dsp:param name="skuLarge" param="element.largeImage.url"/>
                      <dsp:param name="skuSupporting" param="element.supportingImage.url"/>
                      <dsp:param name="skuComparator" param="element.comparatorImage.url"/>
                      <dsp:param name="productThumbnail" param="prod.thumbnailImage.url"/>
                      <dsp:param name="productSmall" param="prod.smallImage.url"/>
                      <dsp:param name="productLarge" param="prod.largeImage.url"/>
                      <dsp:param name="productAuxilariesMedias" param="prod.auxiliaryMedia"/>
                      
		              <dsp:oparam name="output">
		                <dsp:valueof param="out" valueishtml="true"/>
		              </dsp:oparam>
		            </dsp:droplet>
		          </dsp:oparam>
		        </dsp:droplet>
		        
               </dsp:oparam>
             </dsp:droplet>          
            
          </dsp:oparam>
         </dsp:droplet>
        </c:if> 
        </c:if> 
      </dsp:oparam>
    </dsp:droplet>
  </price>  
</dsp:page>