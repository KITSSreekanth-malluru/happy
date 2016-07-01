<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">    
      <script type="text/javascript" src="${contextPath}/js/contactUs.js" ></script>
      <div class="content">
        <dsp:getvalueof var="name" value="user_contactCardAtout.breadCrumb"/>
        <dsp:include page="includes/breadcrumbsClientHeader.jsp">
          <dsp:param name="element" value="${name}"/>
          <dsp:param name="isLatoutBreadCrumb" value="true"/>
        </dsp:include>
		    <dsp:include page="clientMenu.jsp">
		      <dsp:param name="currPage" value="atoutAdvanteges"/>
		    </dsp:include>
        
        
        <div class="formBlock noTopMargin">
          <dsp:include page="includes/contactCardAtout.jsp" flush="true">            
          </dsp:include>
        </div>
      </div>
    </jsp:attribute>
  </cast:pageContainer>
  
  

</dsp:page>