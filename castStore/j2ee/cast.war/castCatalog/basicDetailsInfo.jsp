<dsp:page>

    <div class="m10">

  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/com/castorama/droplet/MagasinLookupDroplet"/>

  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="paramSkuId" param="skuId"/>
  <dsp:getvalueof var="storeID" param="storeId"/>
  <dsp:getvalueof var="quantity" param="quantity"/>
  <dsp:getvalueof var="pageType" value="productDetails"/>
  <dsp:getvalueof var="packId" param="packId"/>
  <dsp:getvalueof var="orderDate" param="orderDate"/>
	   <c:if test="${not empty paramSkuId}">
			<dsp:droplet name="SKULookup">
			  <dsp:param name="id" value="${paramSkuId}"/>
			  <dsp:oparam name="output">
				<dsp:getvalueof var="sku" param="element"/>
			  </dsp:oparam>
			</dsp:droplet>
	   </c:if>
	<dsp:droplet name="MagasinLookupDroplet">
		<dsp:param name="id" param="storeId"/>
		<dsp:oparam name="output">
			  <dsp:getvalueof var="store" param="element"/>
			  <dsp:getvalueof var="storeName" param="element.nom"/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof var="skuId" value="${sku.repositoryId}"/>
	<dsp:droplet name="/com/castorama/droplet/CastPriceItem">
	  <dsp:param name="item" value="${sku}"/>
	  <dsp:param name="elementName" value="pricedSku"/>
	  <dsp:oparam name="output">
		<dsp:getvalueof var="cardPrice" param="pricedSku.priceInfo.cardPrice"/>
		<dsp:getvalueof var="pricedSku" param="pricedSku"/>
		<dsp:getvalueof var="isValidByDateCardPrice" param="pricedSku.priceInfo.IsValidByDateCardPrice"/>
		<c:if test="${(not empty cardPrice) and (cardPrice > 0) and (isValidByDateCardPrice == 'true')}">
		  <dsp:getvalueof var="cpb" value="true"/>
		</c:if>
	  </dsp:oparam>
	</dsp:droplet>
    
      <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
      <dsp:importbean bean="/com/castorama/droplet/CastGetApplicablePromotions"/>
      <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
      <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
      <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
      <dsp:importbean bean="/com/castorama/droplet/ProductDescPreProcessorDroplet"/>

      
      <dsp:getvalueof var="isSearchResult" param="${true}"/>
      
      <dsp:getvalueof var="isRobot" value="false"/>
      
      <!-- slide right panel with shopping list -->
    <dsp:droplet name="ProductLookup">
      <dsp:param name="id" value="${productId}"/>
      <dsp:oparam name="output">
      <dsp:param name="product" param="element"/>
      <dsp:getvalueof var="productImage" param="element.thumbnailImage.url"/>
      <dsp:getvalueof var="childSku" param="element.childSKUs"/>
	  <dsp:getvalueof var="categoryId" param="element.parentCategory.repositoryId"/>

      <dsp:getvalueof var="bgColor" param="product.parentCategory.style.backgroundStyle"/>
      <dsp:getvalueof var="linkColor" param="product.parentCategory.style.linkStyle"/>
      <dsp:getvalueof var="listColor" param="product.parentCategory.style.listStyle"/>
	  <span style="line-height:25px;font-size:16px;">
	  Date: ${orderDate}
	  <c:if test="${storeID != '999'}">&nbsp;- Magasin de ${storeName}
	  </c:if>
	  </span>
      <div class="clear"></div>
      <div class="content width800 width750">
      <div class="productBlock singleProductV2" id="productItemWithPrice">

        <dsp:setvalue param="sku" value="${sku}"/>
        <dsp:getvalueof var="skuCodeArticle" param="sku.CodeArticle" scope="request"/>
		<dsp:getvalueof var="displayName" param="sku.displayName" scope="request"/>
        <dsp:droplet name="/com/castorama/droplet/ProductDetailsCache">
          <dsp:param name="key" value="single_sku_${categoryId}_${productId}_${sku.repositoryId}_1_${isRobot}_${storeId}"/>
          <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
          <dsp:oparam name="output">
            <dsp:include page="includes/productImageAreaInfo.jsp">
              <dsp:param name="element" value="${sku}"/>
              <dsp:param name="categoryId" value="${categoryId}"/>
              <dsp:param name="product" param="product"/>
              <dsp:param name="skuCodeArticle" param="sku.CodeArticle"/>
            </dsp:include>
          </dsp:oparam>
        </dsp:droplet>

        <div class="productContent">

          <dsp:droplet name="/com/castorama/droplet/ProductDetailsCache">
            <dsp:param name="key" value="single_sku_${categoryId}_${productId}_${sku.repositoryId}_2_${isRobot}_${storeId}"/>
            <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
            <dsp:oparam name="output">
              <dsp:setvalue param="sku" value="${sku}"/>
              <h1><dsp:valueof param="sku.displayName"/></h1>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:droplet name="PriceDroplet">
            <dsp:param name="sku" value="${sku}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="listPrice" param="price.listPrice"/>
            </dsp:oparam>
          </dsp:droplet>
		  <dsp:getvalueof var="skuList" param="product.childSKUs"/>
		  <dsp:getvalueof var="size" value="${fn:length(skuList)}"/>
          <div class="productFooter">
		  	<c:if test="${size > 1}">						
				<div class="inutChoiseV2">
				<div class="sorter productTaille">
					<div class="ddWrapper">
					<span type="text" class="selectbox" autocomplete="off" readonly="" tabindex="0" style="padding-right:20px; vertical-align:middle;line-height:27px;" >${displayName}</span>
					</div>
				  </div>
				</div>
			</c:if>
            <c:if test="${not empty listPrice}">
              <dsp:droplet name="/com/castorama/droplet/ProductPriceCache">
                <dsp:param name="key" value="single_sku_${productId}_${sku.repositoryId}_3_${isRobot}_${storeId}"/>
                <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSeconds"/>
                <dsp:oparam name="output">
                  <dsp:setvalue param="sku" value="${sku}"/>
                  <div class="productDecription">
                    <div class="clear"></div>
                              <span class="refNum"> 
                                <fmt:message key="castCatalog_contentProductDetails.ref"/>&nbsp;<dsp:valueof param="sku.CodeArticle"/>
                                <dsp:getvalueof var="skuCodeArticle" param="sku.CodeArticle"/>
                              </span>
                    <ul class="${listColor}">
                      <dsp:getvalueof var="showSeeMore" value="false" vartype="boolean"/>
                      <dsp:droplet name="ProductDescPreProcessorDroplet">
                        <dsp:param name="description" param="sku.LibelleClientLong"/>
                        <dsp:oparam name="output">
                          <dsp:valueof param="result" valueishtml="true"/>
                          <dsp:getvalueof var="showSeeMore" param="showSeeMore"/>
                        </dsp:oparam>
                      </dsp:droplet>
                    </ul>
                    <c:if test="${showSeeMore == true}">
                                <span class="lightBg">
                                    <a href="#prodTabs" class="arrowedLink ${linkColor}" onclick="var id=$('.prodDescMarker').parent('div').attr('id');">
                                      <fmt:message key="castCatalog_contentProductDetails.viewMoreLink"/>
                                    </a>
                                </span>
                    </c:if>
                  </div>
                  <div class="productLabel">
                    <dsp:include page="includes/brandLink.jsp">
                      <dsp:param name="product" param="product"/>
                      <dsp:param name="className" value="greyLink"/>
                      <dsp:param name="showImage" value="${true}"/>
                    </dsp:include>
                  </div>

                </dsp:oparam>
              </dsp:droplet>
            </c:if>
            <div class="clear"></div>


            <div class="clear"></div>
            <c:if test="${not empty listPrice}">
              <%@ include file="/castCatalog/includes/promotions.jspf" %>
              <div class="clear"></div>
            </c:if>

            <dsp:include page="includes/messageInformation.jsp">
              <dsp:param name="sku" value="${sku}"/>
              <dsp:param name="pricedItem" value="${pricedSku}"/>
            </dsp:include>

            <%@ include file="/castCatalog/includes/promotionFields.jspf"%>

            <%@ include file="/castCatalog/includes/productDetailsPromo.jspf" %>
          </div>
        </div>
      </div>
      <div class="clear"></div>
      <dsp:include page="includes/productDetailsTabsInfo.jsp">
        <dsp:param name="listColor" value="${listColor}"/>
        <dsp:param name="bgColor" value="${bgColor}"/>
        <dsp:param name="element" value="${sku}"/>
        <dsp:param name="skuId" value="${sku.repositoryId}"/>
		<dsp:param name="store" value="${store}"/>
		<dsp:param name="quantity" value="${quantity}"/>
		<dsp:param name="product" param="product"/>
		<dsp:param name="packId" param="packId"/>
      </dsp:include>
      <div class="clear"></div>
      </div>
      <div class="rightColumn rightColumnV2 productDetailsRightColumn">
        <div class="shoppingCartV2 clearfix">
		<c:if test="${not empty listPrice}">
			  <dsp:include page="includes/addToCartAreaInfo.jsp">
				<dsp:param name="sku" value="${sku}"/>
				<dsp:param name="productId" value="${productId}"/>
				<dsp:param name="childSku" value="${childSku}"/>
				<dsp:param name="thumbnailImage" value="${productImage}"/>
				<dsp:param name="pageType" value="productDetails"/>
				<dsp:param name="quantity" value="${quantity}"/>
				<dsp:param name="store" value="${store}"/>
				<dsp:param name="packId" value="${packId}"/>
			  </dsp:include>
		</c:if>
        </div>
      </div>
      <div class="clear">
      </div>
      </dsp:oparam>
    </dsp:droplet>
  </div>

</dsp:page>