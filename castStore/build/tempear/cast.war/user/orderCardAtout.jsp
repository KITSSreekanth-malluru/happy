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
      <dsp:getvalueof var="brElement" value="user_orderCardAtout.breadCrumb" scope="request"/>
      <dsp:include page="includes/breadcrumbsClientHeader.jsp">
        <dsp:param name="element" value="${brElement}"/>
      </dsp:include>
		    <dsp:include page="clientMenu.jsp">
		      <dsp:param name="currPage" value="orderCardAtout"/>
		    </dsp:include>
      	<c:set var="footerBreadcrumb" value="client" scope="request"/>
      
        <div class="formBlock noTopMargin">
        <dsp:include page="includes/orderCardAtout.jsp" flush="true">
          
        </dsp:include>  
        </div>
    </div>
    </jsp:attribute>
  </cast:pageContainer>

</dsp:page>