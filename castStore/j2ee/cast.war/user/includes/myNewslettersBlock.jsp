<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
<h2><fmt:message key="header.my.newsletters" /></h2>
<div class="formContent grayCorner grayCornerGray">
    <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
	<div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
	<div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
	<div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
	<div class="cornerOverlay">
		<%@ include file="newsletterSubscription.jspf" %>		
	</div>
</div>