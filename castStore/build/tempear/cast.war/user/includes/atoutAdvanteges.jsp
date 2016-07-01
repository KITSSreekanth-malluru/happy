<dsp:page>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />


  <div class="formAtoutBlock">
  <script type="text/javascript">
        function openWindow(theURL,theName,theFeatures)
        {
          window.open(theURL,theName,theFeatures);
        } 
        
        function popupCentree(url,largeur,hauteur,options) {
          var top=(screen.height-hauteur)/2;
          var left=(screen.width-largeur)/2;
          window.open(url,"","top="+top+",left="+left+",width="+largeur+",height="+hauteur+","+options);
        }
     
  </script>


    <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
	<c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/atoutAdvanteges.html"/>
 	
	
   <div class="clear"></div>
   
  </div>
  <dsp:include page="adviceBlock.jsp" /> 
  <div id="atoutPopup" loaded="false">
  </div>
</dsp:page>