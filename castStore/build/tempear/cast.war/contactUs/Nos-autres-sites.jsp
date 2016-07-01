<dsp:page>
  <dsp:importbean bean="/com/castorama/invite/InviteFormHanlder" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:getvalueof id="requestURI" bean="/OriginatingRequest.requestURI" idtype="java.lang.String"/> 
  <dsp:getvalueof var="result" param="res"/> 
  <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
    
       
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <div class="formMainBlock questionContent">
        <dsp:getvalueof var="name" value="header.faq" />
		<div class="breadcrumbs bluePage">
		    <div class="homeBreadIco">
		      	<a href="${pageContext.request.contextPath}/home.jsp">
			        <img title="" alt="" src="${pageContext.request.contextPath}/images/icoHomeGray.gif"/>
			    </a>
			</div>
		    <div class="splitter">&gt;</div>
		    <div class="active"><fmt:message key="contactUs.other.sites"/></div>
		</div>
     	<div class="custom_content_container">
			<c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/nos-autres-sites.html"/>
		</div>
		
      </jsp:attribute>
  </cast:pageContainer>
</dsp:page>