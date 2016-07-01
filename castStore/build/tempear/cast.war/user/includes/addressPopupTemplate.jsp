<%@ taglib prefix="dsp"
  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
  
<dsp:page>
  
  <dsp:getvalueof var="title" param="title"/>
  <dsp:getvalueof var="popupContainerID" param="popupContainerID"/>
  <dsp:getvalueof var="content" param="content" />
  <dsp:getvalueof var="errorURLparamName" param="errorURLparamName" />
  <dsp:getvalueof var="successURLparamName" param="successURLparamName" />
  <dsp:getvalueof var="errorURLvalue" param="errorURLvalue" />
  <dsp:getvalueof var="successURLvalue" param="successURLvalue" />
  <dsp:getvalueof var="cancelURL" param="cancelURL" />  
  
  <dsp:getvalueof var="bean" param="bean" />
  
  <c:if test="${empty bean }">
    <dsp:getvalueof var="bean" value="CastProfileFormHandler" />
  </c:if>
  
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
	<dsp:setvalue bean="CastProfileFormHandler.extractDefaultValuesFromProfile" value="true"/>
	
	  <div class="whitePopupContainer" id="${popupContainerID }">
	    <div class="whitePopupContent popupFormContainer">
	      <div class="whitePopupHeader">
	        <h1>${title }</h1>
            <fmt:message key="castCatalog_label.close" var="fermer"/>
	        <a href="javascript:void(0)" onclick="hidePopup(this)" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a>
	      </div>
	      <div class="clear"><!--~--></div>                     
	      <div class="popupContentContainer">
	        <div class="popupForm">
	          <div class="formMainBlock">
	          
	          
	            <dsp:form method="post"  formid="${popupContainerID }" id="${popupContainerID}_form">
	  
	              <dsp:include page="/user/includes/profileErrorBlock.jsp">
	               <dsp:param name="formExceptions" param="formExceptions"/>
	               <dsp:param name="formError" param="formError"/>
	              </dsp:include> 
	            
	              <div class=" formContent grayCorner grayCornerGray">
	              <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
	              <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
	              <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
	              <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
	              <div class="cornerOverlay">
	              
	               <c:if test="${content == 'address' }">
	                 <dsp:input bean="CastProfileFormHandler.shipToBillingAddress" type="hidden" value="true" />
	               </c:if>
	               <dsp:include page="/user/includes/contentMyAddress.jsp">
	                 <dsp:param name="content" param="content"/>
	                 <dsp:param name="bean" value="${bean}"/>
	                 <dsp:param name="formExceptions" param="formExceptions"/>
                   <dsp:param name="formError" param="formError"/>
                   <dsp:param name="flagErrorCp" param="flagErrorCp"/>
                   <dsp:param name="editValue" param="editValue"/>
	               </dsp:include>
	              </div></div>
                <div class="formButtons">
                  <span class="ccBascetButton">
                    <button class="ccBascetButton" onclick="hidePopup(this)" type="button" ><fmt:message key="cc.popup.buttons.annuler" /></button>
                  </span>
                  <span class="ccBascetButton blue">
                    <button class="ccBascetButton" id="submit" type="button" onclick="submitCastoramaCard(); hidePopup(this)" ><fmt:message key="cc.popup.buttons.valider"/></button>
                  </span>
                </div>
	            </dsp:form>
	          </div> 
	          <div class="clear"><!--~--></div>                
	        </div>           
	      </div>
	    </div>
	  </div>
</dsp:page>