<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<script type="text/javascript">
function setCountry(f) {
	n = f.state.selectedIndex;
	if(n) {
		f.country.value=f.state.options[n].text;
		if(f.country.value=="France") {
			f.phone1.value="";
			f.phone2.value="";
			if (document.getElementById('phoneFix')) {
				f.telephone_telform.value="";
			}
			setPhoneNumberMaxLength(f, 10);
		} else {
			setPhoneNumberMaxLength(f, 17);
		}
	}
}

function setPhoneNumberMaxLength(form, length) {
	form.phone1.setAttribute('maxlength', length);
	form.phone2.setAttribute('maxlength', length);
	if (document.getElementById('phoneFix'))
		form.telephone_telform.setAttribute('maxlength', length);
}  
</script>

<dsp:page>
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/CastDeliveryFormHandler"/>
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastReserveCatalog" />  
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />  
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>

<dsp:getvalueof var="newAccount" param="newAccount"/>
<dsp:getvalueof var="useKeyUpName" param="useKeyUpName"/>
<dsp:getvalueof var="content" param="content"/>
<dsp:getvalueof var="bean" param="bean"/>
<dsp:getvalueof var="editValue" param="editValue"/>
<dsp:getvalueof var="isCCOrder" param="isCCOrder"/>

<c:if test="${not empty editValue}">
  <dsp:setvalue bean="${bean}.editValue" value="${editValue}"/>
</c:if>


<c:choose>
  <c:when test="${content == 'address' }">
    <dsp:getvalueof var="titleBean" value="${bean}.editValue.prefix"/>
    <dsp:getvalueof var="firstNameBean" value="${bean}.editValue.firstName"/>
    <dsp:getvalueof var="lastNameBean" value="${bean}.editValue.lastName"/>
    <dsp:getvalueof var="ownerIdBean" value="${bean}.editValue.ownerId"/>
    <dsp:getvalueof var="address1Bean" value="${bean}.editValue.address1"/>
    <dsp:getvalueof var="address2Bean" value="${bean}.editValue.address2"/>
    <dsp:getvalueof var="address3Bean" value="${bean}.editValue.address3"/>
    <dsp:getvalueof var="localityBean" value="${bean}.editValue.locality"/>
    <dsp:getvalueof var="postalCodeBean" value="${bean}.editValue.postalCode"/>
    <dsp:getvalueof var="cityBean" value="${bean}.editValue.city"/>
    <dsp:getvalueof var="stateBean" value="${bean}.editValue.state"/>
    <dsp:getvalueof var="countryBean" value="${bean}.editValue.country"/>
    <dsp:getvalueof var="phoneNumberBean" value="${bean}.editValue.phoneNumber"/>
    <dsp:getvalueof var="phoneNumber2Bean" value="${bean}.editValue.phoneNumber2"/>
    <dsp:getvalueof var="phoneNumberFixBean" value="${bean}.editValue.phoneNumberFix"/>
    <dsp:input bean="${ownerIdBean}" type="hidden" beanvalue="Profile.repositoryId"/>
    <dsp:getvalueof var="addressBounds" bean="/com/castorama/util/ContactAddressBounds"/>
  </c:when>
  <c:when test="${content == 'profile' }">
    <dsp:getvalueof var="titleBean" value="${bean}.prefix"/>
    <dsp:getvalueof var="firstNameBean" value="${bean}.firstName"/>
    <dsp:getvalueof var="lastNameBean" value="${bean}.lastName"/>
    <dsp:getvalueof var="ownerIdBean" value="${bean}.ownerId"/>
    <dsp:getvalueof var="address1Bean" value="${bean}.address1"/>
    <dsp:getvalueof var="address2Bean" value="${bean}.address2"/>
    <dsp:getvalueof var="address3Bean" value="${bean}.address3"/>
    <dsp:getvalueof var="localityBean" value="${bean}.locality"/>
    <dsp:getvalueof var="postalCodeBean" value="${bean}.postalCode"/>
    <dsp:getvalueof var="cityBean" value="${bean}.city"/>
    <dsp:getvalueof var="stateBean" value="${bean}.state"/>
    <dsp:getvalueof var="countryBean" value="${bean}.country"/>
    <dsp:getvalueof var="phoneNumberBean" value="${bean}.phoneNumber"/>
    <dsp:getvalueof var="phoneNumber2Bean" value="${bean}.phoneNumber2"/>
    <dsp:input bean="${ownerIdBean}" type="hidden" beanvalue="Profile.repositoryId"/>
    <dsp:getvalueof var="addressBounds" bean="/com/castorama/util/ContactAddressBounds"/>
    <dsp:getvalueof var="currentLocalStore" value="${bean}.currentLocalStore"/>
  </c:when>
  <c:when test="${content == 'reserveCatalogue' }">
    <dsp:getvalueof var="titleBean" value="${bean}.civilite"/>
    <dsp:getvalueof var="firstNameBean" value="${bean}.prenom"/>
    <dsp:getvalueof var="lastNameBean" value="${bean}.nom"/>
    <dsp:getvalueof var="address1Bean" value="${bean}.adresse"/>
    <dsp:getvalueof var="address2Bean" value="${bean}.adresse2"/>
    <dsp:getvalueof var="address3Bean" value="${bean}.adresse3"/>
    <dsp:getvalueof var="localityBean" value="${bean}.adresse4"/>
    <dsp:getvalueof var="postalCodeBean" value="${bean}.codePostal"/>
    <dsp:getvalueof var="cityBean" value="${bean}.ville"/>
    <dsp:getvalueof var="stateBean" value="${bean}.state"/>
    <dsp:getvalueof var="countryBean" value="${bean}.pays"/>
    <dsp:getvalueof var="phoneNumberBean" value="${bean}.phoneNumber1"/>
    <dsp:getvalueof var="phoneNumber2Bean" value="${bean}.phoneNumber2"/>
    <dsp:getvalueof var="addressBounds" bean="/com/castorama/util/ContactAddressBounds"/>
  </c:when>
