<dsp:page>
<dsp:importbean bean="/com/castorama/atout/CastOrderAtoutFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

<dsp:getvalueof var="formErrors" bean="CastOrderAtoutFormHandler.formError" />
<dsp:getvalueof var="success" param="success" />

  
   <div class="formMainBlock">
	<div class="orderTitleM">
  		<h2>
			<fmt:message key="user_orderCardAtout.title"/>  
  		</h2>
  	</div>   
   
   <div class="sTitleMsgFmt"><fmt:message key="user_orderCardAtout.description"/></div>
      
     <dsp:droplet name="Switch">
       <dsp:param bean="CastOrderAtoutFormHandler.formError" name="value" />
       <dsp:oparam name="true">
        <div class="errorList">
           <UL>
             <dsp:droplet name="ErrorMessageForEach">
               <dsp:param bean="CastOrderAtoutFormHandler.formExceptions" name="exceptions" />
               <dsp:oparam name="output">
                <LI> <dsp:valueof param="message" /> 
                </dsp:oparam>
             </dsp:droplet>
           </UL>
         </div>
       </dsp:oparam>
     </dsp:droplet>    

   <c:if test="${success && !formErrors}">
	   
    
     <div class="grayCorner grayCornerGray rounderrorMessage">
         <div class="grayBlockBackground"><!--~--></div>
         <div class="cornerBorder cornerTopLeft"><!--~--></div>
         <div class="cornerBorder cornerTopRight"><!--~--></div>
         <div class="cornerBorder cornerBottomLeft"><!--~--></div>
         <div class="cornerBorder cornerBottomRight"><!--~--></div>                                                                                    
       
         <div class="preMessage">
         
           <table cellspacing="0" cellpadding="0" class="emilateValignCenter">
             <tbody><tr>
               <td class="center">
                 <fmt:message key="user_contactCardAtout.subscribe"/>                      
               </td>
             </tr>
           </tbody></table>                                    
         </div>
       </div>
   </c:if>   
   
     <div class="formContent grayCorner grayCornerWhite">
      <div class="cornerBorder cornerTopLeft"></div>
      <div class="cornerBorder cornerTopRight"></div>
      <div class="cornerBorder cornerBottomLeft"></div>
      <div class="cornerBorder cornerBottomRight"></div>
      <div class="cornerOverlay">
        <div class="contactAdvFormTxt"><fmt:message key="user_contactCardAtout.blockTitle"/></div>
        <div class="centerFormMessage"><fmt:message key="user_contactCardAtout.checkRequired"/></div>
        <dsp:form action="${contextPath}/user/orderCardAtout.jsp" method="post" id="formulaire" name="formulaire">
          <dsp:input type="hidden" bean="CastOrderAtoutFormHandler.successUrl" value="${contextPath}/user/orderCardAtout.jsp?success=true"/>
          <dsp:input type="hidden" bean="CastOrderAtoutFormHandler.errorUrl" value="${contextPath}/user/orderCardAtout.jsp"/>
          <%-- %><dsp:input type="hidden" bean="CastOrderAtoutFormHandler.type" value="atout"/>--%>
          
          <div class="side2barForm">
          
          <dsp:getvalueof var="formName" value="CastOrderAtoutFormHandler" />
          <%@ include file="/contactUs/contactUsProfileSection.jspf" %>
          <div class="f-row">
            <label><strong><fmt:message key="user_orderCardAtout.phone"/></strong></label>
            <div class="f-inputs">
            	<dsp:input iclass="i-text120" type="text" bean="CastOrderAtoutFormHandler.phone" id="phone" name="phone" title="phone" maxlength="13"/>&nbsp;*
            </div>
          </div>
          
          <div class="f-row">
            <label><strong><fmt:message key="user_orderCardAtout.rowOfDays"/></strong></label>
            <div class="f-inputs f-checkbox-line">            	
            	<dsp:input type="checkbox" bean="CastOrderAtoutFormHandler.lundi" name="jour1" iclass="casecocher"/> <span>Lundi</span>
            	<dsp:input type="checkbox" bean="CastOrderAtoutFormHandler.mardi" name="jour2" iclass="casecocher"/> <span>Mardi</span>
            	<dsp:input type="checkbox" bean="CastOrderAtoutFormHandler.mercredi" name="jour3" iclass="casecocher"/> <span>Mercredi</span>
            	<dsp:input type="checkbox" bean="CastOrderAtoutFormHandler.jeudi"  name="jour4" iclass="casecocher"/> <span>Jeudi</span>
            	<dsp:input type="checkbox" bean="CastOrderAtoutFormHandler.vendredi" name="jour5" iclass="casecocher"/> <span>Vendredi</span>
            </div>
          </div>
          
          <div class="f-row">
            <label><strong><fmt:message key="user_orderCardAtout.suitableDayPeriod"/></strong></label>
            <div class="f-inputs f-checkbox-line"> 
            	<dsp:input type="checkbox" bean="CastOrderAtoutFormHandler.matin"  name="moment" /><span>Plut&ocirc;t le matin</span>
            	<dsp:input type="checkbox" bean="CastOrderAtoutFormHandler.apresMidi" name="moment1"/> <span>Plut&ocirc;t l'apr&egrave;s-midi</span>
            </div>
          </div>
          <div class="f-row">
            <label><strong><fmt:message key="user_orderCardAtout.selectFavoriteStore"/></strong></label>
            <div class="f-inputs">
            <dsp:select id="magasin" name="magasin" bean="CastOrderAtoutFormHandler.magasin">
              <dsp:option value=""><fmt:message key="user_contactCardAtout.choose"/></dsp:option>
              
              <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
                <dsp:param name="queryRQL" value="ALL" />
                <dsp:param name="repository" value="/atg/registry/Repository/MagasinGSARepository" />
                <dsp:param name="itemDescriptor" value="magasin" />             
                <dsp:param name="sortProperties" value="+entite.adresse.cp" />
                <dsp:oparam name="output">
                  <dsp:getvalueof var="code" param="element.entite.adresse.cp" />
                  <dsp:getvalueof var="name" param="element.nom" />
                  <dsp:getvalueof var="id" param="element.repositoryId" />
                    <!-- bug id=4000741400010997110. Temporary decision: do not display with id=999 i.e. "CASTORAMA.FR" -->
                    <c:if test="${id!=999}">
                      <dsp:option value="${fn:substring(code, 0, 2)}${name }">                
                        <c:out value="${fn:substring(code, 0, 2)}" /> -  
                        <c:out value="${name }" />
                      </dsp:option>
                    </c:if>
                    <c:if test="${id==999}">
                        <dsp:option value="${id}">
                            <fmt:message key="castorama.fr" />
                        </dsp:option>
                    </c:if> 
                </dsp:oparam>
              </dsp:droplet>
            </dsp:select>&nbsp;*
            </div>
          </div>
          
          </div>
          
		<div class="formButtons padded10">
			<span class="inputButton">					
				<dsp:input type="submit" bean="CastOrderAtoutFormHandler.sendEmail" value="ENVOYER"/>
			</span>
		</div>					                    
      
  
        </dsp:form>
      </div>
    </div>
  </div>
  
  <div class="popupGrayBrgBlock">
	<strong><fmt:message key="user_orderCardAtout.mentions"/></strong><br />
	<dsp:include page="mentionsCNILTargeter.jsp" flush="true"/> <%-- %><fmt:message key="user_orderCardAtout.mentionsInfo"/>--%>	           		
  </div>
		  
  <dsp:include page="adviceBlock.jsp" /> 
</dsp:page>