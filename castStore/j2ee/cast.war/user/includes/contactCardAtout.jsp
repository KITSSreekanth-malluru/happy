<dsp:page>

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
   
   <script type="text/javascript" src="${contextPath}/js/contactUs.js" ></script>
   
   <div class="formMainBlock">
   
	<div class="orderTitleM">
		<h2>
			<fmt:message key="user_contactCardAtout.title"/>
  		</h2>
  	</div>    

    <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
 	<c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/contactCardAtout.html"/>
 	
  </div>  

  <dsp:include page="adviceBlock.jsp" /> 
</dsp:page>