</c:choose>

  <dsp:getvalueof var="formExceptions" param="formExceptions" />
  <dsp:getvalueof var="formError" param="formError" />
  <dsp:getvalueof var="flagErrorCp" param="flagErrorCp" />
  <c:if test="${empty formExceptions}">
    <dsp:getvalueof var="formExceptions" bean="${bean}.formExceptions" />
  </c:if>
  <c:if test="${empty formError}">
    <dsp:getvalueof var="formError" bean="${bean}.formError" />
  </c:if>
  <c:if test="${empty flagErrorCp}">
    <dsp:getvalueof var="flagErrorCp" bean="${bean}.flagErrorCp" />
  </c:if>

<c:if test="${not empty newAccount && newAccount}">
  <div class="f-row">
    
    <label class="required">&nbsp;</label>
    <div class="f-inputs">
      <p class="i-message"><fmt:message key="msg.address.phoneNumberFix.instruction" /></p> 
    </div>
  </div>
  <div class="f-row">
    <label class="required"><fmt:message key="msg.address.phoneNumberFix" /> * :</label>
    <div class="f-inputs">
      <input id="phoneFix" maxlength="${addressBounds.phoneNumberSize}" size="43" type="text"  iclass="i-text" name="telephone_telform" onkeyup="isTel();"/>
      <input type="hidden" value="5000" name="session_telform"/>
    </div>
  </div>
  <div class="splitter"><div class="splitterGrayLine"></div></div>
</c:if>
<div class="f-row">
  <label class="required"><fmt:message key="msg.address.prefix" /> * :</label>
  <div class="f-inputs">
     <dsp:input bean="${titleBean }" type="radio" value="miss"  iclass="i-radio" name="prefix"/>
     <span class="i-radio-lbl"><fmt:message key="msg.address.prefix.miss" /></span>
    <dsp:input bean="${titleBean }" type="radio" value="mrs" iclass="i-radio" name="prefix"/>
    <span class="i-radio-lbl"><fmt:message key="msg.address.prefix.mrs" /></span> 
    <dsp:input bean="${titleBean }" type="radio" value="mr" iclass="i-radio" name="prefix"/>
    <span class="i-radio-lbl"><fmt:message key="msg.address.prefix.mr" /></span> 
    <dsp:input bean="${titleBean }" type="radio" value="organization" iclass="i-radio" name="prefix"/>
    <span class="i-radio-lbl"><fmt:message key="msg.address.prefix.organization" /></span>
  </div>
</div>
<div class="f-row">
  <label class="required"><fmt:message key="msg.address.firstname" /> * :</label>
  <div class="f-inputs">
    <c:choose>
      <c:when test="${not empty useKeyUpName && useKeyUpName}">
        <dsp:input bean="${firstNameBean }" valueishtml="false" maxlength="${addressBounds.firstNameSize}" size="30" type="text"  iclass="i-text" id="passVerifFname" name="firstname" onkeyup="$('#passVerifContainer').validator({obj: $('#passVerifFirstPass')[0], param: new Array('passVerifEmail', 'passVerifFname', 'passVerifLname')})" />
      </c:when>
      <c:otherwise>
        <dsp:input bean="${firstNameBean }" valueishtml="false" maxlength="${addressBounds.firstNameSize}" size="30" type="text"  iclass="i-text" id="passVerifFname" name="firstname"/>
      </c:otherwise>
    </c:choose>
  </div>
