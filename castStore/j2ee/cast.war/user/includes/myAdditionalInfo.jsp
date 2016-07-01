<dsp:page>
	<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
	<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />


    <div class="formContent grayCorner grayCornerGray">
        <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
        <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
        <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
        <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
        <div class="cornerOverlay hightRows">
        	<dsp:include page="contentAdditionalInfo.jsp">
              <dsp:param name="magasinId" param="magasinId" />
            </dsp:include>
        </div>
    </div>
</dsp:page>
