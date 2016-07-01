
<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  <html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/main.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/cc_main.css"/>
  </head>
  <body class="print productInfo">
    <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
    <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
    <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
    <dsp:importbean bean="/com/castorama/droplet/ProductDescPreProcessorDroplet"/>
    <dsp:importbean bean="/com/castorama/droplet/BrandLookupDroplet"/>
    <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
    <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
    <dsp:importbean bean="/atg/dynamo/service/CurrentDate"/>
    <dsp:importbean bean="/com/castorama/droplet/CastGetApplicablePromotions"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
    <dsp:getvalueof var="productId" param="productId"/>
    <dsp:getvalueof var="categoryId" param="categoryId"/>
    <dsp:getvalueof var="skuId" param="skuId"/>
    <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
    <dsp:getvalueof var="notCheapestSkuPromo" param="notCheapestSkuPromo"/>
    <dsp:getvalueof var="notCheapestSkuPromoChosen" param="notCheapestSkuPromoChosen"/>

    <fmt:message key="castCatalog_label.close" var="fermer"/>
    <fmt:message key="castCatalog_label.print" var="imprimer"/>
    <dsp:getvalueof var="activateOmniture" bean="/com/castorama/CastConfiguration.activateOmniture" />
    <dsp:droplet name="ProductLookup">
      <dsp:param name="id" value="${productId}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="product" param="element"/>
        <dsp:getvalueof var="childSku" param="element.childSKUs"/>
      </dsp:oparam>
    </dsp:droplet>
   
    <c:choose>
      <c:when test="${not empty skuId}">
        <dsp:droplet name="SKULookup">
          <dsp:param name="id" value="${skuId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="sku" param="element"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:when>
      <c:otherwise>
        <dsp:droplet name="CastPriceRangeDroplet">
          <dsp:param name="productId" value="${productId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="sku" param="sku"/>
          </dsp:oparam>
        </dsp:droplet>  
      </c:otherwise>
    </c:choose>
    
    <c:choose>
      <c:when test="${isSearchResult}">
        <dsp:param name="product" value="${product}"/>
        <dsp:getvalueof var="listColor" param="product.parentCategory.style.listStyle" /> 
      </c:when>
      <c:otherwise>
        <dsp:droplet name="StyleLookupDroplet">
          <dsp:param name="categoryId" value="${categoryId}" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="listColor" param="style.listStyle" /> 
          </dsp:oparam>
        </dsp:droplet> 
      </c:otherwise>
    </c:choose>
    
    <dsp:param name="sku" value="${sku}"/>
    <div class="whitePopupContent">
      <div class="whitePopupHeader">
        <img src="${pageContext.request.contextPath}/images/logo.png" id="logo" alt="Castorama.fr" class="topLogo"/>
        <a href="javascript:void(0)" onclick="window.close();" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a>
        <a href="javascript:void(0)" onclick="window.print();" class="imprimer" title="${imprimer}"><span><!--~--></span>${imprimer}</a>
        <div class="clear"></div>
        <div class="addPrintInfo">
          <span class="printDate">
             <fmt:message key="print.product.date"/>: <dsp:valueof bean="CurrentDate.timeAsDate" date="dd/MM/yyyy"/> - <dsp:valueof bean="CurrentDate.hour" number="00"/>:<dsp:valueof bean="CurrentDate.minute" number="00"/>
             <dsp:getvalueof var="currentLocalStore" bean="Profile.currentLocalStore"/>
             <dsp:getvalueof var="storeId" bean="Profile.currentLocalStore.id"/>
             <c:if test='${currentLocalStore != null && storeId != "999"}'>
                 <dsp:getvalueof var="storeNom" bean="Profile.currentLocalStore.nom"/> - <fmt:message key="print.product.store"/>&nbsp;${storeNom}
             </c:if>
          </span>
          <div class="clear"></div>
          <p class="title">
            <b><fmt:message key="print.product.title"/>&nbsp;</b><fmt:message key="print.product.title.1"/>
            <dsp:valueof bean="CurrentDate.timeAsDate" date="dd/MM/yyyy"/> à <dsp:valueof bean="CurrentDate.hour" number="00"/>:<dsp:valueof bean="CurrentDate.minute" number="00"/>:<dsp:valueof bean="CurrentDate.second" number="00"/>&nbsp;
            <fmt:message key="print.product.title.2"/>
          </p>
        </div>
      </div>
      <div class="clear"><!--~--></div>

      <div class="popupContentContainer">
        <div class="productBlock">
          <div class="productImageColumn">
          
            <div class="productImage productImagePrint ">
              <dsp:include page="prodHighlight.jsp">
                <dsp:param name="product" value="${product}"/>
                <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromo}"/>
                <dsp:param name="categoryId" value="${categoryId}"/>
                <dsp:param name="view" value="galeryView"/>
                <dsp:param name="isSearchResult" value="${isSearchResult}"/>
                <dsp:param name="printVersion" value="${true}" />
              </dsp:include>
              <dsp:param name="product" value="${product}"/>
              <c:choose>
                <c:when test="${fn:length(childSku)==1 || not empty skuId}">
                  <dsp:getvalueof var="name" param="sku.displayName"/>
                  <dsp:getvalueof var="description" param="sku.LibelleClientLong"/>
                </c:when>
                <c:otherwise>
                  <dsp:getvalueof var="name" param="product.displayName"/>
                  <dsp:getvalueof var="description" param="product.longDescription"/>
                </c:otherwise>
              </c:choose>
              <dsp:getvalueof var="largeImage" param="sku.largeImage.url"/>
              <c:choose>
                <c:when test="${not empty largeImage}">
                  <dsp:img src="${largeImage}" alt="${name}" width="270" height="270"/>
                </c:when>
                <c:otherwise>
                  <dsp:img src="/default_images/h_no_img.jpg" alt="${name}" width="270" height="270" />
                </c:otherwise>
              </c:choose>
            </div>
           
          </div>
          <div class="productContent">
            <h1><dsp:valueof value="${name}"/></h1>
            <div class="productDecription">
              <div class="refAndImg">
                <c:if test="${fn:length(childSku)==1 || not empty skuId}">
                  <span class="refNum">
                    <fmt:message key="castCatalog_contentProductDetails.ref"/>
                    <dsp:valueof param="sku.CodeArticle"/>
                    <dsp:getvalueof var="skuCodeArticle" param="sku.CodeArticle"/>
                  </span>
                </c:if>  
                <dsp:droplet name="BrandLookupDroplet">
                  <dsp:param name="product" value="${product}"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="brandMediaURL" param="brand.mediaMarque.url"/>
                    <c:if test="${not empty brandMediaURL }">
                      <dsp:getvalueof var="brandName" param="brand.name"/>
                      <dsp:img title="${brandName}" alt="${brandName}" src="${brandMediaURL}"/>
                    </c:if>
                  </dsp:oparam>
                </dsp:droplet>
               </div>
              <div class="productPrix printPagePriceBlock">
                <dsp:include page="skuPrice.jsp" flush="true">
                  <dsp:param name="pageType" value="printPage"/>
                  <dsp:param name="sku" param="sku"/>
                  <dsp:param name="productId" value="${productId}"/>
                  <dsp:param name="printVersion" value="${true}"/>
                  <dsp:param name="notCheapestSkuPromo" value="${notCheapestSkuPromoChosen}" />
                </dsp:include>
              </div>
              <div id="printPromotion" style="padding:5px;padding-top:10px;">
                <c:if test="${not empty sku}">
                  <%@ include file="/castCatalog/includes/promotions.jspf" %>
                </c:if>
              </div> 
				</br>
			  <div id="promotions" style="width:auto">
				    <dsp:droplet name="/com/castorama/droplet/SheduleForProductPromotion">
					  <dsp:param name="startDateCommerce" param="sku.startDateCommercePromo"/>
					  <dsp:param name="endDateCommerce" param="sku.endDateCommercePromo"/>
					  <dsp:param name="startDateContenu" param="sku.startDateContenuPromo"/>
					  <dsp:param name="endDateContenu" param="sku.endDateContenuPromo"/>
					  <dsp:oparam name="output">
								  <dsp:getvalueof var="colorCommerce" param="sku.colorLibelleCommerce"/>
								  <dsp:getvalueof var="colorContenu" param="sku.colorLibelleContenu"/>
								  <dsp:getvalueof var="isCommerce" param="isVisibleCommerce"/>
								  <dsp:getvalueof var="isContenu" param="isVisibleContenu"/>
						<c:if test="${(isCommerce == 'true')}">
							<div id="promotions_commerce" style="background:${colorCommerce};padding:7px;">
								<dsp:valueof param="sku.promotionCommerce" valueishtml="true" />
							</div>
							</br>
						</c:if>
						<c:if test="${(isContenu == 'true')}">
							<div id="promotions_contenu" style="background:${colorContenu};padding:7px;">
								<dsp:valueof param="sku.promotionContenu" valueishtml="true" />
							</div>
						</c:if>
                   </dsp:oparam>
				  </dsp:droplet>
              </div>
              <%@ include file="/castCatalog/includes/productDetailsPromo.jspf" %>
            </div>
          </div>
        </div>
      </div>
      <dsp:include page="technicTab.jsp">
        <dsp:param name="skuId" param="sku.repositoryId"/>
        <dsp:param name="listColor" value="${listColor}"/>
        <dsp:param name="isPrintVersion" value="true"/>
      </dsp:include>
      <div class="whitePopupHeader">
        <a href="javascript:void(0)" onclick="window.print();" class="imprimer" title="${imprimer}"><span><!--~--></span>${imprimer}</a>
      </div>
      <div class="clear"></div>
    </div>
    <%-- Omniture params Section begins--%>
    <c:set var="omnitureProducts" value="${skuCodeArticle}"/>
    <fmt:message var="omniturePState" key="omniture.state.product.print"/> 
    <fmt:message var="omnitureChannel" key="omniture.channel.catalogue"/>

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
    <%-- Omniture params Section ends--%>
    
    <dsp:include page="/includes/omnitureCode.jsp">
		  <dsp:param name="omnitureProducts"        value="${omnitureProducts}"/>
		  <dsp:param name="omniturePState"          value="${omniturePState}"/>
		  <dsp:param name="omnitureChannel"            value="${omnitureChannel}"/>
          <dsp:param name="omnitureCurrentLocation" value="${omnitureCurrentLocation}"/>
		</dsp:include>
    
<c:if test="${activateOmniture}">        
    <dsp:include page="/includes/googleAnalytics.jspf"/>
</c:if>
   
  </body>
</html>

</dsp:page>