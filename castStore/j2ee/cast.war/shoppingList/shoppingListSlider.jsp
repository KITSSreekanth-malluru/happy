<dsp:page>
  
  <div id="slideButton"></div>
  <div id="slidePanel">
    <div class="slidePanelShadow">
      <dsp:include page="/shoppingList/shoppingList.jsp"/>
    </div>
  </div>
    <%-- move to main.js --%>
    <script>
          $(document).ready(function() {
              
              $button = $("#slideButton");
              $button.click(function() {
                  rightSpace = parseInt($button.css("right"),10);
                  if (rightSpace > 0) { 
                      hideSlidePanel();
                      $(this).animate({right: 0}, 300);
                  } else {                      ;
                      $(this).animate({right: 300}, 350);
                      showSlidePanel()
                  }
                });
              
              $(window).scroll(function() {
                 var $panel = $("#slidePanel");
                 var $button = $("#slideButton");

                 if( document.documentElement.scrollTop >= 215 || window.pageYOffset >= 215 ) {
                     if( $.browser.msie && $.browser.version == "6.0" ) {
                         $panel.css( "top", ( document.documentElement.scrollTop + 15 ) + "px" );
                         $button.css( "top", ( document.documentElement.scrollTop + 15 ) + "px" );
                     } else {
                         $panel.css( { position: "fixed", top: "15px" } );
                         $button.css( { position: "fixed", top: "15px" } );
                     }
                 } else if( document.documentElement.scrollTop < 215 || window.pageYOffset < 215 ) {
                     $panel.css( { position: "absolute", top: "215px" } );
                     $button.css( { position: "absolute", top: "215px" } );
                 }
              });
          });
          
          function hideSlidePanel() {
              $("#slidePanel").hide('slide', {direction: 'right'}, 300);              
              return false;
          }
          
          function showSlidePanel() {
             $("#slidePanel").show('slide', {direction: 'right'}, 300);
             return false;
          }
    </script>
</dsp:page>