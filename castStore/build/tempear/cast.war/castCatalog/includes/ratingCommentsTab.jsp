<dsp:page>
  <dsp:importbean bean="/com/castorama/bazaarvoice/utils/BVConfiguration"/>
  <dsp:getvalueof var="bvRoot" bean="BVConfiguration.bvRoot"/>
  <dsp:getvalueof var="bvDisplayCode" bean="BVConfiguration.bvDisplayCode"/>
  <dsp:getvalueof var="submitUrl" bean="BVConfiguration.submitUrl"/>

  <dsp:getvalueof var="productId" param="productId"/> 

  <div id="BVReviewsContainer"></div>

  <!-- Reviews -->
  <script type="text/javascript" language="javascript">
  <!--
    var BVisLoaded = false;
    function BVcheckLoadState() {
      if(!BVisLoaded) {
          var page = document.getElementById('BVFrame').src;
            document.getElementById('BVFrame').src='${bvRoot}/logging?page=' + escape(page);
          document.getElementById('BVReviewsContainer').innerHTML = '<!-- TODO connection problem message (retrieval timed out) -->';
       }
    }
    
    function showReviewTab() {
		var tabInvArray = new Array();
		var tabInvArray = document.getElementsByName('tabratingCommentsTab_invisible');
		for(var i = 0; i < tabInvArray.length; i++)
        {
            var tabInv = tabInvArray.item(i);
			tabInv.className=tabInv.className.replace(/ hidden/g, ""); 
        }
		
		document.getElementById('tabs_pd_pageratingCommentsTab').style.display="";

		document.getElementsByName('ul_invisible')[0].style.display="";
		document.getElementsByName('tabsAreaBottomLine_invisible')[0].style.display="";
		document.getElementsByName('tabsBackToTopWr_invisible')[0].style.display="";
    }
  //-->
  </script>

  <div id="BVSubmissionParameters" style="display: none;"></div>
  <div id="BVContainerPageURL" style="display: none;">${submitUrl}</div>

  <iframe id="BVFrame" name="BVFrame" src="" style="visibility: hidden; width: 1px; height: 1px; position: absolute; left: -999px; top: -999px;"></iframe>

  <script type="text/javascript" language="javascript">
  <!--
    function bvLoadRR() {
        var bvPage = '${bvRoot}/${bvDisplayCode}/p${productId}/reviews.htm?format=embedded';
        var bvReviewIDName = 'featurereview';
        
        var bvReviewIDRegex = new RegExp('[?&]' + bvReviewIDName + '=([^&#]+)');
        var bvReviewIDMatch = bvReviewIDRegex.exec(window.location.search);
        var bvReviewID = bvReviewIDMatch ? decodeURIComponent(bvReviewIDMatch[1]) : null;
        
        document.getElementById('BVFrame').src = /^[0-9]+$/.test(bvReviewID) ? bvPage + '&reviewid=' + bvReviewID : bvPage;
        
        //Timeout for review load.  Consider reviews unavailable if not loaded within 15 seconds
        setTimeout("BVcheckLoadState()", 25000);
    }

    $(document).ready(function(){
      bvLoadRR();
	});
  //-->
  </script>
</dsp:page>