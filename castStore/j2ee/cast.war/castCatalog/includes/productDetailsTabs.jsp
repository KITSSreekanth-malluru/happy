<dsp:page>
  <a name="prodTabs"></a>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/util/ProductTabs"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/com/castorama/CastSortPackByPriceDroplet"/>

  <dsp:getvalueof var="fixedRelatedProducts" param="fixedRelatedProducts"/>
  <dsp:setvalue param="element" paramvalue="element"/>
  <dsp:getvalueof var="skuId" param="skuId"/>
  <dsp:getvalueof var="bgColor" param="bgColor"/>
  <dsp:getvalueof var="listColor" param="listColor"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
  <dsp:getvalueof var="singleSku" param="singleSku"/>
  
  
  <c:if test="${singleSku || empty singleSku}">
    <dsp:getvalueof var="sku_attributes" param="element.attributes"/>
    <fmt:message var="attributes" key="msg.products.attributes" />
    <dsp:setvalue bean="ProductTabs.tabs.technicTab.page" value="technicTab.jsp" />
    <dsp:setvalue bean="ProductTabs.tabs.technicTab.title" value="${attributes}" />
  </c:if>
  
  <c:if test="${not empty fixedRelatedProducts and not baFakeContext}">
    <fmt:message var="related" key="msg.products.related" />                      
    <dsp:setvalue bean="ProductTabs.tabs.complementaryTab.page" value="complementaryTab.jsp" />
    <dsp:setvalue bean="ProductTabs.tabs.complementaryTab.title" value="${related}" />
  </c:if>

  <dsp:getvalueof var="packs" param="element.packs"/>
  
    <dsp:droplet name="/com/castorama/droplet/MultiStockVisAvailabilityDroplet">
	  <dsp:param name="productsSet" value="${packs}" />
	  <dsp:param name="store" bean="/atg/userprofiling/Profile.currentLocalStore" />
	  <dsp:oparam name="output">
	      <c:choose>
	      <c:when test="${empty svAvailableMap}">
	        <dsp:getvalueof var="svAvailableMap" param="svAvailableMap" scope="request"/>
	      </c:when>
	      <c:otherwise>
	        <dsp:getvalueof var="addToSvAvailableMap" param="svAvailableMap" />
	        ${castCollection:putAll(svAvailableMap, addToSvAvailableMap)}
	      </c:otherwise>
	      </c:choose>
	  </dsp:oparam>
	</dsp:droplet>

  <dsp:droplet name="CastSortPackByPriceDroplet">
    <dsp:param name="packs" value="${packs}"/>
    <dsp:param name="store" bean="/atg/userprofiling/Profile.currentLocalStore" />
    <dsp:param name="svAvailableMap" value="${svAvailableMap}" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="packList" param="packList"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:getvalueof var="availablePacks" value="${packList}"/>
  <c:if test="${not empty availablePacks and not baFakeContext}">
    <fmt:message var="pack" key="msg.products.pack" />
    <dsp:setvalue bean="ProductTabs.tabs.packsTab.page" value="packsTab.jsp" />
    <dsp:setvalue bean="ProductTabs.tabs.packsTab.title" value="${pack}" />
  </c:if>

  <fmt:message var="review" key="castCatalog_tabNames.review" />
  <dsp:setvalue bean="ProductTabs.tabs.ratingCommentsTab.page" value="ratingCommentsTab.jsp" />
  <dsp:setvalue bean="ProductTabs.tabs.ratingCommentsTab.title" value="${review}" />
  <dsp:setvalue bean="ProductTabs.tabs.ratingCommentsTab.visible" value="false" />


  <dsp:setvalue param="skuId" value="${skuId}"/>
  <dsp:setvalue param="availablePacks" value="${availablePacks}"/>
  <dsp:setvalue param="bgColor" value="${bgColor}"/>
  <dsp:setvalue param="listColor" value="${listColor}"/>
  <%@ include file="productTabbedPane.jspf" %>
</dsp:page>