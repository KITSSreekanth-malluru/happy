<dsp:page>

  <div id="itineraire" class="whitePopupContainer" style="display: block; top:5px;">
  <div class="whitePopupContent popupFormContainer">
    <div class="whitePopupHeader">
      <fmt:message key="castCatalog_label.close" var="fermer"/>
      <a href="javascript:void(0)" onClick="closeEarlyAtoutPopup(this);" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a>
    </div>
      
    <div class="clear"><!--~--></div>
    <div class="popupContentContainer">
    <div class="popupForm">
      <div class="formMainBlock">
       <dsp:getvalueof var="sofincoUrl" bean="/com/castorama/CastConfiguration.sofincoUrl" />
       <iframe width="95%" height="630" frameborder="none" border="none" id="route" 
         src="${sofincoUrl}"  charEncoding="ISO-8859-1"> 
       </iframe>
      </div>
      <div class="clear"><!--~--></div>
    </div>
    </div>
  </div>
  </div>
</dsp:page>