</div>
<div class="f-row">
   <label class="required"><fmt:message key="msg.address.lastname" /> * :</label>
   <div class="f-inputs">
    <c:choose>
      <c:when test="${not empty useKeyUpName && useKeyUpName}">
        <dsp:input bean="${lastNameBean }" maxlength="${addressBounds.lastNameSize}" size="30"  type="text" iclass="i-text" id="passVerifLname" name="lastname" onkeyup="$('#passVerifContainer').validator({obj: $('#passVerifFirstPass')[0], param: new Array('passVerifEmail', 'passVerifFname', 'passVerifLname')})" />
      </c:when>
      <c:otherwise>
        <dsp:input bean="${lastNameBean }" maxlength="${addressBounds.lastNameSize}" size="30"  type="text" iclass="i-text" id="passVerifLname" name="lastname"/>
      </c:otherwise>
    </c:choose>
   </div>
</div>
<div class="splitter"><div class="splitterGrayLine"></div></div>
<div class="f-row">
  <label class="required"><fmt:message key="msg.address.address1" /> * :</label>
  <div class="f-inputs">
    <dsp:input bean="${address1Bean}" maxlength="${addressBounds.address1Size}" size="30"  type="text" iclass="i-text" name="address1"/>
  </div>
</div>          
<div class="f-row">
  <label><fmt:message key="msg.address.address2" /> :</label>
  <div class="f-inputs"> <dsp:input bean="${address2Bean}" maxlength="${addressBounds.address2Size}" size="30" type="text" iclass="i-text" name="address2"/></div>
</div>
<div class="f-row">
  <label><fmt:message key="msg.address.address3" /> :</label>
  <div class="f-inputs"><dsp:input bean="${address3Bean}" maxlength="${addressBounds.address3Size}" size="30" type="text" iclass="i-text" name="address3"/></div>
</div>
<div class="f-row">
  <label><fmt:message key="msg.address.locality" /> :</label>
  <div class="f-inputs"><dsp:input bean="${localityBean}" maxlength="${addressBounds.localitySize}" size="30" type="text" iclass="i-text" name="locality"/></div>
</div>
<div class="f-row">
  <label class="required"> <fmt:message key="msg.address.postalCode" /> * :</label>
  <div class="f-inputs"><dsp:input bean="${postalCodeBean}" maxlength="${addressBounds.postalCodeSize}" size="30" type="text"  iclass="i-postal" name="postcode"/>
     <span class="tFieldAdvLotRows">
       <dsp:droplet name="Switch">
         <dsp:param value="${formError}" name="value"/>
         <dsp:oparam name="true">
           <div class="error">
             <dsp:droplet name="ForEach">
               <dsp:param value="${formExceptions}" name="array"/>
               <dsp:param name="elementName" value="exception"/>
               <dsp:oparam name="output">
                 <dsp:getvalueof var="errorCode" param="exception.errorCode"/>      
                 <c:if test="${errorCode == 'incorrectCity'}">
                   <dsp:valueof param="exception.message" valueishtml="true"/>
                 </c:if>
               </dsp:oparam>
             </dsp:droplet>
           </div>
         </dsp:oparam>
      </dsp:droplet>
      
    </span>
  </div>  
</div>
<div class="f-row">
  <label class="required"><fmt:message key="msg.address.city" /> * :</label>
    <div class="f-inputs">
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
      <dsp:param name="value" value="${flagErrorCp}"/>
      <dsp:oparam name="true">
      <dsp:getvalueof var="postCode" bean="${postalCodeBean}"/>
        <dsp:droplet name="/com/castorama/droplet/CastCityViaPostalCode">
          <dsp:param name="codePostal" value="${postCode}" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="count" param="count"/>

                <dsp:select bean="${cityBean}" iclass="i-selectW204 option-red" id="citySelect" name="city" valueishtml="false">
                  <dsp:getvalueof var="city" bean="${cityBean}">
                    <c:if test="${not empty city }">
                      <dsp:option iclass="option-red" id="0" valueishtml="false">
                          <c:out value="${city }"/>
                      </dsp:option>
                    </c:if>
                   </dsp:getvalueof>
                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="listeVilles"/>
                    <dsp:param name="sortProperties" value="+"/>
                    <dsp:oparam name="output">
                      <dsp:option paramvalue="element" >
                        <dsp:getvalueof var="city" param="element">
                          <c:out value="${city }"/>
                        </dsp:getvalueof>
                      </dsp:option>                             
                    </dsp:oparam>
                  </dsp:droplet>  
                </dsp:select>

          </dsp:oparam>
          <dsp:oparam name="empty">
            <dsp:input bean="${cityBean}" maxlength="${addressBounds.citySize}" size="30" type="text"  iclass="i-text" name="city" valueishtml="false"/>
          </dsp:oparam>        
        </dsp:droplet>  
      </dsp:oparam>
      <dsp:oparam name="false">  
        <dsp:input bean="${cityBean}" maxlength="${addressBounds.citySize}" size="30" type="text"  iclass="i-text" name="city" valueishtml="false"/>
      </dsp:oparam>
    </dsp:droplet>
  </div>
