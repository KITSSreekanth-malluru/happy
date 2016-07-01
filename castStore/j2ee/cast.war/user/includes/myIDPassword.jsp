   <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />              
           
           	<h2>
           	<dsp:getvalueof var="parent" param="parent"/>
           	<c:choose>           	
           		<c:when test="${parent == 'createAccount' || parent == 'createLightAccount'}">
           			<fmt:message key="msg.account.login.header" />
           		</c:when>
           		<c:otherwise>
           			<fmt:message key="msg.profile.login.header" />
           		</c:otherwise>
           	</c:choose>
           	</h2>
               <div class="formContent grayCorner grayCornerGray">
                <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
                <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
                <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
                <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
                <div class="cornerOverlay">
                <div class="f-row">
                	<label class="required"><fmt:message key="msg.accout.address.email" /> * :</label>
   	                <div class="f-inputs">
   	                <dsp:input bean="CastProfileFormHandler.value.login" maxlength="50" size="30" type="text" iclass="i-text" id="passVerifEmail" /></div>
       	        </div>
                <div class="f-row">
                	<label class="required"><fmt:message key="msg.accout.address.email.confirm" /> * :</label>
   	                <div class="f-inputs">
   	                <dsp:input bean="CastProfileFormHandler.confirmLogin" maxlength="50" size="30" type="text" iclass="i-text" value=""/>
				</div>
       	        </div>
                   <div class="f-row">
                	<label class="required"><fmt:message key="msg.accout.password" /> * :</label>
   	                <div class="f-inputs">
   	                <dsp:input bean="CastProfileFormHandler.value.password" maxlength="20" size="20"  type="password" iclass="i-pass" id="passVerifFirstPass" value=""/>
   	                <span class="tFieldAdv">
   	                <c:choose>           	
		           		<c:when test="${parent == 'createAccount' || parent == 'createLightAccount'}">
		           			<fmt:message key="msg.accout.password.explanation" />
		           		</c:when>
		           		<c:otherwise>
		           			<fmt:message key="msg.profile.password.explanation" />
		           		</c:otherwise>
		           	</c:choose>
   	                
   	                </span></div>
       	        </div>
                <div class="f-row">
                	<label class="required"><fmt:message key="msg.accout.password.confirm" /> * :</label>
   	                <div class="f-inputs">
   	                <dsp:input bean="CastProfileFormHandler.confirmPassword" type="hidden" value="false" />
   	                <dsp:input bean="CastProfileFormHandler.value.confirmPassword" maxlength="20" size="20"  type="password" iclass="i-pass" value=""/>
   	                </div>
       	        </div>
                <div class="attentionForm"><fmt:message key="msg.accout.required.fields" /></div>                        
               </div>
           </div>
           

