<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
  <dsp:importbean bean="/com/castorama/invite/InviteFormHanlder" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/com/castorama/CastConfiguration"/>

  <dsp:getvalueof id="requestURI" bean="/OriginatingRequest.requestURI" idtype="java.lang.String"/> 
  <dsp:getvalueof var="result" param="res"/>
  <dsp:getvalueof var="epticaUrl" bean="CastConfiguration.epticaUrl"/>
  <dsp:getvalueof var="epticaUrlContactService" bean="CastConfiguration.epticaUrlContactService"/>
  <dsp:getvalueof var="epticaIFrameHeight" bean="CastConfiguration.epticaIFrameHeight"/>
  <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
  <fmt:message key="castCatalog_label.close" var="fermer"/>
  
  <dsp:getvalueof var="id" param="id"/>
  <dsp:getvalueof var="owner" param="owner"/>
  <c:if test="${empty owner}">
    <dsp:getvalueof var="owner" value="faq2"/>
  </c:if>

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <div class="content">
        <dsp:getvalueof var="brElement" value="header.faq"  scope="request"/>
        <dsp:include page="../user/includes/breadcrumbsClientHeader.jsp">
          <dsp:param name="element" value="${brElement}" />
        </dsp:include>
        <c:set var="footerBreadcrumb" value="client" scope="request"/>
        <dsp:include page="../user/clientMenu.jsp">
          <dsp:param name="currPage" value="${owner}"/>
        </dsp:include>
        <div class="formBlock noTopMargin">
          <c:choose>
            <c:when test="${owner == 'faq1'}">
              <iframe src="${epticaUrlContactService}" width="100%" height="${epticaIFrameHeight}" style="border:0" frameborder="0">
                <p><fmt:message key="contactUs.faq.msg.not.support"/></p>
              </iframe>
            </c:when>
		<c:when test="${owner == 'faq2'}">
		<c:if test="${not empty id}">
	      <iframe src="${epticaUrl}?id=${id}" width="100%" height="${epticaIFrameHeight}" style="border:0" frameborder="0">
                <p><fmt:message key="contactUs.faq.msg.not.support"/></p>
          </iframe>
         </c:if>
         <c:if test="${empty id}">
          <iframe src="${epticaUrl}" width="100%" height="${epticaIFrameHeight}" style="border:0" frameborder="0">
                <p><fmt:message key="contactUs.faq.msg.not.support"/></p>
          </iframe>
	     </c:if>
         </c:when>
         <c:otherwise>
          <iframe  src="${epticaUrl}" width="100%" height="${epticaIFrameHeight}" style="border:0" frameborder="0">
                <p><fmt:message key="contactUs.faq.msg.not.support"/></p>
           </iframe>
         </c:otherwise>
         </c:choose>
           
         <dsp:include page="../user/includes/adviceBlock.jsp"> 
           <dsp:param name="hideFAQBannner" value="true"/>
           <dsp:param name="hideAdviceLink" value="true"/>
         </dsp:include>   
       </div>
    </jsp:attribute>
  </cast:pageContainer>
	<div class="whitePopupContainer" id="faq" style="position: absolute;">
		<div class="whitePopupContent">
			<div class="whitePopupHeader">
				<h1><fmt:message key="header.faq"/></h1>
				<a href="javascript:void(0);" onclick="hidePopup(this)" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a></div>
	
				<div class="clear"><!--~--></div>
				<div class="popupContentContainer">
					<div class="popupForm">
						<div class="formMainBlock questionContent">
							<c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/faq-popup-content.html"/>						
						</div>
					<div class="clear"><!--~--></div>
				</div>
			</div>
		</div>
	</div>
	<div class="whitePopupContainer" id="paymentTerms" style="position: absolute;">
		<div class="whitePopupContent">
			<div class="whitePopupHeader">
				<a href="javascript:void(0);" onclick="hidePopup(this)" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a></div>
	
				<div class="clear"><!--~--></div>
				<div class="popupContentContainer">
					<div class="popupForm">
						<div class="formMainBlock questionContent">
							<c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/paymentTerms-content.html"/>
						</div>
					<div class="clear"><!--~--></div>
				</div>
			</div>
		</div>
	</div>
	
	<dsp:getvalueof var="popupName" param="popupName"/>	
	
	<c:if test="${not empty popupName && fn:length(popupName)>0}">
	<script type="text/javascript">
	 showPopup('${popupName}');
	</script>
	</c:if>
	
</dsp:page>