</div>
<div class="f-row">
  <label class="required"><fmt:message key="msg.address.country" /> * :</label>
  <div class="f-inputs">
    <dsp:getvalueof var="currCountry" bean="${stateBean}"/>
    <dsp:select  bean="${stateBean}" priority="2" iclass="i-selectW204" onchange="setCountry(this.form)" name="state">
      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" bean="/atg/commerce/util/CountryList_fr.places" />
        <dsp:param name="elementName" value="country" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="countryCode" param="country.code"/>
          <c:choose>
             <c:when test="${content == 'address' || content == 'delivery' }">
               <c:if test="${countryCode == 'F'}">
                 <dsp:option paramvalue="country.code" selected="true">
                   <dsp:valueof param="country.displayName"/>
                 </dsp:option>
               </c:if>
             </c:when>
            <c:otherwise>
               <c:choose>
                 <c:when test="${countryCode == 'F' && empty currCountry}">
                   <dsp:option paramvalue="country.code" selected="true">
                     <dsp:valueof param="country.displayName"/>
                   </dsp:option>
                 </c:when>
                 <c:when test="${not empty currCountry && countryCode == currCountry}">
                   <dsp:option paramvalue="country.code" selected="true">
                     <dsp:valueof param="country.displayName"/>
                   </dsp:option>
                 </c:when>
                 <c:otherwise>
                   <dsp:option paramvalue="country.code">
                     <dsp:valueof param="country.displayName"/>                      
                   </dsp:option>
                 </c:otherwise>
               </c:choose>
            </c:otherwise>
          </c:choose>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:select>
     <dsp:input type="hidden" bean="${countryBean}" name="country"/>
    <span class="tFieldAdvLotRows">
      <c:if test="${!isCCOrder}">
        <fmt:message key="msg.delivery.note.not.france" />
      </c:if>
    </span>
  </div>
</div>
<div class="splitter"><div class="splitterGrayLine"></div></div>
<div class="f-row">
  <label class="required"><fmt:message key="msg.address.phoneNumber" /> * :</label>
  <div class="f-inputs"><dsp:input bean="${phoneNumberBean}" maxlength="${addressBounds.phoneNumberSize}" size="30" type="text"  iclass="i-text" name="phone1"/>
    <span class="tFieldAdvLotRows">
      <c:choose>
        <c:when test="${isCCOrder}">
          <fmt:message key="msg.address.phoneNumber2.text.cc" />
        </c:when>
        <c:otherwise>
          <fmt:message key="msg.address.phoneNumber2.text" />
        </c:otherwise>
      </c:choose>
    </span>
  </div>
</div>
<div class="f-row">
  <label><fmt:message key="msg.address.phoneNumber2" /> :</label>
  <div class="f-inputs"><dsp:input bean="${phoneNumber2Bean}" maxlength="${addressBounds.phoneNumber2Size}" size="30" type="text" iclass="i-text" name="phone2"/>&nbsp;</div>
</div>

<div class="attentionForm"><fmt:message key="msg.accout.required.fields" /></div>

<script>

function setPhoneNumberMaxLengthJQ(length) {
	$('input[name="phone1"]').attr("maxlength", length);
	$('input[name="phone2"]').attr("maxlength", length);
	if ($('#phoneFix'))
		$('input[name="telephone_telform"]').attr("maxlength", length);
}  

  $(document).ready(function(){

	var country = $('select[name="state"]').val();
	if(country == "F") {
		setPhoneNumberMaxLengthJQ(10);
	} else {
		setPhoneNumberMaxLengthJQ(17);   		  
    } 
    
  $("#citySelect").change(function(){
   $("#citySelect option:selected").each(function () {
     if (this.id != "0") {
       $('#citySelect').removeClass('option-red');
     } else if (this.id == "0"){
       $('#citySelect').addClass('option-red');
     }
   });
  }).change();  

  });
</script>
</dsp:page>