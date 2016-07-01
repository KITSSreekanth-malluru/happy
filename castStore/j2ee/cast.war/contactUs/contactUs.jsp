<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>
  <dsp:importbean bean="/com/castorama/contact/ContactUsFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof id="requestURI" bean="/OriginatingRequest.requestURI" idtype="java.lang.String"/>

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <script type="text/javascript" src="${contextPath}/js/contactUs.js" ></script>
      <c:choose>
        <c:when test="${param.back == 'faq'}">
          <dsp:getvalueof var="subElement" value="header.faq" scope="request"/>
        </c:when>
        <c:otherwise>
          <dsp:getvalueof var="subElement" value="header.contact.customer"  scope="request"/>
        </c:otherwise>
      </c:choose>
      <dsp:getvalueof var="brElement" value="contactUs.header.after.faq"  scope="request"/>
      <dsp:getvalueof var="pageType" value="contactUs" scope="request"/>
      <dsp:include page="../user/includes/breadcrumbsClientHeader.jsp">
        <dsp:param name="element" value="${brElement}" />
        <dsp:param name="pageType" value="${pageType}" />
        <dsp:param name="subElement" value="${subElement}" />
      </dsp:include>
      <dsp:getvalueof var="owner" param="owner"/> 
      <c:if test="${empty owner}">
        <dsp:getvalueof var="owner" value="faq1"/>
      </c:if>
      <dsp:include page="/user/clientMenu.jsp">
        <dsp:param name="currPage" value="${owner}"/>
      </dsp:include>
        
      <div class="clientSpace">
        <div class="formBlock">
          <div class="formMainBlock">
            <div class="orderTitle">
              <h2><fmt:message key="contactUs.title"/></h2>
            </div>
            
            <dsp:form method="post" action="${requestURI}" id="contactUs" name="contactUs">
              <dsp:setvalue bean="ContactUsFormHandler.initForm" value=""/>
              <dsp:input type="hidden" id="webMasterForm"  name="webMasterForm" bean="ContactUsFormHandler.webMasterForm"/>
              <dsp:input type="hidden" id="iphoneForm"  name="iphoneForm" bean="ContactUsFormHandler.iphoneForm"/>
              <dsp:input type="hidden" id="formInitialized"  name="formInitialized" bean="ContactUsFormHandler.formInitialized"/>
              <dsp:input bean="ContactUsFormHandler.successUrl" type="hidden" value="${requestURI}"/>
              <dsp:input bean="ContactUsFormHandler.errorUrl" type="hidden" value="${requestURI}" />
              
              <dsp:getvalueof var="webMasterForm" bean="ContactUsFormHandler.webMasterForm"/>
              <dsp:getvalueof var="iphone" bean="ContactUsFormHandler.iphoneForm"/>
              
              <dsp:droplet name="Switch">
                <dsp:param bean="ContactUsFormHandler.formError" name="value" />
                <dsp:oparam name="true">
                  <div class="errorList">
                    <UL>
                      <dsp:droplet name="ErrorMessageForEach">
                        <dsp:param bean="ContactUsFormHandler.formExceptions" name="exceptions" />
                        <dsp:oparam name="output">
                          <LI><dsp:valueof param="message" />
                        </dsp:oparam>
                      </dsp:droplet>
                    </UL>
                  </div>
                </dsp:oparam>
              </dsp:droplet>
              
              <dsp:getvalueof var="formSubmited" bean="ContactUsFormHandler.formSubmitted"/>
              <c:if test="${formSubmited}">
                <div class="grayCorner grayCornerGray rounderrorMessage">
                  <div class="grayBlockBackground"><!--~--></div>
                  <div class="cornerBorder cornerTopLeft"><!--~--></div>
                  <div class="cornerBorder cornerTopRight"><!--~--></div>
                  <div class="cornerBorder cornerBottomLeft"><!--~--></div>
                  <div class="cornerBorder cornerBottomRight"><!--~--></div>
                  
                  <div class="preMessage">
                    <table cellspacing="0" cellpadding="0" class="emilateValignCenter">
                      <tbody>
                        <tr>
                          <td class="center">
                            <strong><fmt:message key="contactUs.msg.formSubmitted"/></strong>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </c:if>
              
              <div class="popupContentContainer formMainBlock orderMsgContainer">
                <div class="formContent grayCorner grayCornerWhite">
                    <div class="cornerBorder cornerTopLeft"><!--~--></div>
                    <div class="cornerBorder cornerTopRight"><!--~--></div>
                    <div class="cornerBorder cornerBottomLeft"><!--~--></div>
                    <div class="cornerBorder cornerBottomRight"><!--~--></div>
                    <div class="cornerOverlay">
                      <div class="contactAdvFormTxt">
                        <div id="productMessage" style="display: none;">
                          <fmt:message key="contactUs.msg.comment.product"/>
                        </div>
                        <div id="iphoneMessage">
                          <fmt:message key="contactUs.msg.comment.iphone"/>
                        </div>
                        <div id="webmasterMessage" style="display: none;">
                          <fmt:message key="contactUs.msg.comment.webmaster"/>
                        </div>
                      </div>
                                  
                        <div class="centerFormMessage"><fmt:message key="contactUs.msg.requiredFields"/></div>
                        <div class="f-row grayFrowbg">
                            <label><strong><fmt:message key="contactUs.required.motif"/></strong></label>
                            <div class="f-inputs">
                                <dsp:getvalueof var="motifName" bean="ContactUsFormHandler.motifName"/>
                                <dsp:select bean="ContactUsFormHandler.motifName" name="motifName" id="motifName" iclass="i-selectW408" onchange="checkMotif(this);">
                                    <c:if test="${iphone and (empty motifName)}">
                                        <dsp:option value="" style="display:none;"></dsp:option>
                                    </c:if>
                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" bean="ContactUsFormHandler.motifObjects"/>
                                        <dsp:oparam name="output">
                                            <dsp:getvalueof var="motifId" param="element.id"/>
                                            <dsp:getvalueof var="motifTitle" param="element.motif.title"/>
                                            <c:choose>
                                                <c:when test="${(motifId == 'motif1') or (motifId == 'motif17') or (motifId == 'motif24')}">
                                                    <option value="${motifId}" selected="selected"><dsp:valueof param="element.motif.title" /></option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${motifId}"><dsp:valueof param="element.motif.title" /></option>
                                                </c:otherwise>
                                            </c:choose>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </dsp:select>
                            </div>
                        </div>
                        
                        <script type="text/javascript" >
                            <dsp:droplet name="ForEach">
                                <dsp:param name="array" bean="ContactUsFormHandler.motifObjects"/>
                                <dsp:oparam name="output">
                                        var <dsp:valueof param="element.id" /> = {
                                            "formType" : '<dsp:valueof param="element.formType" />',
                                            "message" : "<dsp:valueof param='element.message' valueishtml='false'/>"
                                        };
                                </dsp:oparam>
                            </dsp:droplet>
                        </script>
                        
                        <dsp:input type="hidden" id="motifValue"  name="motifValue" bean="ContactUsFormHandler.motifValue" />
                            <div id="messageDiv" style="text-align: center;"> </div>
                            <div id="formDiv">
                                <div class="side2barForm">
                                    <dsp:getvalueof var="formName" value="ContactUsFormHandler" />
                                    <%@ include file="/contactUs/contactUsProfileSection.jspf" %>
                                    
                                    <div class="f-row">
                                        <label><strong><fmt:message key="user_orderCardAtout.phone"/></strong></label>
                                        <div class="f-inputs">
                                            <dsp:input bean="${phoneNumberBean}" name="phone" id="phone" maxlength="13" size="30" type="text"  iclass="i-text120" beanvalue="Profile.billingAddress.phoneNumber" />
                                            <div class="subNoticeGray"><fmt:message key="contactUs.msg.optionalFields"/></div>
                                        </div>
                                    </div>
                                    
                                    <c:choose>
                                        <c:when test="${webMasterForm}">
                                            <div class="f-row">
                                                <label><strong><fmt:message key="contactUs.optional.note"/></strong></label>
                                                <div class="f-inputs">
                                                    <dsp:textarea bean="ContactUsFormHandler.message" name="message" id="message" iclass="i-textarea fWidthTxtar396" onkeydown="limitText(this,500);" onkeyup="limitText(this,500);"/>&nbsp;*
                                                </div>
                                            </div>
                                            <div class="f-row f-left">
                                                <label><strong><fmt:message key="contactUs.optional.operating.system"/></strong></label>
                                                <div class="f-inputs">
                                                    <dsp:select bean="ContactUsFormHandler.operatingSystem" name="operatingSystem" id="operatingSystem" iclass="i-select">
                                                        <dsp:option value=""></dsp:option>
                                                        <dsp:droplet name="ForEach">
                                                            <dsp:param bean="ContactUsFormHandler.operatingSystems" name="array"/>
                                                            <dsp:oparam name="output">
                                                                <dsp:option paramvalue="element">
                                                                    <dsp:valueof param="element" />
                                                                </dsp:option>
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                    </dsp:select>&nbsp;*
                                                </div>
                                            </div>
                                            <div class="f-row f-right">
                                                <label><strong><fmt:message key="contactUs.optional.connection.type"/></strong></label>
                                                <div class="f-inputs">  
                                                    <dsp:select bean="ContactUsFormHandler.connectionType" name="connectionType" id="connectionType" iclass="i-select">
                                                        <dsp:option value=""></dsp:option>
                                                        <dsp:droplet name="ForEach">
                                                            <dsp:param bean="ContactUsFormHandler.connectionTypes" name="array"/>
                                                            <dsp:oparam name="output">
                                                                <dsp:option paramvalue="element">
                                                                    <dsp:valueof param="element" />
                                                                </dsp:option>
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                    </dsp:select>&nbsp;*
                                                </div>
                                            </div>
                                            <div class="clear"></div>
                                            <div class="f-row">
                                                <label><strong><fmt:message key="contactUs.optional.browser.version"/></strong></label>
                                                <div class="f-inputs">  
                                                    <dsp:select bean="ContactUsFormHandler.browser" name="browser" id="browser" iclass="i-select">
                                                        <dsp:option value=""></dsp:option>
                                                        <dsp:droplet name="ForEach">
                                                            <dsp:param bean="ContactUsFormHandler.browsers" name="array"/>
                                                            <dsp:oparam name="output">
                                                                <dsp:option paramvalue="element">
                                                                    <dsp:valueof param="element" />
                                                                </dsp:option>
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                    </dsp:select>&nbsp;*
                                                </div>
                                            </div>  
                                        </c:when>
                                        <c:when test="${iphone}">
                                            <div class="f-row">
                                                <label><strong><fmt:message key="contactUs.optional.note"/></strong></label>
                                                <div class="f-inputs">  
                                                    <dsp:textarea bean="ContactUsFormHandler.message" name="message" id="message" iclass="i-textarea fWidthTxtar396" onkeydown="limitText(this,500);" onkeyup="limitText(this,500);"/>&nbsp;*                                                                                                   
                                                </div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="f-row">
                                                <label><strong><fmt:message key="contactUs.optional.product.ref"/></strong></label>
                                                <div class="f-inputs">  
                                                    <dsp:input bean="ContactUsFormHandler.product" name="product" id="product" maxlength="30" size="30" type="text"  iclass="i-text120"/>                                                                                                   
                                                </div>
                                            </div>
                                            <div class="f-row">
                                                <label><strong><fmt:message key="contactUs.optional.store"/></strong></label>
                                                <div class="f-inputs">  
                                                    <dsp:getvalueof var="query" value="entite.adresse.departement.numero != 999"/>
                                                    <dsp:select bean="ContactUsFormHandler.magasin" name="magasin" id="magasin" iclass="i-selectW204">
                                                        <dsp:option value=""></dsp:option>
                                                        <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
                                                            <dsp:param name="queryRQL" value="${query}" />
                                                            <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
                                                            <dsp:param name="itemDescriptor" value="magasin" />
                                                            <dsp:param name="elementName" value="magasinRQL" />
                                                            <dsp:param name="sortProperties" value="+entite.adresse.departement.numero,+nom" />
                                                            <dsp:oparam name="output">
                                                                <dsp:option paramvalue="magasinRQL.nom">
                                                                    <dsp:valueof param="magasinRQL.entite.adresse.departement.numero" /> - <dsp:valueof param="magasinRQL.nom" />
                                                                </dsp:option>
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                    </dsp:select>
                                                </div>
                                            </div>  
                                            <div class="f-row">
                                                <label><strong><fmt:message key="contactUs.optional.question"/></strong></label>
                                                <div class="f-inputs">  
                                                    <dsp:textarea bean="ContactUsFormHandler.question" name="question" id="question" iclass="i-textarea fWidthTxtar396"  onkeydown="limitText(this,500);" onkeyup="limitText(this,500);"/>&nbsp;*
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                 
                                <div class="formButtons padded10">
                                    <span class="inputButton">
                                        <fmt:message key="contactUs.button.send" var="submitButtonLabel"/>
                                        <dsp:input bean="ContactUsFormHandler.send" type="submit" value="${submitButtonLabel}" />
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </dsp:form>
          </div>
          
          <div class="popupGrayBrgBlock">
              <strong><fmt:message key="contactUs.mentions.cnil"/></strong><br />
              <fmt:message key="contactUs.mentions.cnil.content"/>
          </div>
          
          <dsp:include page="../user/includes/adviceBlock.jsp" />
        </div>
      </div>
      </div>
    
      <dsp:getvalueof var="formRedirects" bean="ContactUsFormHandler.formRedirects"/>
      <c:if test="${empty param.motif}">
          <c:if test="${not formRedirects}">
              <script type="text/javascript" >
                  document.getElementById("formDiv").style.display = 'none';
              </script>
          </c:if>
      </c:if>
      
      <script type="text/javascript" >
      $(document).ready(function(){
          var motifSelectBox = document.getElementById("motifName");
          checkMotif(motifSelectBox);
      });
      </script>
    
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>