<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
     

  <div id="videoPopupFormContainer" class="whitePopupContent popupFormContainer">
    <div id="3DPopupHeader" class="whitePopupHeader">
      <dsp:droplet name="SKULookup">
        <dsp:param name="id" param="skuId"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="URL" param="element.flash_produit"/>
          <dsp:getvalueof var="sku" param="element"/>       
          <h1><dsp:valueof param="element.displayName"/></h1>
        </dsp:oparam>
      </dsp:droplet> 
      <fmt:message key="castCatalog_label.close" var="fermer"/>
      <a title="${fermer}" class="closeBut" onclick="hideVideoPopup('product_3D', '${contextPath }/castCatalog/includes/video_popupEmpty.jsp');" href="javascript:void(0);"">
        <span><!--~--></span>${fermer}
      </a>
      <div class="clear"><!--~--></div>
    </div> 

	  <div class="popupContentContainer">
	  <div id="videoPopupContent">
	    <embed
	      height="480"
	      width="640"
	      name="plugin"
	      src="${URL}"
	      type="application/x-shockwave-flash" />
		  </div>
    </div>
  </div>
</dsp:page>