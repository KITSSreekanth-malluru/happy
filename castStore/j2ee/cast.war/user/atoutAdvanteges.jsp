<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">    
    <div class="content">
      <dsp:getvalueof var="brElement" value="user_atoutAdvanteges.breadCrumb" scope="request"/>
      
      <dsp:include page="includes/breadcrumbsClientHeader.jsp">
        <dsp:param name="element" value="${brElement}"/>
      </dsp:include>
      <c:set var="footerBreadcrumb" value="client" scope="request"/>
      
      <dsp:include page="clientMenu.jsp">
	      <dsp:param name="currPage" value="atoutAdvanteges"/>
	    </dsp:include>
      
        <div class="clientSpace">
          <div class="formBlock">
            <dsp:include page="includes/atoutAdvanteges.jsp" flush="true">
          
            </dsp:include>  
          </div>
        </div>
    </div>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>