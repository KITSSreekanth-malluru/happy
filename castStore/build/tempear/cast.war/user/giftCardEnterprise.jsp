<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
    <div class="content"> 
    
    <dsp:getvalueof var="brElement" value="header.my.card.gift" scope="request"/>
    <dsp:include page="includes/breadcrumbsClientHeader.jsp">
      <dsp:param name="element" value="${brElement}"/>
    </dsp:include>
    <c:set var="footerBreadcrumb" value="client" scope="request"/>
    <dsp:include page="clientMenu.jsp">
      <dsp:param name="currPage" value="giftCard"/>
    </dsp:include>
    
    <div class="clientSpace">
    <div class="formBlock giftPage">
      <div class="formMainBlock myaccount">
        
            <h2><fmt:message key="header.my.card.discover"/></h2>
            
            <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
            <c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/giftCardEnterprise.html"/>

    </div>
  <dsp:include page="includes/adviceBlock.jsp" /> 
  </div>  
  </div>
  </jsp:attribute>
</cast:pageContainer>
</dsp:page>