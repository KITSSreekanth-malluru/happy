<dsp:page>
  <%--a name="prodTabs"></a> --%>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/util/ProductTabs"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/com/castorama/CastSortPackByPriceDroplet"/>

  <dsp:setvalue param="element" paramvalue="element"/>
  <dsp:getvalueof var="skuId" param="skuId"/>
  <dsp:getvalueof var="bgColor" param="bgColor"/>
  <dsp:getvalueof var="listColor" param="listColor"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
  <dsp:getvalueof var="store" param="store"/>
  <dsp:getvalueof var="quantity" param="quantity"/>
  <dsp:getvalueof var="packId" param="packId"/>
  <dsp:getvalueof var="productId" param="product.repositoryId"/>
  
  <%-- clear product tabs --%>
  <dsp:setvalue bean="ProductTabs.clear" value="${true}"/>
  
  <dsp:getvalueof var="sku_attributes" param="element.attributes"/>
  <fmt:message var="attributes" key="msg.products.attributes" />
  <dsp:setvalue bean="ProductTabs.tabs.technicTab.page" value="technicTab.jsp" />
  <dsp:setvalue bean="ProductTabs.tabs.technicTab.title" value="${attributes}" />

  <dsp:getvalueof var="availablePacks" param="element.packs"/>
  <%--<dsp:getvalueof var="packListSize" value="${fn:length(availablePacks)}"/>--%>
  <c:if test="${not empty availablePacks}">
    <fmt:message var="pack" key="msg.products.pack" />
    <dsp:setvalue bean="ProductTabs.tabs.packsTab.page" value="packsTabInfo.jsp" />
    <dsp:setvalue bean="ProductTabs.tabs.packsTab.title" value="${pack}" />
  </c:if>

  <dsp:setvalue param="skuId" value="${skuId}"/>
  <dsp:setvalue param="availablePacks" value="${availablePacks}"/>
  <dsp:setvalue param="bgColor" value="${bgColor}"/>
  <dsp:setvalue param="listColor" value="${listColor}"/>
  
  <dsp:droplet name="Switch">
  <dsp:param name="value" bean="ProductTabs.clear" />
  <dsp:oparam name="true" >
  </dsp:oparam>
  <dsp:oparam name="default" >
	<dsp:getvalueof var="tabStyle" param="element.parentCategory.style.tabStyle" />

    <dsp:getvalueof var="set" bean="ProductTabs.keys"/>
    <dsp:param name="list" value="${castCollection:list(set)}" />
    <dsp:getvalueof var="tn" value="tabs_pd" />
    <div class="tabsetBlock" id="${tn}" >
      <div class="tabsArea ${tabStyle }" id="${tn}_tabs">
        
      <dsp:droplet name="ForEach">
        <dsp:param name="array" bean="ProductTabs.tabs" />
        <dsp:oparam name="output">
        
	        <dsp:getvalueof var="page" param="element.page" />
	        <dsp:getvalueof var="key" param="key" />
	        <dsp:getvalueof var="index" param="index" />
            <c:if test="${'defaultTab.jsp' eq page}">
              <c:set var="key" value="${index}" />
            </c:if>        
            <a name="tab_${key}"></a>
            
            <dsp:getvalueof var="outerKey" value="${key}" />
            <%--<div style="width:500px; height:5px; background:#fff; position:absolute; z-index:400; margin-top:-5px;"></div>--%>
            
            <dsp:getvalueof var="visible" param="element.visible" />
            <ul id="ul" <c:if test="${!visible}">name="ul_invisible" style="display:none;"</c:if>>
	          <dsp:droplet name="ForEach">
	            <dsp:param name="array" bean="ProductTabs.tabs" />
	            <dsp:oparam name="output">
	              <dsp:getvalueof var="element" param="element" />
	              <dsp:getvalueof var="tab_title" param="element.title" />
	              <dsp:getvalueof var="visible" param="element.visible" />
	              <dsp:getvalueof var="key" param="key" />
	              <dsp:getvalueof var="index" param="index" />
	              <dsp:getvalueof var="page" param="element.page" />
	
	              <c:if test="${'defaultTab.jsp' eq page}">
	                <c:set var="key" value="${index}" />
	              </c:if>
	
	              <c:set var="li_class" value="" />
	              <c:if test="${key eq outerKey}">
	                <c:set var="li_class" value="active" />
	              </c:if>
	              
	              <c:if test="${!visible}">
	                <c:set var="li_class" value="${li_class} hidden" />
	              </c:if>
	
	              <li id="tab${key}" <c:if test="${!visible}">name="tab${key}_invisible"</c:if> class="${li_class}" >
	                <a href="#tab_${key}" title="${tab_title}">${tab_title}</a>
	              </li>
	            </dsp:oparam>
	          </dsp:droplet>
	        </ul>
	        <dsp:getvalueof var="visible" param="element.visible" />
	        <div class="tabsAreaBottomLine" style='position: relative; top: 19px;<c:if test="${!visible}"> display:none;</c:if>' id="tabsAreaBottomLine" <c:if test="${!visible}">name="tabsAreaBottomLine_invisible"</c:if>><!--~--></div>

		    <dsp:getvalueof var="page" param="element.page" />
            <dsp:getvalueof var="key" param="key" />
            <dsp:getvalueof var="index" param="index" />
            <dsp:param name="tabName" param="element.title"/>

            <c:if test="${'defaultTab.jsp' eq page}">
              <c:set var="key" value="${index}" />
            </c:if>
            
            <dsp:getvalueof var="visible" param="element.visible" />
            <div id="${tn}_page${key}" <c:if test="${!visible}">style="display:none;"</c:if>>
              <dsp:include page="${page}" >
                <dsp:param name="tabName" param="tabName"/>
                <dsp:param name="productId" param="productId"/>
                <dsp:param name="skuId" param="skuId"/>
                <dsp:param name="groupedProdMap" param="groupedProdMap"/>
                <dsp:param name="availablePacks" param="availablePacks"/>
                <dsp:param name="bgColor" param="bgColor"/>
                <dsp:param name="listColor" param="listColor"/>
                <dsp:param name="quantity" param="quantity"/>		
                <dsp:param name="packId" param="packId"/>		
              </dsp:include>
            </div>
            
            <div class="tabsBackToTopWr" <c:if test="${!visible}">name="tabsBackToTopWr_invisible" style="display:none;"</c:if>><font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font> &nbsp;<a href="consForumBackToTop" class="consForumBackToTop">Haut de page</a>&nbsp; <font size="3" style="font-weight:normal;vertical-align:top;color:#4b4b4d;">&#94;</font></div>
          
        </dsp:oparam>
      </dsp:droplet>
      
     </div>
      
    </div>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>