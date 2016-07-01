<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<script type="text/javascript">

    function showCastoramaCard() {
        document.getElementById('castoramaCardBlock').style.display = "block";
        document.getElementById('castoramaCardBlock').style.visibility = "visible";

        document.getElementById('castoramaCard').value = true;
        if (document.getElementById('carteAtout'))
            document.getElementById('carteAtout').value = true;
    }

    function hideCastoramaCard() {

        if (pageName != "myProfile") {
            document.getElementById('castoramaCardNumber').value = "";
        }
        document.getElementById('castoramaCardNumberServerValue').value = true;

        document.getElementById('castoramaCard').value = false;
        if (document.getElementById('carteAtout'))
            document.getElementById('carteAtout').value = false;

        document.getElementById('castoramaCardBlock').style.display = "none";
        document.getElementById('castoramaCardBlock').style.visibility = "hidden";

        hideRedCross();
        hideGreenCheck();
    }

    function deleteCastoramaCardValue() {
        var selected = $('input:checked', '#myInformation').val();
        if (selected == "false") {
            $('#castoramaCardNumber').val('');
        }
    }
</script>

<dsp:page>

	<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
	<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
	
	<dsp:getvalueof var="content" param="content"/>
	<dsp:getvalueof var="bean" param="bean"/>	
	<c:if test="${content == 'profile'}">
		<dsp:getvalueof var="currentLocalStore" value="${bean}.currentLocalStore"/>
	</c:if>
	<dsp:getvalueof var="castoramaCardNumber" value="${bean}.castoramaCardNumber"/>
	<dsp:getvalueof var="castCardNumberCorrect" value="${bean}.castCardNumberCorrect"/>
	<dsp:getvalueof var="page" param="page"/>
	
	<h2><fmt:message key="msg.account.informations.header" /></h2>	
	<div id="myInformation">	
	<div class="formContent grayCorner grayCornerGray ${page}">
		<div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
		<div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
        <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
        <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
        <div class="cornerOverlay">                             	
                
        	<dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
            <c:if test="${fn:contains(requestURI, 'myProfile.jsp') || fn:contains(requestURI, 'createAccount.jsp')}">
            
            <div class="f-row">
            	<label><b><fmt:message key="msg.account.informations.favorite.store" /> :</b></label>
                <div class="f-inputs formSelector">
                	<div class="chooseShopSelectorWr favStorePair">
                		<dsp:getvalueof var="query" value="entite.adresse.departement.numero != 999" />
                		<dsp:select bean="${currentLocalStore}" iclass="styled" id="geo3">
                			<dsp:option value=""><fmt:message key="msg.newsletter.choose.store" /></dsp:option>
                			<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
                				<dsp:param name="queryRQL" value="${query}" />
                				<dsp:param name="repository" value="/atg/registry/Repository/MagasinGSARepository" />
                				<dsp:param name="itemDescriptor" value="magasin" />
                				<dsp:param name="elementName" value="magasinRQL" />
                				<dsp:param name="sortProperties" value="+entite.adresse.cp,+nom" />
                				<dsp:oparam name="output">
                					<dsp:getvalueof var="iscc" param="magasinRQL.retraitMagasin"/>
                					<dsp:option paramvalue="magasinRQL.id" iclass="${iscc?'iscc':''}">
                						<dsp:valueof param="magasinRQL.entite.adresse.cp" /> - <dsp:valueof param="magasinRQL.nom" />
                					</dsp:option>
                				</dsp:oparam>
                			</dsp:droplet>
                		</dsp:select>
                		<img src="/store/images/choosenShopSelectorBg.png" />
                	</div>
                	<div class="removingStore">= <fmt:message key="cc.retrait.en.magasin"/></div>
				</div>
			</div>
			
			<dsp:getvalueof var="carteAtout" bean="CastNewsletterFormHandler.value.carteAtout"/>

			<c:if test="${empty carteAtout}">
				<dsp:setvalue bean="CastProfileFormHandler.value.carteAtout" beanvalue="CastNewsletterFormHandler.value.carteAtout" />
			</c:if>
			<div class="f-row">
				<label><b><fmt:message key="msg.account.informations.castorama.card.presence" /> :</b></label>
				<div class="f-inputs">
					<dsp:input bean="CastProfileFormHandler.castoramaCard" type="radio" value="true" iclass="i-radio" onclick="showCastoramaCard();" />
					<span class="i-radio-lbl"><fmt:message key="msg.accout.oui" /></span>
					<dsp:input bean="CastProfileFormHandler.castoramaCard" type="radio" value="false" iclass="i-radio" onclick="hideCastoramaCard();" />
					<span class="i-radio-lbl"><fmt:message key="msg.accout.non" /></span>
				</div>
			</div>
			
			<dsp:input type="hidden" bean="CastProfileFormHandler.castoramaCard" id="castoramaCard" />
			<dsp:getvalueof var="carteAtout" bean="CastProfileFormHandler.value.carteAtout"/>
			<c:if test="${page == 'createAccount' && not empty carteAtout}">
				<dsp:input type="hidden" bean="CastNewsletterFormHandler.value.carteAtout" id="carteAtout" />
			</c:if>
			
			<dsp:getvalueof var="castoramaCardPresence" bean="CastProfileFormHandler.castoramaCard"/>
			<c:choose>
				<c:when test="${castoramaCardPresence}">
					<dsp:setvalue param="castoramaCardVisibility" value="display: block; visibility: visible;" />
					<dsp:getvalueof var="castoramaCardVisibility" param="castoramaCardVisibility" />
	            </c:when>
	            <c:otherwise>
	            	<dsp:setvalue param="castoramaCardVisibility" value="display: none; visibility: hidden;" />
	            	<dsp:getvalueof var="castoramaCardVisibility" param="castoramaCardVisibility" />
	            </c:otherwise>
	        </c:choose>

			
			<div id="castoramaCardBlock" style="${castoramaCardVisibility}" >
			
			<div class="whiteCornerTop">&nbsp;</div>
			<div class="splitter"><div class="splitterGrayLine" ></div></div>

			<div class="whiteBackground" >
			<div class="f-row">
				<label><fmt:message key="msg.account.informations.castorama.card.number.rule" /> :</label>
				<div class="f-inputs">
					  <dsp:input bean="${castoramaCardNumber}" maxlength="19" size="30" type="text"  iclass="i-text" name="castoramaCardNumber" id="castoramaCardNumber" onclick="validateCastoramaCardNumber(this.value);" onkeyup="validateCastoramaCardNumber(this.value);" />
					  <dsp:input type="hidden" bean="${castCardNumberCorrect}" id="castoramaCardNumberServerValue" />
					  <span id="validateCardNumber">
					  <span id="redCross" style="display: none; visibility: hidden;"><img src="/images/icoRedCross.png" />&nbsp;&nbsp;</span>
					  <span id="greenCheck" style="display: none; visibility: hidden;"><img src="/images/icoGreenCheck.png" />&nbsp;&nbsp;</span>
					  <img src="/images/icoBlueQuestion.png" id="icoBlueQuestion" alt="<fmt:message key="msg.account.informations.castorama.card.tooltip" />" />
					  </span>
				</div>
			</div>
			</div>
			
			<div class="splitter"><div class="splitterGrayLine"></div></div>
			
			</div>
			
			</c:if>
			
		</div>
	</div>
	</div>
</dsp:page>

<script>
function castoramaCardToolTip(targetItem, tooltipName){
	
$("body").append("<div class='"+tooltipName+"' ><div class=\"whiteCornerLeft\">&nbsp;</div><p>"+$(targetItem).attr('alt')+"</p></div>");
var myTooltip = $("."+tooltipName);
myTooltip.hide();

$(targetItem).removeAttr("alt").mouseover(function(){
myTooltip.show(); 
}).mousemove(function(kmouse){
myTooltip.css({left:kmouse.pageX+15, top:kmouse.pageY-15});
}).mouseout(function(){
myTooltip.hide(); });
}

$(document).ready(function(){
	castoramaCardToolTip("#icoBlueQuestion","castoramaCardTooltip");
});

</script>