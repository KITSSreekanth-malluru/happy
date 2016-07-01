<dsp:page>

  <div id="itineraire" class="whitePopupContainer" style="display: block; top:5px;">
  <div class="whitePopupContent popupFormContainer">
    <div class="whitePopupHeader">
      <fmt:message key="castCatalog_label.close" var="fermer"/>
      <a href="javascript:void(0)" onClick="closeAtoutContPopup(this);" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a>
    </div>
      
    <div class="clear"><!--~--></div>
    <div class="popupContentContainer">
    <div class="popupForm">
      <div class="formMainBlock">
        
        
        <dsp:droplet name="/com/castorama/droplet/CastCarteAtout">
         <dsp:oparam name="output">      
          <dsp:getvalueof var="url" param='url' />
            <iframe width="95%" height="630" frameborder="none" border="none" id="route" 
              src="${url }"  charEncoding="ISO-8859-1"> 
            </iframe>
         </dsp:oparam>
        <dsp:oparam name="empty">
        </dsp:oparam>
       </dsp:droplet>
      </div>
      <div class="clear"><!--~--></div>
    </div>
    </div>
  </div>
  </div>
</dsp:page>