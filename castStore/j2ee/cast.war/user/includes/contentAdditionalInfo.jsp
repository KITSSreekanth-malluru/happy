<dsp:page>
<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
	<dsp:getvalueof var="bean" param="bean"/>
	<dsp:getvalueof var="beanValue" param="beanValue"/>
    <dsp:getvalueof var="magasinId" param="magasinId" />

	<dsp:getvalueof var="client" bean="CastNewsletterFormHandler.value.accesPartenairesCasto"/>
	
	 	<div class="f-row"> 	
 		<label><b><fmt:message key="msg.accout.know.better.client" /> * :</b></label>
		<div class="f-inputs">
			<dsp:input bean="CastNewsletterFormHandler.value.accesPartenairesCasto" checked="${client || not empty magasinId}" type="radio" value="true" iclass="i-radio" onclick="showStoreField();"/>
			<span class="i-radio-lbl"><fmt:message key="msg.accout.oui" /></span>
			<dsp:input bean="CastNewsletterFormHandler.value.accesPartenairesCasto" type="radio" value="false" iclass="i-radio" onclick="hideStoreField();"/>
			<span class="i-radio-lbl"><fmt:message key="msg.accout.non" /></span>
		</div>
	</div>
	
	<c:choose>
		<c:when test="${client || not empty magasinId}">
			<div class="f-row" id="store">
		</c:when>
		<c:otherwise>
			<div class="f-row" id="store" style="display:none">
		</c:otherwise>
	</c:choose>
    <label><fmt:message key="msg.accout.know.better.store" /> :</label>
    <div class="f-inputs formSelector">
        <div class="chooseShopSelectorWr favStorePair">
            <dsp:getvalueof var="query" value="entite.adresse.departement.numero != 999" />
            <dsp:select bean="CastNewsletterFormHandler.prefStore" iclass="styled" id="geo2">
                <dsp:option value="">
                    <fmt:message key="msg.newsletter.choose.store" />
                </dsp:option>
                <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
                    <dsp:param name="queryRQL" value="${query}" />
                    <dsp:param name="repository" value="/atg/registry/Repository/MagasinGSARepository" />
                    <dsp:param name="itemDescriptor" value="magasin" />
                    <dsp:param name="elementName" value="magasinRQL" />
                    <dsp:param name="sortProperties" value="+entite.adresse.cp,+nom" />
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="iscc" param="magasinRQL.retraitMagasin" />
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

	<div class="f-row">
    	<label><fmt:message key="msg.accout.know.better.status" /> :</label>
		<div class="f-inputs">
			<dsp:input bean="CastNewsletterFormHandler.value.proprietaire_locataire" type="radio" value="Owner" iclass="i-radio"/>
				<span class="i-radio-lbl"><fmt:message key="msg.accout.know.better.status.owner" /></span>
			<dsp:input bean="CastNewsletterFormHandler.value.proprietaire_locataire" type="radio" value="Tenant" iclass="i-radio"/>
				<span class="i-radio-lbl"><fmt:message key="msg.accout.know.better.status.tenant" /></span>
		</div>
	</div>
	<div class="f-row">
    	<label><b><fmt:message key="msg.accout.know.better.status.homeType" /> * :</b></label>
    	<div class="f-inputs">
			<dsp:input bean="CastNewsletterFormHandler.value.maison" type="radio" value="true" iclass="i-radio"/>
				<span class="i-radio-lbl"><fmt:message key="msg.accout.know.better.status.house" /></span> 
			<dsp:input bean="CastNewsletterFormHandler.value.maison" type="radio" value="false" iclass="i-radio"/>
				<span class="i-radio-lbl"><fmt:message key="msg.accout.know.better.status.apartment" /></span>
		</div>
	</div>
	<div class="f-row">
    	<label><b><fmt:message key="msg.accout.know.better.garden" /> * :</b></label>
    	<div class="f-inputs">
			<dsp:input bean="CastNewsletterFormHandler.value.jardin" type="radio" value="true" iclass="i-radio"/>
				<span class="i-radio-lbl"><fmt:message key="msg.accout.oui" /></span>
			<dsp:input bean="CastNewsletterFormHandler.value.jardin" type="radio" value="false" iclass="i-radio"/>
				<span class="i-radio-lbl"><fmt:message key="msg.accout.non" /></span>
		</div>
	</div>
	<div class="f-row">
    	<label><fmt:message key="msg.accout.know.better.maisonCampagne" /> :</label>
    	<div class="f-inputs">
			<dsp:input bean="CastNewsletterFormHandler.value.maisonCampagne" type="radio" value="true" iclass="i-radio"/>
				<span class="i-radio-lbl"><fmt:message key="msg.accout.oui" /></span>
			<dsp:input bean="CastNewsletterFormHandler.value.maisonCampagne" type="radio" value="false" iclass="i-radio"/>
				<span class="i-radio-lbl"><fmt:message key="msg.accout.non" /></span>
		</div>
	</div>
	
	<div class="f-row">
   	 	<label><fmt:message key="msg.accout.know.better.householdPeople" /> :</label>
		<div class="f-inputs">
			<dsp:input bean="CastNewsletterFormHandler.value.nbPersonnes" type="radio" value="1" iclass="i-radio"/> <span class="i-radio-lbl">1</span>
			<dsp:input bean="CastNewsletterFormHandler.value.nbPersonnes" type="radio" value="2" iclass="i-radio"/> <span class="i-radio-lbl">2</span> 
			<dsp:input bean="CastNewsletterFormHandler.value.nbPersonnes" type="radio" value="3" iclass="i-radio"/> <span class="i-radio-lbl">3</span>
			<dsp:input bean="CastNewsletterFormHandler.value.nbPersonnes" type="radio" value="4 and more" iclass="i-radio"/>
			<span class="i-radio-lbl"><fmt:message key="msg.accout.know.better.householdPeople.moreThan4" /></span>
		</div>
	</div>	
	<div class="splitter"><div class="splitterGrayLine"></div></div>
	<div class="f-row">
    <label><fmt:message key="msg.accout.know.better.dob" /> :</label>
		<div class="f-inputs"><dsp:input bean="CastNewsletterFormHandler.dob" type="text" maxlength="8" iclass="i-date"  id="dob"/>
		<span class="tFieldAdvLotRows2"><fmt:message key="msg.accout.know.better.default.dob" /></span>
		</div>
	</div>
</dsp:page>