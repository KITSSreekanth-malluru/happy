<%@ page contentType="text/html; charset=ISO-8859-1"%>
<dsp:importbean bean="/com/castorama/droplet/CastMagasinItineraireDroplet" />
<div class="whitePopupContainer" id="itineraire">
	<div class="whitePopupContent popupFormContainer">
		<div class="whitePopupHeader">
            <fmt:message key="castCatalog_label.close" var="fermer"/>
			<a href="javascript:void(0)" onclick="hidePopup(this)" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a>
		</div>
			
		<div class="clear"><!--~--></div>
		<div class="popupContentContainer">
		<div class="popupForm">
			<div class="formMainBlock">
				<dsp:droplet name="/atg/dynamo/droplet/Switch">
					<dsp:param param="formOk" name="value" />
					<dsp:oparam name="true">
				
						<dsp:droplet name="CastMagasinItineraireDroplet">
							<dsp:param name="street0" param="street0" />
							<dsp:param name="city0" param="city0" />
							<dsp:param name="magasinId" param="magasinId" />
							<dsp:oparam name="output">
							<dsp:getvalueof var="url" param='url' />
							<c:if test="${not empty url }">			                  
			        <dsp:getvalueof var="url" param="url"/>
							<iframe width="100%" height="500" frameborder="0" border="none" id="route" 
								src="${url }" scrolling="auto"> 
							</iframe>
			        </c:if>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
			</div>
			<div class="clear"><!--~--></div>
		</div>
		</div>
	</div>
</div>