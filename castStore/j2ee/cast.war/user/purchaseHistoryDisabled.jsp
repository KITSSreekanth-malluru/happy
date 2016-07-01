<dsp:page>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">    
    <div class="content">
      <dsp:getvalueof var="brElement" value="user_atoutAdvanteges.breadCrumb" scope="request"/>
      
      <dsp:include page="includes/breadcrumbsClientHeader.jsp">
        <dsp:param name="element" value="${brElement}"/>
        <dsp:param name="pageType" value="store.orders"/>
      </dsp:include>
      <c:set var="footerBreadcrumb" value="client" scope="request"/>
      <dsp:getvalueof var="pageType" value="store.orders" scope="request"/>
      
      <dsp:include page="clientMenu.jsp">
	      <dsp:param name="currPage" value="storeOrders"/>
	    </dsp:include>
      
        <div class="clientSpace">
          <div class="formBlock">
          
             <div class="storeOrdersUnavailable"/>
               <div class="darkRed rdAlert"><fmt:message key="client.store.orders.disabled"/> </div>
             </div>
             <dsp:include page="../user/includes/adviceBlock.jsp" />
             
          </div>
        </div>
    </div>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>