<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
     
  <dsp:getvalueof var="url" param="url"/>
  <dsp:getvalueof var="fullSize" param="fullSize"/>
  
  <dsp:getvalueof var="videoWidth" value="320"/>
  <dsp:getvalueof var="videoHeight" value="260"/>
  <c:if test="${fullSize}">
	 <dsp:getvalueof var="videoWidth" value="640"/>
	 <dsp:getvalueof var="videoHeight" value="500"/>
  </c:if>
  
  <dsp:droplet name="SKULookup">
    <dsp:param name="id" param="skuId"/>
     <dsp:oparam name="output">
       <dsp:getvalueof var="sku" param="element"/>
       <dsp:getvalueof var="displayName" param="element.displayName"/>
       <dsp:getvalueof var="aux_url" param="element.urlAuxVideo"/>       
     </dsp:oparam>
   </dsp:droplet>
   
   <c:if test="${not empty aux_url}">
     <dsp:getvalueof var="videoWidth" value="640"/>
     
     <dsp:getvalueof var="auxWidth" value="${fn:substringAfter(aux_url, 'width=\"')}" />
     <dsp:getvalueof var="auxWidth" value="${fn:substringBefore(auxWidth, '\"')}" />
     <dsp:getvalueof var="oldWidth" value="width=\"${auxWidth}" />
     <dsp:getvalueof var="aux_url" value="${fn:replace(aux_url, oldWidth, 'width=\"640')}"/>
  </c:if>
  
    <c:if test="${not empty url || not empty aux_url}">
      <div id="videoPopupFormContainer" class="whitePopupContent popupFormContainer" style="width: ${videoWidth}px;">
        <div id="videoPopupHeader" class="whitePopupHeader">
	      <h1>${displayName}</h1>
          
          <fmt:message key="castCatalog_label.close" var="fermer"/>
          <a title="${fermer}" class="closeBut" onclick="hideVideoPopup('product_video', '${contextPath }/castCatalog/includes/video_popupEmpty.jsp');" href="javascript:void(0);"">
            <span><!--~--></span>${fermer}
          </a>
          <div class="clear"><!--~--></div>
        </div>

        <dsp:getvalueof var="productId" param="productId"/>
        <c:if test="${not empty productId && not empty sku}">
        <div id="videoPopupPrice">
		<h2>
          <dsp:include page="skuPrice.jsp">
            <dsp:param name="sku" value="${sku }"/>
            <dsp:param name="productId" value="${productId}"/>
          </dsp:include>
        </h2>
		</div>
        </c:if>

        <div class="popupContentContainer" >
			<div id="videoPopupContent">
        <c:choose>
          <c:when test="${not empty aux_url}">
            ${aux_url}
          </c:when>
          <c:otherwise>
            <object type="application/x-shockwave-flash" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/ flash/swflash.cab#version=6,0,40,0" width="${videoWidth}" height="${videoHeight}">
              <param name="movie" value="${httpLink}/images/video/player_flv.swf" />
              <param name="quality" value="high" />
              <param name="allowScriptAccess" value="sameDomain" />
              <param name="scale" value="noscale" />
              <param name="flashvars" value="flv=${url}&amp;autoload=1&amp;autoplay=1&amp;width=${videoWidth}&amp;height=${videoHeight}&amp;margin=0&amp;playercolor=ffffff&amp;buttoncolor=748892&amp;buttonovercolor=748892&amp;slidercolor1=f38025&amp;slidercolor2=f38025&amp;sliderovercolor=f38025&amp;showvolume=1&amp;showtime=1&amp;showplayer=always&amp;bgcolor=323232&amp;bgcolor1=323232&amp;bgcolor2=323232&amp;startimage=/store/images/player_bg.gif&amp;buffermessage=Chargement _n_" />              
              <embed src="${httpLink}/images/video/player_flv.swf" flashvars="flv=${url}&amp;autoload=1&amp;autoplay=1&amp;width=${videoWidth}&amp;height=${videoHeight}&amp;margin=0&amp;playercolor=ffffff&amp;buttoncolor=748892&amp;buttonovercolor=748892&amp;slidercolor1=f38025&amp;slidercolor2=f38025&amp;sliderovercolor=f38025&amp;showvolume=1&amp;showtime=1&amp;showplayer=always&amp;bgcolor=323232&amp;bgcolor1=323232&amp;bgcolor2=323232&amp;startimage=/store/images/player_bg.gif&amp;buffermessage=Chargement _n_" play="false" width="${videoWidth}px" height="${videoHeight}px"  quality="high" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
            </object>
          </c:otherwise>
        </c:choose>
		</div>
       </div>
     </div>
   </c:if>
</dsp:page>