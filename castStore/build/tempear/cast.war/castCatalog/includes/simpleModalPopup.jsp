<dsp:page>
  <!-------------------------------------------------------------
  
  Page fragment displaying the simple Popup.
  
  ------------------------------------------------------------->
  <dsp:getvalueof var="idSuffix" param="idSuffix"/>
  <div id="popup_name${idSuffix}" class="popup_block">
    <div style="padding: 30px; text-align: center">
      <b><dsp:valueof param="messageBody"></dsp:valueof></b>
    </div>
    <div class="clear"><!--~--></div>
    <!--close button is defined as close class-->

    <a class="buttonEmptyBlueBig" href="#"><fmt:message key="castCatalog_label.close.upper"/></a>
  </div>
</dsp:page>